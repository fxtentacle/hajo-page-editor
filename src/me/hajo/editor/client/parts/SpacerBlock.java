package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.client.parts.BlockBase.StyledItem;
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
import com.google.gwt.user.client.ui.HTML;

public class SpacerBlock extends BlockBase implements HajoPagePart {
	HTML html = new HTML();

	int currentSplit = 1;

	public SpacerBlock(final FlowPanel page) {
		super(page, 4);
		content.add(html);

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		for (int i = 1; i < 8; i++)
			entries.add(new DropdownEntry("" + i, "", "" + i + " line" + (i > 1 ? "s" : "")));

		DropdownHelper.makeDropdown(toolbar.addGroup(), "", true, entries, 0, new DropdownCallback() {
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
		SafeHtmlBuilder shb = new SafeHtmlBuilder();
		fill(shb);
		html.setHTML(shb.toSafeHtml());
	}

	public void fill(SafeHtmlBuilder shb) {
		StyledItem spacer = getStyledItemInContext("div", "spacer" + currentSplit);
		shb.append(spacer.open);
		for (int i = 0; i < currentSplit; i++)
			shb.appendHtmlConstant("<br/>");
		shb.append(spacer.close);
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		fill(shb);
	}
}
