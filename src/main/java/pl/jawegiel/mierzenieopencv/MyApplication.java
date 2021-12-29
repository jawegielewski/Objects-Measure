package pl.jawegiel.mierzenieopencv;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.data.StringFormat;

@AcraCore(buildConfigClass = BuildConfig.class, reportFormat = StringFormat.JSON)
@AcraHttpSender(uri = "https://jakuwegiel-backend.herokuapp.com/add_report_real_measure", httpMethod = org.acra.sender.HttpSender.Method.POST)
public class MyApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        ACRA.init(this);
    }

    public long remainingTimeToPlus10 = 0;
}