package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownCallback;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;

public class TextBlock extends BlockBase implements HajoPagePart {
	HTML text = new HTML();
	String currentText = "Click me to edit ...";

	void updateDisplay() {
		content.clear();
		text.setHTML(makeSafeHtml(currentText, true));
		content.add(text);
	}

	protected SafeHtml makeSafeHtml(String text2, boolean previewMode) {
		HajoPage masterPage = page, tryme;
		while ((tryme = masterPage.getParentPage()) != null)
			masterPage = tryme;
		Map<String, String> linkTable = masterPage.linkTable;

		StyledItem useP = getStyledItemInContext("p", currentStyle);
		StyledItem usePL = getStyledItemInContext("p", currentStyle + "L");
		StyledItem usePR = getStyledItemInContext("p", currentStyle + "R");
		StyledItem usePC = getStyledItemInContext("p", currentStyle + "C");
		StyledItem useS = getStyledItemInContext("strong", currentStyle);
		StyledItem useUL = getStyledItemInContext("ul", currentStyle);
		StyledItem useLI = getStyledItemInContext("li", currentStyle);

		SafeHtmlBuilder shb = new SafeHtmlBuilder();
		String[] paragraphs = text2.split("\n");
		boolean isInList = false;
		for (String para : paragraphs) {
			if (para.length() == 0)
				continue;

			boolean justify = para.startsWith("=");
			boolean right = para.startsWith(">");
			boolean left = para.startsWith("<");
			boolean center = para.startsWith("|");
			boolean list = para.startsWith("*");
			if (justify || left || right || center || list)
				para = para.substring(1);

			if (list && !isInList) {
				isInList = true;
				shb.append(useUL.open);
			} else if (!list && isInList) {
				isInList = false;
				shb.append(useUL.close);
			}

			final StyledItem useMe;
			if (isInList) {
				useMe = useLI;
			} else {
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
			}

			if (para.startsWith("LINK[")) {
				String[] parts = para.split("[\\[\\]]+");
				String key = parts[1].trim();
				String url = parts[2].trim();
				String escapedUrl = SafeHtmlUtils.htmlEscape(url);
				linkTable.put(key, escapedUrl);

				if (previewMode) {
					shb.append(useMe.open);
					shb.appendEscaped("Link: [" + key + "] -> " + url);
					shb.append(useMe.close);
				}
			} else {
				shb.append(useMe.open);

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

				shb.append(useMe.close);
			}
		}

		if (isInList)
			shb.append(useUL.close);
		return shb.toSafeHtml();
	}

	protected String currentStyle = "Paragraph";

	public TextBlock(final HajoPage page) {
		super(page, "Text");
		updateDisplay();
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		shb.append(makeSafeHtml(currentText, false));
	}

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Text");
		pps.Text = currentText;
		pps.TextStyle = currentStyle;
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		currentText = pps.Text;
		currentStyle = pps.TextStyle;
		updateDisplay();
	}

	@Override
	public HajoToolbar getToolbar() {
		HajoToolbar toolbar = super.getToolbar();

		List<DropdownEntry> entries = new ArrayList<DropdownEntry>();
		entries.add(new DropdownEntry("Headline", ""));
		entries.add(new DropdownEntry("Sub-Headline", ""));
		entries.add(new DropdownEntry("Paragraph", ""));

		int sel = DropdownHelper.findSelection(entries, currentStyle);
		DropdownHelper.makeDropdown(toolbar.addGroup(), "Style: ", true, entries, sel, new DropdownCallback() {
			@Override
			public void OnSelect(String key) {
				currentStyle = key;
				updateDisplay();
			}
		});

		return toolbar;
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
		editor.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					currentText = editor.getText();
					updateDisplay();
				}
			}
		});
		target.add(editor);
	}
}
