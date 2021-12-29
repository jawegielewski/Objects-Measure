package pl.jawegiel.mierzenieopencv.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.interfaces.OnSwitchFragmentFromStageTwo;

public class Stages extends BaseActivity {

    private final Stage0StageFragment stageZeroFragment = new Stage0StageFragment();
    private final Stage1StageFragment stageOneFragment = new Stage1StageFragment();
    private final Stage2StageFragment stageTwoFragment = new Stage2StageFragment();
    private final Stage3StageFragment stageThreeFragment = new Stage3StageFragment();

    private BottomNavigationView bottomNavView;
    private int startingPosition, newPosition;
    OnSwitchFragmentFromStageTwo onSwitchFragmentFromStageTwo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setThemeBasedOnPrefs(this);
        setContentView(R.layout.stages);

        spEditor.putBoolean("wantToClosePxCmInfo", false).apply();

        bottomNavView = findViewById(R.id.bottom_navigation);
        bottomNavView.setItemIconTintList(null);
        bottomNavView.setOnNavigationItemSelectedListener(item -> {

            if (bottomNavView.getSelectedItemId() == R.id.navigation_stage_zero) {
                if (!sp.getBoolean("correctionDone", false)) {
                    AlertDialog alertDialog = Util.createAlertDialog(Stages.this, android.R.drawable.ic_dialog_info, R.string.hide_dialog_title, getString(R.string.stage_zero_confirmation), (dialog, which) -> {
                        spEditor.putBoolean("correctionDone", true).apply();
                        bottomNavView.setSelectedItemId(item.getItemId());
                        showFragment(item.getItemId());
                    });
                    alertDialog.setOnShowListener(arg0 -> setAlertDialogButtonsAttributes(alertDialog));
                    alertDialog.show();
                    return false;
                } else {
                    showFragment(item.getItemId());
                    return true;
                }
            } else if (bottomNavView.getSelectedItemId() == R.id.navigation_stage_two) {
                try {
                    if (onSwitchFragmentFromStageTwo.onSwitchFragmentFromFragmentTwo() <= 0.5 && !sp.getBoolean("wantToClosePxCmInfo", false)) {
                        AlertDialog ad = Util.createAlertDialog(Stages.this, android.R.drawable.ic_dialog_alert, R.string.hide_dialog_title_alert, getResources().getString(R.string.benchmark_not_drawn), (dialog, which) -> {
                            spEditor.putBoolean("wantToClosePxCmInfo", true).apply();
                            bottomNavView.setSelectedItemId(item.getItemId());

                            showFragment(item.getItemId());
                        });
                        ad.setOnShowListener(arg0 -> setAlertDialogButtonsAttributes(ad));
                        ad.show();

                        return false;
                    } else {
                        showFragment(item.getItemId());
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                showFragment(item.getItemId());
            }
            return true;
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!sp.getBoolean("correctionDone", false))
            ft.replace(R.id.content_frame, stageZeroFragment);
        else {
            ft.replace(R.id.content_frame, stageOneFragment);
            bottomNavView.setSelectedItemId(R.id.navigation_stage_one);
        }
        ft.commit();
    }

    @Override
    BaseActivity getActivity() {
        return this;
    }

    private void setAlertDialogButtonsAttributes(AlertDialog alertDialog2) {
        alertDialog2.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(AppCompatResources.getDrawable(this, R.drawable.button_selector));
        alertDialog2.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(AppCompatResources.getDrawable(this, R.drawable.button_selector));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        params.setMargins(10, 0, 10, 0);
        alertDialog2.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
        alertDialog2.getButton(DialogInterface.BUTTON_POSITIVE).setLayoutParams(params);
    }

    public void showFragment(int viewId) {
        Fragment fragment = null;

        switch (viewId) {
            case R.id.navigation_stage_zero:
                if (bottomNavView.getSelectedItemId() != R.id.navigation_stage_zero) {
                    fragment = stageZeroFragment;
                    newPosition = 0;
                }
                break;
            case R.id.navigation_stage_one:
                if (bottomNavView.getSelectedItemId() != R.id.navigation_stage_one) {
                    fragment = stageOneFragment;
                    newPosition = 1;
                }
                break;
            case R.id.navigation_stage_two:
                if (bottomNavView.getSelectedItemId() != R.id.navigation_stage_two) {
                    fragment = stageTwoFragment;
                    newPosition = 2;
                }
                break;
            case R.id.navigation_stage_three:
                if (bottomNavView.getSelectedItemId() != R.id.navigation_stage_three) {
                    fragment = stageThreeFragment;
                    newPosition = 3;
                }
                break;
        }

        if (fragment != null) {
            if (startingPosition > newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.content_frame, fragment).commit();
            }
            if (startingPosition < newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.content_frame, fragment).commit();
            }

            startingPosition = newPosition;
        }
    }
}
