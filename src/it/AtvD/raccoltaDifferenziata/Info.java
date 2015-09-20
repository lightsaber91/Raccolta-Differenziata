package it.AtvD.raccoltaDifferenziata;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class Info extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
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

}
