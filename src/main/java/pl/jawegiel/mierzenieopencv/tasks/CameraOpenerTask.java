package pl.jawegiel.mierzenieopencv.tasks;

import android.os.AsyncTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.activities.AbstractStageFragment;

public class CameraOpenerTask extends AsyncTask<Void, Void, BaseLoaderCallback> {

    private final AbstractStageFragment fragment;
    private BaseLoaderCallback mLoaderCallback;

    public CameraOpenerTask(AbstractStageFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected BaseLoaderCallback doInBackground(Void... voids) {
        configureBaseLoaderCallback();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!OpenCVLoader.initDebug())
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, fragment.getActivity(), mLoaderCallback);
        else
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        return mLoaderCallback;
    }

    public void configureBaseLoaderCallback() {
        mLoaderCallback = new BaseLoaderCallback(fragment.getActivity()) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {

                    Util.setCameraResolutionBasedOnPrefs(fragment.getResolutionPref(), fragment.getCamera(), fragment.requireActivity());
                    fragment.getCamera().disableView();
                    fragment.getCamera().enableView();
                } else {
                    super.onManagerConnected(status);
                }
            }
        };
    }
}