package pl.jawegiel.mierzenieopencv.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import pl.jawegiel.mierzenieopencv.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}

	@Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String fontPref = sp.getString("font", "Arial");
		if(fontPref.equals("Ginger")) setTheme(R.style.AppThemeWithCustomFont);
		if(fontPref.equals("Arial")) setTheme(R.style.AppThemeWithClassicFont);
		addPreferencesFromResource(R.xml.preferences);

		if (Build.VERSION.SDK_INT < 26) {
			ListPreference mCheckBoxPref = (ListPreference) findPreference("font");
			PreferenceCategory mCategory = (PreferenceCategory) findPreference("other");
			mCategory.removePreference(mCheckBoxPref);
		}

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String s) {
		String fontPref = sp.getString("font", "Arial");
		if(fontPref.equals("Ginger")) {setTheme(R.style.AppThemeWithCustomFont); restartActivity(); }
		if(fontPref.equals("Arial")) {  setTheme(R.style.AppThemeWithClassicFont); restartActivity(); }
	}

	private void restartActivity() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
}
