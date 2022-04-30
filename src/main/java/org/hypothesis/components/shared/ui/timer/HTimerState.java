package org.hypothesis.components.shared.ui.timer;

import com.vaadin.shared.AbstractComponentState;

import java.util.HashSet;
import java.util.Set;

public class HTimerState extends AbstractComponentState {

    /**
     * tick of timer
     */
    public static final int TIMER_TICK = 10;
    public Direction direction = Direction.UP;
    public boolean running = false;
    public Set<Long> intervals = new HashSet<>();

    public enum Direction {UP, DOWN}

}