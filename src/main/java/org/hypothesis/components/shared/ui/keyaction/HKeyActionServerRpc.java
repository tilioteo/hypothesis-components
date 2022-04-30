package org.hypothesis.components.shared.ui.keyaction;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Copied from https://github.com/Artur-/KeyActions
 * <p>
 * modified by:
 *
 * @author kamil
 */
public interface HKeyActionServerRpc extends ServerRpc {

    void trigger(long timestamp);

}
