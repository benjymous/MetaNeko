package com.grapefruitopia.metaneko;

import android.app.Activity;
import android.os.Bundle;

public class WelcomeActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		MetaNeko.announce(this);
	}
	
}
