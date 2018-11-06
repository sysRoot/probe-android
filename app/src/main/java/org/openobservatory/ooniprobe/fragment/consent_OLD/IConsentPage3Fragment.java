package org.openobservatory.ooniprobe.fragment.consent_OLD;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity_OLD;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IConsentPage3Fragment extends Fragment {
	@BindView(R.id.nextButton) Button nextButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_ic_page_3, container, false);
		ButterKnife.bind(this, v);
		nextButton.setOnClickListener(v1 -> {
			if (((InformedConsentActivity_OLD) getActivity()).QUESTION_NUMBER < 3) {
				((InformedConsentActivity_OLD) getActivity()).loadQuizFragment();
			}
		});
		return v;
	}

	public void hideNextButton() {
		nextButton.setVisibility(View.GONE);
	}
}