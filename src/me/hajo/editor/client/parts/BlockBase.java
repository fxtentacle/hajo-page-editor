package me.hajo.editor.client.parts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.hajo.editor.client.GuiContainer;
import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.HajoToolbar.Group;
import me.hajo.editor.helpers.LIWrapper;
import me.hajo.editor.helpers.LinkButton;
import me.hajo.editor.helpers.LinkWithIcon;
import me.hajo.editor.helpers.ListPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

	private Group changeType;

	public BlockBase(final FlowPanel page) {
		Map<String, String> type2icon = new HashMap<String, String>();
		type2icon.put("Text", "");
		type2icon.put("Image", "");
		type2icon.put("Split", "");
		type2icon.put("Center", "");

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

		changeType = toolbar.addGroup();
		final ListPanel typeDropdown = new ListPanel();
		typeDropdown.setStyleName("dropdown-menu");

		for (final Entry<String, String> cur : type2icon.entrySet()) {
			typeDropdown.add(new LinkWithIcon(cur.getValue(), cur.getKey(), "", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// close drop-down
					changeType.setStyleName("btn-group");

					int idx = page.getWidgetIndex(BlockBase.this);
					page.remove(BlockBase.this);
					Widget newWidget = convertToType(cur.getKey(), BlockBase.this);
					page.insert(newWidget, idx);
				}
			}));
		}

		changeType.addCustom("type_dropdown", new LinkButton("caret", "Change type", "dropdown-toggle", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean open = !changeType.getStyleName().contains("open");
				changeType.setStyleName("btn-group" + (open ? " open" : ""));
			}
		}));
		changeType.add(typeDropdown);
	}

	protected Widget convertToType(String type, BlockBase old) {
		if (type.equals("Text")) {
			return new TextBlock(page);
		}
		return null;
	}
}
