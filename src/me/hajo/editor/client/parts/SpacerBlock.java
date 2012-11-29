package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;

public class SpacerBlock extends BlockBase implements HajoPagePart {
	HTML html = new HTML();

	int currentSplit = 1;

	public SpacerBlock(final HajoPage page) {
		super(page, "Spacer");
		content.add(html);

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

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Spacer");
		pps.Split = currentSplit;
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		currentSplit = pps.Split;
		updateSplit();
	}

	@Override
	public HajoToolbar getToolbar() {
		HajoToolbar toolbar = super.getToolbar();

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		for (int i = 1; i < 8; i++)
			entries.add(new DropdownEntry("" + i, "", "" + i + " line" + (i > 1 ? "s" : "")));

		int sel = DropdownHelper.findSelection(entries, Integer.toString(currentSplit));
		DropdownHelper.makeDropdown(toolbar.addGroup(), "", true, entries, sel, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentSplit = Integer.parseInt(key);
				updateSplit();
			}
		});

		return toolbar;
	}
}
