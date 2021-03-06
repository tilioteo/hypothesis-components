package org.hypothesis.components.client.ui.button;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.EventHelper;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.VCaption;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VButton;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.button.ButtonState;
import org.hypothesis.components.client.DateUtility;
import org.hypothesis.components.shared.ui.button.HButtonServerRpc;
import org.hypothesis.components.ui.HButton;

/**
 * @author kamil
 */
@Connect(value = HButton.class, loadStyle = LoadStyle.EAGER)
public class HButtonConnector extends AbstractComponentConnector implements
        BlurHandler, FocusHandler, ClickHandler {

    private HandlerRegistration focusHandlerRegistration = null;
    private HandlerRegistration blurHandlerRegistration = null;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        getWidget().addClickHandler(this);
        getWidget().client = getConnection();
    }

    @OnStateChange("errorMessage")
    void setErrorMessage() {
        if (null != getState().errorMessage) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidget().wrapper.insertFirst(getWidget().errorIndicatorElement);

        } else if (getWidget().errorIndicatorElement != null) {
            getWidget().wrapper.removeChild(getWidget().errorIndicatorElement);
            getWidget().errorIndicatorElement = null;
        }
    }

    @OnStateChange("resources")
    void onResourceChange() {
        if (getWidget().icon != null) {
            getWidget().wrapper.removeChild(getWidget().icon.getElement());
            getWidget().icon = null;
        }
        Icon icon = getIcon();
        if (icon != null) {
            getWidget().icon = icon;
            icon.setAlternateText(getState().iconAltText);
            getWidget().wrapper.insertBefore(icon.getElement(),
                    getWidget().captionElement);
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);
    }

    @OnStateChange({"caption", "captionAsHtml"})
    void setCaption() {
        VCaption.setCaptionText(getWidget().captionElement, getState());
    }

    @OnStateChange("iconAltText")
    void setIconAltText() {
        if (getWidget().icon != null) {
            getWidget().icon.setAlternateText(getState().iconAltText);
        }
    }

    @OnStateChange("clickShortcutKeyCode")
    void setClickShortcut() {
        getWidget().clickShortcut = getState().clickShortcutKeyCode;
    }

    @Override
    public VButton getWidget() {
        return (VButton) super.getWidget();
    }

    @Override
    public ButtonState getState() {
        return (ButtonState) super.getState();
    }

    @Override
    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).focus();
    }

    @Override
    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).blur();
    }

    @Override
    public void onClick(ClickEvent event) {
        long timestamp = DateUtility.getTimestamp();

        if (getState().disableOnClick) {
            // Simulate getting disabled from the server without waiting for the
            // round trip. The server-side RPC handler takes care of updating
            // the server-side state in a similar way to ensure subsequent
            // changes are properly propagated. Changing state on client is not
            // generally supported.
            getState().enabled = false;
            super.updateEnabledState(false);
            getRpcProxy(HButtonServerRpc.class).disableOnClick();
        }

        // Add mouse details
        MouseEventDetails details = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent(), getWidget()
                        .getElement());
        getRpcProxy(HButtonServerRpc.class).click(timestamp, details);

    }
}
