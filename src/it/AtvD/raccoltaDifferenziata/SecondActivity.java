package it.AtvD.raccoltaDifferenziata;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SecondActivity extends Activity {

	private String pkg;
	private String comune;
	private int _id;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutcomune);

		Bundle bundle = getIntent().getExtras();
		pkg = getPackageName();
		_id = bundle.getInt(pkg + "id_comune");
		comune = bundle.getString(pkg + "comune");

		TextView tx = (TextView) this.findViewById(R.id.textView4);
		tx.setText("Comune di " + comune);

		ImageButton b1 = (ImageButton) this.findViewById(R.id.img1);
		b1.setOnClickListener(consulta_calendario);
		ImageButton b2 = (ImageButton) this.findViewById(R.id.img2);
		b2.setOnClickListener(consulta_elenco);
		ImageButton b3 = (ImageButton) this.findViewById(R.id.img3);
		b3.setOnClickListener(cerca_prodotto);
		ImageButton b4 = (ImageButton) this.findViewById(R.id.img4);
		b4.setOnClickListener(back);

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
		File f = new File(MainActivity.PATH);
		if (f.exists())
			f.delete();
		
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	void startCalendario() {
		Intent i = new Intent(SecondActivity.this, RaccoltaPerGiorno.class);
		pkg = getPackageName();
		i.putExtra(pkg + "comune", comune);
		i.putExtra(pkg + "id_comune", _id);
		startActivity(i);
	}

	void startListaProdotti() {
		Intent i = new Intent(SecondActivity.this, ListaProdotti.class);
		pkg = getPackageName();
		i.putExtra(pkg + "comune", comune);
		i.putExtra(pkg + "id_comune", _id);
		startActivity(i);
	}

	View.OnClickListener consulta_calendario = new View.OnClickListener() {
		public void onClick(View v) {
			startCalendario();
		}
	};

	View.OnClickListener consulta_elenco = new View.OnClickListener() {
		public void onClick(View v) {
			startListaProdotti();
		}
	};
	View.OnClickListener cerca_prodotto = new View.OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(SecondActivity.this, ThirdActivity.class);
			i.putExtra(pkg + "comune", comune);
			startActivity(i);
		}

	};

	View.OnClickListener back = new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
}