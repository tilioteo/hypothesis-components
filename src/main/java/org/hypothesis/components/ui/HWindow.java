package org.hypothesis.components.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HWindow extends Window {

    private static final Method WINDOW_INIT_METHOD;
    private static final Method WINDOW_OPEN_METHOD;
    private static final Method WINDOW_CLOSE_METHOD;

    static {
        try {
            WINDOW_INIT_METHOD = InitListener.class.getDeclaredMethod("initWindow", WindowEvent.Init.class);
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException("Internal error, window init method not found");
        }
    }

    static {
        try {
            WINDOW_OPEN_METHOD = OpenListener.class.getDeclaredMethod("openWindow", WindowEvent.Open.class);
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException("Internal error, window open method not found");
        }
    }

    static {
        try {
            WINDOW_CLOSE_METHOD = CloseListener.class.getDeclaredMethod("closeWindow", WindowEvent.Close.class);
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException("Internal error, window close method not found");
        }
    }

    private final ArrayList<CloseListener> closeListeners = new ArrayList<>();
    private boolean initialized = false;
    private boolean opened = false;
    private UI futureUI = null;

    public HWindow() {
        super();
    }

    public void setFutureUI(UI ui) {
        this.futureUI = ui;
    }

    protected void fireOpen() {
        if (!initialized) {
            initialized = true;
            fireEvent(new WindowEvent.Init(this));
        }
        fireEvent(new WindowEvent.Open(this));
    }

    @Override
    protected void fireClose() {
        this.fireEvent(new WindowEvent.Close(this));
    }

    @Override
    public void setParent(HasComponents parent) {
        super.setParent(parent);

        if (getParent() != null) {
            opened = true;
        }
    }

    public void open() {
        if (!opened && futureUI != null) {
            futureUI.addWindow(this);
            fireOpen();
        }
    }

    @Override
    public void close() {
        super.close();
        opened = false;
    }

    public boolean isOpened() {
        return opened;
    }

    /**
     * Add an init listener to the component. The listener is called when the
     * window is opened for the first time.
     * <p>
     * Use {@link #removeInitListener(InitListener)} to remove the listener.
     *
     * @param listener The listener to add
     */
    public void addInitListener(InitListener listener) {
        addListener(WindowEvent.Init.class, listener, WINDOW_INIT_METHOD);
    }

    /**
     * Remove an init listener from the component. The listener should earlier
     * have been added using {@link #addInitListener(InitListener)}.
     *
     * @param listener The listener to remove
     */
    public void removeInitListener(InitListener listener) {
        removeListener(WindowEvent.Init.class, listener, WINDOW_INIT_METHOD);
    }

    /**
     * Add an open listener to the component. The listener is called whenever
     * the window is opened.
     * <p>
     * Use {@link #removeOpenListener(OpenListener)} to remove the listener.
     *
     * @param listener The listener to add
     */
    public void addOpenListener(OpenListener listener) {
        addListener(WindowEvent.Open.class, listener, WINDOW_OPEN_METHOD);
    }

    /**
     * Remove an open listener from the component. The listener should earlier
     * have been added using {@link #addOpenListener(OpenListener)}.
     *
     * @param listener The listener to remove
     */
    public void removeOpenListener(OpenListener listener) {
        removeListener(WindowEvent.Open.class, listener, WINDOW_OPEN_METHOD);
    }

    /**
     * Add a close listener to the component. The listener is called whenever
     * the window is closed.
     * <p>
     * Use {@link #removeCloseListener(CloseListener)} to remove the listener.
     *
     * @param listener The listener to add
     */
    public void addCloseListener(CloseListener listener) {
        addListener(WindowEvent.Close.class, listener, WINDOW_CLOSE_METHOD);
        closeListeners.add(listener);
    }

    /**
     * Remove a close listener from the component. The listener should earlier
     * have been added using {@link #addCloseListener(CloseListener)}.
     *
     * @param listener The listener to remove
     */
    public void removeCloseListener(CloseListener listener) {
        removeListener(WindowEvent.Close.class, listener, WINDOW_CLOSE_METHOD);
        closeListeners.remove(listener);
    }

    public void removeAllCloseListeners() {
        for (CloseListener listener : closeListeners) {
            removeCloseListener(listener);
        }
        closeListeners.clear();
    }

    /**
     * Interface for listening for a {@link WindowEvent.Init} fired by a {@link org.hypothesis.components.ui.HWindow}
     * when user opens the window for the first time.
     *
     * @see WindowEvent.Init
     */
    public interface InitListener extends Serializable {

        /**
         * Called when the user opens a window for first time. A reference to
         * the window is given by {@link WindowEvent.Init#getWindow()}.
         *
         * @param event An event containing information about the window.
         */
        void initWindow(WindowEvent.Init event);
    }

    /**
     * Interface for listening for a {@link WindowEvent.Open} fired by a {@link org.hypothesis.components.ui.HWindow}
     * whenever the user opens the window.
     *
     * @see WindowEvent.Open
     */
    public interface OpenListener extends Serializable {

        /**
         * Called whenever the user opens a window. A reference to the window is
         * given by {@link WindowEvent.Open#getWindow()}.
         *
         * @param event An event containing information about the window.
         */
        void openWindow(WindowEvent.Open event);
    }

    /**
     * Interface for listening for a {@link WindowEvent.Close} fired by a {@link org.hypothesis.components.ui.HWindow}
     * whenever the user closes the window.
     *
     * @see WindowEvent.Close
     */
    public interface CloseListener extends Serializable {

        /**
         * Called whenever the user closes a window. A reference to the window is
         * given by {@link WindowEvent.Close#getWindow()}.
         *
         * @param event An event containing information about the window.
         */
        void closeWindow(WindowEvent.Close event);
    }

    public static abstract class WindowEvent extends Event {

        private final LocalDateTime timestamp;

        protected WindowEvent(Component source) {
            super(source);
            this.timestamp = LocalDateTime.now();
        }

        /**
         * Gets the Window.
         *
         * @return the window.
         */
        public Window getWindow() {
            return (Window) getSource();
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        /**
         * Class for holding information about a window init event. An
         * {@link Init} is fired when the <code>Window</code> is opened for the
         * first time.
         *
         * @see InitListener
         */
        public static class Init extends WindowEvent {

            public Init(Component source) {
                super(source);
            }
        }

        /**
         * Class for holding information about a window open event. An
         * {@link Open} is fired whenever the <code>Window</code> is opened.
         *
         * @see OpenListener
         */
        public static class Open extends WindowEvent {

            public Open(Component source) {
                super(source);
            }
        }

        /**
         * Class for holding information about a window close event. An
         * {@link Close} is fired whenever the <code>Window</code> is closed.
         *
         * @see CloseListener
         */
        public static class Close extends WindowEvent {

            public Close(Component source) {
                super(source);
            }
        }
    }

}
