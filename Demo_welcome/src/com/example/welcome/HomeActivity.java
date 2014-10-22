package com.example.welcome;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;

public class HomeActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		findViewById(R.id.button).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		new SharedConfig(this).ClearConfig();
		
	}
}
