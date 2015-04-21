package clp;

public class TableRow {
	String Property, Value;

	public TableRow(String p, String v) {
		Property = p;
		Value = v;
	}

	public String getProperty() {
		return Property;
	}

	public void setProperty(String property) {
		Property = property;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

}
