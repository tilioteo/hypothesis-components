package org.hypothesis.components.client.ui.keyaction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.DateUtility;
import org.hypothesis.components.shared.ui.keyaction.HKeyActionServerRpc;
import org.hypothesis.components.shared.ui.keyaction.HKeyActionState;
import org.hypothesis.components.ui.HKeyAction;

@Connect(HKeyAction.class)
public class HKeyActionConnector extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        AbstractComponentConnector connector = (AbstractComponentConnector) target;

        connector.getWidget().addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (HKeyActionConnector.this.match(event)) {
                    HKeyActionConnector.this.triggerEvent(event);
                }
            }
        }, KeyDownEvent.getType());

    }

    protected void triggerEvent(final KeyDownEvent event) {
        if (getState().stopPropagation) {
            event.stopPropagation();
        }
        if (getState().preventDefault) {
            event.preventDefault();
        }

        // Deferred so keypress + keyup can be handled before this happens so
        // pressing enter in a text area does not cause the enter to be lost
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (HKeyActionConnector.this.getParent() instanceof BeforeShortcutActionListener) {
                    ((BeforeShortcutActionListener) HKeyActionConnector.this.getParent())
                            .onBeforeShortcutAction(Event.as(event.getNativeEvent()));
                }

                HKeyActionConnector.this.getRpcProxy(HKeyActionServerRpc.class).trigger(DateUtility.getTimestamp());
            }
        });
    }

    protected boolean match(KeyDownEvent event) {
        if (event.getNativeKeyCode() != getState().keyCode) {
            return false;
        }

        if (getState().shift != event.isShiftKeyDown()) {
            return false;
        }
        if (getState().alt != event.isAltKeyDown()) {
            return false;
        }
        if (getState().ctrl != event.isControlKeyDown()) {
            return false;
        }
        return getState().meta == event.isMetaKeyDown();
    }

    @Override
    public HKeyActionState getState() {
        return (HKeyActionState) super.getState();
    }

}
