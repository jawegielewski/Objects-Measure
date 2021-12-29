package pl.jawegiel.mierzenieopencv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import hotchemi.android.rate.AppRate;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Scalar;

import pl.jawegiel.mierzenieopencv.activities.Info;
import pl.jawegiel.mierzenieopencv.activities.MainActivity;
import pl.jawegiel.mierzenieopencv.activities.SettingsActivity;


public class Util {

    private static final String APP_NAME = "pl.jawegiel.mierzenieopencv";// Package Name
    public static final Scalar WHITE = new Scalar(255, 255, 255);
    static final Scalar BLACK = new Scalar(0, 0, 0);
    public static final Scalar BLUE = new Scalar(0, 0, 255);
    public static final Scalar GREEN = new Scalar(0, 255, 0);
    static final Scalar SCALAR = new Scalar(50, 100, 205, 255);
    static final Scalar GRAY = new Scalar(127, 127, 127);
    private Activity activity;

    public Util(Activity activity) {
        this.activity = activity;
    }

    public static int statusBarHeight(android.content.res.Resources res) {
        return (int) (24 * res.getDisplayMetrics().density);
    }

    public static void setCameraResolutionBasedOnPrefs(String resolutionPref, CameraBridgeViewBase kamera, Context context) {
        if (resolutionPref.equals(context.getString(R.string.lowest)))
            kamera.setMaxFrameSize(320, 280);
        if (resolutionPref.equals(context.getString(R.string.low)))
            kamera.setMaxFrameSize(480, 320);
        if (resolutionPref.equals(context.getString(R.string.medium)))
            kamera.setMaxFrameSize(640, 480);
        if (resolutionPref.equals(context.getString(R.string.high)))
            kamera.setMaxFrameSize(800, 600);
    }

    public static String appFolder(Context context) throws PackageManager.NameNotFoundException {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        PackageInfo p = m.getPackageInfo(s, 0);
        return p.applicationInfo.dataDir;
    }



    public static void initAdsFromStageZero(View view) {

        final TypedArray array = view.getContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
        int actionBarHeight =  array.getDimensionPixelSize(0, -1);

        AdView adView = null;
        if (BuildConfig.FLAVOR.equals("dev")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl2);
            TextView tvInfoAboutIndicators = view.findViewById(R.id.info_about_indicators);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,actionBarHeight);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.BELOW, tvInfoAboutIndicators.getId());
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.TEST_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }
        if (BuildConfig.FLAVOR.equals("prod")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl2);
            TextView tvInfoAboutIndicators = view.findViewById(R.id.info_about_indicators);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,actionBarHeight);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.BELOW, tvInfoAboutIndicators.getId());
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.STAGE_ZERO_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }

        AdRequest adRequest = new AdRequest.Builder()
//				                .addTestDevice("91CB707F937AFCA453A6381871B6206F")
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(view.getContext(), loadAdError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void initAdsFromStageOne(View view) {

        AdView adView = null;
        if (BuildConfig.FLAVOR.equals("dev")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.TEST_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }
        if (BuildConfig.FLAVOR.equals("prod")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.STAGE_ONE_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }

        AdRequest adRequest = new AdRequest.Builder()
//				                .addTestDevice("91CB707F937AFCA453A6381871B6206F")
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(view.getContext(), loadAdError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void initAdsFromStageTwo(View view) {

        final TypedArray array = view.getContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
        int actionBarHeight =  array.getDimensionPixelSize(0, -1);

        AdView adView = null;
        if (BuildConfig.FLAVOR.equals("dev")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl2);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,actionBarHeight);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.TEST_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }
        if (BuildConfig.FLAVOR.equals("prod")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl2);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,actionBarHeight);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.STAGE_TWO_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }

        AdRequest adRequest = new AdRequest.Builder()
//				                .addTestDevice("91CB707F937AFCA453A6381871B6206F")
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(view.getContext(), loadAdError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void initAdsFromStageThree(View view) {

        AdView adView = null;
        if (BuildConfig.FLAVOR.equals("dev")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl);
            TextView tvMeasurementsHeightConverter = view.findViewById(R.id.measurements_height_converter);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.BELOW, tvMeasurementsHeightConverter.getId());
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.TEST_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }
        if (BuildConfig.FLAVOR.equals("prod")) {
            RelativeLayout relativeLayout = view.findViewById(R.id.rl);
            TextView tvMeasurementsHeightConverter = view.findViewById(R.id.measurements_height_converter);
            adView = new AdView(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.BELOW, tvMeasurementsHeightConverter.getId());
            adView.setLayoutParams(layoutParams);
            adView.setAdUnitId(SomeConstants.STAGE_THREE_BANNER_ID);
            adView.setAdSize(AdSize.BANNER);
            relativeLayout.addView(adView);
        }

        AdRequest adRequest = new AdRequest.Builder()
//				                .addTestDevice("91CB707F937AFCA453A6381871B6206F")
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(view.getContext(), loadAdError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void initAppRate(Activity activity) {
        AppRate.with(activity)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(5) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(which -> {
                            Log.d(MainActivity.class.getName(), Integer.toString(which));
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_NAME)));
                        }
                )
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(activity);
    }

    public void showNotification(Context context) {
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle().bigPicture(image)
                .setSummaryText("hehe");
        Notification notification = new NotificationCompat.Builder(context, "a").setContentTitle("title")
                .setContentText("rozwiń").setSmallIcon(R.drawable.button_selector).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX).setStyle(style).setVisibility(NotificationCompat.VISIBILITY_PUBLIC).build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        if (nm != null)
            nm.notify(0, notification);
    }

    static void showHelpDialog(Context context) {
        new AlertDialog.Builder(context, R.style.MyStyle).setIcon(R.drawable.ic_action_help)
                .setTitle(R.string.help)
                .setMessage(Html.fromHtml(context.getResources().getString(R.string.help_desc)))
                .setCancelable(false)
                .setNeutralButton("OK", (dialog, id1) -> dialog.cancel()).create().show();
    }


    public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_help) {
            Util.showHelpDialog(activity);
            return true;
        } else if (id == R.id.action_main) {
            activity.startActivity(new Intent(activity, MainActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            activity.startActivity(new Intent(activity, Info.class));
            return true;

        } else if (id == R.id.action_exit) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(homeIntent);
            return true;
        }
        return true;

    }

    //	@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        String permission = permissions[i];
                        if (Manifest.permission.CAMERA.equals(permission)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }

    public Typeface getGingerTypeface() {
        return Typeface.createFromAsset(activity.getAssets(), "fonts/ginger.ttf");
    }

    public static void setThemeBasedOnPrefs(Activity activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String fontPref = sp.getString("font", "Arial");
        if (fontPref.equals("Ginger")) activity.setTheme(R.style.AppThemeWithCustomFont);
        if (fontPref.equals("Arial")) activity.setTheme(R.style.AppThemeWithClassicFont);
    }

    public static String getAppVersion(Activity activity) {
        String versionName = "";
        try {
            versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static AlertDialog createAlertDialog(Context context, int iconId, int titleId, CharSequence message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog ad = new AlertDialog.Builder(context, R.style.MyStyle).create();
        ad.setIcon(iconId);
        ad.setCancelable(false);
        ad.setTitle(titleId);
        ad.setMessage(message);
        ad.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (dialogInterface, i) -> {
        });
        ad.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", onClickListener);
        return ad;
    }

    public static void setNavigationBarBlackColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.getWindow().setNavigationBarColor(Color.parseColor("#000000"));
    }
}
