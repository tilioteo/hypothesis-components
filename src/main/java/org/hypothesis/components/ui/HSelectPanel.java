package org.hypothesis.components.ui;

import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import org.hypothesis.components.shared.ui.selectbutton.HSelectButtonState.LabelPosition;
import org.hypothesis.components.ui.SelectButton.ClickEvent;
import org.hypothesis.components.ui.SelectButton.ClickListener;
import org.hypothesis.ui.Field;

import java.util.*;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author kamil
 */
public class HSelectPanel extends MultipleComponentPanel<SelectButton> implements ClickListener, Field, Validatable {

    protected final List<ClickListener> clickListeners = new ArrayList<>();
    protected final LinkedList<SelectButton> selectedButtons = new LinkedList<>();
    /**
     * The list of validators.
     */
    private final List<Validator> validators = new LinkedList<>();
    private String[] captions;
    private LabelPosition labelPosition = LabelPosition.Right;
    private boolean multiSelect = false;
    /**
     * Is automatic validation enabled.
     */
    private boolean validationVisible = true;

    public HSelectPanel() {
        super();
        setSizeUndefined();
        //addStyleName("light");
    }

    public void addButtonClickListener(SelectButton.ClickListener buttonClickListener) {
        this.clickListeners.add(buttonClickListener);

        for (SelectButton button : childList) {
            button.addClickListener(buttonClickListener);
        }
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        if (this.multiSelect != multiSelect) {
            this.multiSelect = multiSelect;

            addChildren();
        }
    }

    public void setCaptions(String[] captions) {
        this.captions = captions;

        addChildren();
    }

    public Collection<SelectButton> getSelectedButtons() {
        if (selectedButtons.size() == 0) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(selectedButtons);
        }
    }

    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        if (this.labelPosition != labelPosition) {
            this.labelPosition = labelPosition;
            updateLabelPositions();
        }
    }

    private void updateLabelPositions() {
        Iterator<SelectButton> iterator = getChildIterator();
        while (iterator.hasNext()) {
            iterator.next().setLabelPosition(labelPosition);
        }
    }

    public void addSelected(SelectButton button) {
        if (childList.contains(button) && !selectedButtons.contains(button)) {
            if (!multiSelect) {
                for (SelectButton selectButton : selectedButtons) {
                    selectButton.setValue(false);
                }
                selectedButtons.clear();
            }

            selectedButtons.add(button);
            if (!button.getValue()) {
                button.setValue(true);
            }
        }
        if (childList.contains(button) && !validators.isEmpty()) {
            markAsDirty();
        }
    }

    public void removeSelected(SelectButton button) {
        if (selectedButtons.contains(button)) {
            selectedButtons.remove(button);
            if (button.getValue()) {
                button.setValue(false);
            }
            if (!validators.isEmpty()) {
                markAsDirty();
            }
        }
    }

    public boolean isSelected(int index) {
        if (index >= 0 && index < childList.size()) {
            return selectedButtons.contains(childList.get(index));
        }
        return false;
    }

    public void setSelected(int index, boolean value) {
        if (index >= 0 && index < childList.size()) {
            if (value) {
                addSelected(childList.get(index));
            } else {
                removeSelected(childList.get(index));
            }
        }
    }

    protected void addChildren() {
        int i = 1;
        removeAllChildren();

        for (String caption : captions) {
            if (null == caption) {
                caption = "";
            }
            SelectButton selectButton = multiSelect ? new HCheckBox(caption) : new HRadioButton(caption);
            selectButton.setLabelPosition(labelPosition);
            selectButton.setData(String.format("%s_%d", this.getData() != null ? this.getData() : "", i++));

            selectButton.addClickListener(this);
            for (SelectButton.ClickListener listener : clickListeners) {
                selectButton.addClickListener(listener);
            }

            addChild(selectButton);
        }
        updateContent();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        SelectButton button = event.getSelectButton();
        if (button.getValue()) {
            addSelected(button);
        } else {
            removeSelected(button);
        }
    }

    public void clearSelection() {
        for (SelectButton button : selectedButtons) {
            button.setValue(false);
        }
        selectedButtons.clear();
        getUI().focus();
    }

    @Override
    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (Validator.InvalidValueException e) {
            return false;
        }
    }

    @Override
    public void addValidator(Validator validator) {
        validators.add(validator);
        markAsDirty();
    }

    @Override
    public void removeValidator(Validator validator) {
        validators.remove(validator);
        markAsDirty();
    }

    @Override
    public void removeAllValidators() {
        validators.clear();
        markAsDirty();
    }

    @Override
    public Collection<Validator> getValidators() {
        return unmodifiableCollection(validators);
    }

    @Override
    public void validate() throws Validator.InvalidValueException {
        validate(getSelectedButtons());
    }

    protected void validate(Collection<SelectButton> values) throws Validator.InvalidValueException {

        List<Validator.InvalidValueException> validationExceptions = new ArrayList<>();
        // Gets all the validation errors
        for (Validator v : validators) {
            try {
                v.validate(values);
            } catch (final Validator.InvalidValueException e) {
                validationExceptions.add(e);
            }
        }

        // If there were no errors
        if (validationExceptions.isEmpty()) {
            return;
        }

        // If only one error occurred, throw it forwards
        if (validationExceptions.size() == 1) {
            throw validationExceptions.get(0);
        }

        Validator.InvalidValueException[] exceptionArray = validationExceptions
                .toArray(new Validator.InvalidValueException[0]);

        // Create a composite validator and include all exceptions
        throw new Validator.InvalidValueException(null, exceptionArray);
    }

    @Override
    public ErrorMessage getErrorMessage() {

        /*
         * Check validation errors only if automatic validation is enabled.
         * Empty, required fields will generate a validation error containing
         * the requiredError string. For these fields the exclamation mark will
         * be hidden but the error must still be sent to the client.
         */
        Validator.InvalidValueException validationError = null;
        if (isValidationVisible()) {
            try {
                validate();
            } catch (Validator.InvalidValueException e) {
                if (!e.isInvisible()) {
                    validationError = e;
                }
            }
        }

        // Check if there are any systems errors
        final ErrorMessage superError = super.getErrorMessage();

        // Return if there are no errors at all
        if (superError == null && validationError == null) {
            return null;
        }

        // Throw combination of the error types
        return new CompositeErrorMessage(
                superError, AbstractErrorMessage.getErrorMessageForException(validationError));
    }

    public boolean isValidationVisible() {
        return validationVisible;
    }

    public void setValidationVisible(boolean validateAutomatically) {
        if (validationVisible != validateAutomatically) {
            markAsDirty();
            validationVisible = validateAutomatically;
        }
    }

    @Override
    public boolean isInvalidAllowed() {
        return false;
    }

    @Override
    public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException {
        // nop
    }

    public boolean hasClickListener() {
        return !clickListeners.isEmpty();
    }

}
