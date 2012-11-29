package me.hajo.editor.client.parts;

import me.hajo.editor.client.GuiContainer;
import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ImageBlock extends BlockBase implements HajoPagePart {
	public interface MyTemplates extends SafeHtmlTemplates {
		@Template("<img src=\"{0}\" style=\"width: {1}; height: auto;\" />")
		SafeHtml mkimage(String url, String width);
	}

	private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

	HTML show = new HTML();
	String selectedImageID = null;

	void updateDisplay() {
		content.clear();
		show.setHTML(makeSafeHtml(null));
		content.add(show);
	}

	protected SafeHtml makeSafeHtml(ImageRescaleCollector irc) {
		if (selectedImageID == null) {
			if (irc != null)
				return SafeHtmlUtils.fromString("");
			return SafeHtmlUtils.fromString("Click to select image");
		}

		String url = GuiContainer.imagesAvailable.image_download_url + selectedImageID;
		if (irc == null) {
			return TEMPLATES.mkimage(url, "100%");
		} else {
			int width = getWidth();
			String filename = GuiContainer.imagesAvailable.id2name.get(selectedImageID);
			if (filename == null)
				filename = "unknown";
			return TEMPLATES.mkimage(irc.addRequest(selectedImageID, filename, width), width + "px");
		}
	}

	public ImageBlock(final HajoPage page) {
		super(page, "Image");
		updateDisplay();
	}

	@Override
	public void encode(SafeHtmlBuilder shb, ImageRescaleCollector irc) {
		shb.append(makeSafeHtml(irc));
	}

	@Override
	public PagePartStorage serialize() {
		PagePartStorage pps = new PagePartStorage("Image");
		pps.ImageID = selectedImageID;
		return pps;
	}

	@Override
	public void deserialize(PagePartStorage pps) {
		selectedImageID = pps.ImageID;
		updateDisplay();
	}

	@Override
	public void insertEditor(FlowPanel target) {
		FlowPanel imageShow = new FlowPanel();
		fill(imageShow);
		target.add(imageShow);
	}

	public void fill(final FlowPanel imageShow) {
		imageShow.clear();
		if (GuiContainer.imagesAvailable.id2name.size() == 0) {
			Label label = new Label("Please upload images first. Then click here to retry ...");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fill(imageShow);
				}
			});
			imageShow.add(label);
		}
		for (final String key : GuiContainer.imagesAvailable.id2name.keySet()) {
			Image addme = new Image(GuiContainer.imagesAvailable.image_download_url + key);
			addme.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					selectedImageID = key;
					updateDisplay();
				}
			});
			addme.setWidth("128px");
			addme.setHeight("auto");
			addme.getElement().getStyle().setFloat(Float.LEFT);
			addme.getElement().getStyle().setMargin(5, Unit.PX);
			imageShow.add(addme);
		}
		Label clear = new Label(" ");
		clear.getElement().getStyle().setClear(Clear.BOTH);
		imageShow.add(clear);
	}
}
