package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
				event.preventDefault();
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
		StyledItem usePR = getStyledItemInContext("p", currentStyle + "R");
		StyledItem usePC = getStyledItemInContext("p", currentStyle + "C");
		StyledItem useS = getStyledItemInContext("strong", currentStyle);

		Map<String, String> linkTable = new HashMap<String, String>();

		SafeHtmlBuilder shb = new SafeHtmlBuilder();
		String[] paragraphs = text2.split("\n");
		for (String para : paragraphs) {
			if (para.length() == 0)
				continue;

			boolean justify = para.startsWith("=");
			boolean right = para.startsWith(">");
			boolean left = para.startsWith("<");
			boolean center = para.startsWith("|");
			if (justify || left || right || center)
				para = para.substring(1);

			final StyledItem useMe;
			if (justify)
				useMe = useP;
			else if (left)
				useMe = usePL;
			else if (right)
				useMe = usePR;
			else if (center)
				useMe = usePC;
			else
				useMe = usePL;

			shb.append(useMe.open);

			if (para.startsWith("LINK[")) {
				String[] parts = para.split("[\\[\\]]+");
				String escapedUrl = SafeHtmlUtils.htmlEscape(parts[1].trim());
				linkTable.put(parts[2].trim(), escapedUrl);
			} else {
				String[] boldParts = para.split("[*]");
				for (int i = 0; i < boldParts.length; i++) {
					boolean bold = i % 2 != 0;

					if (bold)
						shb.append(useS.open);

					String curPart = boldParts[i];

					while (curPart.length() > 0) {
						int minIndex = curPart.length();
						Entry<String, String> linkEntry = null;
						for (Entry<String, String> cur : linkTable.entrySet()) {
							String modkey = "[" + cur.getKey() + "]";
							int idx = curPart.indexOf(modkey);
							if (idx >= 0) {
								minIndex = Math.min(minIndex, idx);
								linkEntry = cur;
							}
						}
						if (linkEntry != null) {
							shb.appendEscaped(curPart.substring(0, minIndex));
							curPart = curPart.substring(minIndex + linkEntry.getKey().length() + 2);

							StyledItem link = getStyledItemInContext("a", currentStyle, "href=\"" + linkEntry.getValue() + "\"");
							shb.append(link.open);
							shb.appendEscaped(linkEntry.getKey());
							shb.append(link.close);
						} else {
							shb.appendEscaped(curPart);
							curPart = "";
						}
					}

					if (bold)
						shb.append(useS.close);
				}
			}

			shb.append(useMe.close);
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

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Text");
		pps.Text = editor.getText();
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		editor.setText(pps.Text);
		goToDisplayMode();
	}
}
