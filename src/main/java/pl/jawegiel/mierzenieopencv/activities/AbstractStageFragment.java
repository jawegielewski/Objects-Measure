package pl.jawegiel.mierzenieopencv.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;

public abstract class AbstractStageFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase camera;
    private String resolutionPref;
    protected float camLayHeight, camLayWidth, touchedXL, touchedXR, touchedYU, touchedYD;
    protected Mat mRgba, mGray, mRgbNoRect;
    protected Point p1, p2, p3, p4;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor spEditor;

    public String getResolutionPref() {
        return resolutionPref;
    }

    public CameraBridgeViewBase getCamera() {
        return camera;
    }

    abstract int getLayout();


    @Nullable
    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        camera = rootView.findViewById(R.id.java_surface_view);
        camera.setCvCameraViewListener(this);
        configureCamera();
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        spEditor = sp.edit();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resolutionPref = sp.getString("resolution", getResources().getString(R.string.high));
        switch (getLayout()) {
            case R.layout.stage_zero:
                Util.initAdsFromStageZero(view);
                break;
            case R.layout.stage_one:
                Util.initAdsFromStageOne(view);
                break;
            case R.layout.stage_two:
                Util.initAdsFromStageTwo(view);
                break;
            case R.layout.stage_three:
                Util.initAdsFromStageThree(view);
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (camera != null)
            camera.disableView();
    }

    @Override
    public abstract Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC3);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        if (getLayout() == R.layout.stage_two)
            mRgbNoRect = new Mat(height, width, CvType.CV_8UC3);

        camLayHeight = height;
        camLayWidth = width;

        touchedXL = camLayHeight / 2;
        touchedXR = camLayHeight / 2;
        touchedYD = camLayWidth / 2;
        touchedYU = camLayWidth / 2;

        p1 = new Point(camLayWidth / 2, 0);
        p2 = new Point(camLayWidth / 2, camLayHeight);
        p3 = new Point(0, camLayHeight / 2);
        p4 = new Point(camLayWidth, camLayHeight / 2);
    }

    @Override
    public void onCameraViewStopped() {
    }

    void configureCamera() {
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        int height = size.y;
        camera.getLayoutParams().height = height / 2;
    }
}
