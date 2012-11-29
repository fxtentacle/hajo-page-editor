package me.hajo.editor.helpers;

import java.util.List;

import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

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

	public static LinkButton makeDropdown(final HajoToolbar.Group changeType, final String title, final boolean renameDropdownWithValue, List<DropdownEntry> entries, int selectedEntry, final DropdownCallback cb) {
		final LinkButton returnme;

		final ListPanel typeDropdown = new ListPanel();
		typeDropdown.setStyleName("dropdown-menu");

		boolean haveSelectedEntry = renameDropdownWithValue && selectedEntry >= 0;
		String icon = haveSelectedEntry ? entries.get(selectedEntry).icon : "";
		SafeHtml html = SafeHtmlUtils.fromSafeConstant(title + (haveSelectedEntry ? entries.get(selectedEntry).title : "") + " <span class=\"caret\"></span>");
		changeType.addCustom("makeDropdown", returnme = new LinkButton(icon, html, "dropdown-toggle", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean open = !changeType.getStyleName().contains("open");
				changeType.setStyleName("btn-group" + (open ? " open" : ""));
			}
		}));

		for (final DropdownEntry cur : entries) {
			typeDropdown.add(new LinkWithIcon(cur.icon, cur.title, "", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// close drop-down
					changeType.setStyleName("btn-group");
					if (renameDropdownWithValue)
						returnme.replaceIconHtml(cur.icon, SafeHtmlUtils.fromSafeConstant(title + cur.title + " <span class=\"caret\"></span>"));

					cb.OnSelect(cur.key);
				}
			}));
		}
		changeType.add(typeDropdown);

		return returnme;
	}

	public static int findSelection(List<DropdownEntry> entries, String currentValue) {
		for (int i = 0; i < entries.size(); i++) {
			if(entries.get(i).key.equals(currentValue)) return i;
		}
		return -1;
	}
}
