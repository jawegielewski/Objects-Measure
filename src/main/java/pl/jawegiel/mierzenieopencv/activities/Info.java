package pl.jawegiel.mierzenieopencv.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;

public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setThemeBasedOnPrefs(this);
        setContentView(R.layout.activity_info);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowHomeEnabled(false);

        initViews();
        zoom();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Info.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void initViews() {
        TextView tvAppDescription = findViewById(R.id.tvAppDescription);
        tvAppDescription.setText(Html.fromHtml(getResources().getString((R.string.info_dodatkowe))));
        tvAppDescription.setMovementMethod(LinkMovementMethod.getInstance());

        ((TextView)findViewById(R.id.tvVersion)).setText(String.format("v. %s", Util.getAppVersion(this)));
    }

    public void zoom() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        findViewById(R.id.imageView).startAnimation(animation);
    }
}
