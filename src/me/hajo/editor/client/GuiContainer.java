package me.hajo.editor.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.hajo.editor.client.EntryPoint.StateStorage;
import me.hajo.editor.client.HajoPagePart.ImageRescaleCollector;
import me.hajo.editor.client.parts.BlockBase;
import me.hajo.editor.client.parts.HajoPage;
import me.hajo.editor.client.parts.PartRegistry;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.LinkButton;
import me.hajo.editor.model.PagePartStorage;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class GuiContainer extends ResizeComposite {

	interface StateEncoderDecoder extends JsonEncoderDecoder<PagePartStorage> {
	}

	private StateEncoderDecoder stateEncoderDecoder = GWT.create(StateEncoderDecoder.class);

	interface GuiContainerUiBinder extends UiBinder<Widget, GuiContainer> {
	}

	private static GuiContainerUiBinder uiBinder = GWT.create(GuiContainerUiBinder.class);

	@UiField
	ScrollPanel canvas;

	@UiField
	FlowPanel toolbarContainer;

	@UiField
	FlowPanel editUtilsContainer;

	@UiField
	FlowPanel editorContainer;

	@UiField
	LayoutPanel dock;

	HajoToolbar toolbar = new HajoToolbar();

	HTMLPanel content;
	HajoPage page;
	HTML preview = new HTML();

	final LinkButton previewToggle;
	final LinkButton closeButton;

	@UiField(provided = true)
	ImageUploader imageUploader;

	public static class ImagesAvailable {
		public String image_download_url;
		public Map<String, String> id2name;
	}

	public static ImagesAvailable imagesAvailable = new ImagesAvailable();

	public GuiContainer(HTMLPanel ocanvas, String image_download_url, final String close_link, final StateStorage stateStorage) {
		imageUploader = new ImageUploader();
		initWidget(uiBinder.createAndBindUi(this));
		toolbarContainer.add(toolbar);
		this.content = ocanvas;
		content.removeFromParent();
		canvas.add(content);

		page = new HajoPage(-1) {
			@Override
			public void selectItem(BlockBase item) {
				selectedItemChanged(item);
			}
		};
		PartRegistry.addWidget(page, "Text");
		content.clear();
		content.add(page);

		toolbar.addCustom("preview", previewToggle = new LinkButton("icon-plane", "Preview", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean makeActive = !previewToggle.getStyleName().contains("active");
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
		toolbar.addCustom("close", closeButton = new LinkButton("icon-signout", "Close", "btn-warning", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Location.assign(close_link);
			}
		}));
		toolbar.addStyleName("pull-right");

		imagesAvailable.image_download_url = image_download_url;
		imagesAvailable.id2name = imageUploader.getMap();

		String reqi = stateStorage.getRequiredImages();
		String[] lines = reqi.split("\n");
		for (String cur : lines) {
			String[] parts = cur.trim().split("\t");
			if (parts.length < 4)
				continue;

			// dummyUrl + "\t" + width + "\t" + filename + "\t" + imageID);
			imagesAvailable.id2name.put(parts[3], parts[2]);
			imageUploader.idOrdering.add(parts[3]);
		}
		imageUploader.repaint();

		String stateString = stateStorage.getState().trim();
		if (stateString.length() > 0) {
			PagePartStorage state = stateEncoderDecoder.decode(JSONParser.parseStrict(stateString));
			if (state != null)
				page.deserialize(state);
		}
	}

	BlockBase currentlySelectedItem = null;

	protected void selectedItemChanged(BlockBase item) {
		closeButton.replaceButtonStyle("btn-warning");

		editUtilsContainer.clear();
		editorContainer.clear();

		if (item != null) {
			HajoToolbar editBar = item.getToolbar();
			editUtilsContainer.add(editBar);
			Element tbe = editBar.getElement();
			tbe.getStyle().setOverflow(Overflow.VISIBLE);
			tbe.getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
			Element tbpe = editUtilsContainer.getElement();
			tbpe.getStyle().setOverflow(Overflow.VISIBLE);
			tbpe.getParentElement().getStyle().setOverflow(Overflow.VISIBLE);

			item.insertEditor(editorContainer);
		}

		boolean needEditor = editorContainer.getWidgetCount() > 0;
		dock.setWidgetBottomHeight(editorContainer, 0, Unit.PX, needEditor ? 256 : 0, Unit.PX);
		dock.setWidgetTopBottom(canvas, 50, Unit.PX, needEditor ? 256 : 0, Unit.PX);

		currentlySelectedItem = item;
	}

	List<HajoPagePart> parts = new ArrayList<HajoPagePart>();

	private LinkButton saveButton;

	protected void reRender(boolean goToPreview) {
		previewToggle.setStyleName("active btn-primary", goToPreview);

		if (goToPreview) {
			selectedItemChanged(null);

			ImageRescaleCollector irc = new ImageRescaleCollector() {
				@Override
				public String addRequest(String imageID, String filename, int width) {
					return imagesAvailable.image_download_url + imageID;
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
		saveButton.replaceIconText("icon-time", "saving ...");
		saveButton.setEnabled(false);

		new Timer() {
			@Override
			public void run() {
				reRender(false);

				PagePartStorage state = page.serialize();
				JSONValue json = stateEncoderDecoder.encode(state);
				String text = json.toString().replaceAll("\\s*\"[^\"]+\":null,?\\s*", "");
				text = text.replaceAll(",}", "}");
				stateStorage.setState(text);

				final List<String> imageList = new ArrayList<String>();
				ImageRescaleCollector irc = new ImageRescaleCollector() {
					@Override
					public String addRequest(String imageID, String filename, int width) {
						String ext = "";
						int lastDot = filename.lastIndexOf('.');
						if (lastDot > 0)
							ext = filename.substring(lastDot);

						String url = "http://FAKEHOST/" + ImageUploader.makeRandomID() + ext;
						imageList.add(url + "\t" + width + "\t" + filename + "\t" + imageID);
						return url;
					}
				};
				SafeHtmlBuilder shb = new SafeHtmlBuilder();
				page.encode(shb, irc);
				stateStorage.setHTML(shb.toSafeHtml().asString());

				String imageStr = "";
				for (String c : imageList) {
					imageStr += "\n" + c;
				}
				stateStorage.setRequiredImages(imageStr);

				stateStorage.sendToServer(new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						saveButton.replaceIconText("icon-save", "Save");
						saveButton.setEnabled(true);
						closeButton.replaceButtonStyle("");
					}
				});
			}
		}.schedule(5);

	}
}
