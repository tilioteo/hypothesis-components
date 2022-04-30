package org.hypothesis.components.ui;

import com.vaadin.server.Resource;
import com.vaadin.ui.Audio;
import org.hypothesis.components.shared.ui.audio.HAudioServerRpc;
import org.hypothesis.components.shared.ui.audio.HAudioState;
import org.hypothesis.components.ui.Media.*;

/**
 * @author kamil
 */
public class HAudio extends Audio {

    protected final HAudioServerRpc rpc = new HAudioServerRpc() {

        @Override
        public void stop(long timestamp, double time, boolean paused) {
            fireEvent(new StopEvent(timestamp, HAudio.this, time, paused));
        }

        @Override
        public void start(long timestamp, double time, boolean resumed) {
            fireEvent(new StartEvent(timestamp, HAudio.this, time, resumed));
        }

        @Override
        public void canPlayThrough(long timestamp) {
            fireEvent(new CanPlayThroughEvent(timestamp, HAudio.this));
        }
    };

    public HAudio() {
        this("", null);
    }

    /**
     * @param caption The caption of the audio component.
     */
    public HAudio(String caption) {
        this(caption, null);
    }

    /**
     * @param caption The caption of the audio component
     * @param source  The audio file to play.
     */
    public HAudio(String caption, Resource source) {
        super(caption, source);

        registerRpc(rpc);
    }

    @Override
    protected HAudioState getState() {
        return (HAudioState) super.getState();
    }

    public void addCanPlayThroughListener(CanPlayThroughListener listener) {
        addListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener,
                CanPlayThroughListener.MEDIA_CAN_PLAY_THROUGH);
    }

    public void removeCanPlayThroughListener(CanPlayThroughListener listener) {
        removeListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener);
    }

    public void addStartListener(StartListener listener) {
        addListener(StartEvent.EVENT_ID, StartEvent.class, listener,
                StartListener.MEDIA_START_METHOD);
    }

    public void removeStartListener(StartListener listener) {
        removeListener(StartEvent.EVENT_ID, StartEvent.class, listener);
    }

    public void addStopListener(StopListener listener) {
        addListener(StopEvent.EVENT_ID, StopEvent.class, listener,
                StopListener.MEDIA_STOP_METHOD);
    }

    public void removeStopListener(StopListener listener) {
        removeListener(StopEvent.EVENT_ID, StopEvent.class, listener);
    }

}
