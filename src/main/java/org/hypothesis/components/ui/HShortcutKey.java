package org.hypothesis.components.ui;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import org.hypothesis.components.data.ShortcutConstants;
import org.hypothesis.ui.NonVisualComponent;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author kamil
 */
public class HShortcutKey extends AbstractComponent implements Action.ShortcutNotifier, NonVisualComponent {

    protected Shortcut shortcut;
    private int keyCode;
    private int[] modifiers;

    public HShortcutKey(int keyCode, int... modifiers) {
        super();
        setShortcut(keyCode, modifiers);
    }

    public HShortcutKey(int keyCode) {
        this(keyCode, (int[]) null);
    }

    /**
     * Adds the key press listener.
     *
     * @param listener the Listener to be added.
     */
    public void addKeyPressListener(KeyPressListener listener) {
        addListener(KeyPressEvent.class, listener, KeyPressListener.KEY_PRESS_METHOD);
    }

    /**
     * Removes the key press listener.
     *
     * @param listener the Listener to be removed.
     */
    public void removeKeyPressListener(KeyPressListener listener) {
        removeListener(KeyPressEvent.class, listener, KeyPressListener.KEY_PRESS_METHOD);
    }

    /**
     * Fires a key press event to all listeners.
     */
    protected void fireKeyPress(int keyCode, int[] modifierKeys) {
        fireEvent(new HShortcutKey.KeyPressEvent(this, keyCode, modifierKeys));
    }

    /*
     * Simulates a key press, notifying all server-side listeners.
     *
     * No action is taken if the shortcut key is disabled.
     */
    /*public void keyPress() {
        if (isEnabled()) {
            fireKeyPress();
        }
    }*/

    /*
     * Fires a key press event to all listeners without any event details.
     *
     * In subclasses, override {@link #fireClick(int, int[])} instead of
     * this method.
     */
    /*protected void fireKeyPress() {
        fireEvent(new ShortcutKey.KeyPressEvent(this));
    }*/

    /**
     * Makes it possible to invoke an action by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * The shortcut is global (bound to the containing Window).
     *
     * @param keyCode   the keycode for invoking the shortcut
     * @param modifiers the (optional) modifiers for invoking the shortcut, null for
     *                  none
     */
    protected void setShortcut(int keyCode, int... modifiers) {
        if (shortcut != null) {
            removeShortcutListener(shortcut);
        }
        this.keyCode = keyCode;
        this.modifiers = modifiers;

        shortcut = new Shortcut(this, keyCode, modifiers);

        addShortcutListener(shortcut);
    }

    /*
     * Actions
     */

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setShortcut(int, int...)}.
     */
    protected void removeShortcut() {
        if (shortcut != null) {
            removeShortcutListener(shortcut);
            shortcut = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (modifiers != null) {
            for (int modifier : modifiers) {
                switch (modifier) {
                    case ShortcutAction.ModifierKey.SHIFT:
                        builder.append("Shift+");
                        break;
                    case ShortcutAction.ModifierKey.CTRL:
                        builder.append("Ctrl+");
                        break;
                    case ShortcutAction.ModifierKey.ALT:
                        builder.append("Alt+");
                        break;
                    case ShortcutAction.ModifierKey.META:
                        builder.append("Meta+");
                        break;
                    default:
                        break;
                }
            }
        }

        Set<Entry<String, Integer>> entries = ShortcutConstants.SHORTCUT_MAP.entrySet();
        for (Entry<String, Integer> entry : entries) {
            if (keyCode == entry.getValue()) {
                builder.append(entry.getKey());
            }
        }

        return builder.toString();
    }

    /**
     * Interface for listening for a {@link KeyPressEvent} fired by a
     * {@link Component}.
     */
    public interface KeyPressListener extends Serializable {

        Method KEY_PRESS_METHOD = ReflectTools.findMethod(
                KeyPressListener.class, "keyPress", KeyPressEvent.class);

        /**
         * Called when a {@link HShortcutKey} has been clicked. A reference to the
         * shortcut key is given by {@link KeyPressEvent#getShortcutKey()}.
         *
         * @param event An event containing information about the key.
         */
        void keyPress(KeyPressEvent event);

    }

    /**
     * Press event. This event is thrown, when the key is pressed.
     */
    public static class KeyPressEvent extends Component.Event {

        private int keyCode;
        private int[] modifierKeys;

        /**
         * New instance of text change event.
         *
         * @param source the Source of the event.
         */
        public KeyPressEvent(Component source) {
            super(source);
        }

        /**
         * Constructor with key code
         *
         * @param source  The source where the shortcut key took place
         * @param keyCode Details about the key
         */
        public KeyPressEvent(Component source, int keyCode) {
            super(source);
            this.keyCode = keyCode;
        }

        /**
         * Constructor with key code and modifier keys
         *
         * @param source    The source where the shortcut key took place
         * @param keyCode   Details about the key
         * @param modifiers Details about modifier keys
         */
        public KeyPressEvent(Component source, int keyCode, int... modifiers) {
            this(source, keyCode);
            this.modifierKeys = modifiers;
        }

        /**
         * Gets the Button where the event occurred.
         *
         * @return the Source of the event.
         */
        public HShortcutKey getShortcutKey() {
            return (HShortcutKey) getSource();
        }

        private boolean modifiersContain(int modifierKey) {
            for (int modifier : modifierKeys) {
                if (modifier == modifierKey) {
                    return true;
                }
            }
            return false;
        }

        public int getKeyCode() {
            return keyCode;
        }

        /**
         * Checks if the Alt key was down when the mouse event took place.
         *
         * @return true if Alt was down when the event occurred, false otherwise
         * or if unknown
         */
        public boolean isAltKey() {
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.ALT);
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
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.CTRL);
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
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.META);
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
            if (null != modifierKeys) {
                return modifiersContain(ShortcutAction.ModifierKey.SHIFT);
            } else {
                return false;
            }
        }
    }

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that invokes a click on the given button.
     */
    public static class Shortcut extends ShortcutListener {
        protected final HShortcutKey shortcutKey;

        /*
         * Creates a keyboard shortcut for clicking the given button using the
         * shorthand notation defined in {@link ShortcutAction}.
         *
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param shorthandCaption
         *            the caption with shortcut keycode and modifiers indicated
         */
        /*public Shortcut(ShortcutKey shortcutKey, String shorthandCaption) {
            super(shorthandCaption);
            this.shortcutKey = shortcutKey;
        }*/

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         *
         * @param shortcutKey to be clicked when the shortcut is invoked
         * @param keyCode     KeyCode to react to
         * @param modifiers   optional modifiers for shortcut
         */
        public Shortcut(HShortcutKey shortcutKey, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.shortcutKey = shortcutKey;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode}.
         *
         * @param shortcutKey to be clicked when the shortcut is invoked
         * @param keyCode     KeyCode to react to
         */
        public Shortcut(HShortcutKey shortcutKey, int keyCode) {
            this(shortcutKey, keyCode, (int[]) null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            shortcutKey.fireKeyPress(getKeyCode(), getModifiers());
        }
    }
}
