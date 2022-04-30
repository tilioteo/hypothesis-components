package org.hypothesis.components.shared.ui.selectbutton;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 */
public interface HSelectButtonServerRpc extends ServerRpc {

    void setChecked(long timestamp, MouseEventDetails mouseEventDetails, boolean checked);

}
