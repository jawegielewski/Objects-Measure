package pl.jawegiel.mierzenieopencv.activities;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Locale;

import pl.jawegiel.mierzenieopencv.BuildConfig;
import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.SomeConstants;
import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.tasks.CameraOpenerTask;


public class Stage3StageFragment extends AbstractStageFragment implements AdapterView.OnItemSelectedListener {

    private String[] metricUnit;
    private String choosenMetricUnit;
    private int metricUnitConverter = 1;
    private double srednica, px_cm, rozmiar_x, rozmiar_y, rozmiar_x_unit, rozmiar_y_unit, cd_rozmiar_x_unit, cd_rozmiar_y_unit;
    private int width, height, statusBarHeight, titleBarHeight, closeCount;
    private String unitPref, passPref, gridPref;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest2;
    private boolean click = false;
    private SharedPreferences settings;
    private int xCorrection, yCorrection;
    private boolean firstFrame = false;
    private Mat x;
    private Util util;
    private TextView tv;
    private String orientationPattern;
    private float rozmiar_px_x, rozmiar_px_y;
    private int rozmiarWzorca;
    private double cmToIlePx;
    private TextView width_info, height_info;
    private CheckBox checkboxSkip;
    private boolean hideDialog = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private String resolutionPref;
    private boolean isExpanded;

    @Override
    int getLayout() {
        return R.layout.stage_three;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        choosenMetricUnit = getString(R.string.centimetres);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchX = event.getX();
                float touchY = event.getY();
                // poziomo
                if (-touchX + camLayHeight + (xCorrection * 10) < (camLayHeight / 2)) {
                    touchedXR = -touchX + camLayHeight + (xCorrection * 10);
                    if (touchedXR < 0) touchedXR = 0;
                } else {
                    touchedXL = -touchX + camLayHeight + ((xCorrection * 10));
                    if (touchedXL > camLayHeight) touchedXL = camLayHeight;
                }


                // pionowo
                if (touchY - (yCorrection * 10) < (camLayWidth / 2)) {
                    touchedYU = touchY - (yCorrection * 10);
                    if (touchedYU < 0) touchedYU = 0;
                } else {
                    touchedYD = touchY - (yCorrection * 10);
                    if (touchedYD > camLayWidth) touchedYD = camLayWidth;
                }
                return true;
            }
        });
        util = new Util(getActivity());

        metricUnit = new String[]{getString(R.string.centimetres), getString(R.string.decimetres), getString(R.string.metres)};

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        passPref = sp.getString("pass", "");
        unitPref = sp.getString("unit", "cm");
        gridPref = sp.getString("grid", getString(R.string.yes));
        xCorrection = sp.getInt("xCorrection", 0);
        yCorrection = sp.getInt("yCorrection", 0);
        rozmiarWzorca = sp.getInt("param_wzorzec_rozmiar", 0);
        px_cm = sp.getFloat("px_cm", 0f);
        rozmiar_px_x = sp.getFloat("rozmiar_px_x", 0F);
        rozmiar_px_y = sp.getFloat("rozmiar_px_y", 0F);
        hideDialog = sp.getBoolean("hideDialogInStageThree", false);
        resolutionPref = sp.getString("resolution", getString(R.string.medium));

        Log.e("prefs", gridPref + " " + resolutionPref);
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) view.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, metricUnit);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        cmToIlePx = rozmiar_px_x > rozmiar_px_y ? rozmiar_px_x / rozmiarWzorca : rozmiar_px_y / rozmiarWzorca;
        Log.e("x", String.valueOf(cmToIlePx));
        tv = view.findViewById(R.id.alert_pxcm_too_small);
        if (px_cm <= 0.5f) {
            tv.setTextColor(Color.RED);
            tv.setText(getString(R.string.alert_pxcm_too_small));
        } else {
            tv.setTextColor(Color.BLUE);
            orientationPattern = rozmiar_px_x > rozmiar_px_y ? getString(R.string.horizontal) : getString(R.string.vertical);
            Log.e("orient", orientationPattern);
            tv.setText(getString(R.string.pxcm_ok_which_orientation_is_correct, orientationPattern));
        }

//        settings = getSharedPreferences(PREFS_NAME, 0);


        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            srednica = extras.getDouble("srednica");
            click = extras.getBoolean("click");
        }

        statusBarHeight = Util.statusBarHeight(getResources());
        //titleBarHeight=  this.getSupportActionBar().getHeight();

        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            titleBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }


        adRequest2 = new AdRequest.Builder()
                //.addTestDevice(SomeData.testDevice)
                .build();


        mInterstitialAd = new InterstitialAd(getActivity());
        if (BuildConfig.FLAVOR.equals("dev"))
            mInterstitialAd.setAdUnitId(SomeConstants.TEST_INTERSITIAL_ID);
        if (BuildConfig.FLAVOR.equals("prod"))
            mInterstitialAd.setAdUnitId(SomeConstants.INTERSITIAL_ID);
        mInterstitialAd.loadAd(adRequest2);
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                if (!passPref.equals(SomeConstants.PASS) && !click) {
                    showInterstitial(click);
                }
            }

            @Override
            public void onAdLeftApplication() {
                //super.onAdLeftApplication();
                if (haveNetworkConnection()) {
                    click = true;
                    Intent i = null;
//                    i = new Intent(StageThree.this, StageThree.class);
                    i.putExtra("click", click);
                    i.putExtra("px_cm", px_cm);
                    startActivity(i);
                }
                if (!haveNetworkConnection()) {
                    Toast.makeText(getActivity(), (R.string.load_ad), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }

            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        width_info = (TextView) view.findViewById(R.id.measurements_width_converter);
        height_info = (TextView) view.findViewById(R.id.measurements_height_converter);

        getCamera().setCvCameraViewListener(this);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);

        configureInitDialog();

//        Button b = view.findViewById(R.id.b_expand_collapse);
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ScrollView scrollView = view.findViewById(R.id.scroll_view);
//                RelativeLayout rootRL = view.findViewById(R.id.root_rl);
//                int camHeight;
//                if (!isExpanded) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getCamera().getLayoutParams();
//                    FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) rootRL.getLayoutParams();
//                    params.addRule(RelativeLayout.ABOVE, 0);
//                    getCamera().setLayoutParams(params);
////                    params.height = params2.height;
//                    slideView(getCamera(), params.height, params2.height);
//                    //getCamera().setLayoutParams(params);
//                    scrollView.setVisibility(View.GONE);
//
//                   // RelativeLayout.LayoutParams params2= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
////                    new CameraExpanderCollapser(StageThreeStageFragment.this, scrollView, params2.height).execute();
//                    //isExpanded = true;
//                }
//            }
//        });
    }

    public static void slideView(View view,
                                 int currentHeight,
                                 int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(50000);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value.intValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        new CameraOpenerTask(this).execute();
        if (px_cm > 0.5) {
            try {
                x = Imgcodecs.imread(Util.appFolder(getActivity()) + "/recognition-image.jpg");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        x = null;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (x != null) {
            Imgproc.cvtColor(x, mRgba, Imgproc.COLOR_RGBA2RGB, 1);
            Core.transpose(mGray, mGray);
            Core.flip(mGray, mGray, -1);

            Imgproc.line(mRgba, p1, p2, Util.BLUE, 3);
            Imgproc.line(mRgba, p3, p4, Util.BLUE, 3);

            Imgproc.rectangle(mRgba, new Point(touchedYD, touchedXL), new Point(touchedYU, touchedXR), Util.WHITE, 2);

            if (gridPref.equals(getString(R.string.yes))) {
                if (choosenMetricUnit.equals(getString(R.string.centimetres)))
                    metricUnitConverter = 1;
                else if (choosenMetricUnit.equals(getString(R.string.decimetres)))
                    metricUnitConverter = 10;
                else
                    metricUnitConverter = 100;
                for (double i = 0; i < camLayHeight; i += cmToIlePx * metricUnitConverter) {
                    Imgproc.line(mRgba, new Point((camLayWidth / 2) + i, camLayHeight / 2), new Point((camLayWidth / 2) + i, camLayHeight / 2), Util.GREEN, 10);
                    Imgproc.line(mRgba, new Point((camLayWidth / 2) - i, camLayHeight / 2), new Point((camLayWidth / 2) - i, camLayHeight / 2), Util.GREEN, 10);
                    Imgproc.line(mRgba, new Point(0, (camLayHeight / 2) + i), new Point(camLayWidth, (camLayHeight / 2) + i), Util.GREEN, 1);
                    Imgproc.line(mRgba, new Point(0, (camLayHeight / 2) - i), new Point(camLayWidth, (camLayHeight / 2) - i), Util.GREEN, 1);
                }
                for (double i = 0; i < camLayWidth; i += cmToIlePx * metricUnitConverter) {
                    Imgproc.line(mRgba, new Point(camLayWidth / 2, (camLayHeight / 2) + i), new Point(camLayWidth / 2, (camLayHeight / 2) + i), Util.GREEN, 10);
                    Imgproc.line(mRgba, new Point(camLayWidth / 2, (camLayHeight / 2) - i), new Point(camLayWidth / 2, (camLayHeight / 2) - i), Util.GREEN, 10);
                    Imgproc.line(mRgba, new Point((camLayWidth / 2) + i, 0), new Point((camLayWidth / 2) + i, camLayHeight), Util.GREEN, 1);
                    Imgproc.line(mRgba, new Point((camLayWidth / 2) - i, 0), new Point((camLayWidth / 2) - i, camLayHeight), Util.GREEN, 1);
                }
            }

            rozmiar_y = (int) ((touchedYU - touchedYD));
            if (unitPref.equals("cm")) rozmiar_y_unit = (rozmiar_y / px_cm);
            if (unitPref.equals("inch")) rozmiar_y_unit = (rozmiar_y / px_cm) * 0.39;

            rozmiar_x = (int) ((touchedXR - touchedXL));
            if (unitPref.equals("cm")) rozmiar_x_unit = (rozmiar_x / px_cm);
            if (unitPref.equals("inch")) rozmiar_x_unit = (rozmiar_x / px_cm) * 0.39;

            if (srednica != 0.0)
                cd_rozmiar_y_unit = (double) Math.round((rozmiar_y / srednica) * 10) / 10;
            if (srednica == 0.0)
                cd_rozmiar_y_unit = (double) Math.round((rozmiar_y / (Math.max(rozmiar_px_x, rozmiar_px_y))) * 10) / 10;

            if (srednica != 0.0)
                cd_rozmiar_x_unit = (double) Math.round((rozmiar_x / srednica) * 10) / 10;
            if (srednica == 0.0)
                cd_rozmiar_x_unit = (double) Math.round((rozmiar_x / (Math.max(rozmiar_px_x, rozmiar_px_y))) * 10) / 10;

            getActivity().runOnUiThread(() -> {
                Resources res = getResources();
                if (!unitPref.equals("")) {
                    width_info.setText(String.format(res.getString(R.string.width_info), String.valueOf(Math.abs(rozmiar_x)), unitPref, String.format(Locale.ENGLISH, "%.2f", Math.abs(rozmiar_x_unit))));
                    height_info.setText(String.format(res.getString(R.string.height_info), String.valueOf(Math.abs(rozmiar_y)), unitPref, String.format(Locale.ENGLISH, "%.2f", Math.abs(rozmiar_y_unit))));
                } else {
                    width_info.setText((R.string.set_unit_first));
                    height_info.setText((R.string.set_unit_first));
                }
            });
        }

        firstFrame = true;
        return mRgba;
    }

    private void showInterstitial(boolean click) {
        if (!click) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    public void setScrollViewParamsDependingOnFont(View checkboxLayout) {
        ScrollView layout = checkboxLayout.findViewById(R.id.scrollView);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        String fontPref = sp.getString("font", "Arial");
        if (fontPref.equals("Ginger"))
            params.height = (int) getResources().getDimension(R.dimen.height_of_checkbox);
        if (fontPref.equals("Arial")) params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        layout.setLayoutParams(params);
    }

    void configureInitDialog() {
        View checkboxLayout = View.inflate(getActivity(), R.layout.checkbox, null);
        setScrollViewParamsDependingOnFont(checkboxLayout);
        checkboxSkip = checkboxLayout.findViewById(R.id.checkboxSkip);

        AlertDialog ad = new AlertDialog.Builder(getActivity(), R.style.MyStyle).create();
        ad.setView(checkboxLayout);
        ad.setIcon(android.R.drawable.ic_dialog_info);
        ad.setTitle("Info");
        ad.setMessage(getResources().getString(R.string.stage_three_dialog));
        ad.setCancelable(false);
        ad.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", (dialogInterface, i) -> {
            String checkBoxResult = "";
            if (checkboxSkip.isChecked())
                checkBoxResult = "linia2";
            if (checkBoxResult.equals("linia2"))
                spEditor.putBoolean("hideDialogInStageThree", true).apply();
        });
        ad.setOnShowListener(arg0 -> {
            ad.getButton(DialogInterface.BUTTON_NEUTRAL).setBackground(getResources().getDrawable(R.drawable.button_selector));
            TextView textView = (TextView) ad.findViewById(android.R.id.message);
            Log.e("size", String.valueOf(textView.getTextSize()));
        });

        if (!hideDialog)
            ad.show();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        choosenMetricUnit = metricUnit[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}