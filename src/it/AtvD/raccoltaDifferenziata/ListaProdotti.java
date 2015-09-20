package it.AtvD.raccoltaDifferenziata;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ListaProdotti extends Activity implements TextWatcher,
		OnItemClickListener {

	private String comune;
	private String pkg;
	private int _id;

	private ListView lista_prodotti;
	private EditText product;
	private SQLiteDataSource db;
	private ArrayAdapter<String> blankAdapter;
	private String[] prodotti;

	private static final int MAX_COUNT = 10;

	private static final int MIN_LENGTH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_prodotti);

		Bundle bundle = getIntent().getExtras();
		pkg = getPackageName();
		_id = bundle.getInt(pkg + "id_comune");
		comune = bundle.getString(pkg + "comune");

		lista_prodotti = (ListView) findViewById(R.id.listprod);
		product = (EditText) findViewById(R.id.campo_prodotto);
		product.append("");

		// imposto i listener
		lista_prodotti.setOnItemClickListener(this);
		product.addTextChangedListener(this);

		// array adapter speciale per svuotare la lista
		blankAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.row,
				new String[] {});

		// apro connessione con DB
		db = new SQLiteDataSource(getApplicationContext());
		db.open();
		if (!product.getText().toString().equals("")) {
			RicercaProdotti();
		}
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
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	private void RicercaProdotti() {
		String filter = product.getText().toString().trim();
		if (filter.length() < MIN_LENGTH) {
			// svuota la lista
			lista_prodotti.setAdapter(blankAdapter);
			return;
		}

		// apro connessione con DB
		SQLiteDataSource db = new SQLiteDataSource(getApplicationContext());
		db.open();

		// ottengo istanza del DB
		SQLiteDatabase rifiutiDb = db.getDb();

		try {
			// eseguo query sul db

			Cursor c = rifiutiDb.query(false, DbUtility.TABLE_PRODOTTO,
					new String[] { DbUtility.ID, DbUtility.DESCRIZIONE },
					DbUtility.DESCRIZIONE + " LIKE ?",
					new String[] { filter.toUpperCase(Locale.getDefault())
							+ "%" }, null, null, DbUtility.DESCRIZIONE,
					String.valueOf(MAX_COUNT));

			c.moveToFirst();

			if (c.getCount() > 0) {
				showResult(c);
			} else {
				// svuota la lista
				lista_prodotti.setAdapter(blankAdapter);
			}
		} catch (SQLiteException exc) {
			Log.e("DB_QUERY", exc.getMessage());
		}
		// chiude connessione con db
	}

	private void setListView() {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.row, R.id.row, prodotti);
		lista_prodotti.setAdapter(arrayAdapter);
	}

	private void showResult(Cursor c) {
		if (c.isClosed())
			return;

		// controlla il risultato
		if (c.getCount() <= 0) {
			return;
		}

		prodotti = new String[c.getCount()];

		int i = 0;
		// leggo i dati ottenuti dalla query
		while (i < c.getCount()) {
			prodotti[i] = c.getString(c.getColumnIndex(DbUtility.DESCRIZIONE));
			i++;
			if (!c.moveToNext())
				break;
		}

		c.close();
		setListView();
	}

	@Override
	public void afterTextChanged(Editable s) {
		RicercaProdotti();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	private void mostraSchedaProdotto(int selected) {
		db.close();
		// preparo i dati per il passaggio all'activity principale
		Intent i = new Intent(this, SchedaProdotto.class);
		pkg = getPackageName();
		i.putExtra(pkg + "comune", comune);
		i.putExtra(pkg + "id_comune", _id);
		i.putExtra("PRODOTTO", prodotti[selected]);
		startActivity(i);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		mostraSchedaProdotto((int) id);
	}

}