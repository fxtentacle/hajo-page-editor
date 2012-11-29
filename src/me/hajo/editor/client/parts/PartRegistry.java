package me.hajo.editor.client.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hajo.editor.client.HajoPagePart;
import me.hajo.editor.helpers.DropdownHelper;
import me.hajo.editor.helpers.DropdownHelper.DropdownEntry;

import com.google.gwt.user.client.ui.Widget;

public class PartRegistry {

	public static interface BlockTypeProvider {
		public HajoPagePart make(HajoPage page);

		public String getIcon();
	}

	public static List<String> dropdownOrder = new ArrayList<String>();
	public static Map<String, BlockTypeProvider> customBlockTypes = new HashMap<String, BlockTypeProvider>();
	static {
		customBlockTypes.put("", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new BlockBase(page, "");
			}

			@Override
			public String getIcon() {
				return null;
			}
		});
		customBlockTypes.put("Text", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new TextBlock(page);
			}

			@Override
			public String getIcon() {
				return "icon-pencil";
			}
		});
		customBlockTypes.put("Image", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new ImageBlock(page);
			}

			@Override
			public String getIcon() {
				return "icon-picture";
			}
		});
		customBlockTypes.put("Split", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new SplitBlock(page);
			}

			@Override
			public String getIcon() {
				return "icon-columns";
			}
		});
		customBlockTypes.put("Center", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new CenterBlock(page);
			}

			@Override
			public String getIcon() {
				return "icon-align-center";
			}
		});
		customBlockTypes.put("Spacer", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new SpacerBlock(page);
			}

			@Override
			public String getIcon() {
				return "icon-resize-vertical";
			}
		});
		customBlockTypes.put("Custom HTML", new BlockTypeProvider() {
			@Override
			public HajoPagePart make(HajoPage page) {
				return new CustomHtmlBlock(page);
			}

			@Override
			public String getIcon() {
				return "icon-beaker";
			}
		});

		dropdownOrder.add("Text");
		dropdownOrder.add("Image");
		dropdownOrder.add("Split");
		dropdownOrder.add("Center");
		dropdownOrder.add("Spacer");
		dropdownOrder.add("Custom HTML");
	}

	public static List<DropdownEntry> getDropdown() {
		List<DropdownEntry> ret = new ArrayList<DropdownHelper.DropdownEntry>();
		for (String cur : dropdownOrder) {
			BlockTypeProvider btp = customBlockTypes.get(cur);
			ret.add(new DropdownEntry(cur, btp.getIcon()));
		}
		ret.add(new DropdownEntry("Delete", "icon-trash"));
		return ret;
	}

	public static HajoPagePart createWidgetOfType(String type, HajoPage page) {
		BlockTypeProvider btp = customBlockTypes.get(type);
		if (btp == null)
			return null;
		return btp.make(page);
	}

	public static void addWidget(HajoPage page, String type) {
		HajoPagePart p = createWidgetOfType(type, page);
		if (p != null && p instanceof Widget)
			page.add((Widget) p);
	}

}
