package org.hypothesis.components.shared.ui.timer;

import com.vaadin.shared.communication.ClientRpc;

public interface HTimerClientRpc extends ClientRpc {

    void start(long time);

    void stop(boolean silent);

    void pause();

    void resume();

    void getRunning();

}