package org.hypothesis.components.ui;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.ReflectTools;
import org.hypothesis.ui.event.TimekeepingComponentEvent;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Random;

public class HClientSim extends HPanel {

    public static final String CLIENT_EVENT = "client";
    private final HTimer timer;
    private final Button button;
    private State state;

    public HClientSim() {
        super();

        setWidth(100, Unit.PIXELS);
        setHeight(100, Unit.PIXELS);

        timer = new HTimer();
        timer.addStopListener(e -> {
            Date now = new Date();
            fireEvent(new ClientEvent(now.getTime(), HClientSim.this));
            if (state == State.STOPPING) {
                setStoppedState();
            } else if (state == State.RUNNING) {
                timer.start(generateTime());
            }
        });

        button = new Button();
        button.addClickListener(e -> {
            switch (state) {
                case STOPPED:
                    start();
                    break;
                case RUNNING:
                    stop();
                    break;
                default:
                    break;
            }
        });
        VerticalLayout layout = new VerticalLayout(button);
        layout.setSizeFull();
        layout.addComponent(button);
        setContent(layout);

        setStoppedState();
    }

    private void setStoppedState() {
        state = State.STOPPED;

        button.setCaption("Start");
        button.setEnabled(true);
    }

    private void setStoppingState() {
        state = State.STOPPING;

        button.setCaption("Stop");
        button.setEnabled(false);
    }

    private void setRunningState() {
        state = State.RUNNING;

        button.setCaption("Stop");
        button.setEnabled(true);
    }

    public void start() {
        if (state == State.STOPPED) {
            setRunningState();
            timer.start(generateTime());
        }
    }

    public void stop() {
        if (state == State.RUNNING) {
            setStoppingState();
            timer.stop();
        }
    }

    private long generateTime() {
        Random random = new Random();
        return 10 * random.nextInt(500);
    }

    @Override
    public void attach() {
        super.attach();

        UI ui = getUI();
        if (ui instanceof WithTimers) {
            ((WithTimers) ui).addTimer(timer);
        }
    }

    @Override
    public void detach() {
        UI ui = getUI();
        if (ui instanceof WithTimers) {
            ((WithTimers) ui).removeTimer(timer);
        }

        super.detach();
    }

    public void addClientListener(ClientListener listener) {
        addListener(CLIENT_EVENT, ClientEvent.class, listener, ClientListener.clientMethod);
    }

    /*public void removeLoadListener(LoadListener listener) {
        removeListener(CLIENT_EVENT, ClientEvent.class, listener);
    }*/

    private enum State {
        STOPPED, RUNNING, STOPPING
    }

    /**
     * Interface for listening for a {@link ClientEvent} fired by a
     * {@link HClientSim}.
     *
     * @author kamil
     * @see ClientEvent
     */
    public interface ClientListener extends ConnectorEventListener {

        Method clientMethod = ReflectTools.findMethod(ClientListener.class, CLIENT_EVENT,
                ClientEvent.class);

        void client(ClientEvent event);
    }

    public static class ClientEvent extends TimekeepingComponentEvent {

        public ClientEvent(long timestamp, Component source) {
            super(timestamp, source);
        }

    }

}
