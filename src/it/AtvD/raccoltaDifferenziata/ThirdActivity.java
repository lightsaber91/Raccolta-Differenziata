package it.AtvD.raccoltaDifferenziata;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ThirdActivity extends Activity implements View.OnClickListener {

	private String pkg;
	private String comune;
	private int _id;
	private Button ricercaEAN;
	private EditText campoEAN;

	private static final Intent SCAN_INTENT = new Intent(
			"com.google.zxing.client.android.SCAN");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutinput);

		Bundle bundle = getIntent().getExtras();
		pkg = getPackageName();
		comune = bundle.getString(pkg + "comune");
		_id = bundle.getInt(pkg + "id_comune");

		campoEAN = (EditText) findViewById(R.id.etxprodotto);
		ricercaEAN = (Button) findViewById(R.id.btncerca);
		ricercaEAN.setOnClickListener(this);

		PackageManager packageManager = this.getPackageManager();
		ResolveInfo resolveInfo = packageManager.resolveActivity(SCAN_INTENT,
				PackageManager.GET_RESOLVED_FILTER);
		if (resolveInfo == null) {
			((ImageButton) this.findViewById(R.id.imgbar)).setEnabled(false);
			((TextView) this.findViewById(R.id.textView3))
					.setText("Non hai installato nessuna applicazione per effettuare lo scan!!!\nScaricala qui: http://www.appbrain.com/app/com.google.zxing.client.android");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {
			String scanResult = data.getStringExtra("SCAN_RESULT");
			((TextView) this.findViewById(R.id.textView3)).setText(String
					.format("Risultato dello scan: %1$s", scanResult));

			String prodotto = cercaEAN(scanResult);
			if (prodotto == null) {
				Toast.makeText(getApplicationContext(), R.string.prodnotfound,
						Toast.LENGTH_SHORT).show();
			} else {
				visualizzaSchedaProdotto(prodotto);
			}
		} else {
			((TextView) this.findViewById(R.id.textView3))
					.setText("Operazione annullata!");
		}
	}

	public void onScan(View view) {
		startActivityForResult(SCAN_INTENT, 0);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(this.getText(R.string.strhome)).setIcon(R.drawable.home)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						home();
						return true;
					}
				});
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
	
	public void home() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}


	/**
	 * Restituisce il nome del prodotto corrispondente al codice a barre cercato
	 */
	public String cercaEAN(String ean) {
		// apro connessione con DB
		SQLiteDataSource db = new SQLiteDataSource(getApplicationContext());
		db.open();

		// ottengo istanza del DB
		SQLiteDatabase rifiutiDb = db.getDb();
		String prodotto = null;
		try {

			// eseguo query sul db
			Cursor c = rifiutiDb.rawQuery("SELECT " + DbUtility.DESCRIZIONE
					+ " FROM " + DbUtility.TABLE_PRODOTTO + " WHERE "
					+ DbUtility.getCol(DbUtility.TABLE_PRODOTTO, DbUtility.EAN)
					+ " = ?", new String[] { ean });

			c.moveToFirst();

			// controlla il risultato
			if (c.getCount() != 1) {
				return null;
			}

			prodotto = c.getString(c.getColumnIndex(DbUtility.DESCRIZIONE));

		} catch (SQLiteException exc) {
			Log.e("DB_QUERY", exc.getMessage());
		}

		// chiude connessione con db
		db.close();
		return prodotto;

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btncerca) {
			String prodotto = cercaEAN(campoEAN.getText().toString().trim());
			if (prodotto == null) {
				Toast.makeText(getApplicationContext(), R.string.prodnotfound,
						Toast.LENGTH_SHORT).show();
			} else {
				visualizzaSchedaProdotto(prodotto);
			}

		}
	}

	private void visualizzaSchedaProdotto(String prodotto) {
		Intent i = new Intent(this, SchedaProdotto.class);
		pkg = getPackageName();
		i.putExtra(pkg + "comune", comune);
		i.putExtra(pkg + "id_comune", _id);
		i.putExtra("PRODOTTO", prodotto);
		startActivity(i);
	}
}