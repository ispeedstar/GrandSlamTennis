/*<START_HEADER>**************************************************************
	FILENAME:    	GrandSlam.java
	S/W LIBRARY #:	LIB/Android - GrandSlamTennis
	COPYRIGHT @ Grand Slam Tools
	$Date$   $Revision$
	------------------------
	DESCRIPTION:    Show winning tactics and strategies patterns and description.
	
  
	REVISION HISTORY:
	
      $Log$
      Revision 1.0  Jan 15, 2013  slam
      Initial revision
<END_HEADER>******************************************************************/
package com.gst.grandslamtennis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.content.Context;
import android.content.DialogInterface;



public class GrandSlam extends Activity {
	
	// String used when logging application messages
	private static final String TAG = "Grand Slam Tennis";
	
	private List<String> patternNameList;			// tennis pattern names
	private String patternName;						// view tennis pattern name
	private int iStartPattern;						// start pattern index
	private List<String> playNameList;  			// tennis play shot file names
	private List<String> playNumberList;			// tennis play pattern number
	private String playName;						// view play shot name
	private static int iPplay;						// view play shot index
	private static int playPatternNumber;			// play pattern number
	private List<String> playDescriptionList;		// list of all play shot descriptions
	private List<String> playObjectiveList;			// list of all play objectives
	
	private TabHost tabHost;						// display pattern tab selection
	private EditText playNameEditText;				// display play name text
	private EditText playDescEditText;				// display play description text
	private ImageView playImageView;				// display play shot image
	private Button previousButton;					// view previous shot image
	private Button nextButton;						// view next shot image
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grand_slam);
		
		Log.i(TAG,this.getClass().getName()+"."+ new Exception().getStackTrace()[0].getMethodName());
		
        // initiate list of tennis patterns and play name list, the index number match with the pattern number-1
		patternNameList = new ArrayList<String>();		// list of tennis patterns 
		playNameList = new ArrayList<String>();			// list of tennis play	
		playNumberList = new ArrayList<String>();		// list of pattern play number
		playDescriptionList = new ArrayList<String>();  // list of pattern play description
		playObjectiveList = new ArrayList<String>();	// list of play objective 
		
		// get reference to GUI components
		tabHost = (TabHost)findViewById(R.id.tabHost);
		playImageView = (ImageView)findViewById(R.id.playImageView);
		playImageView.setOnClickListener(selImageListener);
		playNameEditText = (EditText)findViewById(R.id.playNameEditText);
		playNameEditText.setFocusable(false);
		playDescEditText = (EditText)findViewById(R.id.playDescEditText);
		playDescEditText.setFocusable(false);
		previousButton = (Button)findViewById(R.id.previousButton);
		previousButton.setOnClickListener(selButtonListener);
		nextButton = (Button)findViewById(R.id.nextButton);
		nextButton.setOnClickListener(selButtonListener);
		Log.i(TAG,"Get reference to GUI components");
		
		startApplication();
		
	} // end onCreate

	
	
	// Start Grand Slam application.
	private void startApplication() {
		// get array of pattern play description
		String[] playDescriptions = getResources().getStringArray(R.array.playDescriptionList);
		for (int i=0; i<playDescriptions.length; i++)
		{
			playDescriptionList.add(playDescriptions[i]);
		}
		
		// get array of tennis patterns from strings.mxl
		String[] patterns = getResources().getStringArray(R.array.patternsList);
		for (int i=0; i<patterns.length; i++)
		{
			patternNameList.add(patterns[i]);
		}
		
		getPlayObjectiveList();
		setPatternTabSelection(patterns);  // set tab selection
		
		// start viewing first tennis serve pattern
		tabHost.setCurrentTab(2);  			// set a initial default tab other than serve
		iStartPattern = 0;				// start the first pattern (ie Serve)
		patternName = patterns[iStartPattern];  
		Log.i(TAG,"setCurrentTab " + patternName);
		tabHost.setCurrentTab(iStartPattern);
	} // end startApplication


	
	// Get list of play objective from file from /res/raw folder
	private void getPlayObjectiveList()
	{
		try
		{
			String line;
			Context context = getApplicationContext();
			InputStream inputStream = context.getResources().openRawResource(R.raw.tennis_tactics_objectives);
	        InputStreamReader inputReader = new InputStreamReader(inputStream);
	        BufferedReader buffReader = new BufferedReader(inputReader);
	         
	        while ((line = buffReader.readLine()) != null) 
	        {
	        	// add to list
	        	String[] fieldToken = line.split("-");
 				if (fieldToken.length == 2)
 				{
 					playObjectiveList.add(fieldToken[1]);
 				}
 				else
 				{
 					playObjectiveList.add("na");
 				}
	        	
	        }
	        buffReader.close();
	        inputReader.close();
	        inputStream.close();
	   } catch (IOException ex) 
	   {
		   Log.e(TAG,"Error unable to getPlayObjectiveList " + ex.getMessage());
	   }
	} // end getObjectiveList
	
	

    // Load and view selected winning pattern
 	private void viewPattern(String pattern)
 	{
 		Log.i(TAG,this.getClass().getName()+"."+ new Exception().getStackTrace()[0].getMethodName() + " " + pattern);
		
 		patternName = pattern;
 		// used AssetManager to get list of tennis play file names
 		AssetManager assets = getAssets();  // get the app's AssetManager
 		playNameList.clear();  				// empty the play name and number list 
 		playNumberList.clear();
 		
 		try
 		{
 			// get a list of all play name and number from files in the selected pattern folder
 			String[] paths = assets.list(pattern);
 			for (String path : paths)
 			{
 				String[] pathToken = path.split("-");
 				if (pathToken.length == 2)
 				{
 					playNumberList.add(pathToken[0]);
 					playNameList.add(pathToken[1].replace(".png",""));
 				}
 				else
 				{
 					Log.i(TAG,"Warning unable to load " + path);
 				}
 			}
 		}
 		catch(IOException ex)
 		{
 			Log.e(TAG,"Error viewPattern " + pattern, ex);
 		}  // end try-catch
 		
 		// start loading first image from selected pattern
 		iPplay = 0;
 		viewPlay(iPplay);
 	} // end viewPattern
     

 	
	// Load play image and update play name and selected button status
	private void viewPlay(int play_index)
	{
		updatePlayNameAndDescription(play_index);
		loadPlayImage(play_index);
		updateButtonStatus(play_index);
	}  // end viewPlay
	

	
	// Load play image 
	private void loadPlayImage(int play_index) 
	{
		// get the filename of the selected shot
		StringBuilder sb = new StringBuilder();
		sb.append(playNumberList.get(play_index));
		sb.append("-");
		sb.append(playNameList.get(play_index));
		sb.append(".png");
		String playImageFilename = sb.toString();
		Log.i(TAG,"loadPlayImage " + playImageFilename);
		
		// use AssetManager to load the image from assets folder
		AssetManager assets = getAssets();
		InputStream stream;		// used to read shot image file
		try
		{
			// get an InputStream to shot image asset
			stream = assets.open(patternName + "/" + playImageFilename);
			
			// load the asset as a Drawable and display on shotImageView
			Drawable playImage = Drawable.createFromStream(stream, playImageFilename);
			playImageView.setImageDrawable(playImage);
		}
		catch (IOException ex)
		{
			Log.e(TAG,"Error loadPlayImage " + playImageFilename, ex);
		} // end try-catch
	} // end loadPlayImage

	
	
	// Update play name and play description text
	private void updatePlayNameAndDescription(int play_index) 
	{	
		// Update play name display
		StringBuilder sb = new StringBuilder();
		String playNumberStr = playNumberList.get(play_index);
		playNumberStr = playNumberStr.replace("P0","P");
		playPatternNumber = Integer.parseInt(playNumberStr.substring(1));  // get pattern number
		playNumberStr = playNumberStr.replace("P", "Pattern ");
		sb.append(playNumberStr);
		sb.append(" - ");
		sb.append(playNameList.get(play_index).replace('_', ' '));
		playName = sb.toString();
		playNameEditText.setText(playName);
		playNameEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		playNameEditText.setFocusable(false);
		
		// update play description display
		int play_desc_index = Integer.parseInt(playNumberList.get(play_index).substring(1));
		playDescEditText.setText("Tip - " + playDescriptionList.get(play_desc_index -1));
		playDescEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		playDescEditText.setFocusable(false);
		playDescEditText.setVisibility(View.VISIBLE);
	} // end updatePlayNameAndDescription
	
	
	
	// Update play objective text in playNameEditText 
	private void updatePlayObjective(int play_pattern)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Objective - ");
		sb.append(playObjectiveList.get(play_pattern-1));
		playNameEditText.setText(sb.toString());
		playDescEditText.setVisibility(View.GONE);
	} // end updatePlayObjective
	
	
	
	// Update Previous or Next button status
	private void updateButtonStatus(int play_index)
	{
		// disable Previous and Next button when index reach begin or end of shot image list
		if (play_index == 0)
		{
			previousButton.setEnabled(false);		
			nextButton.setEnabled(true);
		}
		else if (play_index == (playNameList.size()-1))
		{
			nextButton.setEnabled(false);
			previousButton.setEnabled(true);
		}
		else
		{
			previousButton.setEnabled(true);
			nextButton.setEnabled(true);
		}
	} // end updateButtonStatus
	
   	
	
   	// Perform button action
   	private View.OnClickListener selButtonListener = new  View.OnClickListener()
   	{
   		@Override
   		public void onClick(View v) {
   			
   			submitButton((Button) v);
   			viewPlay(iPplay);
   		}

   	}; // end actionButtonListener
   	
   	
   	
   	// Submit button
 	private void submitButton(Button button)
 	{
 		String submitAction = button.getText().toString();
 		Log.i(TAG,"submitButton " + submitAction);
 		
 		if (submitAction.equals("Previous"))
 		{
 			iPplay--;
 		}
 		else if (submitAction.equals("Next"))
 		{
 			iPplay++;
 		}
 		else
 		{
 			Log.e(TAG,"Error: submitButton " + submitAction);
 			//Log.e(TAG,"Error: attempt to view " + String.valueOf(playIndex) + " of " + patternName);
 		}
 	} // end submitButton
 	
 	
 	
 	// Show pattern objective when user click on image
 	private View.OnClickListener selImageListener = new View.OnClickListener() 
 	{
		
		@Override
		public void onClick(View v) {
			updatePlayObjective(playPatternNumber);
		}
	};
 	
	
 	
 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
 
	
 	
	// Called when the user selects an option from the menu
   	@Override
   	public boolean onOptionsItemSelected(MenuItem item) 
   	{
   		String title;
   		String message;
   		// switch the menu id when user selected menu option
   		int menuId = item.getItemId();
   		switch(menuId)
   		{
	   		case R.id.menu_instruction:
				// create instruction dialog
   				title = getString(R.string.instruction);
   				message = getString(R.string.instruction_msg);
   				showMenuDialog(menuId,title,message);
				return true;
   			case R.id.menu_credits:
   				// create a credits dialog
   				title = getString(R.string.credits);
   				message = getString(R.string.credits_msg);
   				showMenuDialog(menuId,title,message);
   				return true;
   			case R.id.menu_about:
   				// create a credits dialog
   				title = getString(R.string.about);
   				StringBuilder sb = new StringBuilder();
   				sb.append(getString(R.string.app_name));
   				sb.append("\n");
   				sb.append("Version: ");
   				sb.append(getPackageVersionName());
   				sb.append("\n");
   				sb.append("Developer: ");
   				sb.append(getString(R.string.app_developer));
   				sb.append("\n");
   				sb.append("Company: ");
   				sb.append(getString(R.string.app_company));
   				message = sb.toString();
   				//message = getString(R.string.about_msg);
   				showMenuDialog(menuId,title,message);
   				return true;
   			default:
   				return super.onOptionsItemSelected(item);
   					
   		} // end switch
   	} // end onOptionsItemSelected

   	
   	
   	// Get package version name from application manifest
	private String getPackageVersionName() {
		try
		{
			return(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		}
		catch(Exception ex)
		{
			Log.e(TAG,"Error: unable to get package version name " + ex);
			return("n/a");
		}
	} // end getPackageVersionName

	

   	// Show menu dialog message
	private void showMenuDialog(int menuId, String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (menuId == R.id.menu_about)
		{
			builder.setIcon(R.drawable.stanley_cup);
		}
		else
		{
			builder.setIcon(R.drawable.ic_launcher);
		}
		builder.setTitle(title);
		builder.setMessage(msg);	
		builder.setCancelable(false);
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
				
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	} // end showMenuDialog

	
	
	// Set tab control to select tennis pattern
	private void setPatternTabSelection(String[] patterns) {
		// setup pattern selection tab
		tabHost.setup();
		TabSpec[] specs = new TabSpec[patterns.length];
	
		for (int i=0; i<patterns.length; i++)
		{
			specs[i] = tabHost.newTabSpec(patterns[i]);
			specs[i].setContent(R.id.tabPattern);
			specs[i].setIndicator(patterns[i]);
			tabHost.addTab(specs[i]);
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			@Override
			public void onTabChanged(String tabId) {
	            patternName = tabId;  	// set the pattern name to the select pattern tab
				//Log.i(TAG,"select tab " + tabId);
				viewPattern(patternName);
			}
		});
	} // end setPatternTabSelection
	
} // end GrandSlam
