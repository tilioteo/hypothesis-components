package org.hypothesis.components.shared.ui.timer;

import com.vaadin.shared.communication.ServerRpc;

public interface HTimerServerRpc extends ServerRpc {

    void started(long timestamp, long time, String direction, boolean resumed);

    void stopped(long timestamp, long time, String direction, boolean paused);

    void update(long time, String direction, long interval);

}
