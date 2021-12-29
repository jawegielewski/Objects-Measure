package pl.jawegiel.mierzenieopencv.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;

public abstract class BaseActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    BaseActivity childActivity;

    @Override
    @SuppressWarnings("CommitPrefEdits")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        spEditor = sp.edit();
        childActivity = getActivity();

        if(childActivity instanceof MainActivity)
            MobileAds.initialize(this, initializationStatus -> {});
    }

    @Override
    protected void onPause() {
        if (childActivity instanceof Stages) {
            spEditor.putFloat("px_cm", 0f);
            spEditor.putFloat("rozmiar_px", 0f);
            spEditor.putFloat("rozmiar_px_x", (float) 0f);
            spEditor.putFloat("rozmiar_px_y", (float) 0f);
            spEditor.apply();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (childActivity instanceof Stages) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (childActivity instanceof Stages)
            return Util.onOptionsItemSelected(this, item);
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        if (childActivity instanceof Stages) {
            Intent intent = new Intent(childActivity, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    abstract BaseActivity getActivity();
}
