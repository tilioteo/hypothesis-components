package org.hypothesis.components.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 */
public class VHShortcutKey extends Widget {

    public static final String CLASSNAME = "v-shortcutkey";

    public VHShortcutKey() {
        setElement(Document.get().createDivElement());
        setStyleName(CLASSNAME);
        setVisible(false);
    }

}
