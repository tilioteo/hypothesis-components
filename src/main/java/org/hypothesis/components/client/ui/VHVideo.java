package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Util;
import org.hypothesis.components.client.MediaEvents;

/**
 * @author kamil
 */
public class VHVideo extends Widget {

    public static final String CLASSNAME = "v-video";
    private final VideoElement video;
    private final Element container;
    private Text altText;

    public VHVideo() {
        container = Document.get().createDivElement();
        setElement(container);
        video = Document.get().createVideoElement();
        container.appendChild(video);
        setStyleName(CLASSNAME);

        updateDimensionsWhenMetadataLoaded(Element.as(video));
    }

    /**
     * Registers a listener that updates the dimensions of the widget when the
     * video metadata has been loaded.
     *
     * @param el
     */
    private native void updateDimensionsWhenMetadataLoaded(Element el) /*-{
        var self = this;
        el.addEventListener('loadedmetadata', $entry(function (e) {
            self.@org.hypothesis.components.client.ui.VHVideo::updateElementDynamicSize(II)(el.videoWidth, el.videoHeight);
        }), false);

    }-*/;

    /**
     * Updates the dimensions of the widget.
     *
     * @param w
     * @param h
     */
    private void updateElementDynamicSize(int w, int h) {
        video.getStyle().setWidth(100, Unit.PCT);
        video.getStyle().setHeight(100, Unit.PCT);
        container.getStyle().setWidth(w, Unit.PX);
        container.getStyle().setHeight(h, Unit.PX);
        Util.notifyParentOfSizeChange(this, true);
    }

    public void play() {
        video.play();
    }

    public void pause() {
        video.pause();
    }

    public void setAltText(String alt) {
        if (altText == null) {
            altText = Document.get().createTextNode(alt);
            video.appendChild(altText);
        } else {
            altText.setNodeValue(alt);
        }
    }

    public void setControls(boolean shouldShowControls) {
        video.setControls(shouldShowControls);
    }

    public void setAutoplay(boolean shouldAutoplay) {
        video.setAutoplay(shouldAutoplay);
    }

    public void setMuted(boolean mediaMuted) {
        video.setMuted(mediaMuted);
    }

    public void removeAllSources() {
        NodeList<com.google.gwt.dom.client.Element> l = video.getElementsByTagName(SourceElement.TAG);
        for (int i = l.getLength() - 1; i >= 0; i--) {
            video.removeChild(l.getItem(i));
        }

    }

    public void load() {
        video.load();
    }

    public void addSource(String sourceUrl, String sourceType) {
        Element src = Document.get().createElement(SourceElement.TAG).cast();
        src.setAttribute("src", sourceUrl);
        src.setAttribute("type", sourceType);
        video.appendChild(src);
    }

    public void setPoster(String poster) {
        video.setPoster(poster);
    }

    protected final VideoElement getVideoElement() {
        return video;
    }

    public double getCurrentTime() {
        return video.getCurrentTime();
    }

    public void setCurrentTime(double time) {
        video.setCurrentTime(time);
    }

    @Override
    public void sinkBitlessEvent(String eventTypeName) {
        DOM.sinkBitlessEvent(Element.as(video), eventTypeName);
    }

    public HandlerRegistration addPauseHandler(MediaEvents.PauseHandler handler) {
        return addBitlessDomHandler(handler, MediaEvents.PauseEvent.TYPE);
    }

    public HandlerRegistration addPlayHandler(MediaEvents.PlayHandler handler) {
        return addBitlessDomHandler(handler, MediaEvents.PlayEvent.TYPE);
    }

    public HandlerRegistration addCanPlayThroughHandler(CanPlayThroughHandler handler) {
        return addBitlessDomHandler(handler, CanPlayThroughEvent.getType());
    }

    public HandlerRegistration addEndedHandler(EndedHandler handler) {
        return addBitlessDomHandler(handler, EndedEvent.getType());
    }

}
