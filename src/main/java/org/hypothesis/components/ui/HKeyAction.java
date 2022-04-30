package org.hypothesis.components.ui;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import org.hypothesis.components.shared.ui.keyaction.HKeyActionServerRpc;
import org.hypothesis.components.shared.ui.keyaction.HKeyActionState;
import org.hypothesis.ui.event.TimekeepingComponentEvent;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Set;

import static org.hypothesis.components.data.ShortcutConstants.SHORTCUT_MAP;

/**
 * Copied from https://github.com/Artur-/KeyActions
 * <p>
 * modified by:
 *
 * @author kamil
 */
public class HKeyAction extends AbstractExtension {

    public HKeyAction(int keyCode, int... modifiers) {
        super();
        registerRpc((HKeyActionServerRpc) timestamp -> fireEvent(new KeyActionEvent(timestamp, (Component) getParent())));
        getState().keyCode = keyCode;
        for (int modifier : modifiers) {
            if (modifier == ModifierKey.ALT) {
                getState().alt = true;
            } else if (modifier == ModifierKey.CTRL) {
                getState().ctrl = true;
            } else if (modifier == ModifierKey.META) {
                getState().meta = true;
            } else if (modifier == ModifierKey.SHIFT) {
                getState().shift = true;
            }
        }
    }

    public boolean isStopPropagation() {
        return getState(false).stopPropagation;
    }

    public void setStopPropagation(boolean stopPropagation) {
        getState().stopPropagation = stopPropagation;
    }

    public boolean isPreventDefault() {
        return getState(false).preventDefault;
    }

    public void setPreventDefault(boolean preventDefault) {
        getState().preventDefault = preventDefault;
    }

    @Override
    protected HKeyActionState getState() {
        return (HKeyActionState) super.getState();
    }

    @Override
    protected HKeyActionState getState(boolean markAsDirty) {
        return (HKeyActionState) super.getState(markAsDirty);
    }

    public void extend(AbstractComponent target) {
        super.extend(target);
    }

    public void removeKeyActionListener(KeyActionListener keyActionListener) {
        removeListener(KeyActionEvent.class, keyActionListener);
    }

    public void addKeypressListener(KeyActionListener keyActionListener) {
        addListener(KeyActionEvent.class, keyActionListener, KeyActionListener.method);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (getState().shift) {
            builder.append("Shift+");
        }
        if (getState().ctrl) {
            builder.append("Ctrl+");
        }
        if (getState().alt) {
            builder.append("Alt+");
        }
        if (getState().meta) {
            builder.append("Meta+");
        }

        int keyCode = getState().keyCode;

        Set<Entry<String, Integer>> entries = SHORTCUT_MAP.entrySet();
        for (Entry<String, Integer> entry : entries) {
            if (keyCode == entry.getValue()) {
                builder.append(entry.getKey());
            }
        }

        return builder.toString();
    }

    public interface KeyActionListener extends ConnectorEventListener {

        Method method = ReflectTools.findMethod(KeyActionListener.class, "keyPressed",
                KeyActionEvent.class);

        void keyPressed(KeyActionEvent keyPressEvent);
    }

    public static class KeyActionEvent extends TimekeepingComponentEvent {

        public KeyActionEvent(long timestamp, Component source) {
            super(timestamp, source);
        }

    }
}
