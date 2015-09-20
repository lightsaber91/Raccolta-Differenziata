package it.AtvD.raccoltaDifferenziata;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RaccoltaPerGiorno extends Activity {

	private TextView oggitxt;

	private String pkg;
	private String comune;
	private int _id;
	private String oggi;
	private ArrayAdapter<String> blankAdapter;
	private static final String[] days = { "Domenica", "Lunedì", "Martedì",
			"Mercoledì", "Giovedì", "Venerdì", "Sabato" };
	private ListView materiali_giorno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_raccolta_per_giorno);

		// array adapter speciale per svuotare la lista
		blankAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.row,
				new String[] {});

		Bundle bundle = getIntent().getExtras();
		pkg = getPackageName();
		_id = bundle.getInt(pkg + "id_comune");
		comune = bundle.getString(pkg + "comune");

		oggi = getDay();

		oggitxt = (TextView) findViewById(R.id.oggitxt);
		oggitxt.setText("Oggi è " + oggi + " e si butta...");

		materiali_giorno = (ListView) findViewById(R.id.listaprodotti);

		String[] materiali = cosaButto(oggi);
		if (materiali == null) {
			materiali_giorno.setAdapter(blankAdapter);
			oggitxt.append("\n Niente da buttare oggi");
		} else {
			setListView(materiali);
			// oggitxt.append(coloresecchiomateriale("Plastica"));
		}
	}

	/**
	 * Imposta la lista per la visualizzazione dei comuni.
	 */
	private void setListView(String[] materiali) {

		String[] toShow = new String[materiali.length];
		for (int i = 0; i < materiali.length; i++) {
			toShow[i] = materiali[i] + " nel secchio "
					+ coloresecchiomateriale(materiali[i]);
			Log.i("MATERIALE", materiali[i]);
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.row, R.id.row, toShow);
		materiali_giorno.setAdapter(arrayAdapter);
	}

	private String getDay() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		return days[day - 1];
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(this.getText(R.string.strhome)).setIcon(R.drawable.home)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						home();
						return true;
					}
				});
		return true;
	}
	
	private void home() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
	
	/**
	 * Prende in input un giorno della settimana scritto con Lettera Maiuscola
	 * iniziale e ì accentata finale e da un array di stringhe con i nomi dei
	 * materiali da buttare in quel giorno
	 */
	public String[] cosaButto(String giorno) {

		// apro connessione con DB
		SQLiteDataSource db = new SQLiteDataSource(getApplicationContext());
		db.open();

		// ottengo istanza del DB
		SQLiteDatabase rifiutiDb = db.getDb();
		String materiali[] = null;

		try {
			// eseguo query sul db
			String colonnaRis = "materialiC";
			Cursor c = rifiutiDb.rawQuery(
					"SELECT "
							+ DbUtility.getCol(DbUtility.TABLE_MATERIALE,
									DbUtility.DESCRIZIONE)
							+ " AS "
							+ colonnaRis
							+ " FROM "
							+ DbUtility.TABLE_COMUNE
							+ " , "
							+ DbUtility.TABLE_RACCOLTA
							+ " , "
							+ DbUtility.TABLE_RACCOLTAMATERIALI
							+ " , "
							+ DbUtility.TABLE_MATERIALE
							+ " WHERE "
							+ DbUtility.getCol(DbUtility.TABLE_COMUNE,
									DbUtility.ID)
							+ " = "
							+ DbUtility.getCol(DbUtility.TABLE_RACCOLTA,
									DbUtility.ID_COMUNE)
							+ " AND "
							+ DbUtility.getCol(DbUtility.TABLE_RACCOLTA,
									DbUtility.ID)
							+ " = "
							+ DbUtility.getCol(
									DbUtility.TABLE_RACCOLTAMATERIALI,
									DbUtility.ID_RACCOLTA)
							+ " AND "
							+ DbUtility.getCol(
									DbUtility.TABLE_RACCOLTAMATERIALI,
									DbUtility.ID_MATERIALE)
							+ " = "
							+ DbUtility.getCol(DbUtility.TABLE_MATERIALE,
									DbUtility.ID)
							+ " AND "
							+ DbUtility.getCol(DbUtility.TABLE_RACCOLTA,
									DbUtility.DESCRIZIONE)
							+ " = ? AND "
							+ DbUtility.getCol(DbUtility.TABLE_COMUNE,
									DbUtility.DESCRIZIONE) + " = ?",
					new String[] { giorno, comune });
			c.moveToFirst();

			// controlla il risultato
			if (c.getCount() <= 0) {
				return null;
			}

			int i = 0;
			materiali = new String[c.getCount()];
			// leggo i dati ottenuti dalla query
			while (i < c.getCount()) {
				materiali[i] = c.getString(c.getColumnIndex(colonnaRis));
				i++;
				if (!c.moveToNext())
					break;
			}

			c.close();

		} catch (SQLiteException exc) {
			Log.e("DB_QUERY", exc.getMessage());
		}

		// chiude connessione con db
		db.close();
		return materiali;
	}

	/**
	 * Prende in input il nome di un materiale e restituisce il colore del
	 * secchio.
	 * 
	 */
	public String coloresecchiomateriale(String materiale) {

		// apro connessione con DB
		SQLiteDataSource db = new SQLiteDataSource(getApplicationContext());
		db.open();

		// ottengo istanza del DB
		SQLiteDatabase rifiutiDb = db.getDb();
		String colori = null;

		try {
			// eseguo query sul db
			Cursor c = rifiutiDb.rawQuery(
					"SELECT "
							+ DbUtility.COLORE
							+ " FROM "
							+ DbUtility.TABLE_COLORESECCHIO
							+ " , "
							+ DbUtility.TABLE_MATERIALE
							+ " WHERE "
							+ DbUtility.getCol(DbUtility.TABLE_COLORESECCHIO,
									DbUtility.ID_COMUNE)
							+ " = ? AND "
							+ DbUtility.getCol(DbUtility.TABLE_COLORESECCHIO,
									DbUtility.ID_MATERIALE)
							+ " = "
							+ DbUtility.getCol(DbUtility.TABLE_MATERIALE,
									DbUtility.ID)
							+ " AND "
							+ DbUtility.getCol(DbUtility.TABLE_MATERIALE,
									DbUtility.DESCRIZIONE) + " like ?",
					new String[] { String.valueOf(_id), materiale });

			c.moveToFirst();

			// controlla il risultato
			if (c.getCount() <= 0) {
				c.close();
				db.close();
				return null;
			}

			colori = new String(c.getString(c.getColumnIndex(DbUtility.COLORE)));

			c.close();

		} catch (SQLiteException exc) {
			Log.e("DB_QUERY", exc.getMessage());
		}

		// chiude connessione con db
		db.close();
		return colori;
	}
}
