package clp;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel {
	private ArrayList<TableRow> db = new ArrayList<TableRow>();

	public TableModel() {
	}

	public void add(TableRow row) {
		db.add(row);
		fireTableRowsInserted(db.size(), db.size());
	}

	public void set(int idx, TableRow row) {
		db.set(idx, row);
		fireTableRowsUpdated(idx, idx);
	}

	public TableRow get(int idx) {
		return db.get(idx);
	}

	public int getSize() {
		return db.size();
	}

	public void clear() {
		db.clear();
	}

	public void removeItem(int idx) {
		if (idx > 0) {
			db.remove(idx);

			fireTableRowsDeleted(idx, idx);
		}
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return db.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		switch (arg1) {
		case 0:
			return (Object) db.get(arg0).getProperty();
		case 1:
			return (Object) db.get(arg0).getValue();
		}

		return new Object();
	}

	public void setValueAt(Object obj, int arg0, int arg1) {
		switch (arg1) {
		case 0:
			db.get(arg0).setProperty((String) obj);
			break;
		case 1:
			db.get(arg0).setValue((String) obj);
			break;
		}
	}

	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Property name";
		case 1:
			return "Value";
		}

		return "";
	}

	public Class getColumnClass(int arg0) {
		return String.class;
	}

	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	public void refreshTable() {
		fireTableDataChanged();
	}

}
