package org.hypothesis.components.shared;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 */
public interface ClickRpc extends ServerRpc {

    /**
     * Common click event.
     *
     * @param mouseEventDetails serialized mouse event details
     */
    void click(long timestamp, MouseEventDetails mouseEventDetails);

}
