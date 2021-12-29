package pl.jawegiel.mierzenieopencv.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.interfaces.OnSwitchFragmentFromStageTwo;
import pl.jawegiel.mierzenieopencv.tasks.CameraOpenerTask;

public class Stage2StageFragment extends AbstractStageFragment implements OnSwitchFragmentFromStageTwo {

    private String rozmiarWzorca;
    private double rozmiar_x = 1, rozmiar_y = 1, px_cm;
    private int xCorrection, yCorrection;
    private CheckBox checkboxSkip;
    private boolean hideDialog;
    private AlertDialog alertDialog;

    @Override
    int getLayout() {
        return R.layout.stage_two;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Stages)requireActivity()).onSwitchFragmentFromStageTwo = this;
        hideDialog = sp.getBoolean("hideStageTwoInfoDialog", false);

        configureInitDialog();

        getCamera().setCvCameraViewListener(this);


        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        getCamera().getLayoutParams().height = size.y / 2;

        view.setOnTouchListener((v, event) -> {
             int touchedX = (int) event.getX();
            int touchedY = (int) event.getY();

            // poziomo
            if (-touchedX + camLayHeight + (xCorrection * 10) < (camLayHeight / 2)) {
                touchedXR = -touchedX + camLayHeight + (xCorrection * 10);
                if (touchedXR < 0) touchedXR = 0;
            } else {
                touchedXL = -touchedX + camLayHeight + (xCorrection * 10);
                if (touchedXL > camLayHeight) touchedXL = camLayHeight;
            }

            // pionowo
            if (touchedY - (yCorrection * 10) < (camLayWidth / 2)) {
                touchedYU = touchedY - (yCorrection * 10);
                if (touchedYU < 0) touchedYU = 0;
            } else {
                touchedYD = touchedY - (yCorrection * 10);
                if (touchedYD > camLayWidth) touchedYD = camLayWidth;
            }
            return true;
        });
        rozmiarWzorca = String.valueOf(sp.getInt("param_wzorzec_rozmiar", 0));
        xCorrection = sp.getInt("xCorrection", 0);
        yCorrection = sp.getInt("yCorrection", 0);
    }

    private void configureInitDialog() {
        View imageviewChecboxLayout = View.inflate(getActivity(), R.layout.stage_two_information, null);
        checkboxSkip = imageviewChecboxLayout.findViewById(R.id.checkboxSkip);

        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyStyle).create();

        alertDialog.setView(imageviewChecboxLayout);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.instruction);
//;
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", (dialogInterface, i) -> {
            String checkBoxResult = "";
            if (checkboxSkip.isChecked())
                checkBoxResult = "linia2";
            if (checkBoxResult.equals("linia2"))
                spEditor.putBoolean("hideStageTwoInfoDialog", true).apply();
        });
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackground(AppCompatResources.getDrawable(requireActivity(), R.drawable.button_selector)));
        if (!hideDialog)
            alertDialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        new CameraOpenerTask(this).execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        px_cm = 0;
        if (alertDialog.isShowing())
            alertDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Core.transpose(mGray, mGray);
        Core.flip(mGray, mGray, -1);

        Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_RGBA2RGB, 1);
        mRgba.copyTo(mRgbNoRect);

        Imgproc.line(mRgba, p1, p2, Util.BLUE);
        Imgproc.line(mRgba, p3, p4, Util.BLUE);

        Imgproc.rectangle(mRgba, new Point(touchedYD, touchedXL), new Point(touchedYU, touchedXR), Util.WHITE, 2);

        rozmiar_y = (int) ((touchedYD - touchedYU));
        rozmiar_x = (int) ((touchedXL - touchedXR));

        if (rozmiar_x > rozmiar_y)
            px_cm = (double) Math.round((rozmiar_x / Integer.parseInt(rozmiarWzorca)) * 100000) / 100000d;
        if (rozmiar_x < rozmiar_y)
            px_cm = (double) Math.round((rozmiar_y / Integer.parseInt(rozmiarWzorca)) * 100000) / 100000d;


        return mRgba;
    }



    @Override
    public double onSwitchFragmentFromFragmentTwo() throws PackageManager.NameNotFoundException {
        if (px_cm > 0.5) {
            Imgcodecs.imwrite(Util.appFolder(requireActivity()) + "/recognition-image.jpg", mRgbNoRect);
            spEditor.putFloat("px_cm", (float) px_cm);
            if (rozmiar_x > rozmiar_y) {
                spEditor.putFloat("rozmiar_px_x", (float) rozmiar_x);
                spEditor.putFloat("rozmiar_px_y", (float) 0f);
            }
            if (rozmiar_x == rozmiar_y) {
                spEditor.putFloat("rozmiar_px_x", (float) rozmiar_x);
                spEditor.putFloat("rozmiar_px_y", (float) 0f);
            }
            if (rozmiar_x < rozmiar_y) {
                spEditor.putFloat("rozmiar_px_y", (float) rozmiar_y);
                spEditor.putFloat("rozmiar_px_x", (float) 0f);
            }
            spEditor.apply();

            touchedXL = 0;
            touchedXR = 0;
            touchedYU = 0;
            touchedYD = 0;
            rozmiar_x = 0;
            rozmiar_y = 0;
            Log.e("px_cm", ">0.5");
        } else {
            spEditor.putFloat("px_cm", 0f);
            spEditor.putFloat("rozmiar_px", 0f);
            spEditor.putFloat("rozmiar_px_x", (float) 0f);
            spEditor.putFloat("rozmiar_px_y", (float) 0f);
            spEditor.commit();
            Log.e("px_cm", "<=0.5");
        }
        return px_cm;
    }
}