package demo;

import java.util.Arrays;

public class Result {
	private String categories;
	private String value;
	
	public Result(String categories, String value) {
		super();
		this.categories = categories;
		this.value = value;
	}

	public Result(Object object, Object object2) {
		super();
		this.categories = object.toString();
		this.value = object2.toString();
	}

	public String getCategories() {
		return categories;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "[categories=" + categories + ", value=" + value + "]\n";
	}
	
	
}
