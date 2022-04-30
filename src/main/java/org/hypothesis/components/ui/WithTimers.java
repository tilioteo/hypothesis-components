package org.hypothesis.components.ui;

public interface WithTimers {

    void addTimer(HTimer timer);

    void removeTimer(HTimer timer);

    void removeAllTimers();

}
