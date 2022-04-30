package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ui.VAudio;
import org.hypothesis.components.client.MediaEvents;

/**
 * @author kamil
 */
public class VHAudio extends VAudio {

    protected final AudioElement getAudioElement() {
        return (AudioElement) Element.as(getElement());
    }

    public double getCurrentTime() {
        return getAudioElement().getCurrentTime();
    }

    public void setCurrentTime(double time) {
        getAudioElement().setCurrentTime(time);
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
