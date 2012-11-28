package me.hajo.editor.helpers;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LIWrapper extends SimplePanel {
	public LIWrapper() {
		super(Document.get().createLIElement());
	}

	public LIWrapper(Widget w) {
		super(Document.get().createLIElement());
		setWidget(w);
	}

}