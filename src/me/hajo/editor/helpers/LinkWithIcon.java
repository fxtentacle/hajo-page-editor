package me.hajo.editor.helpers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;

public class LinkWithIcon extends Anchor {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<i class=\"{0}\"></i> {1}")
		SafeHtml mkhtml(String icon, String text);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	public LinkWithIcon(String icon, String title, String linkStyle, final String historyToken) {
		init(icon, title, linkStyle);
		setHref("#" + historyToken);
	}

	public LinkWithIcon(String icon, String title, String linkStyle, ClickHandler clickHandler) {
		init(icon, title, linkStyle);
		setHref("#");
		addClickHandler(clickHandler);
	}

	public void init(String icon, String title, String linkStyle) {
		if (icon == null)
			setText(title);
		else
			replaceIconText(icon, title);
		setStyleName(linkStyle);
	}

	public void replaceIconText(String icon, String title) {
		setHTML(TEMPLATES.mkhtml(icon, title));
	}

}
