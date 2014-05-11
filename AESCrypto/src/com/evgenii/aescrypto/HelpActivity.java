package com.evgenii.aescrypto;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HelpActivity extends Activity {

	public void loadText() throws IOException {
		final AssetsFileReader fileReader = new AssetsFileReader(this);
		final String text = fileReader.ReadFile("help.txt");
		final TextView helpTextView = (TextView) findViewById(R.id.helpTextView);
		helpTextView.setText(text);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		try {
			loadText();
		} catch (final IOException e) {
			ShowFatalError.showAlertAndExit(this, "Can not read help file.", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
