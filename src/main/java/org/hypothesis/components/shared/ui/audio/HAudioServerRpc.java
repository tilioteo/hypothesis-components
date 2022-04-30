package org.hypothesis.components.shared.ui.audio;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 */
public interface HAudioServerRpc extends ServerRpc {

    void start(long timestamp, double time, boolean resumed);

    void stop(long timestamp, double time, boolean paused);

    void canPlayThrough(long timestamp);

}
