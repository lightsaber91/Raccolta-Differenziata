package it.AtvD.raccoltaDifferenziata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDataSource {
	private DbHelper helper;
	private SQLiteDatabase database;

	public SQLiteDataSource(Context context) {
		helper = new DbHelper(context);
	}

	public void open() {
		database = helper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public SQLiteDatabase getDb() {
		return database;
	}
}
