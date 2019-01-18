package com.android.gudana.linphone.assistant;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.gudana.R;
import com.android.gudana.chatapp.activities.WelcomeActivity;

/**
 * @author Sylvain Berfini
 */
public class WelcomeFragment extends Fragment implements OnClickListener {
	private Button createAccount, logLinphoneAccount, logGenericAccount, remoteProvisioning;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.assistant_welcome, container, false);

		logGenericAccount = (Button) view.findViewById(R.id.login_generic);
		if (getResources().getBoolean(R.bool.hide_generic_accounts_in_assistant)) {
			logGenericAccount.setVisibility(View.GONE);
		} else {
			logGenericAccount.setOnClickListener(this);
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.login_generic) {

			Intent myIntent = new Intent(getActivity(), WelcomeActivity.class);
			this.startActivity(myIntent);

			// AssistantActivity.instance().displayLoginGeneric();
		}
	}
}
