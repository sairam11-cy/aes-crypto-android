package com.evgenii.aescrypto;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.evgenii.jsevaluator.interfaces.JsCallback;

public class MainActivity extends Activity {

	protected JsEncryptor mJsEncryptor;
	protected EditText mMessage;
	protected EditText mPassword;
	public boolean isEncryptingOrDecrypting = false;
	protected boolean mJustCopied = false;
	public Clipboard mClipboard;
	public Decrypt mDecrypt;

	public String getEncryptMenuTitle() {
		if (mJustCopied)
			return getResources().getString(R.string.menu_encrypt_title_copied);
		else
			return getResources().getString(R.string.menu_encrypt_title);
	}

	public JsEncryptor getEncryptor() {
		return mJsEncryptor;
	}

	public boolean hasMessage() {
		return messageTrimmed().length() > 0;
	}

	public boolean hasPassword() {
		return passwordTrimmed().length() > 0;
	}

	public boolean isEncryptable() {
		return hasMessage() && hasPassword() && !isEncryptingOrDecrypting;
	}

	public String messageTrimmed() {
		return mMessage.getText().toString().trim();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mJsEncryptor = JsEncryptor.evaluateAllScripts(this);

		mMessage = (EditText) findViewById(R.id.message);
		mPassword = (EditText) findViewById(R.id.password);
		mClipboard = new Clipboard(this);
		mDecrypt = new Decrypt(this);

		setupInputChange();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onEncryptClicked() {
		isEncryptingOrDecrypting = true;
		invalidateOptionsMenu();
		mJsEncryptor.encrypt(messageTrimmed(), passwordTrimmed(), new JsCallback() {
			@Override
			public void onResult(final String encryptedMessage) {
				isEncryptingOrDecrypting = false;
				invalidateOptionsMenu();
				storeMessageInClipboard(encryptedMessage);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_encrypt:
			onEncryptClicked();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void onPasswordOrMessageChanged() {
		mJustCopied = false;
		invalidateOptionsMenu();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_decrypt).setVisible(mDecrypt.isDecryptable());
		menu.findItem(R.id.action_decrypt).setTitle(mDecrypt.currentDecryptMenuTitle);

		menu.findItem(R.id.action_encrypt).setTitle(getEncryptMenuTitle());

		menu.findItem(R.id.action_encrypt).setEnabled(isEncryptable());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDecrypt.storeTextToDecrypt();
		mDecrypt.decryptAndUpdate();
	}

	public String passwordTrimmed() {
		return mPassword.getText().toString().trim();
	}

	private void setupInputChange() {
		mMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				onPasswordOrMessageChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		mPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				onPasswordOrMessageChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	private void storeMessageInClipboard(String message) {
		mClipboard.set(message);
		mJustCopied = true;
		invalidateOptionsMenu();
	}
}
