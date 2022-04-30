package org.hypothesis.components.client.ui;

import com.vaadin.client.ui.AbstractComponentConnector;

/**
 * @author kamil
 */
public abstract class AbstractNonVisualComponentConnector extends AbstractComponentConnector {

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

}
