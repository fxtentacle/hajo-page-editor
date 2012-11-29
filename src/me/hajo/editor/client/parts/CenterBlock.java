package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;

public class CenterBlock extends BlockBase implements HajoPagePart {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<td width=\"{0}\" style=\"vertical-align: top;\">")
		SafeHtml mktd(String width);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	private HajoPage center;

	int currentSplit = 4;

	public CenterBlock(final FlowPanel page) {
		super(page, 3);

		center = new HajoPage(page, -1);
		center.add(new AddBlockButton(center));
		Style s = center.getElement().getStyle();
		s.setProperty("margin", "0px auto");
		content.add(center);

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		for (int i = 2; i < 8; i += 2)
			entries.add(new DropdownEntry("" + i, "", "" + i + "/8"));

		DropdownHelper.makeDropdown(toolbar.addGroup(), "Split at ", true, entries, 1, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentSplit = Integer.parseInt(key);
				updateSplit();
			}
		});

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
}
