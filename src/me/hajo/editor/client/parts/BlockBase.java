package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.HajoToolbar.Group;
import me.hajo.editor.helpers.LinkButton;

import com.gargoylesoftware.htmlunit.javascript.host.css.ComputedCSSStyleDeclaration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class BlockBase extends Composite implements HajoPagePart {
	private static BlockBaseUiBinder uiBinder = GWT.create(BlockBaseUiBinder.class);

	interface BlockBaseUiBinder extends UiBinder<Widget, BlockBase> {
	}

	@UiField
	FlowPanel border;

	@UiField
	protected FlowPanel content;

	@UiField
	FlowPanel toolbarContainer;

	protected HajoToolbar toolbar = new HajoToolbar();

	protected FlowPanel page;

	public BlockBase(final FlowPanel page) {
		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		entries.add(new DropdownEntry("Text", ""));
		entries.add(new DropdownEntry("Image", ""));
		entries.add(new DropdownEntry("Split", ""));
		entries.add(new DropdownEntry("Center", ""));

		this.page = page;
		initWidget(uiBinder.createAndBindUi(this));
		toolbarContainer.add(toolbar);
		Style borderStyle = border.getElement().getStyle();
		borderStyle.setBorderColor("#000000");
		borderStyle.setBorderWidth(1, Unit.PX);
		borderStyle.setBorderStyle(BorderStyle.DOTTED);
		borderStyle.setMarginBottom(5, Unit.PX);

		Group upDown = toolbar.addGroup();
		// upDown.setStyleName("btn-group-vertical");
		upDown.addCustom("sort-up", new LinkButton("icon-sort-up", "", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = page.getWidgetIndex(BlockBase.this);
				page.remove(BlockBase.this);
				page.insert(BlockBase.this, Math.max(idx - 1, 0));
			}
		}));

		upDown.addCustom("sort-down", new LinkButton("icon-sort-down", "", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int count = page.getWidgetCount();
				int idx = page.getWidgetIndex(BlockBase.this);
				page.remove(BlockBase.this);
				page.insert(BlockBase.this, Math.min(idx + 1, count - 1));
			}
		}));

		DropdownHelper.makeDropdown(toolbar.addGroup(), "Change type", entries, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				int idx = page.getWidgetIndex(BlockBase.this);
				page.remove(BlockBase.this);
				Widget newWidget = convertToType(key, BlockBase.this);
				page.insert(newWidget, idx);
			}
		});
	}

	protected Widget convertToType(String type, BlockBase old) {
		if (type.equals("Text")) {
			return new TextBlock(page);
		}
		return null;
	}

	@Override
	public void encode(SafeHtmlBuilder shb) {
	}

	public static class StyledItem {
		SafeHtml open;
		SafeHtml close;

		public StyledItem(SafeHtml open, SafeHtml close) {
			super();
			this.open = open;
			this.close = close;
		}
	}

	protected StyledItem getStyledItemInContext(String tag, String context) {
		String stexName = "stex_" + context.replaceAll("[^a-zA-Z]", "_") + "_" + tag;
		Element stex = DOM.getElementById(stexName);

		if (stex == null)
			return new StyledItem(SafeHtmlUtils.fromTrustedString("<" + tag + ">"), SafeHtmlUtils.fromTrustedString("</" + tag + ">"));

		String safeStyleString = stex.getStyle().getProperty("cssText");
		return new StyledItem(SafeHtmlUtils.fromTrustedString("<" + stex.getTagName() + " style=\"" + safeStyleString + "\">"), SafeHtmlUtils.fromTrustedString("</" + stex.getTagName() + ">"));
	}
}
