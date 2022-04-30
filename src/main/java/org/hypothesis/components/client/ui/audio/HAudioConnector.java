package org.hypothesis.components.client.ui.audio;

import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.audio.AudioConnector;
import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.MediaEvents;
import org.hypothesis.components.client.ui.VHAudio;
import org.hypothesis.components.shared.ui.audio.HAudioServerRpc;
import org.hypothesis.components.shared.ui.audio.HAudioState;
import org.hypothesis.components.ui.HAudio;

import static org.hypothesis.components.client.DateUtility.getTimestamp;

/**
 * @author kamil
 */
@Connect(HAudio.class)
public class HAudioConnector extends AudioConnector
        implements CanPlayThroughHandler, MediaEvents.PlayHandler, EndedHandler, MediaEvents.PauseHandler {

    private boolean started = false;

    @Override
    protected void init() {
        super.init();

        getWidget().addCanPlayThroughHandler(this);
        getWidget().addEndedHandler(this);
        getWidget().addPlayHandler(this);
        getWidget().addPauseHandler(this);
    }

    @Override
    public HAudioState getState() {
        return (HAudioState) super.getState();
    }

    @Override
    public VHAudio getWidget() {
        return (VHAudio) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

    }

    @Override
    public void onCanPlayThrough(CanPlayThroughEvent event) {
        getRpcProxy(HAudioServerRpc.class).canPlayThrough(getTimestamp());
    }

    @Override
    public void onEnded(EndedEvent event) {
        getRpcProxy(HAudioServerRpc.class).stop(getTimestamp(), getWidget().getCurrentTime(), false);
    }

    @Override
    public void onPause(MediaEvents.PauseEvent event) {
        getRpcProxy(HAudioServerRpc.class).stop(getTimestamp(), getWidget().getCurrentTime(), true);
    }

    @Override
    public void onPlay(MediaEvents.PlayEvent event) {
        getRpcProxy(HAudioServerRpc.class).start(getTimestamp(), started ? getWidget().getCurrentTime() : 0.0, started);

        if (!started) {
            started = true;
        }
    }

}
