package com.grapefruitopia.metaneko;

import java.io.IOException;
import java.io.InputStream;

import com.bugsense.trace.BugSenseHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IntentReceiver extends BroadcastReceiver  {

	static boolean bugsenseInit = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(MetaNeko.TAG, "onReceive()");

		if (!bugsenseInit) {
			
			bugsenseInit = true;
			
	        // If you want to use BugSense for your fork, register with them
	        // and place your API key in /assets/bugsense.txt
	        // (This prevents me receiving reports of crashes from forked versions
	        // which is somewhat confusing!)      
	        try {
				InputStream inputStream = context.getAssets().open("bugsense.txt");
				String key = Utils.ReadInputStream(inputStream);
				key=key.trim();
				Log.d(MetaNeko.TAG, "BugSense enabled");
				BugSenseHandler.setup(context, key);
			} catch (IOException e) {
				Log.d(MetaNeko.TAG, "No BugSense keyfile found");
			}
			
		}
		
		String action = intent.getAction();
		if (action==null)
			return;
		
		if (action.equals("org.metawatch.manager.APPLICATION_DISCOVERY")) {
			Log.d(MetaNeko.TAG, "Received APPLICATION_DISCOVERY intent");

			MetaNeko.announce(context);

		} else if (action.equals("org.metawatch.manager.APPLICATION_ACTIVATE")) {
			Log.d(MetaNeko.TAG, "Received APPLICATION_ACTIVATE intent");

			String id = intent.getStringExtra("id");
			
			if (id!=null && id.equals(MetaNeko.id))
				context.startService(new Intent(context, MetaNeko.class));
			
		} else if (action.equals("org.metawatch.manager.APPLICATION_DEACTIVATE")) {
			Log.d(MetaNeko.TAG, "Received APPLICATION_DEACTIVATE intent");

			String id = intent.getStringExtra("id");
			
			if (id!=null && id.equals(MetaNeko.id))
				context.stopService(new Intent(context, MetaNeko.class));
			
		} else if (action.equals("org.metawatch.manager.BUTTON_PRESS")) {
			Log.d(MetaNeko.TAG, "Received BUTTON_PRESS intent");
			
			String id = intent.getStringExtra("id");
			int button = intent.getIntExtra("button", 0); // button index
			int type = intent.getIntExtra("type", 0); // type: 0=pressed, 1=held short, 1=held long
			
			if (id!=null && id.equals(MetaNeko.id))
				MetaNeko.button(context, button, type);
		}
	}
}
