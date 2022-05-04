package org.hypothesis.components.ui;

import com.tilioteo.common.event.TimekeepingComponentEvent;
import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractMedia;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author kamil
 */
public abstract class Media {

    /**
     * Interface for listening for a {@link CanPlayThroughEvent} fired by a
     * {@link Media}.
     */
    public interface CanPlayThroughListener extends ConnectorEventListener {

        Method MEDIA_CAN_PLAY_THROUGH = ReflectTools.findMethod(
                CanPlayThroughListener.class, CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class);

        /**
         * Called when a {@link AbstractMedia} can play through without having to stop for buffering.
         * A reference to the component is given by {@link CanPlayThroughEvent#getComponent()}.
         *
         * @param event An event containing information about the component.
         */
        void canPlayThrough(CanPlayThroughEvent event);
    }

    public interface StartListener extends Serializable {

        Method MEDIA_START_METHOD = ReflectTools
                .findMethod(StartListener.class, StartEvent.EVENT_ID, StartEvent.class);

        /**
         * Called when a {@link AbstractMedia} has been started. A reference to the
         * component is given by {@link StartEvent#getComponent()}.
         *
         * @param event An event containing information about the timer.
         */
        void start(StartEvent event);

    }

    public interface StopListener extends Serializable {

        Method MEDIA_STOP_METHOD = ReflectTools.findMethod(
                StopListener.class, StopEvent.EVENT_ID, StopEvent.class);

        /**
         * Called when a {@link AbstractMedia} has been stopped. A reference to the
         * component is given by {@link StopEvent#getComponent()}.
         *
         * @param event An event containing information about the timer.
         */
        void stop(StopEvent event);

    }

    public static abstract class MediaEvent extends TimekeepingComponentEvent {

        private final double mediaTime;

        protected MediaEvent(long timestamp, Component source, double mediaTime) {
            super(timestamp, source);
            this.mediaTime = mediaTime;
        }

        public double getMediaTime() {
            return mediaTime;
        }
    }

    public static class CanPlayThroughEvent extends MediaEvent {

        public static final String EVENT_ID = "canPlayThrough";

        public CanPlayThroughEvent(long timestamp, Component source) {
            super(timestamp, source, 0.0);
        }

    }

    public static class StartEvent extends MediaEvent {

        public static final String EVENT_ID = "start";

        private final boolean resumed;

        public StartEvent(long timestamp, Component source, double mediaTime, boolean resumed) {
            super(timestamp, source, mediaTime);
            this.resumed = resumed;
        }

        public boolean isResumed() {
            return resumed;
        }
    }

    public static class StopEvent extends MediaEvent {

        public static final String EVENT_ID = "stop";

        private final boolean paused;

        public StopEvent(long timestamp, Component source, double mediaTime, boolean paused) {
            super(timestamp, source, mediaTime);
            this.paused = paused;
        }

        public boolean isPaused() {
            return paused;
        }
    }

}
