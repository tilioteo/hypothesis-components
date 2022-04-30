package org.hypothesis.components.ui;

import com.vaadin.ui.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kamil Morong - Hypothesis
 */
public abstract class MultipleComponentPanel<C extends AbstractComponent> extends Panel {

    protected final List<C> childList = new LinkedList<>();
    private Orientation orientation = Orientation.Horizontal;
    private String childWidth = null;
    private String childHeight = null;
    private String childStyle = null;
    private String oldChildStyle = null;

    protected MultipleComponentPanel() {
        // nop
    }

    protected void addChild(C child) {
        childList.add(child);
    }

    protected void removeChild(C child) {
        childList.remove(child);
    }

    protected void removeAllChildren() {
        childList.clear();
    }

    public int getChildIndex(C child) {
        int index = 0;
        for (C child2 : childList) {
            if (child2.equals(child))
                return index;

            ++index;
        }
        return -1;
    }

    protected Iterator<C> getChildIterator() {
        return childList.iterator();
    }

    public String getChildrenHeight() {
        return childHeight;
    }

    public void setChildrenHeight(String height) {
        this.childHeight = height;
        markAsDirty();
    }

    public String getChildrenWidth() {
        return childWidth;
    }

    public void setChildrenWidth(String width) {
        this.childWidth = width;
        markAsDirty();
    }

    public String getChildrenStyle() {
        return childStyle;
    }

    public void setChildrenStyle(String style) {
        this.oldChildStyle = childStyle;
        this.childStyle = style;
        markAsDirty();
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        updateContent();
    }

    private void setChildHeight(C child) {
        child.setHeight(childHeight);
    }

    private void setChildWidth(C child) {
        child.setWidth(childWidth);
    }

    private void setChildStyle(C child) {
        if (oldChildStyle != null) {
            child.removeStyleName(oldChildStyle);
        }

        child.addStyleName(childStyle);
    }

    public void updateContent() {
        AbstractOrderedLayout layout = (AbstractOrderedLayout) getContent();
        AbstractOrderedLayout newLayout = null;

        if (orientation.equals(Orientation.Horizontal) &&
                (null == layout || layout instanceof VerticalLayout)) {
            newLayout = new HorizontalLayout();
        } else if (orientation.equals(Orientation.Vertical) &&
                (null == layout || layout instanceof HorizontalLayout)) {
            newLayout = new VerticalLayout();
        }

        if (layout != null) {
            layout.removeAllComponents();
        }
        if (newLayout != null) {
            setContent(newLayout);
            newLayout.setSizeFull();

            layout = newLayout;
        }

        for (C child : childList) {
            setChildWidth(child);
            setChildHeight(child);
            setChildStyle(child);

            if (layout != null) {
                layout.addComponent(child);
                layout.setComponentAlignment(child, Alignment.MIDDLE_CENTER);
            }
        }
        markAsDirty();
    }

    public enum Orientation {
        Vertical, Horizontal
    }

}
