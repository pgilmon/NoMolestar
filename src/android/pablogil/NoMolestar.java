/*
 * Copyright 2011 Pablo Gil
 * 
 * This file is part of NoMolestar.

 * NoMolestar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * NoMolestar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with NoMolestar.  If not, see <http://www.gnu.org/licenses/>.

 */
package android.pablogil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import pablogil.android.R;

/**
 * @author Pablo Gil
 *
 */
public class NoMolestar extends Activity implements OnSharedPreferenceChangeListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        
        final SharedPreferences preferences = 
        	PreferenceManager.getDefaultSharedPreferences(context);
        setContentView(R.layout.main);
        
        preferences.registerOnSharedPreferenceChangeListener(this);
        CheckBox enabledCB = (CheckBox)findViewById(R.id.enabledCB);
        enabledCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor prefEditor = preferences.edit();
				prefEditor.putBoolean(Constants.ENABLED, isChecked);
				prefEditor.commit();
			}
		});
        enabledCB.setChecked(preferences.getBoolean(Constants.ENABLED, false));
        
        int noOfBlockedCalls = preferences.getInt(Constants.NO_OF_BLOCKED_CALLS, 0);
        updateCounter(noOfBlockedCalls);
    }
    
    
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key){
    	if (Constants.NO_OF_BLOCKED_CALLS.equals(key)){
    		int newValue = preferences.getInt(Constants.NO_OF_BLOCKED_CALLS, 0);
    		updateCounter(newValue);
    	}
    }
    
    public void updateCounter(int newValue){
    	TextView noOfBlockedCallsTV = 
        	(TextView)findViewById(R.id.textView_noOfBlockedCalls);
        noOfBlockedCallsTV.setText("" + newValue);
        noOfBlockedCallsTV.getRootView().invalidate();
    }
}