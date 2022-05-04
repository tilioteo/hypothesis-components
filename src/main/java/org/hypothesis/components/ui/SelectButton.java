package org.hypothesis.components.ui;

import com.tilioteo.common.event.TimekeepingComponentEvent;
import com.vaadin.event.FieldEvents.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.ReflectTools;
import org.hypothesis.components.shared.ui.selectbutton.HSelectButtonServerRpc;
import org.hypothesis.components.shared.ui.selectbutton.HSelectButtonState;
import org.hypothesis.components.shared.ui.selectbutton.HSelectButtonState.LabelPosition;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

/**
 * @author Kamil Morong - Hypothesis
 */
public abstract class SelectButton extends AbstractField<Boolean> {

    private final HSelectButtonServerRpc rpc = (timestamp, mouseEventDetails, checked) -> {
        if (isReadOnly()) {
            return;
        }

        /*
         * Client side updates the state before sending the event, so we need
         * to make sure the cached state is updated to match the client. If
         * we do not do this, a reverting setValue() call in a listener will
         * not cause the new state to be sent to the client.
         *
         * See #11028, #10030.
         */
        getUI().getConnectorTracker().getDiffState(SelectButton.this)
                .put("checked", checked);

        final Boolean oldValue = getValue();
        final Boolean newValue = checked;

        if (!newValue.equals(oldValue)) {
            // The event is only sent if the switch state is changed
            setValue(newValue);
        }
        fireClick(timestamp, mouseEventDetails);
    };
    protected ClickShortcut clickShortcut;
    FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {
        @Override
        protected void fireEvent(Event event) {
            SelectButton.this.fireEvent(event);
        }
    };

    /**
     * Creates a new select button. The value of the select button is false, and
     * it is immediate by default.
     */
    public SelectButton() {
        registerRpc(rpc);
        registerRpc(focusBlurRpc);
        setValue(Boolean.FALSE);
    }

    /**
     * Creates a new select button.
     * <p>
     * The value of the select button is false, and it is immediate by default.
     *
     * @param caption the SelectButton caption.
     */
    public SelectButton(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new select button with a caption and a set initial state.
     *
     * @param caption      the caption of the select button
     * @param initialState the initial state of the select button
     */
    public SelectButton(String caption, boolean initialState) {
        this(caption);
        setValue(initialState);
    }

    /**
     * Creates a new select button with click listener.
     *
     * @param caption  the SelectButton caption.
     * @param listener the SelectButton click listener.
     */
    public SelectButton(String caption, ClickListener listener) {
        this(caption);
        addClickListener(listener);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    protected HSelectButtonState getState() {
        return (HSelectButtonState) super.getState();
    }

    /*
     * Overridden to keep the shared state in sync with the AbstractField
     * internal value. Should be removed once AbstractField is refactored to use
     * shared state.
     *
     * See tickets #10921 and #11064.
     */
    @Override
    protected void setInternalValue(Boolean newValue) {
        super.setInternalValue(newValue);
        if (newValue == null) {
            newValue = false;
        }
        getState().checked = newValue;
    }

    @Override
    public void setValue(Boolean newValue) {
        setInternalValue(newValue);
    }

    /**
     * Adds the select button click listener.
     *
     * @param listener the Listener to be added.
     */
    public void addClickListener(ClickListener listener) {
        addListener(ClickEvent.class, listener, ClickListener.BUTTON_CLICK_METHOD);
    }

    /**
     * Removes the select button click listener.
     *
     * @param listener the Listener to be removed.
     */
    public void removeClickListener(ClickListener listener) {
        removeListener(ClickEvent.class, listener, ClickListener.BUTTON_CLICK_METHOD);
    }

    /**
     * Simulates a select button click, notifying all server-side listeners.
     * <p>
     * No action is taken if the select button is disabled.
     */
    public void click() {
        if (isEnabled() && !isReadOnly()) {
            fireClick();
        }
    }

    /**
     * Fires a click event to all listeners without any event details.
     */
    protected void fireClick() {
        Date now = new Date();
        fireEvent(new ClickEvent(now.getTime(), this));
    }

    /**
     * Fires a click event to all listeners.
     *
     * @param details MouseEventDetails from which keyboard modifiers and other
     *                information about the mouse click can be obtained. If the
     *                button was clicked by a keyboard event, some fields may
     *                be empty/undefined.
     */
    protected void fireClick(long timestamp, MouseEventDetails details) {
        fireEvent(new ClickEvent(timestamp, this, details));
    }

    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener, BlurListener.blurMethod);
    }

    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener, FocusListener.focusMethod);
    }

    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * Makes it possible to invoke a click on this select button by pressing the
     * given {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * The shortcut is global (bound to the containing Window).
     *
     * @param keyCode   the keycode for invoking the shortcut
     * @param modifiers the (optional) modifiers for invoking the shortcut, null for
     *                  none
     */
    public void setClickShortcut(int keyCode, int... modifiers) {
        if (clickShortcut != null) {
            removeShortcutListener(clickShortcut);
        }
        clickShortcut = new ClickShortcut(this, keyCode, modifiers);
        addShortcutListener(clickShortcut);
        getState().clickShortcutKeyCode = clickShortcut.getKeyCode();
    }

    /*
     * Actions
     */

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setClickShortcut(int, int...)}.
     */
    public void removeClickShortcut() {
        if (clickShortcut != null) {
            removeShortcutListener(clickShortcut);
            clickShortcut = null;
            getState().clickShortcutKeyCode = 0;
        }
    }

    public LabelPosition getLabelPosition() {
        return getState().labelPosition;
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        getState().labelPosition = labelPosition;
    }

    public boolean getLabelVisible() {
        return getState().labelVisible;
    }

    public void setLabelVisible(boolean visible) {
        getState().labelVisible = visible;
    }

    @Override
    public void attach() {
        super.attach();

        setId(getConnectorId());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#readDesign(org.jsoup.nodes.Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        if (design.hasAttr("checked")) {
            this.setValue(DesignAttributeHandler.readAttribute("checked",
                    design.attributes(), Boolean.class));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#getCustomAttributes()
     */
    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        attributes.add("checked");
        return attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#writeDesign(org.jsoup.nodes.Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        CheckBox def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("checked", attr, getValue(),
                def.getValue(), Boolean.class);
    }

    @Override
    public void clear() {
        setValue(Boolean.FALSE);
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null || getValue().equals(Boolean.FALSE);

    }

    /**
     * Interface for listening for a {@link ClickEvent} fired by a
     * {@link Component}.
     */
    public interface ClickListener extends Serializable {

        Method BUTTON_CLICK_METHOD = ReflectTools
                .findMethod(ClickListener.class, "buttonClick", ClickEvent.class);

        /**
         * Called when a {@link Button} has been clicked. A reference to the
         * button is given by {@link ClickEvent#getSelectButton()}.
         *
         * @param event An event containing information about the click.
         */
        void buttonClick(ClickEvent event);

    }

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that invokes a click on the given select button.
     */
    public static class ClickShortcut extends ShortcutListener {
        protected final SelectButton button;

        /**
         * Creates a keyboard shortcut for clicking the given select button
         * using the shorthand notation defined in {@link ShortcutAction}.
         *
         * @param button           to be clicked when the shortcut is invoked
         * @param shorthandCaption the caption with shortcut keycode and modifiers indicated
         */
        public ClickShortcut(SelectButton button, String shorthandCaption) {
            super(shorthandCaption);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given select button
         * using the given {@link KeyCode} and {@link ModifierKey}s.
         *
         * @param button    to be clicked when the shortcut is invoked
         * @param keyCode   KeyCode to react to
         * @param modifiers optional modifiers for shortcut
         */
        public ClickShortcut(SelectButton button, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given select button
         * using the given {@link KeyCode}.
         *
         * @param button  to be clicked when the shortcut is invoked
         * @param keyCode KeyCode to react to
         */
        public ClickShortcut(SelectButton button, int keyCode) {
            this(button, keyCode, (int[]) null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            button.click();
        }
    }

    /**
     * Click event. This event is thrown, when the select button is clicked.
     */
    public static class ClickEvent extends TimekeepingComponentEvent {

        private final MouseEventDetails details;

        /**
         * New instance of text change event.
         *
         * @param source the Source of the event.
         */
        public ClickEvent(long timestamp, Component source) {
            super(timestamp, source);
            details = null;
        }

        /**
         * Constructor with mouse details
         *
         * @param source  The source where the click took place
         * @param details Details about the mouse click
         */
        public ClickEvent(long timestamp, Component source, MouseEventDetails details) {
            super(timestamp, source);
            this.details = details;
        }

        /**
         * Returns the mouse position (x coordinate) when the click took place.
         * The position is relative to the browser client area.
         *
         * @return The mouse cursor x position or -1 if unknown
         */
        public int getClientX() {
            if (null != details) {
                return details.getClientX();
            } else {
                return -1;
            }
        }

        /**
         * Returns the mouse position (y coordinate) when the click took place.
         * The position is relative to the browser client area.
         *
         * @return The mouse cursor y position or -1 if unknown
         */
        public int getClientY() {
            if (null != details) {
                return details.getClientY();
            } else {
                return -1;
            }
        }

        /**
         * Gets the SelectButton where the event occurred.
         *
         * @return the Source of the event.
         */
        public SelectButton getSelectButton() {
            return (SelectButton) getSource();
        }

        /**
         * Returns the relative mouse position (x coordinate) when the click
         * took place. The position is relative to the clicked component.
         *
         * @return The mouse cursor x position relative to the clicked layout
         * component or -1 if no x coordinate available
         */
        public int getRelativeX() {
            if (null != details) {
                return details.getRelativeX();
            } else {
                return -1;
            }
        }

        /**
         * Returns the relative mouse position (y coordinate) when the click
         * took place. The position is relative to the clicked component.
         *
         * @return The mouse cursor y position relative to the clicked layout
         * component or -1 if no y coordinate available
         */
        public int getRelativeY() {
            if (null != details) {
                return details.getRelativeY();
            } else {
                return -1;
            }
        }

        /**
         * Checks if the Alt key was down when the mouse event took place.
         *
         * @return true if Alt was down when the event occurred, false otherwise
         * or if unknown
         */
        public boolean isAltKey() {
            if (null != details) {
                return details.isAltKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Ctrl key was down when the mouse event took place.
         *
         * @return true if Ctrl was pressed when the event occurred, false
         * otherwise or if unknown
         */
        public boolean isCtrlKey() {
            if (null != details) {
                return details.isCtrlKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Meta key was down when the mouse event took place.
         *
         * @return true if Meta was pressed when the event occurred, false
         * otherwise or if unknown
         */
        public boolean isMetaKey() {
            if (null != details) {
                return details.isMetaKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Shift key was down when the mouse event took place.
         *
         * @return true if Shift was pressed when the event occurred, false
         * otherwise or if unknown
         */
        public boolean isShiftKey() {
            if (null != details) {
                return details.isShiftKey();
            } else {
                return false;
            }
        }
    }

}
