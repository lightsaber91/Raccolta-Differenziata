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
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ListaComuni extends Activity implements TextWatcher,
		OnItemClickListener {

	// controlli della UI
	private ListView list;
	private EditText filter_txt;

	// arrays e dati utili
	private SQLiteDataSource db;
	private ArrayAdapter<String> blankAdapter;
	private Comune[] lista;
	private boolean ricorda;

	// max numero di citta visualizzabili nella listView
	private static final int MAX_COUNT = 10;

	// lunghezza minima del testo per effettuare la query
	private static final int MIN_LENGTH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_comuni);

		// apro connessione con DB
		db = new SQLiteDataSource(getApplicationContext());
		db.open();

		Intent data = getIntent();
		Bundle dataGot = data.getExtras();
		String filter = dataGot.getString("COMUNE");
		ricorda = dataGot.getBoolean("RICORDA");

		// inizializzazione
		list = (ListView) findViewById(R.id.list);
		filter_txt = (EditText) findViewById(R.id.filter_txt);

		filter_txt.append(filter);

		// imposto i listener
		list.setOnItemClickListener(this);
		filter_txt.addTextChangedListener(this);

		// array adapter speciale per svuotare la lista
		blankAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.row,
				new String[] {});

		if (!filter_txt.getText().toString().equals("")) {
			effettuaRicerca();
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

	public void home() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	/**
	 * Mostra i risultati della query sul DB in una lista box
	 * 
	 * @param c
	 *            Cursore ricavato dalla query.
	 */
	private void showResult(Cursor c) {
		if (c.isClosed())
			return;

		// prendo gli indici delle colonne ottenute dalla query
		int comune_index = c.getColumnIndex(DbUtility.DESCRIZIONE);
		int id_index = c.getColumnIndex(DbUtility.ID);

		// alloco array
		lista = new Comune[c.getCount()];

		int i = 0;

		// leggo i dati ottenuti dalla query
		while (i < c.getCount()) {

			Comune comune = new Comune();
			comune.setNome(c.getString(comune_index));
			comune.setId(c.getInt(id_index));

			lista[i] = comune;
			i++;
			if (!c.moveToNext())
				break;
		}
		c.close();

		setListView();
	}

	/**
	 * Imposta la lista per la visualizzazione dei comuni.
	 */
	private void setListView() {
		String[] comuni_list = new String[lista.length];
		for (int i = 0; i < comuni_list.length; i++) {
			comuni_list[i] = lista[i].getNome();
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.row, R.id.row, comuni_list);
		list.setAdapter(arrayAdapter);
	}

	private void effettuaRicerca() {
		String filter = filter_txt.getText().toString().trim();
		if (filter.length() < MIN_LENGTH) {
			// svuota la lista
			list.setAdapter(blankAdapter);
			return;
		}

		// ottengo istanza del DB
		SQLiteDatabase rifiutiDb = db.getDb();

		try {
			// eseguo query sul db
			Cursor c = rifiutiDb.query(false, DbUtility.TABLE_COMUNE,
					new String[] { DbUtility.ID, DbUtility.DESCRIZIONE },
					DbUtility.DESCRIZIONE + " LIKE ?",
					new String[] { filter.toUpperCase(Locale.getDefault())
							+ "%" }, null, null, DbUtility.DESCRIZIONE,
					String.valueOf(MAX_COUNT));

			c.moveToFirst();

			// non mostrare più di un certo numero di risultati dalla query
			if (c.getCount() > 0) {
				showResult(c);
			} else {
				// svuota la lista
				list.setAdapter(blankAdapter);
			}
		} catch (SQLiteException exc) {
			Log.e("DB_QUERY", exc.getMessage());
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		effettuaRicerca();
	}

	private void terminaRicerca(String comune, int id) {

		// chiudo connessione con DB
		db.close();

		if (ricorda)
			MainActivity.saveData(comune, id);

		// preparo i dati per il passaggio all'activity principale
		Intent i = new Intent();
		i.putExtra("COMUNE", comune);
		i.putExtra("ID", id);

		// imposto il codice di ritorno
		setResult(RESULT_OK, i);

		// termino questa activity
		super.finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		terminaRicerca(lista[(int) id].getNome(), lista[(int) id].getId());
	}
}