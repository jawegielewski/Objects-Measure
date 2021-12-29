package pl.jawegiel.mierzenieopencv.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.tasks.CameraOpenerTask;

public class Stage0StageFragment extends AbstractStageFragment {

    private CheckBox checkboxSkip;
    private int xCorrection, yCorrection;
    private IndicatorSeekBar indicatorSeekBar;
    private IndicatorSeekBar indicatorSeekBar2;
    private AlertDialog alertDialog;

    @Override
    int getLayout() {
        return R.layout.stage_zero;
    }

    @Override
    public void onResume() {
        super.onResume();
        new CameraOpenerTask(this).execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        spEditor.putInt("indicator1", indicatorSeekBar.getProgress());
        spEditor.putInt("indicator2", indicatorSeekBar2.getProgress());
        spEditor.apply();
        if (alertDialog.isShowing())
            alertDialog.dismiss();

    }

    @Override
    @SuppressLint({"ClickableViewAccessibility", "CommitPrefEdits"})
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener((v, event) -> {
            int aktX = (int) event.getX();
            int aktY = (int) event.getY();

            setParamsToDrawRectangle(aktX, aktY);

            return true;
        });
        configureSeekBars(view);
        configureInitDialog();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_RGBA2RGB, 1);

        Core.transpose(mGray, mGray);
        Core.flip(mGray, mGray, -1);

        Imgproc.line(mRgba, p1, p2, Util.BLUE);
        Imgproc.line(mRgba, p3, p4, Util.BLUE);

        Imgproc.rectangle(mRgba, new Point(touchedYD, touchedXL), new Point(touchedYU, touchedXR), Util.WHITE, 2);

        return mRgba;
    }


    private void setParamsToDrawRectangle(int aktX, int aktY) {
        // poziomo
        if (-aktX + camLayHeight + (xCorrection * 10) < (camLayHeight / 2)) {
            touchedXR = -aktX + camLayHeight + (xCorrection * 10);
            if (touchedXR < 0) touchedXR = 0;
        } else {
            touchedXL = -aktX + camLayHeight + (xCorrection * 10);
            if (touchedXL > camLayHeight) touchedXL = camLayHeight;
        }

        // pionowo
        if (aktY - (yCorrection * 10) < (camLayWidth / 2)) {
            touchedYU = aktY - (yCorrection * 10);
            if (touchedYU < 0) touchedYU = 0;
        } else {
            touchedYD = aktY - (yCorrection * 10);
            if (touchedYD > camLayWidth) touchedYD = camLayWidth;
        }
    }

    public void configureSeekBars(View view) {
        indicatorSeekBar = view.findViewById(R.id.percent_indicator);
        indicatorSeekBar.setOnSeekChangeListener(new MyOnSeekChangeListener("x"));
        indicatorSeekBar2 = view.findViewById(R.id.percent_indicator2);
        indicatorSeekBar2.setOnSeekChangeListener(new MyOnSeekChangeListener("y"));

        TextView tv = view.findViewById(R.id.info_about_indicators);
        if (sp.getInt("indicator1", 0) != 0)
            indicatorSeekBar.setProgress(sp.getInt("indicator1", 0));
        if (sp.getInt("indicator2", 0) != 0)
            indicatorSeekBar2.setProgress(sp.getInt("indicator2", 0));
        if (sp.getInt("indicator1", 0) != 0 || sp.getInt("indicator2", 0) != 0)
            tv.setVisibility(View.VISIBLE);
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

        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyStyle).create();
        alertDialog.setView(checkboxLayout);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle("Info");
        alertDialog.setMessage(getResources().getString(R.string.stage_zero_dialog));
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", (dialogInterface, i) -> {
            String checkBoxResult = "";
            checkboxSkip = checkboxLayout.findViewById(R.id.checkboxSkip);
            if (checkboxSkip.isChecked())
                checkBoxResult = "linia2";
            if (checkBoxResult.equals("linia2"))
                spEditor.putBoolean("hideDialog", true).apply();
        });
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_selector, null)));
//        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackground(getResources().getDrawable(R.drawable.button_selector)));
        if (!sp.getBoolean("hideDialog", false))
            alertDialog.show();
    }

    public class MyOnSeekChangeListener implements OnSeekChangeListener {
        private final String axisCorrection;

        MyOnSeekChangeListener(String axisCorrection) {
            this.axisCorrection = axisCorrection;
        }

        @Override
        public void onSeeking(SeekParams seekParams) {
            if (axisCorrection.equals("x"))
                xCorrection = seekParams.progress;
            if (axisCorrection.equals("y"))
                yCorrection = seekParams.progress;

            spEditor.putInt("xCorrection", xCorrection);
            spEditor.putInt("yCorrection", yCorrection);
            spEditor.apply();
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
        }
    }
}