package org.hypothesis.components.shared;

import com.vaadin.shared.EventId;

/**
 * @author kamil
 */
public interface HEventId extends EventId {

    String DOUBLE_CLICK_EVENT_IDENTIFIER = "doubleClick";
    String LOAD_EVENT_IDENTIFIER = "load";
    String ERROR_EVENT_IDENTIFIER = "error";
    String INIT_EVENT_IDENTIFIER = "init";
    String OPEN_EVENT_IDENTIFIER = "open";

}
