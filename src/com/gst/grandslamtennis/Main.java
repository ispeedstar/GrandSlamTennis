/*<START_HEADER>**************************************************************
	FILENAME:    	Main.java
	S/W LIBRARY #:	LIB/Android - GrandSlamTennis
	COPYRIGHT @ Grand Slam Tools
	$Date$   $Revision$
	------------------------
	DESCRIPTION:    Main application to help users improve tennis skills by learning
	winning patterns of play. 
	
  
	REVISION HISTORY:
	
      $Log$
      Revision 1.0  Jan 15, 2013  slam
      Initial revision
<END_HEADER>******************************************************************/
package com.gst.grandslamtennis;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;



//Display tennis winning patterns to help player improve
public class Main extends Activity {

	// String used when logging error messages
	private static final String TAG = "Grand Slam Tennis";
	private Button startButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i(TAG,this.getClass().getName()+"."+ new Exception().getStackTrace()[0].getMethodName());
		
		final Context context = getApplicationContext();
		startButton = (Button)findViewById(R.id.buttonStart);
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentGrandSlam = new Intent(context,com.gst.grandslamtennis.GrandSlam.class);
				startActivity(intentGrandSlam);
				Log.i(TAG,"Start Grand Slam Tennis");
			}
		});
	} // end onCreate

} // end Main
