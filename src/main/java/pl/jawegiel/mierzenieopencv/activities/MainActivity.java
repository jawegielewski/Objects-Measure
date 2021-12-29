package pl.jawegiel.mierzenieopencv.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import pl.jawegiel.mierzenieopencv.BuildConfig;
import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.SomeConstants;
import pl.jawegiel.mierzenieopencv.Util;

public class MainActivity extends BaseActivity {

    private AdView adView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setThemeBasedOnPrefs(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        initAdsFromMainActivity();
        Util.initAppRate(this);

        initPermissions();

        findViewById(R.id.button3).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Stages.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        findViewById(R.id.buttonSettings).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        findViewById(R.id.buttonAbout).setOnClickListener(view -> {
            Intent intent = new Intent(new Intent(MainActivity.this, Info.class));
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        findViewById(R.id.buttonContact).setOnClickListener(view -> sendEmail());

        findViewById(R.id.buttonExit).setOnClickListener(view -> {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
    }

    @Override
    public void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
    }

    @Override
    BaseActivity getActivity() {
        return this;
    }

    void initPermissions() {
        ArrayList<String> arrPerm = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    public void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"jawegielewski@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.review_from_client) + " " + createPhoneTemplate() + "[" + Util.getAppVersion(this) + "]");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
            Toast.makeText(MainActivity.this, getResources().getString(R.string.do_not_change_subject), Toast.LENGTH_LONG).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public String createPhoneTemplate() {
        return "[" + Build.MANUFACTURER + "]" +
                "[" + Build.MODEL + "]" +
                "[" + Build.VERSION.SDK_INT + "]";
    }

    public void initAdsFromMainActivity() {
        LinearLayout linearLayout = findViewById(R.id.llAdView);
        adView = new AdView(this);
        adView.setAdUnitId(BuildConfig.FLAVOR.equals("prod") ? SomeConstants.MAIN_ACTIVITY_BANNER_ID : SomeConstants.TEST_BANNER_ID);
        adView.setAdSize(AdSize.BANNER);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        linearLayout.addView(adView);
    }
}