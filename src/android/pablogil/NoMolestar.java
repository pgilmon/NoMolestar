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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author Pablo Gil
 *
 */
public class NoMolestar extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        
        final SharedPreferences preferences = 
        	PreferenceManager.getDefaultSharedPreferences(context);
        setContentView(R.layout.main);
        
        CheckBox enabledCB = (CheckBox)findViewById(R.id.enabledCB);
        enabledCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor prefEditor = preferences.edit();
				prefEditor.putBoolean(Constants.ENABLED, isChecked);
				prefEditor.commit();
			}
		});
        enabledCB.setChecked(preferences.getBoolean(Constants.ENABLED, false));
    }
}