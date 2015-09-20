package it.AtvD.raccoltaDifferenziata;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

public class SchedaProdotto extends Activity {

	private String comune;
	private String prodotto;
	@SuppressWarnings("unused")
	private int _id;
	private String pkg;
	private TextView scheda;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheda_prodotto);

		Bundle bundle = getIntent().getExtras();
		pkg = getPackageName();
		comune = bundle.getString(pkg + "comune");
		_id = bundle.getInt(pkg + "id_comune");
		prodotto = bundle.getString("PRODOTTO");

		scheda = (TextView) findViewById(R.id.scheda);

		generaDescrizione();
	}

	@Override
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
		Intent i = new Intent(SchedaProdotto.this, MainActivity.class);
		startActivity(i);
	}

	/**
	 * Prende in input il nome di una prodotto e restituisce un array di coppie
	 * <materiale, colore secchio>.
	 * 
	 */
	public MaterialeSecchio[] coloreSecchi(String prodotto) {

		// apro connessione con DB
		SQLiteDataSource db = new SQLiteDataSource(getApplicationContext());
		db.open();

		// ottengo istanza del DB
		SQLiteDatabase rifiutiDb = db.getDb();
		MaterialeSecchio materiali[] = null;

		try {
			// eseguo query sul db
			Cursor c = rifiutiDb.rawQuery(
					"SELECT "
							+ DbUtility.getColName(DbUtility.TABLE_MATERIALE,
									DbUtility.DESCRIZIONE)
							+ " , "
							+ DbUtility.getColName(
									DbUtility.TABLE_COLORESECCHIO,
									DbUtility.COLORE)
							+ " FROM "
							+ DbUtility.TABLE_COLORESECCHIO
							+ " , "
							+ DbUtility.TABLE_COMUNE
							+ " , "
							+ DbUtility.TABLE_MATERIALE
							+ " , "
							+ DbUtility.TABLE_MATERIALIPRODOTTI
							+ " , "
							+ DbUtility.TABLE_PRODOTTO
							+ " WHERE "
							+ DbUtility.getCol(DbUtility.TABLE_COMUNE,
									DbUtility.ID)
							+ " = "
							+ DbUtility.getCol(DbUtility.TABLE_COLORESECCHIO,
									DbUtility.ID_COMUNE)
							+ " AND "

							+ DbUtility.getCol(DbUtility.TABLE_COLORESECCHIO,
									DbUtility.ID_MATERIALE)
							+ " = "
							+ DbUtility.getCol(
									DbUtility.TABLE_MATERIALIPRODOTTI,
									DbUtility.ID_MATERIALE)
							+ " AND "
							+ DbUtility.getCol(
									DbUtility.TABLE_MATERIALIPRODOTTI,
									DbUtility.ID_PRODOTTO)
							+ " = "
							+ DbUtility.getCol(DbUtility.TABLE_PRODOTTO,
									DbUtility.ID)
							+ " AND "
							+ DbUtility.getCol(DbUtility.TABLE_MATERIALE,
									DbUtility.ID)
							+ " = "
							+ DbUtility.getCol(
									DbUtility.TABLE_MATERIALIPRODOTTI,
									DbUtility.ID_MATERIALE)
							+ " AND "
							+ DbUtility.getCol(DbUtility.TABLE_PRODOTTO,
									DbUtility.DESCRIZIONE)
							+ " = ? AND "
							+ DbUtility.getCol(DbUtility.TABLE_COMUNE,
									DbUtility.DESCRIZIONE) + " = ?",
					new String[] { prodotto, comune });
			c.moveToFirst();

			// controlla il risultato
			if (c.getCount() <= 0) {
				c.close();
				db.close();
				return null;
			}

			int i = 0;
			materiali = new MaterialeSecchio[c.getCount()];
			// leggo i dati ottenuti dalla query
			while (i < c.getCount()) {
				materiali[i] = new MaterialeSecchio(c.getString(c
						.getColumnIndex("Descrizione")), c.getString(c
						.getColumnIndex("colore")));
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

	void generaDescrizione() {
		MaterialeSecchio[] mat = coloreSecchi(prodotto);
		if (mat == null) {
			scheda.append(getApplicationContext().getString(
					R.string.prodnotfound1));
			return;
		}

		scheda.append(Html.fromHtml("<font color='#FFFFFF'><b>" + prodotto
				+ ":</b></font><br><br>"));
		scheda.append(Html.fromHtml(getApplicationContext().getString(
				R.string.scheda1)
				+ "<br><br><ul>"));
		for (int i = 0; i < mat.length; i++) {
			scheda.append(Html.fromHtml("<li>" + mat[i].getDescrizione() + " "
					+ getApplicationContext().getString(R.string.scheda2) + " "
					+ mat[i].getColore() + "</li><br><br>"));
		}

		scheda.append(Html.fromHtml("</ul>"));

	}

}
