package me.hajo.editor.client;

import me.hajo.editor.client.EntryPoint.StateStorage;
import me.hajo.editor.client.parts.AddBlockButton;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.LinkButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class GuiContainer extends ResizeComposite {
	private static GuiContainerUiBinder uiBinder = GWT.create(GuiContainerUiBinder.class);

	interface GuiContainerUiBinder extends UiBinder<Widget, GuiContainer> {
	}

	@UiField
	FlowPanel canvas;

	@UiField
	FlowPanel toolbarContainer;

	HajoToolbar toolbar = new HajoToolbar();

	FlowPanel page;

	public GuiContainer(HTMLPanel ocanvas, String image_upload_url, final StateStorage stateStorage) {
		initWidget(uiBinder.createAndBindUi(this));
		toolbarContainer.add(toolbar);
		ocanvas.removeFromParent();
		canvas.add(ocanvas);
		page = new FlowPanel();
		ocanvas.add(page);

		page.add(new AddBlockButton(page));

		toolbarContainer.add(new LinkButton("icon-save", "Save", "btn-success", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stateStorage.sendToServer(new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
					}
				});
			}
		}));

	}
}
