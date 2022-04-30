package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import org.hypothesis.components.client.Timer;

import java.util.Set;

public class VHTimer extends Widget {

    public static final String CLASSNAME = "v-timer";

    private final Timer timer;

    public VHTimer() {
        setElement(Document.get().createDivElement());
        setStyleName(CLASSNAME);
        setVisible(false);
        timer = new Timer();
    }

    public void start(final long time) {
        timer.start(time);
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean silent) {
        timer.stop(silent);
    }

    public void pause() {
        timer.pause();
    }

    public void resume() {
        timer.resume();
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public void setDirection(String direction) {
        setDirection(Timer.Direction.valueOf(direction));
    }

    public void setDirection(Timer.Direction direction) {
        try {
            timer.setDirection(direction);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void addStartEventHandler(Timer.StartEventHandler handler) {
        timer.addStartEventHandler(handler);
    }

    public void removeStartEventHandler(Timer.StartEventHandler handler) {
        timer.removeStartEventHandler(handler);
    }

    public void addStopEventHandler(Timer.StopEventHandler handler) {
        timer.addStopEventHandler(handler);
    }

    public void removeStopEventHandler(Timer.StopEventHandler handler) {
        timer.removeStopEventHandler(handler);
    }

    public void addUpdateEventHandler(long interval, Timer.UpdateEventHandler handler) {
        timer.addUpdateEventHandler(interval, handler);
    }

    public void removeUpdateEventHandler(long interval, Timer.UpdateEventHandler handler) {
        timer.removeUpdateEventHandler(interval, handler);
    }

    public void removeUpdateEventHandler(Timer.UpdateEventHandler handler) {
        timer.removeUpdateEventHandler(handler);
    }

    public boolean hasUpdateEventHandler(long interval, Timer.UpdateEventHandler handler) {
        return timer.hasUpdateEventHandler(interval, handler);
    }

    public Set<Long> getHandledIntervals() {
        return timer.getHandledIntervals();
    }

    @Override
    protected void onDetach() {
        if (timer != null) {
            timer.stop(true);
        }

        super.onDetach();
    }
}