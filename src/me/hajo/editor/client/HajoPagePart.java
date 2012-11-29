package me.hajo.editor.client;

import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;

public interface HajoPagePart {
	public interface ImageRescaleCollector {
		public String addRequest(String imageID, String filename, int width);
	}

	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc);

	public PagePartStorage serialize();

	public void deserialize(PagePartStorage storage);

	public HajoToolbar getToolbar();

	public void insertEditor(FlowPanel target);
}
