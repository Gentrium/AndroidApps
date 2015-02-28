package com.example.mycontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * The Splash Screen class.
 */
public class SplashScreen extends Activity {

	/**
	 * 
	 */
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);
		
		runStopper();

	}
	
	/**
	 * Makes delay for Splash Screen.
	 */
	private void runStopper() {

		Handler splashNew = new Handler();

		splashNew.postDelayed(new SplashHandler(), 2000);

	}

	/**
	 * 
	 */
	class SplashHandler implements Runnable {

		/**
		 * Starts Main Screen of application.
		 */
		public void run() {
			startActivity(new Intent(getApplication(), MainScreen.class));
			finish();
			// finishing Splash screen

		}

	}

	
}
