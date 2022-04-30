package org.hypothesis.components.client.ui;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.shared.Connector;
import org.hypothesis.components.client.Timer.*;
import org.hypothesis.components.client.ui.timer.HTimerConnector;

import java.util.Date;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public class VHTimerLabel extends Label implements StartEventHandler, StopEventHandler, UpdateEventHandler {

    public static final String CLASSNAME = "v-timerlabel";

    private String timeFormat;
    private DateTimeFormat dateTimeFormat;
    private long time = 0L;
    private VHTimer timer = null;
    private int updateInterval = 100; // ms

    public VHTimerLabel() {
        super();
        setStyleName(CLASSNAME);
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
        dateTimeFormat = DateTimeFormat.getFormat(timeFormat);

        setTime(time);
    }

    public void setTime(long time) {
        if (this.time != time)
            this.time = time;

        // NOTE don't know why need to subtract one hour to display right time?
        setText(dateTimeFormat.format(new Date(this.time - 3600000)));
    }

    public void setUpdateInterval(int updateInterval) {
        if (this.updateInterval != updateInterval) {
            this.updateInterval = updateInterval;

            if (timer != null) {
                timer.removeUpdateEventHandler(this);
                timer.addUpdateEventHandler(this.updateInterval, this);
            }
        }
    }

    public void registerTimer(Connector connector) {
        if (connector != null) {
            if (connector instanceof HTimerConnector) {
                VHTimer newTimer = ((HTimerConnector) connector).getWidget();
                if (timer != newTimer) {
                    // unregister event handlers from current timer
                    removeTimerHandlers();

                    // set new timer and register event handlers
                    timer = newTimer;
                    addTimerHandlers();
                }
            }
        } else {
            // unregister event handlers from current timer
            removeTimerHandlers();

            timer = null;
        }
    }

    private void removeTimerHandlers() {
        if (timer != null) {
            timer.removeStartEventHandler(this);
            timer.removeUpdateEventHandler(this);
            timer.removeStopEventHandler(this);

            // clear time
            setTime(0L);
        }
    }

    private void addTimerHandlers() {
        if (timer != null) {
            timer.addStartEventHandler(this);
            timer.addUpdateEventHandler(updateInterval, this);
            timer.addStopEventHandler(this);
        }
    }

    @Override
    public void start(StartEvent event) {
        setTime(event.getTime());
    }

    @Override
    public void update(UpdateEvent event) {
        setTime(event.getTime());
    }

    @Override
    public void stop(StopEvent event) {
        setTime(event.getTime());
    }
}
