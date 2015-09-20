package it.AtvD.raccoltaDifferenziata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private Context context;
	public SQLiteDatabase myDataBase;

	public DbHelper(Context context) {
		super(context, DbUtility.DB_NAME, null, DB_VERSION);
		this.context = context;
		boolean dbexist = checkdatabase();

		if (!dbexist) {
			try {

				Toast.makeText(context, R.string.initdbstart,
						Toast.LENGTH_SHORT).show();

				createdatabase();
				Toast.makeText(context, R.string.initdbend, Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				Log.e("DB", e.getMessage());
			}
		}
	}

	public void createdatabase() throws IOException {
		boolean dbexist = checkdatabase();
		if (dbexist) {
		} else {
			this.getReadableDatabase();
			try {
				copydatabase();
			} catch (IOException e) {
				Log.e("DB", e.getMessage());
			}
		}
	}

	private boolean checkdatabase() {
		/**
		 * Controllo che il DB non esista già
		 */
		boolean checkdb = false;
		try {
			File dbfile = context.getDatabasePath(DbUtility.DB_NAME);
			checkdb = dbfile.exists();
		} catch (SQLiteException e) {
			Log.e("DB", e.getMessage());
		}

		return checkdb;
	}

	private void copydatabase() throws IOException {

		// Apro database come stream
		InputStream myinput = context.getAssets().open(DbUtility.DB_NAME);

		// Apro db nuovo vuoto come stream in scrittura
		OutputStream myoutput = new FileOutputStream(
				context.getDatabasePath(DbUtility.DB_NAME));

		// eseguo copia
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myinput.read(buffer)) > 0) {
			myoutput.write(buffer, 0, length);
		}

		// chiusura streams
		myoutput.flush();
		myoutput.close();
		myinput.close();

	}

	public void open() {
		// Apro DB
		String mypath = context.getDatabasePath(DbUtility.DB_NAME)
				.getAbsolutePath();
		myDataBase = SQLiteDatabase.openDatabase(mypath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	public synchronized void close() {
		// chiudo DB
		myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}