package org.hypothesis.components.shared.ui.button;

import org.hypothesis.components.shared.ClickRpc;

/**
 * @author kamil
 */
public interface HButtonServerRpc extends ClickRpc {

    /**
     * Indicate to the server that the client has disabled the button as a
     * result of a click.
     */
    void disableOnClick();
}
