package org.hypothesis.components.client.ui.image;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.image.ImageState;
import org.hypothesis.components.client.DateUtility;
import org.hypothesis.components.client.ui.VHImage;
import org.hypothesis.components.shared.ui.image.HImageServerRpc;
import org.hypothesis.components.ui.HImage;

/**
 * @author kamil
 */
@Connect(HImage.class)
public class HImageConnector extends AbstractComponentConnector implements LoadHandler, ErrorHandler {

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event, MouseEventDetails mouseDetails) {
            getRpcProxy(HImageServerRpc.class).click(DateUtility.getTimestamp(), mouseDetails);
        }
    };

    @Override
    protected void init() {
        super.init();
        getWidget().addLoadHandler(this);
        getWidget().addErrorHandler(this);
    }

    @Override
    public VHImage getWidget() {
        return (VHImage) super.getWidget();
    }

    @Override
    public ImageState getState() {
        return (ImageState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

        String url = getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE);
        getWidget().setUrl(url != null ? url : "");

        String alt = getState().alternateText;
        // Some browsers turn a null alt text into a literal "null"
        getWidget().setAltText(alt != null ? alt : "");
    }

    @Override
    public void onLoad(LoadEvent event) {
        getLayoutManager().setNeedsMeasure(HImageConnector.this);
        getRpcProxy(HImageServerRpc.class).load(DateUtility.getTimestamp());
    }

    @Override
    public void onError(ErrorEvent event) {
        getRpcProxy(HImageServerRpc.class).error(DateUtility.getTimestamp());
    }

}
