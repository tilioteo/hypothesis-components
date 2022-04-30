package org.hypothesis.components.shared.ui.keyaction;

import com.vaadin.shared.communication.SharedState;

/**
 * Copied from https://github.com/Artur-/KeyActions
 * <p>
 * modified by:
 *
 * @author kamil
 */
public class HKeyActionState extends SharedState {

    public int keyCode;
    public boolean shift, ctrl, alt, meta;
    public boolean stopPropagation = false;
    public boolean preventDefault = false;

}
