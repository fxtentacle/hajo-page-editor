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
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Label;

public class SplitBlock extends BlockBase implements HajoPagePart {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<td width=\"{0}\" style=\"vertical-align: top;\">")
		SafeHtml mktd(String width);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	private HajoPage left;
	private HajoPage right;

	int currentSplit = 3;
	double currentPadding = -0.5;

	public SplitBlock(final HajoPage page) {
		super(page, 2);

		left = new HajoPage(-1);
		left.add(new TextBlock(left));

		right = new HajoPage(-1);
		right.add(new TextBlock(right));

		left.getElement().getStyle().setFloat(Float.LEFT);
		right.getElement().getStyle().setFloat(Float.RIGHT);

		Label clear = new Label(" ");
		clear.getElement().getStyle().setClear(Clear.BOTH);

		content.add(left);
		content.add(right);
		content.add(clear);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateSplit();
			}
		});
	}

	private void updateSplit() {
		final double percentL, percentR;
		if (currentPadding > 0) {
			percentL = (100.0 * (double) currentSplit / 8.0);
			percentR = 100 - (100.0 * (double) (currentSplit + currentPadding) / 8.0);
		} else {
			percentL = (100.0 * (double) (currentSplit + currentPadding) / 8.0);
			percentR = 100 - (100.0 * (double) currentSplit / 8.0);
		}
		left.getElement().getStyle().setWidth(percentL, Unit.PCT);
		right.getElement().getStyle().setWidth(percentR, Unit.PCT);

		updatePageWidth(getWidth());
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		int width = getWidth();
		updatePageWidth(width);
		final int padw = width - left.pageWidth - right.pageWidth;

		shb.appendHtmlConstant("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		shb.appendHtmlConstant("<tr>");

		shb.append(TEMPLATES.mktd("" + left.pageWidth + "px"));
		left.encode(shb, irc);
		shb.appendHtmlConstant("</td>");

		if (padw > 1) {
			shb.append(TEMPLATES.mktd("" + padw + "px"));
			shb.appendHtmlConstant("</td>");
		}

		shb.append(TEMPLATES.mktd("" + right.pageWidth + "px"));
		right.encode(shb, irc);
		shb.appendHtmlConstant("</td>");

		shb.appendHtmlConstant("</tr>");
		shb.appendHtmlConstant("</table>");
	}

	public void updatePageWidth(int width) {
		final int lw, rw;
		if (currentPadding > 0) {
			lw = (int) ((double) width * (double) currentSplit / 8.0);
			rw = width - (int) ((double) width * (double) (currentSplit + currentPadding) / 8.0);
		} else {
			lw = (int) ((double) width * (double) (currentSplit + currentPadding) / 8.0);
			rw = width - (int) ((double) width * (double) currentSplit / 8.0);
		}
		left.setPageWidth(lw);
		right.setPageWidth(rw);
	}

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Split");
		pps.Width = getWidth();
		pps.Split = currentSplit;
		pps.Padding = currentPadding;
		pps.Children = new ArrayList<PagePartStorage>();
		pps.Children.add(left.serialize());
		pps.Children.add(right.serialize());
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		currentSplit = pps.Split;
		currentPadding = pps.Padding;
		left.clear();
		right.clear();
		updatePageWidth(pps.Width);
		left.deserialize(pps.Children.get(0));
		right.deserialize(pps.Children.get(1));

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateSplit();
			}
		});
	}

	@Override
	public HajoToolbar getToolbar() {
		HajoToolbar toolbar = super.getToolbar();

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		for (int i = 1; i < 8; i++)
			entries.add(new DropdownEntry("" + i, "", "" + i + "/8"));

		int sel = DropdownHelper.findSelection(entries, Integer.toString(currentSplit));
		DropdownHelper.makeDropdown(toolbar.addGroup(), "Split at ", true, entries, sel, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentSplit = Integer.parseInt(key);
				updateSplit();
			}
		});

		entries = new ArrayList<DropdownEntry>();
		entries.add(new DropdownEntry("0", "", "Off"));
		entries.add(new DropdownEntry("-0.5", "", "Left: 1/2"));
		entries.add(new DropdownEntry("-1", "", "Left: 1"));
		entries.add(new DropdownEntry("-2", "", "Left: 2"));
		entries.add(new DropdownEntry("0.5", "", "Right: 1/2"));
		entries.add(new DropdownEntry("1", "", "Right: 1"));
		entries.add(new DropdownEntry("2", "", "Right: 2"));

		sel = DropdownHelper.findSelection(entries, Double.toString(currentPadding));
		DropdownHelper.makeDropdown(toolbar.addGroup(), "Padding: ", true, entries, sel, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentPadding = Double.parseDouble(key);
				updateSplit();
			}
		});

		return toolbar;
	}
}
