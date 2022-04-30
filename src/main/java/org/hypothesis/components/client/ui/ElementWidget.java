package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kamil Morong - Hypothesis
 */
public class ElementWidget extends Widget {

    public ElementWidget(Element element) {
        super();
        setElement(element);
    }

}
