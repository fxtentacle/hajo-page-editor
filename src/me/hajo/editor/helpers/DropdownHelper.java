package me.hajo.editor.helpers;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class DropdownHelper {
	public static interface DropdownCallback {
		public void OnSelect(String key);
	}

	public static class DropdownEntry {
		String key;
		String icon;
		String title;

		public DropdownEntry(String key, String icon, String title) {
			super();
			this.key = key;
			this.icon = icon;
			this.title = title;
		}

		public DropdownEntry(String key, String icon) {
			super();
			this.key = key;
			this.icon = icon;
			this.title = key;
		}
	}

	public static void makeDropdown(final HajoToolbar.Group changeType, String title, List<DropdownEntry> entries, final DropdownCallback cb) {
		final ListPanel typeDropdown = new ListPanel();
		typeDropdown.setStyleName("dropdown-menu");

		for (final DropdownEntry cur : entries) {
			typeDropdown.add(new LinkWithIcon(cur.icon, cur.title, "", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// close drop-down
					changeType.setStyleName("btn-group");

					cb.OnSelect(cur.key);
				}
			}));
		}

		changeType.addCustom("type_dropdown", new LinkButton("caret", title, "dropdown-toggle", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean open = !changeType.getStyleName().contains("open");
				changeType.setStyleName("btn-group" + (open ? " open" : ""));
			}
		}));
		changeType.add(typeDropdown);
	}
}
