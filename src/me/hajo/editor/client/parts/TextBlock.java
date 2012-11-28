package me.hajo.editor.client.parts;

import me.hajo.editor.client.HajoPagePart;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;

public class TextBlock extends BlockBase implements HajoPagePart {
	HTML text = new HTML("Click me to edit ...");
	TextArea editor = new TextArea();

	HandlerRegistration handlerReg;

	void goToDisplayMode() {
		if (handlerReg != null)
			handlerReg.removeHandler();

		content.clear();
		content.add(text);
		handlerReg = content.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				goToEditMode();
			}
		}, ClickEvent.getType());
	}

	void goToEditMode() {
		if (handlerReg != null)
			handlerReg.removeHandler();

		content.clear();
		content.add(editor);
		editor.setFocus(true);
		handlerReg = editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				goToDisplayMode();
			}
		});
	}

	public TextBlock(final FlowPanel page) {
		super(page);
		goToDisplayMode();
	}
}
