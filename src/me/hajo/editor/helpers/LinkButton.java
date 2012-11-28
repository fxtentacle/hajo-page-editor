package me.hajo.editor.helpers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasEnabled;

public class LinkButton extends Button implements HasEnabled {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<i class=\"{0}\"></i> {1}")
		SafeHtml mkbutton(String icon, String text);

		@Template("<i class=\"{0}\"></i> {1}")
		SafeHtml mkbuttonHTML(String icon, SafeHtml html);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	public LinkButton(String icon, String title, String buttonStyle) {
		init(icon, title, buttonStyle);
	}

	public LinkButton(String icon, String title, String buttonStyle, ClickHandler handler) {
		init(icon, title, buttonStyle);
		addClickHandler(handler);
	}

	public LinkButton(String icon, SafeHtml title, String buttonStyle, ClickHandler handler) {
		init(icon, title, buttonStyle);
		addClickHandler(handler);
	}

	public LinkButton(String icon, String title, String buttonStyle, final String historyToken) {
		init(icon, title, buttonStyle);
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.newItem(historyToken);
			}
		});
	}

	public void init(String icon, String title, String buttonStyle) {
		if (icon == null)
			setText(title);
		else
			replaceIconText(icon, title);
		replaceButtonStyle(buttonStyle);
	}

	public void init(String icon, SafeHtml title, String buttonStyle) {
		if (icon == null)
			setHTML(title);
		else
			replaceIconHtml(icon, title);
		replaceButtonStyle(buttonStyle);
	}

	public void replaceIconText(String icon, String title) {
		setHTML(TEMPLATES.mkbutton(icon, title));
	}

	public void replaceIconHtml(String icon, SafeHtml html) {
		setHTML(TEMPLATES.mkbuttonHTML(icon, html));
	}

	public void replaceButtonStyle(String buttonStyle) {
		setStyleName("btn " + buttonStyle);
	}

}
