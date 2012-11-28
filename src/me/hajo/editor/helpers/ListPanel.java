package me.hajo.editor.helpers;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListPanel extends ComplexPanel {
	public ListPanel() {
		setElement(Document.get().createULElement());
	}

	@Override
	public void add(Widget w) {
		if (w instanceof LIWrapper)
			super.add(w, getElement());
		else
			super.add(new LIWrapper(w), getElement());
	}
}
