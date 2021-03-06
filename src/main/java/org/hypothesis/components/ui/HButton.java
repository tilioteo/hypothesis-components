package org.hypothesis.components.ui;

import com.tilioteo.common.event.TimekeepingComponentEvent;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.button.ButtonState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.ReflectTools;
import org.hypothesis.components.shared.ui.button.HButtonServerRpc;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

/**
 * @author kamil Redesigned basic Vaadin Button component
 */
public class HButton extends AbstractComponent implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier, Focusable,
        Action.ShortcutNotifier {

    protected final FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {

        @Override
        protected void fireEvent(Event event) {
            HButton.this.fireEvent(event);
        }
    };
    private final HButtonServerRpc rpc = new HButtonServerRpc() {

        @Override
        public void click(long timestamp, MouseEventDetails mouseEventDetails) {
            fireClick(timestamp, mouseEventDetails);
        }

        @Override
        public void disableOnClick() throws RuntimeException {
            setEnabled(false);
            // Makes sure the enabled=false state is noticed at once - otherwise
            // a following setEnabled(true) call might have no effect. see
            // ticket #10030
            getUI().getConnectorTracker().getDiffState(HButton.this)
                    .put("enabled", false);
        }
    };
    protected ClickShortcut clickShortcut;

    /**
     * Creates a new push button.
     */
    public HButton() {
        registerRpc(rpc);
        registerRpc(focusBlurRpc);
    }

    /**
     * Creates a new push button with the given caption.
     *
     * @param caption the Button caption.
     */
    public HButton(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new push button with the given icon.
     *
     * @param icon the icon
     */
    public HButton(Resource icon) {
        this();
        setIcon(icon);
    }

    /**
     * Creates a new push button with the given caption and icon.
     *
     * @param caption the caption
     * @param icon    the icon
     */
    public HButton(String caption, Resource icon) {
        this();
        setCaption(caption);
        setIcon(icon);
    }

    /**
     * Creates a new push button with a click listener.
     *
     * @param caption  the Button caption.
     * @param listener the Button click listener.
     */
    public HButton(String caption, ClickListener listener) {
        this(caption);
        addClickListener(listener);
    }

    /**
     * Adds the button click listener.
     *
     * @param listener the Listener to be added.
     */
    public void addClickListener(ClickListener listener) {
        addListener(ClickEvent.class, listener,
                ClickListener.BUTTON_CLICK_METHOD);
    }

    /**
     * Removes the button click listener.
     *
     * @param listener the Listener to be removed.
     */
    public void removeClickListener(ClickListener listener) {
        removeListener(ClickEvent.class, listener,
                ClickListener.BUTTON_CLICK_METHOD);
    }

    /**
     * Simulates a button click, notifying all server-side listeners.
     * <p>
     * No action is taken if the button is disabled.
     */
    public void click() {
        if (isEnabled() && !isReadOnly()) {
            fireClick();
        }
    }

    /**
     * Fires a click event to all listeners without any event details.
     * <p>
     * In subclasses, override {@link #fireClick(long, MouseEventDetails)} instead of
     * this method.
     */
    protected void fireClick() {
        Date now = new Date();
        fireEvent(new HButton.ClickEvent(now.getTime(), this));
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
        fireEvent(new HButton.ClickEvent(timestamp, this, details));
    }

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    public void addListener(BlurListener blurListener) {
        addBlurListener(blurListener);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    @Override
    public void removeListener(BlurListener blurListener) {
        removeBlurListener(blurListener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public void addListener(FocusListener focusListener) {
        addFocusListener(focusListener);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    @Override
    public void removeListener(FocusListener focusListener) {
        removeFocusListener(focusListener);
    }

    /**
     * Makes it possible to invoke a click on this button by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
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

    /**
     * Determines if a button is automatically disabled when clicked. See
     * {@link #setDisableOnClick(boolean)} for details.
     *
     * @return true if the button is disabled when clicked, false otherwise
     */
    public boolean isDisableOnClick() {
        return getState(false).disableOnClick;
    }

    /**
     * Determines if a button is automatically disabled when clicked. If this is
     * set to true the button will be automatically disabled when clicked,
     * typically to prevent (accidental) extra clicks on a button.
     * <p>
     * Note that this is only used when the click comes from the user, not when
     * calling {@link #click()}.
     * </p>
     *
     * @param disableOnClick true to disable button when it is clicked, false otherwise
     */
    public void setDisableOnClick(boolean disableOnClick) {
        getState().disableOnClick = disableOnClick;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component.Focusable#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

    @Override
    public void focus() {
        // Overridden only to make public
        super.focus();
    }

    @Override
    protected ButtonState getState() {
        return (ButtonState) super.getState();
    }

    @Override
    protected ButtonState getState(boolean markAsDirty) {
        return (ButtonState) super.getState(markAsDirty);
    }

    /**
     * Sets the component's icon and alt text.
     * <p>
     * An alt text is shown when an image could not be loaded, and read by
     * assistive devices.
     *
     * @param icon        the icon to be shown with the component's caption.
     * @param iconAltText String to use as alt text
     */
    public void setIcon(Resource icon, String iconAltText) {
        super.setIcon(icon);
        getState().iconAltText = iconAltText == null ? "" : iconAltText;
    }

    /**
     * Returns the icon's alt text.
     *
     * @return String with the alt text
     */
    public String getIconAlternateText() {
        return getState(false).iconAltText;
    }

    public void setIconAlternateText(String iconAltText) {
        getState().iconAltText = iconAltText;
    }

    /**
     * Return HTML rendering setting
     *
     * @return <code>true</code> if the caption text is to be rendered as HTML,
     * <code>false</code> otherwise
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).captionAsHtml;
    }

    /**
     * Set whether the caption text is rendered as HTML or not. You might need
     * to re-theme button to allow higher content than the original text style.
     * <p>
     * If set to true, the captions are passed to the browser as html and the
     * developer is responsible for ensuring no harmful html is used. If set to
     * false, the content is passed to the browser as plain text.
     *
     * @param htmlContentAllowed <code>true</code> if caption is rendered as HTML,
     *                           <code>false</code> otherwise
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().captionAsHtml = htmlContentAllowed;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#readDesign(org.jsoup.nodes .Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        String content = design.html();
        setCaption(content);
        // plain-text (default is html)
        Boolean plain = DesignAttributeHandler.readAttribute(
                DESIGN_ATTR_PLAIN_TEXT, attr, Boolean.class);
        if (plain == null || !plain) {
            setHtmlContentAllowed(true);
        }
        if (attr.hasKey("icon-alt")) {
            setIconAlternateText(DesignAttributeHandler.readAttribute(
                    "icon-alt", attr, String.class));
        }
        // click-shortcut
        removeClickShortcut();
        ShortcutAction action = DesignAttributeHandler.readAttribute(
                "click-shortcut", attr, ShortcutAction.class);
        if (action != null) {
            setClickShortcut(action.getKeyCode(), action.getModifiers());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#getCustomAttributes()
     */
    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add(DESIGN_ATTR_PLAIN_TEXT);
        result.add("caption");
        result.add("icon-alt");
        result.add("icon-alternate-text");
        result.add("click-shortcut");
        result.add("html-content-allowed");
        result.add("caption-as-html");
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#writeDesign(org.jsoup.nodes.Element
     * , com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        Attributes attr = design.attributes();
        HButton def = designContext.getDefaultInstance(this);
        String content = getCaption();
        if (content != null) {
            design.html(content);
        }
        // plain-text (default is html)
        if (!isHtmlContentAllowed()) {
            design.attr(DESIGN_ATTR_PLAIN_TEXT, "");
        }
        // icon-alt
        DesignAttributeHandler.writeAttribute("icon-alt", attr,
                getIconAlternateText(), def.getIconAlternateText(),
                String.class);
        // click-shortcut
        if (clickShortcut != null) {
            DesignAttributeHandler.writeAttribute("click-shortcut", attr,
                    clickShortcut, null, ShortcutAction.class);
        }
    }

    /**
     * Interface for listening for a {@link ClickEvent} fired by a
     * {@link Component}.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public interface ClickListener extends Serializable {

        Method BUTTON_CLICK_METHOD = ReflectTools
                .findMethod(ClickListener.class, "buttonClick",
                        ClickEvent.class);

        /**
         * Called when a {@link HButton} has been clicked. A reference to the
         * button is given by {@link ClickEvent#getButton()}.
         *
         * @param event An event containing information about the click.
         */
        void buttonClick(ClickEvent event);

    }

    /**
     * Click event. This event is thrown, when the button is clicked.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class ClickEvent extends TimekeepingComponentEvent {

        private final MouseEventDetails details;

        /**
         * New instance of text change event.
         *
         * @param source the Source of the event.
         */
        public ClickEvent(long timestamp, Component source) {
            this(timestamp, source, null);
        }

        /**
         * Constructor with mouse details
         *
         * @param source  The source where the click took place
         * @param details Details about the mouse click
         */
        public ClickEvent(long timestamp, Component source,
                          MouseEventDetails details) {
            super(timestamp, source);
            this.details = details;
        }

        /**
         * Gets the Button where the event occurred.
         *
         * @return the Source of the event.
         */
        public HButton getButton() {
            return (HButton) getSource();
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

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that invokes a click on the given button.
     */
    public static class ClickShortcut extends ShortcutListener {
        protected final HButton button;

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * shorthand notation defined in {@link ShortcutAction}.
         *
         * @param button           to be clicked when the shortcut is invoked
         * @param shorthandCaption the caption with shortcut keycode and modifiers indicated
         */
        public ClickShortcut(HButton button, String shorthandCaption) {
            super(shorthandCaption);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         *
         * @param button    to be clicked when the shortcut is invoked
         * @param keyCode   KeyCode to react to
         * @param modifiers optional modifiers for shortcut
         */
        public ClickShortcut(HButton button, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode}.
         *
         * @param button  to be clicked when the shortcut is invoked
         * @param keyCode KeyCode to react to
         */
        public ClickShortcut(HButton button, int keyCode) {
            this(button, keyCode, (int[]) null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            button.click();
        }
    }
}
