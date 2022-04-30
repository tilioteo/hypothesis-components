package org.hypothesis.components.client.ui.timer;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.DateUtility;
import org.hypothesis.components.client.ui.AbstractNonVisualComponentConnector;
import org.hypothesis.components.client.ui.VHTimer;
import org.hypothesis.components.shared.ui.timer.HTimerClientRpc;
import org.hypothesis.components.shared.ui.timer.HTimerServerRpc;
import org.hypothesis.components.shared.ui.timer.HTimerState;
import org.hypothesis.components.ui.HTimer;

import java.util.Set;

@Connect(HTimer.class)
public class HTimerConnector extends AbstractNonVisualComponentConnector implements
        org.hypothesis.components.client.Timer.StartEventHandler, org.hypothesis.components.client.Timer.StopEventHandler,
        org.hypothesis.components.client.Timer.UpdateEventHandler {

    @Override
    protected void init() {
        super.init();
        getWidget().addStartEventHandler(this);
        getWidget().addStopEventHandler(this);

        registerRpc(HTimerClientRpc.class, new HTimerClientRpc() {

            @Override
            public void start(long time) {
                getWidget().start(time);
            }

            @Override
            public void stop(boolean silent) {
                getWidget().stop(silent);
            }

            @Override
            public void pause() {
                getWidget().pause();
            }

            @Override
            public void resume() {
                getWidget().resume();
            }

            @Override
            public void getRunning() {
                getState().running = getWidget().isRunning();
            }

        });

        addStateChangeHandler("direction", new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                HTimerConnector.this.getWidget().setDirection(HTimerConnector.this.getState().direction.name());
            }
        });
    }

    @Override
    public VHTimer getWidget() {
        return (VHTimer) super.getWidget();
    }

    @Override
    public HTimerState getState() {
        return (HTimerState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("intervals")) {
            Set<Long> requiredIntervals = getState().intervals;
            Set<Long> handledIntervals = getWidget().getHandledIntervals();

            // unregister unwanted interval handlers
            for (Long interval : handledIntervals) {
                if (!requiredIntervals.contains(interval)) {
                    getWidget().removeUpdateEventHandler(interval, this);
                }
            }
            // register required interval handlers
            for (Long interval : requiredIntervals) {
                if (!getWidget().hasUpdateEventHandler(interval, this)) {
                    getWidget().addUpdateEventHandler(interval, this);
                }
            }
        }
    }

    @Override
    public void start(final org.hypothesis.components.client.Timer.StartEvent event) {
        getRpcProxy(HTimerServerRpc.class).started(DateUtility.getTimestamp(), event.getTime(), event.getDirection().name(), event.isResumed());
    }

    @Override
    public void stop(final org.hypothesis.components.client.Timer.StopEvent event) {
        getRpcProxy(HTimerServerRpc.class).stopped(DateUtility.getTimestamp(), event.getTime(), event.getDirection().name(), event.isPaused());
    }

    @Override
    public void update(final org.hypothesis.components.client.Timer.UpdateEvent event) {
        getRpcProxy(HTimerServerRpc.class).update(event.getTime(), event.getDirection().name(), event.getInterval());
    }

}
