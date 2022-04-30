package org.hypothesis.components.client.ui.shortcutkey;

import com.vaadin.shared.ui.Connect;
import org.hypothesis.components.client.ui.AbstractNonVisualComponentConnector;
import org.hypothesis.components.client.ui.VHShortcutKey;
import org.hypothesis.components.ui.HShortcutKey;

/**
 * @author kamil
 */
@Connect(HShortcutKey.class)
public class HShortcutKeyConnector extends AbstractNonVisualComponentConnector {

    @Override
    public VHShortcutKey getWidget() {
        return (VHShortcutKey) super.getWidget();
    }

}
