package org.hypothesis.components.ui;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.image.ImageState;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import org.hypothesis.components.shared.HEventId;
import org.hypothesis.components.shared.ui.image.HImageServerRpc;
import org.hypothesis.interfaces.mask.Maskable;
import org.hypothesis.ui.event.MouseEvents;
import org.hypothesis.ui.event.TimekeepingComponentEvent;

import java.lang.reflect.Method;

/**
 * @author kamil
 */
public class HImage extends AbstractEmbedded implements Maskable {

    protected final HImageServerRpc rpc = new HImageServerRpc() {
        @Override
        public void click(long timestamp, MouseEventDetails mouseDetails) {
            fireEvent(new MouseEvents.ClickEvent(timestamp, HImage.this, mouseDetails));
        }

        @Override
        public void load(long timestamp) {
            fireEvent(new LoadEvent(timestamp, HImage.this));
        }

        @Override
        public void error(long timestamp) {
            fireEvent(new ErrorEvent(timestamp, HImage.this));
        }
    };

    /**
     * Creates a new empty Image.
     */
    public HImage() {
        registerRpc(rpc);
    }

    /**
     * Creates a new empty Image with caption.
     *
     * @param caption
     */
    public HImage(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new Image whose contents are loaded from given resource. The
     * dimensions are assumed if possible. The type is guessed from resource.
     *
     * @param caption
     * @param source  the Source of the embedded object.
     */
    public HImage(String caption, Resource source) {
        this(caption);
        setSource(source);
    }

    @Override
    protected ImageState getState() {
        return (ImageState) super.getState();
    }

    /**
     * Add a load listener to the component. The listener is called when the
     * image is successfully loaded.
     * <p>
     * Use {@link #removeLoadListener(LoadListener)} to remove the listener.
     *
     * @param listener The listener to add
     */
    public void addLoadListener(LoadListener listener) {
        addListener(HEventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class, listener, LoadListener.loadMethod);
    }

    /**
     * Remove a load listener from the component. The listener should earlier
     * have been added using {@link #addLoadListener(LoadListener)}.
     *
     * @param listener The listener to remove
     */
    public void removeLoadListener(LoadListener listener) {
        removeListener(HEventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class, listener);
    }

    /**
     * Add an error listener to the component. The listener is called when the
     * image loading failed.
     * <p>
     * Use {@link #removeErrorListener(ErrorListener)} to remove the listener.
     *
     * @param listener The listener to add
     */
    public void addErrorListener(ErrorListener listener) {
        addListener(HEventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class, listener, ErrorListener.errorMethod);
    }

    /**
     * Remove an error listener from the component. The listener should earlier
     * have been added using {@link #addErrorListener(ErrorListener)}.
     *
     * @param listener The listener to remove
     */
    public void removeErrorListener(ErrorListener listener) {
        removeListener(HEventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class, listener);
    }

    /**
     * Add a click listener to the component. The listener is called whenever
     * the user clicks inside the component. Depending on the content the event
     * may be blocked and in that case no event is fired.
     * <p>
     * Use {@link #removeClickListener(MouseEvents.ClickListener)} to remove the listener.
     *
     * @param listener The listener to add
     */
    public void addClickListener(MouseEvents.ClickListener listener) {
        addListener(HEventId.CLICK_EVENT_IDENTIFIER, MouseEvents.ClickEvent.class, listener, MouseEvents.ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the component. The listener should earlier
     * have been added using {@link #addClickListener(MouseEvents.ClickListener)}.
     *
     * @param listener The listener to remove
     */
    public void removeClickListener(MouseEvents.ClickListener listener) {
        removeListener(HEventId.CLICK_EVENT_IDENTIFIER, MouseEvents.ClickEvent.class, listener);
    }

    /**
     * Interface for listening for a {@link LoadEvent} fired by a {@link HImage}.
     *
     * @author kamil
     * @see LoadEvent
     */
    public interface LoadListener extends ConnectorEventListener {

        Method loadMethod = ReflectTools.findMethod(
                LoadListener.class, HEventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class);

        /**
         * Called when a {@link HImage} has been successfully loaded. A reference
         * to the component is given by {@link LoadEvent#getComponent()}.
         *
         * @param event An event containing information about the image.
         */
        void load(LoadEvent event);
    }

    /**
     * Interface for listening for a {@link ErrorEvent} fired by a {@link HImage}
     *
     * @author kamil
     * @see ErrorEvent
     */
    public interface ErrorListener extends ConnectorEventListener {

        Method errorMethod = ReflectTools.findMethod(
                ErrorListener.class, HEventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class);

        /**
         * Called when a {@link HImage} loading failed. A reference to the
         * component is given by {@link ErrorEvent#getComponent()}.
         *
         * @param event An event containing information about the image.
         */
        void error(ErrorEvent event);
    }

    /**
     * Class for holding information about an image load event. A
     * {@link LoadEvent} is fired when the <code>Image</code> is successfully
     * loaded.
     *
     * @author kamil.
     * @see LoadListener
     */
    public static class LoadEvent extends TimekeepingComponentEvent {

        public LoadEvent(long timestamp, Component source) {
            super(timestamp, source);
        }

    }

    /**
     * Class for holding information about an image error event. An
     * {@link ErrorEvent} is fired when the <code>Image</code> loading fails.
     *
     * @author kamil
     * @see ErrorListener
     */
    public static class ErrorEvent extends TimekeepingComponentEvent {

        public ErrorEvent(long timestamp, Component source) {
            super(timestamp, source);
        }

    }
}
