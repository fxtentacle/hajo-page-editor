package me.hajo.editor.client.parts;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.LinkButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class AddBlockButton extends FlowPanel implements HajoPagePart {

	public AddBlockButton(final FlowPanel page) {
		setStyleName("bootstrap");
		add(new LinkButton("icon-plus", "Add content block", "btn-block", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = page.getWidgetIndex(AddBlockButton.this);
				page.insert(new BlockBase(page), idx);
			}
		}));
	}

}
