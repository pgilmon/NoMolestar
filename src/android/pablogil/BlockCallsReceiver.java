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
 *
 *
 * This file incorporates work covered by the two following copyright and
 * permission notices:
 * 
 * ======================================================================
 * Copyright (C) 2010 EverySoft
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *========================================================================
 *   Copyright (C) 2010 Tedd Scofield
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package android.pablogil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class BlockCallsReceiver extends BroadcastReceiver {
	
	private static int TARGET_NUMBER_DIGITS = 4;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences preferences = 
			PreferenceManager.getDefaultSharedPreferences(context);

		// Check phone state
		String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		
		//To prevent problems with anonymous calls
		if (number != null){
			if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING) && 
					number.length() == TARGET_NUMBER_DIGITS &&
					preferences.getBoolean(Constants.ENABLED, false)) {
				
	
				// Call a service, since this could take a few seconds
				context.startService(new Intent(context, BlockCallsService.class));
			}	
		}
	}

}
