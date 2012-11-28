package me.hajo.editor.helpers;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public class HajoToolbar extends FlowPanel {
	public HajoToolbar() {
		setStyleName("btn-toolbar");
	}

	public class Group extends FlowPanel {
		public Group() {
			setStyleName("btn-group");
		}

		public <T extends Widget & HasEnabled> T addCustom(String id, T addme) {
			add(addme);
			id2button.put(id, addme);
			return addme;
		}
	}

	Map<String, HasEnabled> id2button = new HashMap<String, HasEnabled>();

	public Group addGroup() {
		Group addme = new Group();
		add(addme);
		return addme;
	}

	public <T extends Widget & HasEnabled> void addCustom(String id, T addme) {
		add(addme);
		id2button.put(id, addme);
	}

	public void enable(String id) {
		id2button.get(id).setEnabled(true);
	}

	public void disable(String id) {
		id2button.get(id).setEnabled(false);
	}

	public void enableIf(String string, boolean b) {
		if (b)
			enable(string);
		else
			disable(string);
	}
}
