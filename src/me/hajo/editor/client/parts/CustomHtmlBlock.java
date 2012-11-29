package me.hajo.editor.client.parts;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;

public class CustomHtmlBlock extends BlockBase implements HajoPagePart {
	HTML text = new HTML();
	String currentText = "<span>Click me to edit ...</span>";

	void updateDisplay() {
		content.clear();
		text.setHTML(currentText);
		content.add(text);
	}

	public CustomHtmlBlock(final HajoPage page) {
		super(page, "CustomHTML");
		text.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
			}
		});
		updateDisplay();
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		shb.append(SafeHtmlUtils.fromTrustedString(currentText));
	}

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Custom HTML");
		pps.Text = currentText;
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		currentText = pps.Text;
		updateDisplay();
	}

	@Override
	public void insertEditor(FlowPanel target) {
		final TextArea editor = new TextArea();
		editor.setText(currentText);
		editor.setCharacterWidth(80);
		editor.setVisibleLines(20);
		Style elem = editor.getElement().getStyle();
		elem.setPosition(Position.ABSOLUTE);
		elem.setTop(0, Unit.PX);
		elem.setLeft(0, Unit.PX);
		elem.setRight(0, Unit.PX);
		elem.setBottom(0, Unit.PX);
		editor.setWidth("auto");
		editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				currentText = editor.getText();
				updateDisplay();
			}
		});
		target.add(editor);
	}
}
