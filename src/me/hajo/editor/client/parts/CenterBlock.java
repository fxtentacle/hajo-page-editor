package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CenterBlock extends BlockBase implements HajoPagePart {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<td width=\"{0}\" style=\"vertical-align: top;\">")
		SafeHtml mktd(String width);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	private HajoPage center;

	int currentSplit = 4;

	public CenterBlock(final HajoPage page) {
		super(page, "Center");

		center = new HajoPage(-1);
		PartRegistry.addWidget(center,"Text");
		Style s = center.getElement().getStyle();
		s.setProperty("margin", "0px auto");
		content.add(center);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateSplit();
			}
		});
	}

	private void updateSplit() {
		final double percent = (100.0 * (double) currentSplit / 8.0);
		center.getElement().getStyle().setWidth(percent, Unit.PCT);
		updatePageWidth(getWidth());
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		int width = getWidth();
		updatePageWidth(width);
		final int padw = (width - center.pageWidth) / 2;

		shb.appendHtmlConstant("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		shb.appendHtmlConstant("<tr>");

		shb.append(TEMPLATES.mktd("" + padw + "px"));
		shb.appendHtmlConstant("</td>");

		shb.append(TEMPLATES.mktd("" + center.pageWidth + "px"));
		center.encode(shb, irc);
		shb.appendHtmlConstant("</td>");

		shb.append(TEMPLATES.mktd("" + padw + "px"));
		shb.appendHtmlConstant("</td>");

		shb.appendHtmlConstant("</tr>");
		shb.appendHtmlConstant("</table>");
	}

	public void updatePageWidth(int width) {
		final int w = (int) ((double) width * (double) currentSplit / 8.0);
		center.setPageWidth(w);
	}

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Center");
		pps.Width = getWidth();
		pps.Split = currentSplit;
		pps.Children = new ArrayList<PagePartStorage>();
		pps.Children.add(center.serialize());
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		currentSplit = pps.Split;
		center = new HajoPage(-1);
		updatePageWidth(pps.Width);
		center.deserialize(pps.Children.get(0));
	}

	@Override
	public HajoToolbar getToolbar() {
		HajoToolbar toolbar = super.getToolbar();

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		for (int i = 2; i < 8; i += 2)
			entries.add(new DropdownEntry("" + i, "", "" + i + "/8"));

		int sel = DropdownHelper.findSelection(entries, Integer.toString(currentSplit));
		DropdownHelper.makeDropdown(toolbar.addGroup(), "Split at ", true, entries, sel, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentSplit = Integer.parseInt(key);
				updateSplit();
			}
		});

		return toolbar;
	}
}
