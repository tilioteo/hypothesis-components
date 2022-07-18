package org.hypothesis.components.ui;

import com.tilioteo.common.event.TimekeepingComponentEvent;
import com.vaadin.event.EventRouter;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.util.ReflectTools;
import org.hypothesis.components.shared.ui.timer.HTimerClientRpc;
import org.hypothesis.components.shared.ui.timer.HTimerServerRpc;
import org.hypothesis.components.shared.ui.timer.HTimerState;
import org.hypothesis.components.shared.ui.timer.HTimerState.Direction;
import org.hypothesis.ui.NonVisualComponent;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author kamil
 */
public class HTimer extends AbstractComponent implements NonVisualComponent {

    private final HTimerClientRpc clientRpc;
    private final HashMap<Long, EventRouter> eventRouterMap = new HashMap<>();
    private Date stopTime;
    private long startCounter = 0;
    private long counter = 0;
    private boolean running = false;
    private long elapsed = 0;
    private Date startTime;
    private final HTimerServerRpc rpc = new HTimerServerRpc() {

        @Override
        public void started(long timestamp, long time, String direction, boolean resumed) {
            fireEvent(new StartEvent(timestamp, startTime, HTimer.this, time, Direction.valueOf(direction), resumed));
        }

        @Override
        public void stopped(long timestamp, long time, String direction, boolean paused) {
            fireEvent(new StopEvent(timestamp, stopTime, HTimer.this, time, Direction.valueOf(direction), paused));
        }

        @Override
        public void update(long time, String direction, long interval) {
			/*EventRouter eventRouter = eventRouterMap.get(interval);
			if (eventRouter != null) {
				log.debug("TimerServerRpc: update() interval:" + interval);
				eventRouter.fireEvent(new UpdateEvent(Timer.this, time, Direction.valueOf(direction), interval));
			}*/
        }

    };
    private boolean infinite = false;
    private long time = 0;
    private transient Timer internalTimer;

    public HTimer() {
        registerRpc(rpc);
        clientRpc = getRpcProxy(HTimerClientRpc.class);
    }

    @Override
    public HTimerState getState() {
        return (HTimerState) super.getState();
    }

    public void start(long time) {
        setTime(time);

        if (time < 0) {
            setInfinite(true);
        }

        elapsed = 0;

        if (time >= HTimerState.TIMER_TICK) {
            startCounter = time;

            if (Direction.UP.equals(getState().direction)) {
                counter = 0;
            } else {
                counter = startCounter;
            }

            resume();

        } else {
            startCounter = 0;
        }
    }

    public void start() {
        start(getTime());
    }

    protected void resume() {
        if (!running) {
            running = true;
            startTime = new Date();
            clientRpc.start(time);
            internalTimer = new Timer();
            internalTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    setElapsed();
                }
            }, HTimerState.TIMER_TICK, HTimerState.TIMER_TICK);

        }
    }

    protected long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time < 0 ? -1 : time;
    }

    protected void setElapsed() {
        elapsed = new Date().getTime() - startTime.getTime();
        updateCounter(elapsed);

        // stop timer when the time passed
        if (!infinite && elapsed >= startCounter) {
            running = false;
            internalTimer.cancel();

            final UI ui = getUI();
            if (ui != null) {
                ui.access/*Synchronously*/(() -> {
                    try {
                        stopTime = new Date();
                        clientRpc.stop(false);
                        if (PushMode.MANUAL.equals(ui.getPushConfiguration().getPushMode())) {
                            try {
                                ui.push();
                            } catch (Throwable e) {
                            }
                        }
                    } catch (Throwable e) {
                    }
                });
            }

        } else {
            signalTimeSlices(elapsed);
        }
    }

    private void updateCounter(long elapsed) {
        if (Direction.UP.equals(getState().direction)) {
            counter = elapsed;
        } else {
            counter = startCounter - elapsed;
            if (counter < 0) {
                counter = 0;
            }
        }
    }

    private void signalTimeSlices(long elapsed) {
        for (final Long timeSlice : eventRouterMap.keySet()) {
            // modulo for time slices passed in elapsed time
            long rest = elapsed % timeSlice;
            // if rest passes into first timer tick interval then fire update event
            // for this time slice
            if (rest >= 0 && rest < HTimerState.TIMER_TICK) {
                final UI ui = getUI();
                if (ui != null) {
                    ui.access/*Synchronously*/(() -> {
                        try {
                            eventRouterMap.get(timeSlice).fireEvent(new UpdateEvent(0, new Date(), HTimer.this, counter, getState().direction, timeSlice));
                            if (PushMode.MANUAL.equals(ui.getPushConfiguration().getPushMode())) {
                                try {
                                    ui.push();
                                } catch (Throwable e) {
                                }
                            }
                        } catch (Throwable e) {
                        }
                    });
                }
            }
        }
    }

    public boolean isInfinite() {
        return infinite;
    }

    protected void setInfinite(boolean infinite) {
        this.infinite = infinite;
        if (infinite) {
            getState().direction = Direction.UP;
        }
    }

    public void stop() {
        stop(false);
    }

    private void stop(boolean pause, boolean silent) {
        if (running) {
            running = false;
            internalTimer.cancel();
            setElapsed();

            if (pause) {
                clientRpc.pause();
            } else {
                clientRpc.stop(false);
            }

            if (!silent) {
                final UI ui = getUI();
                if (ui != null) {
                    ui.access/*Synchronously*/(() -> {
                        try {
                            stopTime = new Date();
                            clientRpc.stop(silent);
                            if (PushMode.MANUAL.equals(ui.getPushConfiguration().getPushMode())) {
                                try {
                                    ui.push();
                                } catch (Throwable e) {
                                }
                            }
                        } catch (Throwable e) {
                        }
                    });
                }
            }
            if (!pause) {
                startCounter = 0;
            }
        }
    }

    public void stop(boolean silent) {
        stop(false, silent);
    }

    public boolean isRunning() {
        return running;
    }

    public Direction getDirection() {
        return getState().direction;
    }

    public void setDirection(Direction direction) {
        getState().direction = direction;
    }

    public void addStartListener(StartListener listener) {
        addListener(StartEvent.EVENT_ID, StartEvent.class, listener,
                StartListener.TIMER_START_METHOD);
    }

    public void removeStartListener(StartListener listener) {
        removeListener(StartEvent.EVENT_ID, StartedEvent.class, listener);
    }

    public void addStopListener(StopListener listener) {
        addListener(StopEvent.EVENT_ID, StopEvent.class, listener,
                StopListener.TIMER_STOP_METHOD);
    }

    public void removeStopListener(StopListener listener) {
        removeListener(StopEvent.EVENT_ID, StopEvent.class, listener);
    }

    public void addUpdateListener(long interval, UpdateListener listener) {
        boolean needRepaint = eventRouterMap.isEmpty();

        EventRouter eventRouter = eventRouterMap.get(interval);
        if (null == eventRouter) {
            eventRouter = new EventRouter();
            eventRouterMap.put(interval, eventRouter);
            getState().intervals.add(interval);
        }

        eventRouter.addListener(UpdateEvent.class, listener, UpdateListener.TIMER_UPDATE_METHOD);

        if (needRepaint) {
            ComponentStateUtil.addRegisteredEventListener(getState(), UpdateEvent.EVENT_ID);
        }
    }

    public void removeUpdateListener(long interval, UpdateListener listener) {
        EventRouter eventRouter = eventRouterMap.get(interval);

        if (eventRouter != null) {
            eventRouter.removeListener(UpdateEvent.class, listener);
            if (!eventRouter.hasListeners(UpdateEvent.class)) {
                eventRouterMap.remove(interval);
                getState().intervals.remove(interval);

                if (eventRouterMap.isEmpty())
                    ComponentStateUtil.removeRegisteredEventListener(getState(), UpdateEvent.EVENT_ID);
            }
        }
    }

    public void removeUpdateListener(UpdateListener listener) {
        boolean needRepaint = !eventRouterMap.isEmpty();

        ArrayList<Long> pruneList = new ArrayList<>();

        for (Long interval : eventRouterMap.keySet()) {
            EventRouter eventRouter = eventRouterMap.get(interval);
            eventRouter.removeListener(UpdateEvent.class, listener);

            if (!eventRouter.hasListeners(UpdateEvent.class))
                pruneList.add(interval);
        }

        for (Long interval : pruneList) {
            eventRouterMap.remove(interval);
            getState().intervals.remove(interval);
        }

        if (needRepaint && eventRouterMap.isEmpty())
            ComponentStateUtil.removeRegisteredEventListener(getState(), UpdateEvent.EVENT_ID);
    }

    @Override
    public void detach() {
        if (internalTimer != null) {
            internalTimer.cancel();
        }

        try {
            super.detach();
        } catch (Throwable e) {
        }
    }

    public interface StartListener extends Serializable {

        Method TIMER_START_METHOD = ReflectTools
                .findMethod(StartListener.class, StartEvent.EVENT_ID, StartEvent.class);

        /**
         * Called when a {@link HTimer} has been started. A reference to the
         * component is given by {@link StartEvent#getComponent()}.
         *
         * @param event An event containing information about the timer.
         */
        void start(StartEvent event);

    }

    public interface StopListener extends Serializable {

        Method TIMER_STOP_METHOD = ReflectTools.findMethod(
                StopListener.class, StopEvent.EVENT_ID, StopEvent.class);

        /**
         * Called when a {@link HTimer} has been stopped. A reference to the
         * component is given by {@link StopEvent#getComponent()}.
         *
         * @param event An event containing information about the timer.
         */
        void stop(StopEvent event);

    }

    public interface UpdateListener extends Serializable {

        Method TIMER_UPDATE_METHOD = ReflectTools
                .findMethod(UpdateListener.class, UpdateEvent.EVENT_ID, UpdateEvent.class);

        /**
         * Called when a {@link HTimer} has been updated. A reference to the
         * component is given by {@link StartEvent#getComponent()}.
         *
         * @param event An event containing information about the timer.
         */
        void update(UpdateEvent event);

    }

    public abstract static class TimerEvent extends TimekeepingComponentEvent {

        private final Date serverTimestamp;

        private final long time;
        private final Direction direction;

        protected TimerEvent(long timestamp, Date serverTimestamp, Component source, long time, Direction direction) {
            super(timestamp, source);
            this.serverTimestamp = serverTimestamp;
            this.time = time;
            this.direction = direction;
        }

        public long getTime() {
            return time;
        }

        public Direction getDirection() {
            return direction;
        }

        @Override
        public long getServerTimestamp() {
            return this.serverTimestamp.getTime();
        }

        @Override
        public Date getServerDatetime() {
            return this.serverTimestamp;
        }
    }

    public static class StartEvent extends TimerEvent {

        public static final String EVENT_ID = "start";

        private final boolean resumed;

        public StartEvent(long timestamp, Date serverTimestamp, Component source, long time, Direction direction, boolean resumed) {
            super(timestamp, serverTimestamp, source, time, direction);
            this.resumed = resumed;
        }

        public boolean isResumed() {
            return resumed;
        }
    }

    public static class StopEvent extends TimerEvent {

        public static final String EVENT_ID = "stop";

        private final boolean paused;

        public StopEvent(long timestamp, Date serverTimestamp, Component source, long time, Direction direction, boolean paused) {
            super(timestamp, serverTimestamp, source, time, direction);
            this.paused = paused;
        }

        public boolean isPaused() {
            return paused;
        }
    }

    public static class UpdateEvent extends TimerEvent {

        public static final String EVENT_ID = "update";

        private final long interval;

        public UpdateEvent(long timestamp, Date serverTimestamp, Component source, long time, Direction direction, long interval) {
            super(timestamp, serverTimestamp, source, time, direction);
            this.interval = interval;
        }

        public long getInterval() {
            return interval;
        }
    }

}
