package pl.jawegiel.mierzenieopencv.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.activities.AbstractStageFragment;

public class CameraExpanderCollapser extends AsyncTask<Void, Integer, Void> {

    private final AbstractStageFragment fragment;
    private int cameraHeight;
    private int targetCameraHeight;
    private RelativeLayout.LayoutParams params;
    private ScrollView scrollView;

    public CameraExpanderCollapser(AbstractStageFragment fragment, ScrollView scrollView, int targetCameraHeight) {
        this.fragment = fragment;
        this.scrollView = scrollView;
        this.targetCameraHeight = targetCameraHeight;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        cameraHeight = fragment.getCamera().getLayoutParams().height;
        params = (RelativeLayout.LayoutParams) fragment.getCamera().getLayoutParams();

    }

    @Override
    protected Void doInBackground(Void... voids) {
//        if (cameraHeight < targetCameraHeight) {
            for (int i = 0; i < 100; i++) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i);

            }

//        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.e("a", String.valueOf(values[0]));
        params.height = params.height+values[0];
        fragment.getCamera().setLayoutParams(params);

            scrollView.setVisibility(View.GONE);
    }
}