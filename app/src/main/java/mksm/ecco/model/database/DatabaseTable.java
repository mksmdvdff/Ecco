package mksm.ecco.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mksm on 10.08.2016.
 */
public abstract class DatabaseTable {
	protected final DatabaseHelper mDbHelper;
	protected final SQLiteDatabase mDb;
	protected final String GET_ALL_STRING;
	protected final String DELETE_ALL_STRING;
	protected final String VACUUM = "VACUUM";
	protected String TABLE_NAME;
	protected Map<String, String> columns = new HashMap<>();

	protected DatabaseTable(Context context) {
		this.init();
		this.mDbHelper = DatabaseHelper.getInstance(context, this);
		this.mDb = mDbHelper.getWritableDatabase();
		GET_ALL_STRING = "SELECT * FROM " + getTableName();
		DELETE_ALL_STRING = "DELETE FROM " + getTableName();

	}

	public Map<String, String> getColumns() {
		return columns;
	}

	public Set<String> getColumnNames() {
		return columns.keySet();
	}

	//в этом методе будем инициализировать TableName и Columns
	public abstract void init();

	public String getTableName() {
		return TABLE_NAME;
	}


	//раз уж в DatabaseTable сделали Set<Datatable> - то переопределим
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof DatabaseTable)) {
			return false;
		}
		DatabaseTable other = (DatabaseTable) o;
		return this.getTableName().equals(other.getTableName()) && this.getColumnNames().equals(other.getColumnNames());
	}

	@Override
	public int hashCode() {
		int hash = getTableName().hashCode();
		for (String columnName : getColumnNames()) {
			hash += columnName.hashCode();
		}
		return hash;
	}
}
