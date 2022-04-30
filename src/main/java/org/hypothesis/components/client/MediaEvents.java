package org.hypothesis.components.client;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.vaadin.client.ui.VMediaBase;

/**
 * @author kamil
 */
public class MediaEvents {

    public static final String PLAY = "play";
    public static final String PAUSE = "pause";

    public interface PlayHandler extends EventHandler {
        void onPlay(PlayEvent event);
    }

    public interface PauseHandler extends EventHandler {
        void onPause(PauseEvent event);
    }

    public static class PlayEvent extends DomEvent<PlayHandler> {

        public static final Type<PlayHandler> TYPE = new Type<>(PLAY, new PlayEvent());

        protected PlayEvent() {
        }

        public PlayEvent(VMediaBase media) {
            setSource(media);
        }

        public VMediaBase getMediaBase() {
            return (VMediaBase) getSource();
        }

        @Override
        public Type<PlayHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(PlayHandler handler) {
            handler.onPlay(this);
        }
    }

    public static class PauseEvent extends DomEvent<PauseHandler> {

        public static final Type<PauseHandler> TYPE = new Type<>(PAUSE, new PauseEvent());

        private double time;

        protected PauseEvent() {
        }

        public PauseEvent(VMediaBase media, double time) {
            setSource(media);
            this.time = time;
        }

        public VMediaBase getMediaBase() {
            return (VMediaBase) getSource();
        }

        public double getTime() {
            return time;
        }

        @Override
        public Type<PauseHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(PauseHandler handler) {
            handler.onPause(this);
        }
    }

}
