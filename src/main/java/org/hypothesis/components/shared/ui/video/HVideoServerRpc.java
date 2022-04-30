package org.hypothesis.components.shared.ui.video;

import com.vaadin.shared.MouseEventDetails;
import org.hypothesis.components.shared.ui.audio.HAudioServerRpc;

/**
 * @author kamil
 */
public interface HVideoServerRpc extends HAudioServerRpc {

    /**
     * Called when a click event has occurred and there are server side
     * listeners for the event.
     *
     * @param mouseDetails Details about the mouse when the event took place
     */
    void click(long timestamp, MouseEventDetails mouseDetails, double mediaTime);

}
