package pl.jawegiel.mierzenieopencv;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AuthPreference extends EditTextPreference {

	private boolean change;

	public AuthPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AuthPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {

		if(getSharedPreferences().getString("pass", "").equals(SomeConstants.PASS))
			change = true;

		return super.onCreateView(parent);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		ImageView icon = view.findViewById(R.id.iconSelected);
		if(change)
			icon.setImageResource(R.drawable.ic_action_yes);
		else
			icon.setImageResource(R.drawable.ic_action_no);

		if(getEditText().getText().toString().equals(SomeConstants.PASS))
			icon.setImageResource(R.drawable.ic_action_yes);
		else
			icon.setImageResource(R.drawable.ic_action_no);

		if(getSharedPreferences().getString("pass", "").equals(SomeConstants.PASS))
			icon.setImageResource(R.drawable.ic_action_yes);
		else
			icon.setImageResource(R.drawable.ic_action_no);
	}
}
