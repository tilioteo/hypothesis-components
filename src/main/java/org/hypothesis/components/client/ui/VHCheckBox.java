package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * @author kamil
 */
public class VHCheckBox extends SelectButton {

    public static final String CLASSNAME = "v-checkbox";

    @Override
    protected Element createInputElement() {
        return DOM.createInputCheck();
    }

}
