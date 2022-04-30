package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * @author kamil
 */
public class VHRadioButton extends SelectButton {

    public static final String CLASSNAME = "v-radiobutton";

    @Override
    protected Element createInputElement() {
        return DOM.createInputRadio("");
    }

}
