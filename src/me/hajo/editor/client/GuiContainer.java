package me.hajo.editor.client;

import java.util.ArrayList;
import java.util.List;

import me.hajo.editor.client.EntryPoint.StateStorage;
import me.hajo.editor.client.parts.AddBlockButton;
import me.hajo.editor.client.parts.TextBlock;
import me.hajo.editor.helpers.HajoToolbar;
import me.hajo.editor.helpers.LinkButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	private static GuiContainerUiBinder uiBinder = GWT.create(GuiContainerUiBinder.class);

	interface GuiContainerUiBinder extends UiBinder<Widget, GuiContainer> {
	}

	@UiField
	FlowPanel canvas;

	@UiField
	FlowPanel toolbarContainer;

	HajoToolbar toolbar = new HajoToolbar();

	FlowPanel page;

	final LinkButton previewToggle;

	public GuiContainer(HTMLPanel ocanvas, String image_upload_url, final StateStorage stateStorage) {
		initWidget(uiBinder.createAndBindUi(this));
		toolbarContainer.add(toolbar);
		ocanvas.removeFromParent();
		canvas.add(ocanvas);
		page = new FlowPanel();
		ocanvas.add(page);

		page.add(new TextBlock(page));
		page.add(new AddBlockButton(page));

		toolbar.addCustom("preview", previewToggle = new LinkButton("", "Preview", "", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean makeActive = !previewToggle.getStyleName().contains("active");
				previewToggle.setStyleName("active", makeActive);
				reRender(makeActive);
			}
		}));
		toolbar.addCustom("save", new LinkButton("icon-save", "Save", "btn-success", new ClickHandler() {
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

	List<HajoPagePart> parts = new ArrayList<HajoPagePart>();

	protected void reRender(boolean preview) {
		if (preview) {
			for (int i = 0; i < page.getWidgetCount(); i++) {
				Widget w = page.getWidget(i);
				if (w instanceof HajoPagePart) {
					parts.add((HajoPagePart) w);
				}
			}
			page.clear();

			SafeHtmlBuilder shb = new SafeHtmlBuilder();
			for (HajoPagePart cur : parts) {
				cur.encode(shb);
			}
			page.add(new HTML(shb.toSafeHtml()));
		} else {
			page.clear();
			for (HajoPagePart cur : parts) {
				page.add((Widget) cur);
			}
			parts.clear();
		}
	}
}
