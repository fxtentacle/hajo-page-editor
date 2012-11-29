package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.client.HajoPagePart.ImageRescaleCollector;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;

public class TextBlock extends BlockBase implements HajoPagePart {
	HTML text = new HTML();
	TextArea editor = new TextArea();

	HandlerRegistration handlerReg;

	void goToDisplayMode() {
		if (handlerReg != null)
			handlerReg.removeHandler();

		content.clear();
		text.setHTML(makeSafeHtml(editor.getText()));
		content.add(text);
		handlerReg = text.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				goToEditMode();
			}
		});
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

	protected SafeHtml makeSafeHtml(String text2) {
		StyledItem useP = getStyledItemInContext("p", currentStyle);
		StyledItem usePL = getStyledItemInContext("p", currentStyle + "L");
		StyledItem useS = getStyledItemInContext("strong", currentStyle);

		SafeHtmlBuilder shb = new SafeHtmlBuilder();
		String[] paragraphs = text2.split("\n");
		for (String para : paragraphs) {
			boolean justify = para.startsWith("=");
			if (justify)
				para = para.substring(1);

			if (justify)
				shb.append(useP.open);
			else
				shb.append(usePL.open);

			String[] boldParts = para.split("[*]");
			for (int i = 0; i < boldParts.length; i++) {
				boolean bold = i % 2 != 0;

				if (bold)
					shb.append(useS.open);
				shb.appendEscaped(boldParts[i]);
				if (bold)
					shb.append(useS.close);
			}

			if (justify)
				shb.append(useP.close);
			else
				shb.append(usePL.close);
		}
		return shb.toSafeHtml();
	}

	protected String currentStyle = "Paragraph";

	public TextBlock(final FlowPanel page) {
		super(page, 0);

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		entries.add(new DropdownEntry("Headline", ""));
		entries.add(new DropdownEntry("Sub-Headline", ""));
		entries.add(new DropdownEntry("Paragraph", ""));

		currentStyle = "Headline";
		DropdownHelper.makeDropdown(toolbar.addGroup(), "Style: ", true, entries, 0, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentStyle = key;
				goToDisplayMode();
			}
		});

		editor.setText("Click me to edit ...");
		editor.setCharacterWidth(80);
		editor.setVisibleLines(20);
		goToDisplayMode();
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		shb.append(makeSafeHtml(editor.getText()));
	}
}
