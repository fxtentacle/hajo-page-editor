package me.hajo.editor.model;

import java.util.List;

public class PagePartStorage {
	public PagePartStorage() {
	}

	public PagePartStorage(String type) {
		this.Type = type;
	}

	public String Type;
	public String Text;
	public String TextStyle;
	public String ImageID;
	public Integer Width;
	public Integer Split;
	public Double Padding;
	public List<PagePartStorage> Children;
}
