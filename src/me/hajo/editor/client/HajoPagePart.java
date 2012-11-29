package me.hajo.editor.client;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public interface HajoPagePart {
	public interface ImageRescaleCollector {
		public String addRequest(String fullURL, int width);
	}

	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc);
}
