package pl.jawegiel.mierzenieopencv.activities;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pl.jawegiel.mierzenieopencv.R;
import pl.jawegiel.mierzenieopencv.Util;
import pl.jawegiel.mierzenieopencv.database.DBManager;
import pl.jawegiel.mierzenieopencv.database.DatabaseHelper;
import pl.jawegiel.mierzenieopencv.tasks.CameraOpenerTask;

public class Stage1StageFragment extends AbstractStageFragment {

    private static final String[] FROM = new String[]{DatabaseHelper._ID, DatabaseHelper.NAME, DatabaseHelper.REAL_CM};
    private static final int[] TO = new int[]{R.id.textView17, R.id.textView13, R.id.textView14};
    private static final int COLUMN_PATTERN = 1;
    private static final int COLUMN_SIZE = 2;

    private Dialog dialog;
    private DBManager dbManager;
    private EditText et1, et2;
    private TextView wzorzec, nazwa_wzorca, rozmiar_wzorca;
    private String rozmiar = "12";
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    int getLayout() {
        return R.layout.stage_one;
    }

    @Override
    public void onResume() {
        super.onResume();
        new CameraOpenerTask(this).execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents(view);

        configureDialog();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_RGBA2RGB, 1);
        Core.transpose(mGray, mGray);
        Core.flip(mGray, mGray, -1);
        return mRgba;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 0, 1, (R.string.delete)).setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                if (acmi.id == 1)
                    Toast.makeText(getActivity(), R.string.no_del, Toast.LENGTH_SHORT).show();
                else {
                    dbManager.delete(acmi.id);

                    spEditor.putString("param_wzorzec_wzorzec", "");
                    spEditor.putInt("param_wzorzec_rozmiar", 0);
                    spEditor.apply();

                    Toast.makeText(getActivity(), getString(R.string.benchmark_deleted), Toast.LENGTH_SHORT).show();
                    cursor.requery();
                    dialog.dismiss();
                }
                return true;
            }
            return false;
        });
    }

    public void initializeComponents(View view) {
        if (sp.getInt("param_wzorzec_rozmiar", 0) == 0)
            spEditor.putInt("param_wzorzec_rozmiar", 12).apply();
        else
            rozmiar = String.valueOf(sp.getInt("param_wzorzec_rozmiar", 0));

        wzorzec = view.findViewById(R.id.textView26);
        if (!sp.getString("param_wzorzec_wzorzec", "").equals("")) {
            wzorzec.setTextColor(Color.BLACK);
            wzorzec.setText(sp.getString("param_wzorzec_wzorzec", ""));
        }
        else
            wzorzec.setText("CD");




        configureButtons(view);

        dbManager = new DBManager(getActivity());
        dbManager.open();
        cursor = dbManager.fetch();
        adapter = new SimpleCursorAdapter(requireActivity(), R.layout.benchmark_entry_layout, cursor, FROM, TO, 0);
        adapter.notifyDataSetChanged();
    }

    public void configureButtons(View view) {
        final ImageView buttonImageView = view.findViewById(R.id.iv7);
        buttonImageView.setOnClickListener(view2 -> {
            blink(buttonImageView);
            dialog.show();
        });
    }

    public void configureDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.benchmark_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button b3 = dialog.findViewById(R.id.button2);
        b3.setOnClickListener(view -> {
            et1 = dialog.findViewById(R.id.editText);
            et2 = dialog.findViewById(R.id.editText2);
            if (!et1.getText().toString().equals("") && !et2.getText().toString().equals("")) {
                dbManager.insert(et1.getText().toString(), et2.getText().toString());
                Toast.makeText(getActivity(), (R.string.add), Toast.LENGTH_SHORT).show();
                cursor.requery();
                et1.setText("");
                et2.setText("");
                dialog.dismiss();
            } else
                Toast.makeText(getActivity(), (R.string.no_add), Toast.LENGTH_SHORT).show();
        });

        configureListview();
    }

    public void configureListview() {
        final ListView lv = dialog.findViewById(R.id.listView1);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);

        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            nazwa_wzorca = dialog.findViewById(R.id.textView13);
            rozmiar_wzorca = dialog.findViewById(R.id.textView14);

            Cursor c = (Cursor) lv.getItemAtPosition(i);

            wzorzec.setText(c.getString(COLUMN_PATTERN));
            wzorzec.setTextColor(Color.BLACK);
            rozmiar = c.getString(COLUMN_SIZE);

            spEditor.putString("param_wzorzec_wzorzec", c.getString(COLUMN_PATTERN));
            spEditor.putInt("param_wzorzec_rozmiar", Integer.parseInt(rozmiar));
            spEditor.apply();
            dialog.dismiss();
        });
    }

    public void blink(ImageView iv) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        iv.startAnimation(animation);
    }
}
