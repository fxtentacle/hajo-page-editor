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
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class SplitBlock extends BlockBase implements HajoPagePart {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<td width=\"{0}\">")
		SafeHtml mktd(String width);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	private HajoPage left;
	private HajoPage right;

	int currentSplit = 4;

	public SplitBlock(final FlowPanel page) {
		super(page, 2);

		left = new HajoPage(page);
		left.add(new AddBlockButton(left));

		right = new HajoPage(page);
		right.add(new AddBlockButton(right));

		left.getElement().getStyle().setFloat(Float.LEFT);
		right.getElement().getStyle().setFloat(Float.RIGHT);

		Label clear = new Label(" ");
		clear.getElement().getStyle().setClear(Clear.BOTH);

		content.add(left);
		content.add(right);
		content.add(clear);

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		for (int i = 1; i < 8; i++)
			entries.add(new DropdownEntry("" + i, "", "" + i + "/8"));

		DropdownHelper.makeDropdown(toolbar.addGroup(), "Split at ", true, entries, 3, new DropdownCallback() {
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

	int lastWidth = -1;

	private void updateSplit() {
		int width = content.getOffsetWidth();
		if (width == 0)
			width = lastWidth;
		else
			lastWidth = width;

		int percent = (int) (100.0 * (double) currentSplit / 8.0);
		left.getElement().getStyle().setWidth(percent, Unit.PCT);
		right.getElement().getStyle().setWidth(100 - percent, Unit.PCT);
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		int lw = (int) ((double) lastWidth * (double) currentSplit / 8.0);

		shb.appendHtmlConstant("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		shb.appendHtmlConstant("<tr>");

		shb.append(TEMPLATES.mktd("" + lw + "px"));
		left.encode(shb, irc);
		shb.appendHtmlConstant("</td>");

		shb.append(TEMPLATES.mktd("" + (lastWidth - lw) + "px"));
		right.encode(shb, irc);
		shb.appendHtmlConstant("</td>");

		shb.appendHtmlConstant("</tr>");
		shb.appendHtmlConstant("</table>");
	}
}
