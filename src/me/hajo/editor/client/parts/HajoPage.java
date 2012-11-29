package me.hajo.editor.client.parts;

import java.util.ArrayList;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class HajoPage extends FlowPanel implements HajoPagePart {

	public int pageWidth = -1;

	public HajoPage(int pageWidth) {
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

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Page");
		pps.Children = new ArrayList<PagePartStorage>();
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof HajoPagePart) {
				pps.Children.add(((HajoPagePart) w).serialize());
			}
		}
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage storage) {
		clear();
		for (PagePartStorage cur : storage.Children) {
			HajoPagePart w = BlockBase.createWidgetOfType(cur.Type, this);
			w.deserialize(cur);
			add((Widget) w);
		}
	}

	@Override
	public HajoToolbar getToolbar() {
		return new HajoToolbar();
	}

	@Override
	public void insertEditor(FlowPanel target) {
	}

	public void selectItem(BlockBase item) {
		Widget w = getParent();
		while (w != null) {
			if (w instanceof BlockBase) {
				((BlockBase) w).page.selectItem(item);
				return;
			}
			w = w.getParent();
		}
	}
}
