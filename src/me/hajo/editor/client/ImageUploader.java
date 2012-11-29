package me.hajo.editor.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hajo.editor.helpers.LinkButton;

import org.swfupload.client.SWFUpload;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.SWFUpload.WindowMode;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.FileDialogCompleteHandler;
import org.swfupload.client.event.FileQueuedHandler;
import org.swfupload.client.event.UploadCompleteHandler;
import org.swfupload.client.event.UploadSuccessHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

public class ImageUploader extends Composite {
	FlowPanel contents = new FlowPanel();
	FlowPanel files = new FlowPanel();
	HTMLPanel uploadButton = new HTMLPanel("");

	public static String makeRandomID() {
		final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		char[] retme = new char[36];
		for (int i = 0; i < 36; i++) {
			int r = (int) (Math.random() * CHARS.length);
			retme[i] = CHARS[r % CHARS.length];
		}
		return new String(retme);
	}

	Map<String, String> id2name = new HashMap<String, String>();
	List<String> idOrdering = new ArrayList<String>();
	private SWFUpload uploader;

	public ImageUploader() {
		final com.google.gwt.user.client.Element origDiv = DOM.createDiv();
		origDiv.setId(makeRandomID());
		uploadButton.getElement().appendChild(origDiv);
		final LinkButton linkButton = new LinkButton("icon-file", "Upload", "btn-inverse");
		uploadButton.add(linkButton);
		contents.add(files);
		contents.add(uploadButton);
		initWidget(contents);
		repaint();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				UploadBuilder builder = new UploadBuilder();
				builder.setFlashURL(DOM.getElementById("swfupload_url").getAttribute("content"));
				builder.setUploadURL(DOM.getElementById("image_upload").getAttribute("action"));

				// Configure which file types may be selected
				builder.setFileTypes("*.png;*.jpg;*.jpeg;*.gif");
				builder.setFileTypesDescription("Images");
				builder.setFileSizeLimit(5 * 1024);

				// Configure the button to display
				builder.setButtonPlaceholderID(origDiv.getId());
				builder.setButtonWidth(linkButton.getOffsetWidth());
				builder.setButtonHeight(linkButton.getOffsetHeight());

				StyleInjector.inject(".swfupload {position: absolute;z-index: 1;}");

				builder.setButtonAction(ButtonAction.SELECT_FILES);

				final Map<String, String> internalIdToAttachmentID = new HashMap<String, String>();

				builder.setFileQueuedHandler(new FileQueuedHandler() {
					@Override
					public void onFileQueued(FileQueuedEvent event) {
						String id = event.getFile().getId();
						String rid = makeRandomID();
						internalIdToAttachmentID.put(id, rid);
						uploader.addFileParam(id, "ClientID", rid);
					}
				});
				builder.setFileDialogCompleteHandler(new FileDialogCompleteHandler() {
					@Override
					public void onFileDialogComplete(FileDialogCompleteEvent e) {
						uploader.startUpload();
					}
				});
				builder.setUploadSuccessHandler(new UploadSuccessHandler() {
					@Override
					public boolean onUploadSuccess(UploadSuccessEvent e) {
						String id = e.getFile().getId();
						String key = internalIdToAttachmentID.get(id);
						id2name.put(key, e.getFile().getName());
						idOrdering.add(key);
						internalIdToAttachmentID.remove(id);
						uploader.startUpload();
						repaint();
						return true;
					}
				});
				builder.setUploadCompleteHandler(new UploadCompleteHandler() {
					@Override
					public void onUploadComplete(UploadCompleteEvent e) {
						repaint();
					}
				});

				builder.setWindowMode(WindowMode.TRANSPARENT);

				uploader = builder.build();
			}
		});
	}

	private TextBox deleteName = TextBox.wrap(DOM.getElementById("image_delete_id"));
	private FormPanel deleteForm = FormPanel.wrap(DOM.getElementById("image_delete"), true);

	void repaint() {
		files.clear();
		for (final String key : idOrdering) {
			files.add(new LinkButton("icon-remove", id2name.get(key), "", new ClickHandler() {
				HandlerRegistration handler;

				@Override
				public void onClick(ClickEvent event) {
					deleteName.setText(key);
					handler = deleteForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
						@Override
						public void onSubmitComplete(SubmitCompleteEvent event) {
							handler.removeHandler();
							idOrdering.remove(key);
							id2name.remove(key);
							repaint();
						}
					});
					deleteForm.submit();
				}
			}));
			files.add(new InlineLabel(" "));
		}
		if (id2name.size() > 0)
			files.add(new HTML("<br/>"));
	}

	public Map<String, String> getMap() {
		return id2name;
	}
}
