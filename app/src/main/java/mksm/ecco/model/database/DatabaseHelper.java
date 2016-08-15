package mksm.ecco.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mksm on 10.08.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "EccoShops";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper sInstance;
	private final Context appContext;
	private static Set<DatabaseTable> databaseTables = new HashSet<>();

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.appContext = context;
	}

	public static synchronized DatabaseHelper getInstance(Context context, DatabaseTable table) {

		databaseTables.add(table);

		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (DatabaseTable databaseTable : getDatabaseTables()) {
			StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
			queryBuilder.append(databaseTable.getTableName()).append(" (");
			for (Map.Entry<String, String> columns : databaseTable.getColumns().entrySet()) {
				queryBuilder.append(columns.getKey()).append(" ").append(columns.getValue()).append(",");
			}
			queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));
			queryBuilder.append(")");
			db.execSQL(queryBuilder.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (DatabaseTable databaseTable : getDatabaseTables()) {
			db.execSQL("DROP TABLE IF EXISTS " + databaseTable.getTableName());
		}
		this.onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (DatabaseTable databaseTable : getDatabaseTables()) {
			db.execSQL("DROP TABLE IF EXISTS " + databaseTable.getTableName());
		}
		this.onCreate(db);
	}

	public Set<DatabaseTable> getDatabaseTables() {
		return databaseTables;
	}
}
