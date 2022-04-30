/**
 *
 */
package org.hypothesis.components.client.ui;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.ui.Icon;
import org.hypothesis.components.shared.ui.selectbutton.HSelectButtonState.LabelPosition;

/**
 * @author Kamil Morong - Hypothesis
 */
public abstract class SelectButton extends FocusWidget implements ClickHandler, HasValue<Boolean> {

	public static final String CLASSNAME = "v-selectbutton";

	// mouse movement is checked before synthesizing click event on mouseout
	protected static final int MOVE_THRESHOLD = 3;
	public final Element wrapper = DOM.createDiv();
	public final Element captionElement = DOM.createLabel();
	protected final Element inputElement;
	public ApplicationConnection client;
	public Element errorIndicatorElement;
	public Icon icon;
	public int clickShortcut = 0;
	public LabelPosition labelPosition = LabelPosition.Right;
	public boolean labelVisible = true;
	protected int mousedownX = 0;
	protected int mousedownY = 0;
	protected Element tableElement;
	protected Element outerInputElement;

	/*
	 * BELOW PRIVATE MEMBERS COPY-PASTED FROM GWT CustomButton
	 */
	/**
	 * Helper flag to handle special-case where the button is moved from under
	 * mouse while clicking it. In this case mouse leaves the button without
	 * moving.
	 */
	protected boolean clickPending;
	private boolean enabled = true;
	private int tabIndex = 0;
	/**
	 * If <code>true</code>, this widget is capturing with the mouse held down.
	 */
	private boolean isCapturing;
	/**
	 * If <code>true</code>, this widget has focus with the space bar down. This
	 * means that we will get events when the button is released, but we should
	 * trigger the button only if the button is still focused at that point.
	 */
	private boolean isFocusing;
	/**
	 * Used to decide whether to allow clicks to propagate up to the superclass
	 * or container elements.
	 */
	private boolean disallowNextClick = false;
	private boolean isHovering;
	private HandlerRegistration focusHandlerRegistration;
	private HandlerRegistration blurHandlerRegistration;
	private Boolean oldValue;

	public SelectButton() {
		super(DOM.createDiv());
		inputElement = createInputElement();

		setTabIndex(0);
		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.KEYEVENTS);

		setStyleName(CLASSNAME);

		// Add a11y role "button"
		Roles.getButtonRole().set(getElement());

		getElement().appendChild(wrapper);
		buildLabel();

		setStyleName(CLASSNAME);
	}

	private static native int getHorizontalBorderAndPaddingWidth(Element elem)
		/*-{
			// THIS METHOD IS ONLY USED FOR INTERNET EXPLORER, IT DOESN'T WORK WITH OTHERS

			var convertToPixel = function (elem, value) {
				// From the awesome hack by Dean Edwards
				// http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291

				// Remember the original values
				var left = elem.style.left, rsLeft = elem.runtimeStyle.left;

				// Put in the new values to get a computed value out
				elem.runtimeStyle.left = elem.currentStyle.left;
				elem.style.left = value || 0;
				var ret = elem.style.pixelLeft;

				// Revert the changed values
				elem.style.left = left;
				elem.runtimeStyle.left = rsLeft;

				return ret;
			}

			var ret = 0;

			var sides = ["Right", "Left"];
			for (var i = 0; i < 2; i++) {
				var side = sides[i];
				var value;
				// Border -------------------------------------------------------
				if (elem.currentStyle["border" + side + "Style"] != "none") {
					value = elem.currentStyle["border" + side + "Width"];
					if (!/^\d+(px)?$/i.test(value) && /^\d/.test(value)) {
						ret += convertToPixel(elem, value);
					} else if (value.length > 2) {
						ret += parseInt(value.substr(0, value.length - 2));
					}
				}

				// Padding -------------------------------------------------------
				value = elem.currentStyle["padding" + side];
				if (!/^\d+(px)?$/i.test(value) && /^\d/.test(value)) {
					ret += convertToPixel(elem, value);
				} else if (value.length > 2) {
					ret += parseInt(value.substr(0, value.length - 2));
				}
			}

			return ret;
		}-*/;

	protected abstract Element createInputElement();

	@Override
	public void setStyleName(String style) {
		super.setStyleName(style);
		wrapper.setClassName(getStylePrimaryName() + "-wrap");
		captionElement.setClassName(getStylePrimaryName() + "-caption");
	}

	@Override
	public void setStylePrimaryName(String style) {
		super.setStylePrimaryName(style);
		wrapper.setClassName(getStylePrimaryName() + "-wrap");
		captionElement.setClassName(getStylePrimaryName() + "-caption");
	}

	public void setText(String text) {
		captionElement.setInnerText(text);
	}

	public void setHtml(String html) {
		captionElement.setInnerHTML(html);
	}

	/*
	 * ALL BELOW COPY-PASTED FROM GWT CustomButton
	 */

	@SuppressWarnings("deprecation")
	@Override
	/*
	 * Copy-pasted from GWT CustomButton, some minor modifications done:
	 *
	 * -for IE/Opera added CLASSNAME_PRESSED
	 *
	 * -event.preventDefault() commented from ONMOUSEDOWN (Firefox won't apply
	 * :active styles if it is present)
	 *
	 * -Tooltip event handling added
	 *
	 * -onload event handler added (for icon handling)
	 */
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONLOAD) {
			Util.notifyParentOfSizeChange(this, true);
		}
		// Should not act on button if disabled.
		if (!isEnabled()) {
			// This can happen when events are bubbled up from non-disabled
			// children
			return;
		}

		int type = DOM.eventGetType(event);
		switch (type) {
			case Event.ONCLICK:
				// If clicks are currently disallowed, keep it from bubbling or
				// being passed to the superclass.
				if (disallowNextClick) {
					event.stopPropagation();
					disallowNextClick = false;
					return;
				}

				/*
				 * COPY-PASTE from gwt RadioButton
				 */
				EventTarget target = event.getEventTarget();
				if (Element.is(target)) {
					if (captionElement.isOrHasChild(Element.as(target))) {
						// They clicked the label. Note our pre-click value, and
						// short circuit event routing so that other click handlers
						// don't hear about it
						oldValue = getValue();
						return;
					} else if (outerInputElement.equals(Element.as(target)) || tableElement.equals(Element.as(target))) {
						// synthesize click event
						InputElement.as(inputElement).click();
						return;
					}
				}

				// It's not the label. Let our handlers hear about the
				// click...
				super.onBrowserEvent(event);
				// ...and now maybe tell them about the change
				ValueChangeEvent.fireIfNotEqual(SelectButton.this, oldValue,
						getValue());
				return;

			case Event.ONMOUSEDOWN:
				if (DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event))) {
					// This was moved from mouseover, which iOS sometimes skips.
					// We're certainly hovering at this point, and we don't actually
					// need that information before this point.
					setHovering(true);
				}
				if (event.getButton() == NativeEvent.BUTTON_LEFT) {
					// save mouse position to detect movement before synthesizing
					// event later
					mousedownX = event.getClientX();
					mousedownY = event.getClientY();

					disallowNextClick = true;
					clickPending = true;
					setFocus(true);
					DOM.setCapture(getElement());
					isCapturing = true;

					if (BrowserInfo.get().isIE8() || BrowserInfo.get().isIE9()) {
						/*
						 * We need to prevent the default behavior on these browsers
						 * since user-select is not available.
						 */
						event.preventDefault();
					}
				}
				break;
			case Event.ONMOUSEUP:
				if (isCapturing) {
					isCapturing = false;
					DOM.releaseCapture(getElement());
					if (isHovering() && event.getButton() == NativeEvent.BUTTON_LEFT) {
						// Click ok
						disallowNextClick = false;
					}

					// Explicitly prevent IE 8 from propagating mouseup events
					// upward (fixes #6753)
					if (BrowserInfo.get().isIE8()) {
						event.stopPropagation();
					}
				}
				break;
			case Event.ONMOUSEMOVE:
				clickPending = false;
				if (isCapturing) {
					// Prevent dragging (on other browsers);
					DOM.eventPreventDefault(event);
				}
				break;
			case Event.ONMOUSEOUT:
				Element to = event.getRelatedTarget();
				if (getElement().isOrHasChild(DOM.eventGetTarget(event)) && (to == null || !getElement().isOrHasChild(to))) {
					if (clickPending
							&& Math.abs(mousedownX - event.getClientX()) < MOVE_THRESHOLD
							&& Math.abs(mousedownY - event.getClientY()) < MOVE_THRESHOLD) {
						onClick();
						break;
					}
					clickPending = false;
					setHovering(false);
				}
				break;
			case Event.ONBLUR:
				if (isFocusing) {
					isFocusing = false;
				}
				break;
			case Event.ONLOSECAPTURE:
				if (isCapturing) {
					isCapturing = false;
				}
				break;
		}

		super.onBrowserEvent(event);

		// Synthesize clicks based on keyboard events AFTER the normal key
		// handling.
		if ((event.getTypeInt() & Event.KEYEVENTS) != 0) {
			switch (type) {
				case Event.ONKEYDOWN:
					// Stop propagation when the user starts pressing a button that
					// we are handling to prevent actions from getting triggered
					if (event.getKeyCode() == 32 /* space */) {
						isFocusing = true;
						event.preventDefault();
						event.stopPropagation();
					} else if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
						event.stopPropagation();
					}
					break;
				case Event.ONKEYUP:
					if (isFocusing && event.getKeyCode() == 32 /* space */) {
						isFocusing = false;
						onClick();
						event.stopPropagation();
						event.preventDefault();
					}
					break;
				case Event.ONKEYPRESS:
					if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
						onClick();
						event.stopPropagation();
						event.preventDefault();
					}
					break;
			}
		}
	}

	final boolean isHovering() {
		return isHovering;
	}

	final void setHovering(boolean hovering) {
		if (hovering != isHovering()) {
			isHovering = hovering;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
	 * .dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		if (BrowserInfo.get().isSafari()) {
			SelectButton.this.setFocus(true);
		}

		clickPending = false;
	}

	/**
	 * To add custom code for a click event, override
	 * {@link #onClick(ClickEvent)} instead of this.
	 */
	protected void onClick() {
		// Allow the click we're about to synthesize to pass through to the
		// superclass and containing elements. Element.dispatchEvent() is
		// synchronous, so we simply set and clear the flag within this method.

		disallowNextClick = false;

		// Mouse coordinates are not always available (e.g., when the click is
		// caused by a keyboard event).
		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
		getElement().dispatchEvent(evt);
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	/*
	 * SelectButton invention
	 */

	/**
	 * Sets whether this button is enabled.
	 *
	 * @param enabled <code>true</code> to enable the button, <code>false</code> to
	 *                disable it
	 */

	@Override
	public final void setEnabled(boolean enabled) {
		if (isEnabled() != enabled) {
			this.enabled = enabled;
			if (!enabled) {
				cleanupCaptureState();
				Roles.getButtonRole().setAriaDisabledState(getElement(), !enabled);
				super.setTabIndex(-1);
			} else {
				Roles.getButtonRole().removeAriaDisabledState(getElement());
				super.setTabIndex(tabIndex);
			}

		}
	}

	@Override
	public final void setTabIndex(int index) {
		super.setTabIndex(index);
		tabIndex = index;
	}

	/**
	 * Resets internal state if this button can no longer service events. This
	 * can occur when the widget becomes detached or disabled.
	 */
	private void cleanupCaptureState() {
		if (isCapturing || isFocusing) {
			DOM.releaseCapture(getElement());
			isCapturing = false;
			isFocusing = false;
		}
	}

	public void buildLabel() {
		// remove layout panel from element
		if (wrapper.getFirstChild() != null)
			wrapper.removeChild(wrapper.getFirstChild());

		ElementWidget inputWidget = new ElementWidget(inputElement);
		SimplePanel inputPanel = new SimplePanel(inputWidget);
		outerInputElement = inputPanel.getElement();

		inputPanel.setSize("100%", "100%");

		ElementWidget captionWidget = new ElementWidget(captionElement);
		CellPanel panel = null;
		if (labelVisible) {

			switch (labelPosition) {
				case Right:
				case Left: {
					panel = new HorizontalPanel();
					((HorizontalPanel) panel).setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				}
				break;

				case Top:
				case Bottom: {
					panel = new VerticalPanel();
					((VerticalPanel) panel).setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				}
				break;
			}

			switch (labelPosition) {
				case Right:
				case Bottom: {
					panel.add(inputPanel);
					panel.add(captionWidget);
				}
				break;

				case Left:
				case Top: {
					panel.add(captionWidget);
					panel.add(inputPanel);
				}
			}
		} else {
			panel = new HorizontalPanel();
			((HorizontalPanel) panel).setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			panel.add(inputWidget);
		}

		if (panel != null) {
			panel.setSpacing(2);
			tableElement = panel.getElement();
			wrapper.appendChild(panel.getElement());
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Boolean getValue() {
		if (isAttached()) {
			return InputElement.as(inputElement).isChecked();
		} else {
			return InputElement.as(inputElement).isDefaultChecked();
		}
	}

	@Override
	public void setValue(Boolean value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		if (value == null) {
			value = Boolean.FALSE;
		}

		Boolean oldValue = getValue();
		InputElement.as(inputElement).setChecked(value);
		InputElement.as(inputElement).setDefaultChecked(value);
		if (value.equals(oldValue)) {
			return;
		}
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	/*@Override
	public void sinkEvents(int eventBitsToAdd) {
		// Like CheckBox, we want to hear about inputElem. We
		// also want to know what's going on with the label, to
		// make sure onBrowserEvent is able to record value changes
		// initiated by label events
		if (isOrWasAttached()) {
			Event.sinkEvents(inputElement,
					eventBitsToAdd | Event.getEventsSunk(inputElement));
			Event.sinkEvents(captionElement,
					eventBitsToAdd | Event.getEventsSunk(captionElement));
		} else {
			super.sinkEvents(eventBitsToAdd);
		}
	}*/

	public void updateIdRelatives() {
		String id = getElement().getId();
		if (id != null) {
			String inputId = id + "_input";
			inputElement.setId(inputId);
			captionElement.setAttribute("for", inputId);
			captionElement.setAttribute("htmlFor", inputId);
		}
	}

}
