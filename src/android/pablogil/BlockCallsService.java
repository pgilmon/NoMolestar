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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import pablogil.android.R;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

public class BlockCallsService extends IntentService {

	private static long WAIT_OFF_HOOK_SLEEP_TIME = 100;
	private static long WAIT_OFF_HOOK_TIMEOUT = 5000;
	
	public BlockCallsService() {
		super("BlockCallsService");
	}

	protected ITelephony getTelephony() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException{
	
		// Set up communication with the telephony service (thanks to Tedd's Droid Tools!)
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		@SuppressWarnings("rawtypes")
		Class c = Class.forName(tm.getClass().getName());
		Method m = c.getDeclaredMethod("getITelephony");
		m.setAccessible(true);
		ITelephony telephonyService;
		telephonyService = (ITelephony)m.invoke(tm);
		
		@SuppressWarnings("rawtypes")
		Class telephonyServiceClass = telephonyService.getClass();
		
		Method[] methods = telephonyServiceClass.getMethods();
		
		String methodsString = "";
		
		for (Method method : methods){
			methodsString = methodsString + method + "\n";
		}
		Log.d("NoMolestar", "Methods:\n" + methodsString);
		
		return telephonyService;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Context context = getBaseContext();

		// Make sure the phone is still ringing
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
			return;
		}

		try{
			ITelephony telephonyService = getTelephony();
			// Answer the phone
			
			try {
				answerPhoneAidl(telephonyService);
			}
			catch (Exception e) {
				e.printStackTrace();
				Log.d("NoMolestar","Error trying to answer using telephony service.  Falling back to headset.");
				answerPhoneHeadsethook(context);
			}
			
			//Now, end call
			try{
				waitOffHook(tm);
				endCallAidl(telephonyService);
			}
			catch(Throwable e){
				Log.d("NoMolestar", "Could not hang up phone. Fallback", e);
			}
			
		}
		catch (Exception e){
			Log.e("NoMolestar", "Could not get ITelephony", e);
		}
		
		
		return;
	}

	private void answerPhoneHeadsethook(Context context) {
		// Simulate a press of the headset button to pick up the call
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);		
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

		// froyo and beyond trigger on buttonUp instead of buttonDown
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);		
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
	}

	private void answerPhoneAidl(ITelephony telephonyService) throws Exception {
		

		// Silence the ringer and answer the call!
		telephonyService.silenceRinger();
		telephonyService.answerRingingCall();
	}
	
	protected void waitOffHook(TelephonyManager telephonyManager){
		long startTime = System.currentTimeMillis();
		long offset = 0;
		
		while(telephonyManager.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK
				&& offset < WAIT_OFF_HOOK_TIMEOUT){
			try {
				Thread.sleep(WAIT_OFF_HOOK_SLEEP_TIME);
			} catch (InterruptedException e) {
				Log.w("NoMolestar", 
						"Unexpectedly interrupted while waiting for offhook", 
						e);
			}
			offset = System.currentTimeMillis() - startTime;
		}
	}
	
	private void endCallAidl(ITelephony telephonyService) throws Exception {
		

		// Silence the ringer and answer the call!
		telephonyService.endCall();
	}
}