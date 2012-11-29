package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.HajoToolbar.Group;
import me.hajo.editor.helpers.LinkButton;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class BlockBase extends Composite implements HajoPagePart {
	private static BlockBaseUiBinder uiBinder = GWT.create(BlockBaseUiBinder.class);

	interface BlockBaseUiBinder extends UiBinder<Widget, BlockBase> {
	}

	@UiField
	FocusPanel border;

	@UiField
	protected FlowPanel content;

	protected HajoPage page;

	protected int currentType;

	public BlockBase(final HajoPage page, final int selectedType) {
		this.page = page;
		this.currentType = selectedType;
		initWidget(uiBinder.createAndBindUi(this));
		Style borderStyle = border.getElement().getStyle();
		borderStyle.setBorderColor("#000000");
		borderStyle.setBorderWidth(1, Unit.PX);
		borderStyle.setBorderStyle(BorderStyle.DOTTED);
		borderStyle.setMarginBottom(5, Unit.PX);
		content.getElement().getStyle().setPadding(5, Unit.PX);
		border.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				page.selectItem(BlockBase.this);
			}
		});
	}

	public static HajoPagePart createWidgetOfType(String type, HajoPage page) {
		if (type.equals("Text")) {
			return new TextBlock(page);
		} else if (type.equals("Image")) {
			return new ImageBlock(page);
		} else if (type.equals("Split")) {
			return new SplitBlock(page);
		} else if (type.equals("Center")) {
			return new CenterBlock(page);
		} else if (type.equals("Spacer")) {
			return new SpacerBlock(page);
		} else {
			return new BlockBase(page, -1);
		}
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
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

	protected int getWidth() {
		Widget p = getParent();
		if (p instanceof HajoPage) {
			int hpw = ((HajoPage) p).pageWidth;
			if (hpw > 0)
				return hpw;
		}
		return content.getOffsetWidth() + 2;
	}

	protected StyledItem getStyledItemInContext(String tag, String context) {
		return getStyledItemInContext(tag, context, "");
	}

	protected StyledItem getStyledItemInContext(String tag, String context, String additionalAttrbutes) {
		String stexName = "stex_" + context.replaceAll("[^a-zA-Z]", "_") + "_" + tag;
		com.google.gwt.dom.client.Element stex = DOM.getElementById(stexName);

		if (stex == null)
			return new StyledItem(SafeHtmlUtils.fromTrustedString("<" + tag + " " + additionalAttrbutes + " >"), SafeHtmlUtils.fromTrustedString("</" + tag + ">"));

		tag = stex.getTagName();
		String safeStyleString = "";
		while (stex != null) {
			if (stex.getId().equals("stex"))
				break;
			safeStyleString = stex.getStyle().getProperty("cssText") + ";" + safeStyleString;
			stex = stex.getParentElement();
		}
		return new StyledItem(SafeHtmlUtils.fromTrustedString("<" + tag + " style=\"" + safeStyleString + "\" " + additionalAttrbutes + " >"), SafeHtmlUtils.fromTrustedString("</" + tag + ">"));
	}

	@Override
	public PagePartStorage serialize() {
		return new PagePartStorage("Base");
	}

	@Override
	public void deserialize(PagePartStorage storage) {
	}

	@Override
	public HajoToolbar getToolbar() {
		HajoToolbar toolbar = new HajoToolbar();

		Group upDown = toolbar.addGroup();
		// upDown.setStyleName("btn-group-vertical");
		upDown.addCustom("sort-up", new LinkButton("icon-sort-up", "", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = page.getWidgetIndex(BlockBase.this);
				page.remove(BlockBase.this);
				page.insert(BlockBase.this, Math.max(idx - 1, 0));
				border.setFocus(true);
			}
		}));

		upDown.addCustom("sort-down", new LinkButton("icon-sort-down", "", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int count = page.getWidgetCount();
				int idx = page.getWidgetIndex(BlockBase.this);
				page.remove(BlockBase.this);
				page.insert(BlockBase.this, Math.min(idx + 1, count - 1));
				border.setFocus(true);
			}
		}));

		upDown.addCustom("add", new LinkButton("icon-plus", "Add", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int count = page.getWidgetCount();
				int idx = page.getWidgetIndex(BlockBase.this);
				int idx2 = Math.min(idx + 1, count - 1);
				HajoPagePart addme = createWidgetOfType("Text", page);
				page.insert((Widget) addme, idx2);
				if (addme instanceof BlockBase)
					((BlockBase) addme).border.setFocus(true);
			}
		}));

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		entries.add(new DropdownEntry("Text", "icon-pencil"));
		entries.add(new DropdownEntry("Image", "icon-picture"));
		entries.add(new DropdownEntry("Split", "icon-columns"));
		entries.add(new DropdownEntry("Center", "icon-align-center"));
		entries.add(new DropdownEntry("Spacer", "icon-resize-vertical"));
		entries.add(new DropdownEntry("Delete", "icon-trash"));

		DropdownHelper.makeDropdown(toolbar.addGroup(), "Type: ", true, entries, currentType, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				int idx = page.getWidgetIndex(BlockBase.this);
				page.remove(BlockBase.this);
				HajoPagePart newWidget = createWidgetOfType(key, page);
				page.insert((Widget) newWidget, idx);
				if (newWidget instanceof BlockBase)
					((BlockBase) newWidget).border.setFocus(true);
			}
		});

		return toolbar;
	}

	@Override
	public void insertEditor(FlowPanel target) {
	}
}
