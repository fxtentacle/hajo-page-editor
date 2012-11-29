package me.hajo.editor.client.parts;

import me.hajo.editor.client.HajoPagePart;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class HajoPage extends FlowPanel implements HajoPagePart {

	public int pageWidth;

	public HajoPage(final FlowPanel page, int pageWidth) {
		this.pageWidth = pageWidth;
		setStyleName("hajopage");
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof HajoPagePart) {
				((HajoPagePart) w).encode(shb, irc);
			}
		}
	}
}
