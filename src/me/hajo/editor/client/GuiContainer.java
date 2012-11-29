package me.hajo.editor.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import me.hajo.editor.client.EntryPoint.StateStorage;
import me.hajo.editor.client.HajoPagePart.ImageRescaleCollector;
import me.hajo.editor.client.parts.AddBlockButton;
import me.hajo.editor.client.parts.HajoPage;
import me.hajo.editor.client.parts.ImageBlock;
import me.hajo.editor.client.parts.SplitBlock;
import me.hajo.editor.client.parts.TextBlock;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.LinkButton;
import me.hajo.editor.model.PagePartStorage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class GuiContainer extends ResizeComposite {

	interface StateEncoderDecoder extends JsonEncoderDecoder<PagePartStorage> {
	}

	private StateEncoderDecoder stateEncoderDecoder = GWT.create(StateEncoderDecoder.class);

	interface GuiContainerUiBinder extends UiBinder<Widget, GuiContainer> {
	}

	private static GuiContainerUiBinder uiBinder = GWT.create(GuiContainerUiBinder.class);

	@UiField
	FlowPanel canvas;

	@UiField
	FlowPanel toolbarContainer;

	HajoToolbar toolbar = new HajoToolbar();

	HTMLPanel content;
	HajoPage page;
	HTML preview = new HTML();

	final LinkButton previewToggle;

	@UiField(provided = true)
	ImageUploader imageUploader;

	public static class ImagesAvailable {
		public String image_download_url;
		public Map<String, String> id2name;
	}

	public static ImagesAvailable imagesAvailable = new ImagesAvailable();

	public GuiContainer(HTMLPanel ocanvas, String image_upload_url, String image_download_url, final StateStorage stateStorage) {
		imageUploader = new ImageUploader(image_upload_url);
		initWidget(uiBinder.createAndBindUi(this));
		toolbarContainer.add(toolbar);
		this.content = ocanvas;
		content.removeFromParent();
		canvas.add(content);

		page = new HajoPage(-1);
		page.add(new SplitBlock(page));
		page.add(new ImageBlock(page));
		page.add(new TextBlock(page));
		page.add(new AddBlockButton(page));
		content.clear();
		content.add(page);

		toolbar.addCustom("preview", previewToggle = new LinkButton("", "Preview", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean makeActive = !previewToggle.getStyleName().contains("active");
				previewToggle.setStyleName("active", makeActive);
				reRender(makeActive);
			}
		}));
		saveButton = new LinkButton("icon-save", "Save", "btn-success", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				savePage(stateStorage);
			}
		});
		toolbar.addCustom("save", saveButton);

		imagesAvailable.image_download_url = image_download_url;
		imagesAvailable.id2name = imageUploader.getMap();
	}

	List<HajoPagePart> parts = new ArrayList<HajoPagePart>();

	private LinkButton saveButton;

	protected void reRender(boolean goToPreview) {
		if (goToPreview) {
			ImageRescaleCollector irc = new ImageRescaleCollector() {
				@Override
				public String addRequest(String fullURL, int width) {
					return fullURL + "&width=" + width;
				}
			};
			SafeHtmlBuilder shb = new SafeHtmlBuilder();
			page.encode(shb, irc);

			content.clear();
			preview.setHTML(shb.toSafeHtml());
			content.add(preview);
		} else {
			content.clear();
			content.add(page);
		}
	}

	public void savePage(final StateStorage stateStorage) {
		PagePartStorage state = page.serialize();
		JSONValue json = stateEncoderDecoder.encode(state);
		stateStorage.setState(json.toString());

		final List<String> imageList = new ArrayList<String>();
		ImageRescaleCollector irc = new ImageRescaleCollector() {
			@Override
			public String addRequest(String fullURL, int width) {
				String ext = "";
				int lastDot = fullURL.lastIndexOf('.');
				if (lastDot > 0)
					ext = fullURL.substring(lastDot);

				String url = "http://FAKEHOST/" + ImageUploader.makeRandomID() + ext;
				imageList.add(url + "\t" + width + "\t" + fullURL);
				return url;
			}
		};
		SafeHtmlBuilder shb = new SafeHtmlBuilder();
		page.encode(shb, irc);
		stateStorage.setHTML(shb.toSafeHtml().asString());

		String imageStr = Strings.join(imageList.toArray(new String[0]), "\n");
		stateStorage.setRequiredImages(imageStr);

		saveButton.replaceIconText("icon-time", "saving ...");
		saveButton.replaceButtonStyle("");
		stateStorage.sendToServer(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				saveButton.replaceIconText("icon-save", "Save");
				saveButton.replaceButtonStyle("btn-success");
			}
		});
	}
}
