package org.hypothesis.components.client.ui.video;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.MediaControl;
import com.vaadin.shared.ui.video.VideoConstants;
import org.hypothesis.components.client.DateUtility;
import org.hypothesis.components.client.MediaEvents;
import org.hypothesis.components.client.ui.VHVideo;
import org.hypothesis.components.shared.ui.video.HVideoServerRpc;
import org.hypothesis.components.shared.ui.video.HVideoState;
import org.hypothesis.components.ui.HVideo;

/**
 * @author kamil
 */
@Connect(HVideo.class)
public class HVideoConnector extends AbstractComponentConnector implements CanPlayThroughHandler, MediaEvents.PlayHandler, EndedHandler, MediaEvents.PauseHandler {

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event, MouseEventDetails mouseDetails) {
            getRpcProxy(HVideoServerRpc.class).click(DateUtility.getTimestamp(), mouseDetails, getWidget().getCurrentTime());
        }
    };
    private boolean started = false;

    @Override
    protected void init() {
        super.init();

        registerRpc(MediaControl.class, new MediaControl() {
            @Override
            public void play() {
                getWidget().play();
            }

            @Override
            public void pause() {
                getWidget().pause();
            }
        });

        getWidget().addCanPlayThroughHandler(this);
        getWidget().addEndedHandler(this);
        getWidget().addPlayHandler(this);
        getWidget().addPauseHandler(this);
    }

    @Override
    public HVideoState getState() {
        return (HVideoState) super.getState();
    }

    @Override
    public VHVideo getWidget() {
        return (VHVideo) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);

        final VHVideo widget = getWidget();
        final HVideoState state = getState();

        setAltText(state.altText); // must do before loading sources
        widget.setAutoplay(state.autoplay);
        widget.setMuted(state.muted);
        widget.setControls(state.showControls);

        if (event.hasPropertyChanged("sources")) {
            widget.removeAllSources();
            for (int i = 0; i < state.sources.size(); i++) {
                URLReference source = state.sources.get(i);
                String sourceType = state.sourceTypes.get(i);
                widget.addSource(source.getURL(), sourceType);
            }
            widget.load();
        }

        getWidget().setPoster(getResourceUrl(VideoConstants.POSTER_RESOURCE));

        clickEventHandler.handleEventHandlerRegistration();
    }

    private void setAltText(String altText) {

        if (altText == null || "".equals(altText)) {
            altText = getDefaultAltHtml();
        } else if (!getState().htmlContentAllowed) {
            altText = Util.escapeHTML(altText);
        }
        getWidget().setAltText(altText);
    }

    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>video</code> element.";
    }

    @Override
    public void onCanPlayThrough(CanPlayThroughEvent event) {
        getRpcProxy(HVideoServerRpc.class).canPlayThrough(DateUtility.getTimestamp());
    }

    @Override
    public void onEnded(EndedEvent event) {
        getRpcProxy(HVideoServerRpc.class).stop(DateUtility.getTimestamp(), getWidget().getCurrentTime(), false);
    }

    @Override
    public void onPause(MediaEvents.PauseEvent event) {
        getRpcProxy(HVideoServerRpc.class).stop(DateUtility.getTimestamp(), getWidget().getCurrentTime(), true);
    }

    @Override
    public void onPlay(MediaEvents.PlayEvent event) {
        getRpcProxy(HVideoServerRpc.class).start(DateUtility.getTimestamp(), started ? getWidget().getCurrentTime() : 0.0, started);

        if (!started) {
            started = true;
        }
    }

}
