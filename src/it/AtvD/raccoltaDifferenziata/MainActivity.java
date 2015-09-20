package it.AtvD.raccoltaDifferenziata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String comune;
	private String pkg;
	private EditText et;
	private Toast toast;
	private Button btn_ricerca;
	private Button b0;
	private ImageButton binfo;
	private CheckBox ricordaCitta;
	private boolean ricorda;
	public static final String PATH = Environment.getExternalStorageDirectory()
			+ "//RaccoltaDifferenziata//localita.dat";

	public static final int MAX_COUNT = 1;
	private static final int REQUEST_ID = 10;
	public int id_comune;
	public String nome_comune;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SQLiteDataSource db = new SQLiteDataSource(MainActivity.this);
		db.open();
		db.close();

		id_comune = -1;
		nome_comune = null;
		ricorda = false;

		loadData();

		// sono presenti dati nel file
		if (id_comune != -1)
			startSecondActivity();

		et = (EditText) this.findViewById(R.id.etxInsComune);
		toast = Toast.makeText(this, "Comune non presente nel DB",
				Toast.LENGTH_LONG);

		b0 = (Button) this.findViewById(R.id.btnConferma);
		b0.setOnClickListener(check_comune);

		btn_ricerca = (Button) this.findViewById(R.id.btn_ricerca);
		btn_ricerca.setOnClickListener(cerca_comune);

		binfo = (ImageButton) this.findViewById(R.id.imginfo);
		binfo.setOnClickListener(showInfo);

		ricordaCitta = (CheckBox) findViewById(R.id.ricordacheck);
		ricordaCitta.setChecked(true);
		ricorda = true;
		ricordaCitta.setOnCheckedChangeListener(checked_change);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(this.getText(R.string.strexit)).setIcon(R.drawable.exit)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						exit();
						return true;
					}
				});
		return true;
	}

	public void exit() {
		Intent intentF = new Intent(Intent.ACTION_MAIN);
		intentF.addCategory(Intent.CATEGORY_HOME);
		intentF.addCategory(Intent.CATEGORY_DEFAULT);
		startActivity(intentF);
	}

	CompoundButton.OnCheckedChangeListener checked_change = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (buttonView.getId() == R.id.ricordacheck)
				ricorda = isChecked;
		}
	};

	View.OnClickListener check_comune = new View.OnClickListener() {
		public void onClick(View v) {
			comune = et.getText().toString().trim();
			if (!cercaComune(comune)) {
				toast.show();
			} else {
				if (!et.getText().toString().equals(""))
					startSecondActivity();
			}
		}
	};

	private void startInfoActivity() {
		Intent i = new Intent(MainActivity.this, Info.class);
		startActivity(i);
	}

	View.OnClickListener showInfo = new View.OnClickListener() {
		public void onClick(View v) {
			startInfoActivity();
		}
	};

	public static void saveData(String comune, int id) {
		try {
			boolean success = new File(
					Environment.getExternalStorageDirectory()
							+ "//RaccoltaDifferenziata").mkdir();
			if (!success)
				Log.e("DIRECTORY", "Directory created");
			FileWriter fstream = new FileWriter(PATH);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(comune);
			out.write("\n");
			out.write(String.valueOf(id));
			out.close();
		} catch (Exception e) {
			Log.e("ERRORE_FILE_WRITE", e.getMessage());
		}
	}

	private void loadData() {
		BufferedReader br = null;
		File f = new File(PATH);
		if (!f.exists())
			return;
		try {
			br = new BufferedReader(new FileReader(PATH));
			nome_comune = br.readLine();
			id_comune = Integer.valueOf(br.readLine()).intValue();
			br.close();

		} catch (IOException e) {
			Log.e("ERRORE_FILE_READ", e.getMessage());
		}
	}

	void startSecondActivity() {
		if (ricorda) {
			saveData(nome_comune, id_comune);
		}
		Intent i = new Intent(MainActivity.this, SecondActivity.class);
		pkg = getPackageName();
		i.putExtra(pkg + "comune", nome_comune);
		i.putExtra(pkg + "id_comune", id_comune);
		startActivity(i);
	}

	View.OnClickListener cerca_comune = new View.OnClickListener() {
		public void onClick(View v) {
			comune = et.getText().toString().trim();
			Intent i = new Intent(MainActivity.this, ListaComuni.class);
			i.putExtra("COMUNE", comune);
			i.putExtra("RICORDA", ricorda);
			startActivityForResult(i, REQUEST_ID);
		}
	};

	/**
	 * Ricerca il comune e restituisce true se lo trova e false altrimenti. Se
	 * lo trova imposta anche la variabile 'id_index' con l'id del comune in
	 * modo da usarlo per gli scopi successivi.
	 * 
	 * @param filter
	 *            il comune da cercare.
	 */
	public boolean cercaComune(String filter) {

		// apro connessione con DB
		SQLiteDataSource db = new SQLiteDataSource(getApplicationContext());
		db.open();

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

			// controlla il risultato
			if (c.getCount() != 1) {
				c.close();
				db.close();
				return false;
			}

			// ottiene l'id del comune
			int id_index = c.getColumnIndex(DbUtility.ID);
			int comune_index = c.getColumnIndex(DbUtility.DESCRIZIONE);
			id_comune = c.getInt(id_index);
			nome_comune = c.getString(comune_index);

		} catch (SQLiteException exc) {
			Log.e("DB_QUERY", exc.getMessage());
		}

		// chiude connessione con db
		db.close();
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ID && resultCode == RESULT_OK) {
			Bundle dataGot = data.getExtras();
			nome_comune = dataGot.getString("COMUNE");
			id_comune = dataGot.getInt("ID");
			et.setText("");
			startSecondActivity();
		}
	}

}