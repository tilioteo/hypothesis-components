package org.hypothesis.components.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Kamil Morong - Hypothesis
 */
public class Timer {
	/**
	 * tick of internal timer
	 */
	public static final int TIMER_TICK = 10;

	/**
	 * handle manager for StartEvent and StopEvent
	 */
	protected final HandlerManager handlerManager = new HandlerManager(this);
	/**
	 * map of handler managers
	 * for registered timeSlice (key) is created its own handler manager
	 */
	protected final HashMap<Long, HandlerManager> handlerManagerMap = new HashMap<>();
	private long startCounter = 0;
	private long counter = 0;
	private boolean running = false;
	private long elapsed = 0;
	private Date startTime;
	private Direction direction = Direction.UP;
	private boolean infinite = false;

	public long getCounter() {
		return counter;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) throws Exception {
		if (!running)
			this.direction = direction;
		else
			throw new Exception("Timer cannot change direction until running");
	}

	public boolean isRunning() {
		return running;
	}

	public void pause() {
		stop(true, false);
	}

	public void stop() {
		stop(false);
	}

	protected void resume(boolean started) {
		if (!running) {
			handlerManager.fireEvent(new StartEvent(Timer.this, counter, direction, !started));
			running = true;
			startTime = new Date();
			internalTimer.scheduleRepeating(TIMER_TICK);
		}
	}

	public void resume() {
		if (startCounter > 0) {
			resume(false);
		}
	}

	protected void setElapsed() {
		elapsed = new Date().getTime() - startTime.getTime();
		updateCounter(elapsed);

		// stop timer when the time passed
		if (!infinite && elapsed >= startCounter) {
			running = false;
			internalTimer.cancel();

			handlerManager.fireEvent(new StopEvent(Timer.this, counter, direction, false));

		} else {
			signalTimeSlices(elapsed);
		}
	}

	/**
	 * internal timer runs for 1 period of TIMER_TICK ms
	 */
	private final com.google.gwt.user.client.Timer internalTimer = new com.google.gwt.user.client.Timer() {
		@Override
		public void run() {
			if (running) {
				Scheduler.get().scheduleDeferred(() -> setElapsed());
			}
		}
	};

	public boolean isInfinite() {
		return infinite;
	}

	protected void setInfinite(boolean infinite) {
		this.infinite = infinite;
		if (infinite) {
			direction = Direction.UP;
		}
	}

	public void start(long milliSeconds) {
		if (milliSeconds < 0) {
			setInfinite(true);
		}

		elapsed = 0;

		if (milliSeconds >= TIMER_TICK) {
			startCounter = milliSeconds;

			if (Direction.UP.equals(direction))
				counter = 0;
			else
				counter = startCounter;

			Scheduler.get().scheduleDeferred(() -> resume(true));

		} else {
			startCounter = 0;
		}
	}

	private void stop(boolean pause, boolean silent) {
		if (running) {
			running = false;
			internalTimer.cancel();
			setElapsed();
			if (!silent) {
				handlerManager.fireEvent(new StopEvent(Timer.this, counter, direction, pause));
			}
			if (!pause) {
				startCounter = 0;
			}
		}
	}

	public void stop(boolean silent) {
		stop(false, silent);
	}

	private void updateCounter(long elapsed) {
		if (Direction.UP.equals(direction)) {
			counter = elapsed;
		} else {
			counter = startCounter - elapsed;
			if (counter < 0) {
				counter = 0;
			}
		}
	}

	private void signalTimeSlices(long elapsed) {
		for (Long timeSlice : handlerManagerMap.keySet()) {
			// modulo for time slices passed in elapsed time
			long rest = elapsed % timeSlice;
			// if rest passes into first timer tick interval then fire update event
			// for this time slice
			if (rest >= 0 && rest < TIMER_TICK) {
				handlerManagerMap.get(timeSlice).fireEvent(new UpdateEvent(Timer.this, counter, direction, timeSlice));
			}
		}
	}

	public void addUpdateEventHandler(long interval, UpdateEventHandler handler) {
		HandlerManager handlerManager = handlerManagerMap.get(interval);

		// there is not registered handler manager for interval
		// create and register it
		if (null == handlerManager) {
			handlerManager = new HandlerManager(this);
			handlerManagerMap.put(interval, handlerManager);
		}

		handlerManager.addHandler(UpdateEvent.TYPE, handler);
	}

	/**
	 * Removes update handler from interval registration.
	 *
	 * @param interval
	 * @param handler
	 */
	public void removeUpdateEventHandler(long interval, UpdateEventHandler handler) {
		HandlerManager handlerManager = handlerManagerMap.get(interval);
		if (handlerManager != null) {
			handlerManager.removeHandler(UpdateEvent.TYPE, handler);

			// clean handler manager map
			if (handlerManager.getHandlerCount(UpdateEvent.TYPE) == 0)
				handlerManagerMap.remove(interval);
		}
	}

	/**
	 * Removes update handler from all interval registrations.
	 *
	 * @param handler
	 */
	public void removeUpdateEventHandler(UpdateEventHandler handler) {
		ArrayList<Long> pruneList = new ArrayList<>();

		for (Long interval : handlerManagerMap.keySet()) {
			HandlerManager handlerManager = handlerManagerMap.get(interval);
			handlerManager.removeHandler(UpdateEvent.TYPE, handler);

			if (handlerManager.getHandlerCount(UpdateEvent.TYPE) == 0)
				pruneList.add(interval);
		}

		// clean unused handler managers
		for (Long interval : pruneList) {
			handlerManagerMap.remove(interval);
		}
	}

	public void addStartEventHandler(StartEventHandler handler) {
		handlerManager.addHandler(StartEvent.TYPE, handler);
	}

	public void removeStartEventHandler(StartEventHandler handler) {
		handlerManager.removeHandler(StartEvent.TYPE, handler);
	}

	public void addStopEventHandler(StopEventHandler handler) {
		handlerManager.addHandler(StopEvent.TYPE, handler);
	}

	public void removeStopEventHandler(StopEventHandler handler) {
		handlerManager.removeHandler(StopEvent.TYPE, handler);
	}

	public boolean hasUpdateEventHandler(long interval, UpdateEventHandler handler) {
		HandlerManager handlerManager = handlerManagerMap.get(interval);
		if (handlerManager != null) {
			int count = handlerManager.getHandlerCount(UpdateEvent.TYPE);
			if (count > 0) {
				for (int i = 0; i < count; ++i) {
					if (handler == handlerManager.getHandler(UpdateEvent.TYPE, i))
						return true;
				}
			}
		}
		return false;
	}

	public Set<Long> getHandledIntervals() {
		return handlerManagerMap.keySet();
	}

	public enum Direction {UP, DOWN}

	public interface UpdateEventHandler extends EventHandler {
		void update(UpdateEvent event);
	}

	public interface StartEventHandler extends EventHandler {
		void start(StartEvent event);
	}

	public interface StopEventHandler extends EventHandler {
		void stop(StopEvent event);
	}

	public static class UpdateEvent extends GwtEvent<UpdateEventHandler> {

		public static final Type<UpdateEventHandler> TYPE = new Type<>();

		private final long time;
		private final Direction direction;
		private final long interval;

		public UpdateEvent(Timer timer, long time, Direction direction,
						   long interval) {
			setSource(timer);
			this.time = time;
			this.direction = direction;
			this.interval = interval;
		}

		public Timer getTimer() {
			return (Timer) getSource();
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}

		public long getInterval() {
			return interval;
		}

		@Override
		public Type<UpdateEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateEventHandler handler) {
			handler.update(this);

		}

	}

	public static class StartEvent extends GwtEvent<StartEventHandler> {

		public static final Type<StartEventHandler> TYPE = new Type<>();
		private final boolean resumed;
		private final long time;
		private final Direction direction;

		public StartEvent(Timer timer, long time, Direction direction,
						  boolean resumed) {
			setSource(timer);
			this.time = time;
			this.direction = direction;
			this.resumed = resumed;
		}

		public Timer getTimer() {
			return (Timer) getSource();
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}

		public boolean isResumed() {
			return resumed;
		}

		@Override
		public Type<StartEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(StartEventHandler handler) {
			handler.start(this);

		}

	}

	public static class StopEvent extends GwtEvent<StopEventHandler> {

		public static final Type<StopEventHandler> TYPE = new Type<>();

		private final long time;
		private final Direction direction;
		private final boolean paused;

		public StopEvent(Timer timer, long time, Direction direction,
						 boolean paused) {
			setSource(timer);
			this.time = time;
			this.direction = direction;
			this.paused = paused;
		}

		public Timer getTimer() {
			return (Timer) getSource();
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}

		public boolean isPaused() {
			return paused;
		}

		@Override
		public Type<StopEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(StopEventHandler handler) {
			handler.stop(this);

		}

	}


}
