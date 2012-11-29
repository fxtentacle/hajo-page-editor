package me.hajo.editor.client;

import org.fusesource.restygwt.client.Defaults;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryPoint implements com.google.gwt.core.client.EntryPoint {
	public static interface StateStorage {
		public String getState();

		public void setState(String state);

		public void setHTML(String html);

		public String getRequiredImages();

		public void setRequiredImages(String text);

		public void sendToServer(SubmitCompleteHandler handler);
	}

	public void onModuleLoad() {
		Defaults.setServiceRoot("./");
		Defaults.setRequestTimeout(5000);
		Defaults.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");

		HTMLPanel canvas = HTMLPanel.wrap(DOM.getElementById("canvas"));

		Element image_upload_form = DOM.getElementById("image_upload");
		String image_upload_url = image_upload_form.getAttribute("action");
		String image_download_url = DOM.getElementById("image_download").getAttribute("src");
		String close_link = DOM.getElementById("close_link").getAttribute("href");

		final TextArea html_output = TextArea.wrap(DOM.getElementById("html_output"));
		final TextArea required_images = TextArea.wrap(DOM.getElementById("required_images"));
		final TextArea json_state = TextArea.wrap(DOM.getElementById("json_state"));
		final FormPanel state_storage = FormPanel.wrap(DOM.getElementById("state_storage"), true);

		image_upload_form.removeFromParent();
		state_storage.setVisible(false);

		RootLayoutPanel.get().add(new GuiContainer(canvas, image_upload_url, image_download_url, close_link, new StateStorage() {
			@Override
			public void setState(String state) {
				json_state.setText(state);
			}

			@Override
			public void setHTML(String html) {
				html_output.setText(html);
			}

			@Override
			public void setRequiredImages(String html) {
				required_images.setText(html);
			}

			@Override
			public void sendToServer(SubmitCompleteHandler handler) {
				state_storage.addSubmitCompleteHandler(handler);
				state_storage.submit();
			}

			@Override
			public String getState() {
				return json_state.getText();
			}

			@Override
			public String getRequiredImages() {
				return required_images.getText();
			}
		}));
	}
}
