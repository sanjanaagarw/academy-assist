package com.nss.academyassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
//import statements for client
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import statements for client
import android.os.AsyncTask;

import com.nss.academyassist.ParseOperations;

@SuppressLint({ "NewApi", "DefaultLocale" })
public class MainActivity extends Activity implements OnClickListener {

	EditText question;
	static final int check = 1111;
	TextToSpeech tts;
	
	//Client Server sockets & variables
	private Socket client;
	private PrintWriter printwriter;
	private String toTag;
    private BufferedReader bufferedReader;
	//create to receive from server
	InputStreamReader inputStreamReader;

	//int makeClient = 1;
	
	//Navigation Drawer items
    private String[] drawerListViewItems;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
	
    Receiver receiver;
    String taggedQuestion;
    ArrayList<String> splitTaggedQuestion;
    ArrayList<String> splitQuestion;
    TextView textView;
    
    ParseOperations p = new ParseOperations();
    
    static String namedEntity="abc";
    static boolean entityIsFound;
//    String finalNamedEntity;
    static String namedEntityClass="abc";
    
    ProgressDialog loading;
    
    boolean breakSwitch;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	/*
		//following two lines for full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	*/	
		setContentView(R.layout.activity_main);
		
		 
        // get list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.items);
 
        // get ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);
 
                // Set the adapter for the list view
        drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, drawerListViewItems));

        // App Icon 
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        // create ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                );
 
        // Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
 
        //comment this if fullscreen
        getActionBar().setDisplayHomeAsUpEnabled(true); 
 
        // just styling option add shadow the right edge of the drawer
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
 
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

		// create client as soon as the activity starts
		CreateClient clientConnection = new CreateClient();
		clientConnection.execute();
		
		question = (EditText)findViewById(R.id.editText1);
		//question.setText("where is the incubation center?");
		
		Button query = (Button)findViewById(R.id.button2);
		query.setOnClickListener(this);
		
		textView = (TextView)findViewById(R.id.textView1);

		tts = new TextToSpeech(MainActivity.this,  new TextToSpeech.OnInitListener() {
			
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if(status != TextToSpeech.ERROR)
				{
					tts.setLanguage(Locale.US);
				}
			}
		});
		
		
		ImageButton listen = (ImageButton)findViewById(R.id.button1);
		listen.setOnClickListener(this);
		
		//query.performClick();
		
	}

	private class CreateClient extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			try {
				//Toast.makeText(getApplicationContext(), "Connection made", 
				//	   Toast.LENGTH_LONG).show();
				client = new Socket("192.168.43.160", 4444); //to connect to the server, put server ip address				
				//client = new Socket("172.20.10.2", 4444); //to connect to the server, put server ip address				
				//client = new Socket("192.168.1.102", 4444); //to connect to the server, put server ip address				
				//client = new Socket("192.168.1.112", 4444); //to connect to the server, put server ip address				
				printwriter = new PrintWriter(client.getOutputStream(), true);

				try {
					inputStreamReader = new InputStreamReader(client.getInputStream());
			        bufferedReader = new BufferedReader(inputStreamReader);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//printwriter.write(toTag); // write the message to output stream
				//printwriter.write("\n"); //delimiter
				
				//printwriter.flush();
				//makeClient = 0;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();	
			}
			return null;
		}
		
		/*
		@Override
		protected void onPostExecute(Void result) {
			
			//create to receive from server
			InputStreamReader inputStreamReader;
			try {
				inputStreamReader = new InputStreamReader(client.getInputStream());
	            bufferedReader = new BufferedReader(inputStreamReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
           Receiver receiver = new Receiver(); // Initialize receiver AsyncTask.
           receiver.execute();
		}
		*/

	}
	
	private class CloseClient extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if(printwriter != null) {
				try {
					printwriter.write("\n");
					printwriter.close();
					client.close(); // closing the connection

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}

	}
	
	private class SendMessage extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

				printwriter.write(toTag); // write the message to output stream
				printwriter.write("\n"); //delimiter
				
				printwriter.flush();

			return null;
		}
		/*
		@Override
		protected void onPostExecute(Void result) {			
	           Receiver receiver = new Receiver(); // Initialize receiver AsyncTask.
	           receiver.execute();
		}*/
	}

	private class Receiver extends AsyncTask<Void, Void, Void> {
		   
	        //private String fromServer;
			
			@Override
	        protected Void doInBackground(Void... params) {

	                try {	 
	                    if (bufferedReader.ready()) {
	                    	taggedQuestion = bufferedReader.readLine();
	                    	splitTaggedQuestion = new ArrayList<String>(Arrays.asList(taggedQuestion.split(" ")));
	                    	publishProgress();
	                    }
	                } catch (UnknownHostException e) {
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }

	            return null;
	        }
	        
	        @Override
	        protected void onProgressUpdate(Void... values) {
	        	//Toast.makeText(getApplicationContext(), fromServer, 
					//   Toast.LENGTH_SHORT).show();	
	        	//textView.setText(taggedQuestion + "\n");	
	        	textView.setText("\nProcessing question...");
	        }
	}
	
	public void onClick(View v)
	{
		switch(v.getId())
		{
		    case R.id.button1:
		    	Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				i.putExtra(RecognizerIntent.EXTRA_PROMPT, "SPeak your query..");
				startActivityForResult(i, check);
				break;
				
		    case R.id.button2:
		 
		    	//Display loading dialog box for 4 secondds
				//final Dialog loading = new Dialog(MainActivity.this);
		    	loading = new ProgressDialog(MainActivity.this);
		    	//loading.setCancelable(true);
		    	loading.setMessage("Getting answer.. Please wait...");
		    	//loading.show();
				
		    	//loading.setTitle("Getting answer...");
				loading.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
				
			    loading.show();
		    	
	    		toTag = question.getText().toString().toLowerCase();
	    		
		    	if(validate())
		    	{
		    		//tts.speak("Have patience. It's coming soon!", TextToSpeech.QUEUE_FLUSH, null);
		      		
					int questionType = checkQuestionType(toTag);
					
					switch(questionType) {
					
					case 1:
						//	Factoid questions...
						SendMessage sendMessageTask = new SendMessage();
						sendMessageTask.execute();
						//breakSwitch = false;
						
						new Handler().postDelayed(new Runnable() {
		                      @Override
		                      public void run() {
		          				receiver = new Receiver(); // Initialize receiver AsyncTask.
		        	    		receiver.execute();
		        	    		//tts.speak("This is the part of speech tagged string.", TextToSpeech.QUEUE_FLUSH, null);
		                    	tts.speak("Part of speech tagging successful.", TextToSpeech.QUEUE_FLUSH, null);
		                    	
		                    	/*
		                    	//Replace special case POSTags
		                    	
		                    	int index = splitTaggedQuestion.indexOf("be_VB");
		                    	if(index != -1)
		                    	{
			                    	splitTaggedQuestion.set(index, "be_NN");
		                    	}
		                    	index = splitTaggedQuestion.indexOf("lost_VBN");
		                    	if(index != -1)
		                    	{
			                    	splitTaggedQuestion.set(index, "lost_NN");
		                    	}
		                    	index = splitTaggedQuestion.indexOf("found_VBN");
		                    	if(index != -1)
		                    	{
			                    	splitTaggedQuestion.set(index, "found_NN");
		                    	}
		                    	
		                    	Log.d("Updated POS tags", Arrays.toString(splitTaggedQuestion.toArray()));
		                    	*/
		                    	
		                    	NamedEntityRecognition();
		                    	
		                      }
		                  }, 2000); //To display after how many seconds
						
						/*
						if(breakSwitch)
							break;
						*/
						//String namedEntity = getNamedEntity();
						int subType = getSubType();
					
						switch(subType) {
							
							case 1:
								
								//String namedEntityClass = "room"; //getNamedEntityClass()
								//String namedEntity = "library"; // getNamedEntity();
																
								new Handler().postDelayed(new Runnable() {
				                      @Override
				                      public void run() {

				                    	  	// Remove ? mark from the end
				                    	  	String replaceThis = splitQuestion.get(splitQuestion.size() - 1).toString();
				                  			
				                  			if (replaceThis.length() > 0 && replaceThis.charAt(replaceThis.length()-1)=='?') {
				                  				replaceThis = replaceThis.substring(0, replaceThis.length()-1);
				                  		    }
				                  			
				                  			splitQuestion.remove(splitQuestion.size()-1);
				                  			splitQuestion.add(replaceThis);
				                  			//splitQuestion.get(splitQuestion.size() - 1).toString().substring(0,splitQuestion.get(splitQuestion.size() - 1).toString().length()-1);
				                  			
				                  			Log.d("ensure", splitQuestion.get(splitQuestion.size() - 1).toString());
				                  			//str = str.substring(0, str.length()-1);
				                    	  	String tag = getTag();
				                    	  	
											if(tag != null) {
												//p = new ParseOperations();
												Log.d("Named entity class is ", namedEntityClass);
												Log.d("Named entity is ", namedEntity);
												Log.d("Tag is ", tag);
												p.getDocumentList(namedEntityClass, namedEntity, tag);
												loading.setMessage("Final step...");
							    	        	textView.setText("\nFinal step...");
							    	        	
												new Handler().postDelayed(new Runnable() {
								                      @Override
								                      public void run() {
								                    	  
								                    	  loading.dismiss();
								                    	  
								                    	  textView.setText(com.nss.academyassist.ParseOperations.answer.get(0).toString());
								                    	  tts.speak(com.nss.academyassist.ParseOperations.answer.get(0).toString(), TextToSpeech.QUEUE_FLUSH, null);
								      		      		
								                      }
								                  }, 4000); //Displays the answer
												
											}
											else {
												//No tag found
												loading.dismiss();
												textView.setText("Sorry, but that information is not available, yet!" + "\n");
												tts.speak("Sorry, but that information is not available, yet!", TextToSpeech.QUEUE_FLUSH, null);
											}
				                      }
				                  }, 16000); //Displays the answer
								
								
								/*
								if(splitQuestion.contains("library"))
									namedEntity = "library"; // getNamedEntity() or maybe combine the two in one fucntion
								else
									namedEntity = "incubation center";
								*/
								
								
								break;
								
							case 2:
								break;
							
							case 3:
								break;
							
							default:
								break;
						}
									
						break;
					case 2:
						//	Y/N questions...
						loading.dismiss();
						textView.setText("Yes/No question type not supported, yet!" + "\n");
						tts.speak("Yes/No question type not supported, yet!", TextToSpeech.QUEUE_FLUSH, null);
						break;
					case 3:
						//	Reasoning questions...
						loading.dismiss();
						textView.setText("Reasoning question type not supported, yet!" + "\n");
						tts.speak("Reasoning question type not supported, yet!", TextToSpeech.QUEUE_FLUSH, null);
						break;
					default:
						loading.dismiss();
						textView.setText("This question type not supported, yet!" + "\n");
			        	//tts.speak("Question type not supported, yet!", TextToSpeech.QUEUE_FLUSH, null);
						break;					
					}
					
		    	}
		    	else
		    	{
		    		loading.dismiss();
		    		tts.speak("Please ask the question in proper format!", TextToSpeech.QUEUE_FLUSH, null);
			   	}
		    	
		    	break;
		}
		
	}
	
	public boolean checkKeyword(ArrayList <String> question, ArrayList <String> keywords) {
		for(int i=0; i<keywords.size(); i++) {
				if(question.contains(keywords.get(i)))
					return true;
		}
		
		return false;
	}
	String TAG = "MainActivity";
	
	void NamedEntityRecognition() {

		//value = editText.getText().toString();
        //Log.v(TAG, "" + splitTaggedQuestion);
		
		//namedEntity is Sanjana_NNP Agarwal_NNP
        namedEntity = stringMatching(taggedQuestion);
        Log.v(TAG, taggedQuestion);
        Log.v(TAG, "" + namedEntity);
        //String finalNamedEntity="";
        
        //Removing POS Tags from Named Entity
        for(int i=0;i<namedEntity.length();i++){
            namedEntity = namedEntity.replace("_NNP","");
            namedEntity = namedEntity.replace("_NNS","");
            namedEntity = namedEntity.replace("_NN","");
            //finalNamedEntity+=namedEntity+ " ";
        }
        
        namedEntity = namedEntity.trim();

        String onlyLastTwo[] = namedEntity.split(" ");
        if(onlyLastTwo.length >= 2)
        {
        	String newNamedEntity = onlyLastTwo[onlyLastTwo.length-2] + " " + onlyLastTwo[onlyLastTwo.length-1]; 
        	namedEntity = newNamedEntity;
        }
        
        
//        namedEntity = namedEntity.trim();
        Log.v(TAG, namedEntity);
        p.forNER("person", namedEntity);
        //Log.d("Entity value is",object.entityFound+"");
        //if (!object.entityFound) {
         //   Log.v(TAG,"Holo");
        
        /*
        if(p.entityFound)
    	{
        	namedEntityClass = "person";
        	return;
    	}
        */
        
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, namedEntity+ " 1" );
                    Log.v(TAG, namedEntityClass+ " 1" );
                    Log.v(TAG, entityIsFound+ " " );
    	        	textView.setText("\nHang in there...");
					loading.setMessage("Hang in there...");
					
                    //Log.d("Entity value in room is",entityIsFound+"");
                    if (!entityIsFound) {
                        p.forNER("room", namedEntity);
                        
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.v(TAG, namedEntity+ " 2" );
                                Log.v(TAG, namedEntityClass+ " 2" );
                                Log.d("E value in utility is", entityIsFound + "");
                	        	textView.setText("\nA little bit more...");
            					loading.setMessage("A little bit more...");
            					
                                if (!entityIsFound) {
                                    p.forNER("utility", namedEntity);
                                    
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                        	if(!entityIsFound)
                                        	{
                                        		namedEntityClass = "out of domain";
                                                Log.v(TAG, "Out of domain question!" );
                                        	}
                                        }
                                    }, 3500);
                                    
                                }
                            }
                        }, 3500);
                    }
                }
            }, 3500);
    }
	
    public String stringMatching(String value)
    {
        String regexWords=" ";
        String regex = "\\w+(_NNP|_NNS|_NN)";
        //Log.d("Regex is ", ""+regex);
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        while(m.find()){
            Log.d("Matcher", "It matched");
            //Log.d("m group is ", ""+m.group());
            regexWords += m.group(0) + " ";
            //Log.d("Regex words are:", ""+regexWords);
        }
        return regexWords;
    }
	
	
	String getTag() {
		//	For each tag in the knowledge base, there should be an associated keyword 
		/*
		Based on a single word:
		
		When - Time //Or whatever tag corresponds to time
		Where - location
		Who - fullName
		Whom - fullName
		Whose - fullName
		
		Based on phrases
		Which
		What
			time
		How
		
		Not supported
		Why (already taken care of in checkQuestionType())
		
		*/
		
		Log.d("NamedEntityClass value",namedEntityClass);
		
		if(namedEntityClass.equals("person"))
		{

			if(splitQuestion.contains("where")) {
				return "location";
			}
			
			if(splitQuestion.contains("who")) {
				
				return "Type";
			}
			
			if(splitQuestion.contains("what")) {
				
				Log.d("Going in if: ",namedEntityClass);

				Log.d("question ",Arrays.toString(splitQuestion.toArray()));
				
				ArrayList <String> subjectKeywords = new ArrayList <String>();
				subjectKeywords.add("teach");
				subjectKeywords.add("teaches");
				subjectKeywords.add("conduct");
				subjectKeywords.add("conducts");
				
				if(checkKeyword(splitQuestion, subjectKeywords))
				{
					Log.d("Returning subject ",namedEntityClass);
					return "subject";	
				}
				
				ArrayList <String> UIDKeywords = new ArrayList <String>();
				UIDKeywords.add("roll");
				UIDKeywords.add("UID");
				UIDKeywords.add("id");
				
				if(checkKeyword(splitQuestion, UIDKeywords))
					return "UID";
				
				
				ArrayList <String> branchKeywords = new ArrayList <String>();
				branchKeywords.add("branch");
				branchKeywords.add("department");
				
				if(checkKeyword(splitQuestion, branchKeywords))
					return "branch";
				
				ArrayList <String> batchKeywords = new ArrayList <String>();
				batchKeywords.add("batch");
				batchKeywords.add("year");
				
				if(checkKeyword(splitQuestion, batchKeywords))
					return "batch";
				
				
				ArrayList <String> degreeKeywords = new ArrayList <String>();
				degreeKeywords.add("degree");
				
				if(checkKeyword(splitQuestion, degreeKeywords))
					return "degree";
				
				ArrayList <String> numberKeywords = new ArrayList <String>();
				numberKeywords.add("number");
				
				if(checkKeyword(splitQuestion, numberKeywords))
					return "number";

				ArrayList <String> emailKeywords = new ArrayList <String>();
				emailKeywords.add("email");
				emailKeywords.add("mail");
				
				if(checkKeyword(splitQuestion, emailKeywords))
					return "email";
				
				ArrayList <String> descriptionKeywords = new ArrayList <String>();
				descriptionKeywords.add("about");
				descriptionKeywords.add("description");
				descriptionKeywords.add("describe");
				
				if(checkKeyword(splitQuestion, descriptionKeywords))
					return "about";
				
				ArrayList <String> postKeywords = new ArrayList <String>();
				postKeywords.add("post");
				postKeywords.add("position");
				postKeywords.add("role");
				
				if(checkKeyword(splitQuestion, postKeywords))
					return "designation";			

				
			}
			
			if(splitQuestion.contains("how")) {

				ArrayList <String> locationKeywords = new ArrayList <String>();
				locationKeywords.add("find");
				locationKeywords.add("get");
				locationKeywords.add("arrive");
				locationKeywords.add("reach");
				locationKeywords.add("visit");
				//locationKeywords.add("sit");
				
				if(checkKeyword(splitQuestion, locationKeywords))
					return "location";			
			}
			
		}
		
		if(namedEntityClass.equals("room"))
		{
			if(splitQuestion.contains("when")) {
				return "time";
			}
			
			if(splitQuestion.contains("where")) {
				return "location";
			}
			
			if(splitQuestion.contains("who") || splitQuestion.contains("whose") || splitQuestion.contains("whom")) {
				
				ArrayList <String> inChargeKeywords = new ArrayList<String>();
				inChargeKeywords.add("manage");
				inChargeKeywords.add("manages");
				inChargeKeywords.add("incharge");
				inChargeKeywords.add("manager");
				inChargeKeywords.add("handles");
				inChargeKeywords.add("leads");
				inChargeKeywords.add("control");
				inChargeKeywords.add("controls");
				inChargeKeywords.add("administer");
				inChargeKeywords.add("administers");
				inChargeKeywords.add("regulate");
				inChargeKeywords.add("regulates");
				inChargeKeywords.add("supervise");
				inChargeKeywords.add("supervises");
				inChargeKeywords.add("maintain");
				inChargeKeywords.add("maintains");
				inChargeKeywords.add("run");
				inChargeKeywords.add("runs");
				
				if(checkKeyword(splitQuestion, inChargeKeywords))
					return "inCharge";

				return "inCharge";
			}
			
			if(splitQuestion.contains("what")) {

				ArrayList <String> descriptionKeywords = new ArrayList <String>();
				descriptionKeywords.add("happen");
				descriptionKeywords.add("happens");
				descriptionKeywords.add("about");
				descriptionKeywords.add("mean");
				descriptionKeywords.add("meaning");
				descriptionKeywords.add("description");
				descriptionKeywords.add("describe");
				
				if(checkKeyword(splitQuestion, descriptionKeywords))
					return "description";
				
				ArrayList <String> timeKeywords = new ArrayList <String>();
				timeKeywords.add("time");
				timeKeywords.add("timing");
				timeKeywords.add("timings");
				if(checkKeyword(splitQuestion, descriptionKeywords))
					return "time";			
				
				return "description";
			}
			
			if(splitQuestion.contains("how")) {

				ArrayList <String> locationKeywords = new ArrayList <String>();
				locationKeywords.add("find");
				locationKeywords.add("get");
				locationKeywords.add("access");
				locationKeywords.add("collect");
				locationKeywords.add("arrive");
				locationKeywords.add("reach");
				locationKeywords.add("enter");
				locationKeywords.add("visit");
				//locationKeywords.add("sit");
				
				if(checkKeyword(splitQuestion, locationKeywords))
					return "location";			
			}
			
		}
		
		if(namedEntityClass.equals("utility"))
		{
			
			if(splitQuestion.contains("where")) {
				return "location";
			}
			
			if(splitQuestion.contains("who") || splitQuestion.contains("whose") || splitQuestion.contains("whom")) {
				
				ArrayList <String> inChargeKeywords = new ArrayList<String>();
				inChargeKeywords.add("manage");
				inChargeKeywords.add("manages");
				inChargeKeywords.add("incharge");
				inChargeKeywords.add("manager");
				inChargeKeywords.add("handles");
				inChargeKeywords.add("leads");
				inChargeKeywords.add("control");
				inChargeKeywords.add("controls");
				inChargeKeywords.add("administer");
				inChargeKeywords.add("administers");
				inChargeKeywords.add("regulate");
				inChargeKeywords.add("regulates");
				inChargeKeywords.add("supervise");
				inChargeKeywords.add("supervises");
				inChargeKeywords.add("maintain");
				inChargeKeywords.add("maintains");
				inChargeKeywords.add("run");
				inChargeKeywords.add("runs");
				
				if(checkKeyword(splitQuestion, inChargeKeywords))
					return "inCharge";

				return "inCharge";
			}
			
			if(splitQuestion.contains("what")) {

				ArrayList <String> usernameKeywords = new ArrayList <String>();
				usernameKeywords.add("username");
				usernameKeywords.add("account");
				usernameKeywords.add("login");
				if(checkKeyword(splitQuestion, usernameKeywords))
					return "username";		
				
				ArrayList <String> passwordKeywords = new ArrayList <String>();
				passwordKeywords.add("password");
				passwordKeywords.add("account");
				passwordKeywords.add("login");
				if(checkKeyword(splitQuestion, passwordKeywords))
					return "password";	

				ArrayList <String> wifirangeKeywords = new ArrayList <String>();
				wifirangeKeywords.add("range");
				wifirangeKeywords.add("access");
				wifirangeKeywords.add("signal");
				wifirangeKeywords.add("available");
				if(checkKeyword(splitQuestion, wifirangeKeywords))
					return "wifirange";	

				ArrayList <String> criteriaKeywords = new ArrayList <String>();
				criteriaKeywords.add("criteria");
				criteriaKeywords.add("eligible");
				criteriaKeywords.add("benchmark");
				criteriaKeywords.add("standard");
				if(checkKeyword(splitQuestion, wifirangeKeywords))
					return "criteria";	

				ArrayList <String> benefitsKeywords = new ArrayList <String>();
				benefitsKeywords.add("benefits");
				benefitsKeywords.add("advantage");
				benefitsKeywords.add("standard");
				benefitsKeywords.add("assist");
				if(checkKeyword(splitQuestion, wifirangeKeywords))
					return "benefits";		
				
				ArrayList <String> descriptionKeywords = new ArrayList <String>();
				descriptionKeywords.add("happen");
				descriptionKeywords.add("happens");
				descriptionKeywords.add("about");
				descriptionKeywords.add("mean");
				descriptionKeywords.add("meaning");
				descriptionKeywords.add("description");
				descriptionKeywords.add("describe");
				
				if(checkKeyword(splitQuestion, descriptionKeywords))
					return "description";
				
				return "description";
			}
			
			if(splitQuestion.contains("how")) {

				ArrayList <String> locationKeywords = new ArrayList <String>();
				locationKeywords.add("find");
				locationKeywords.add("get");
				locationKeywords.add("access");
				//locationKeywords.add("collect");
				locationKeywords.add("arrive");
				locationKeywords.add("reach");
				locationKeywords.add("enter");
				locationKeywords.add("visit");
				//locationKeywords.add("sit");
				
				if(checkKeyword(splitQuestion, locationKeywords))
					return "location";			
			}
			
			
		}
				
		//If none of the above then it is a phrase, thus following...
		
		
		return null;
	}
	
	int getSubType() {
		
		/*
		if(hasNamedEntity() && !hasVerb()) {// Named Entity without verb (Where is the examination office?)
			return 1;
		}
		else
			if(hasNamedEntity() && hasVerb()) {// Named Entity with verb (Where can I find the lockers?)
				return 2;
			}
			else
				if(!hasNamedEntity && hasVerb()) {// No Named Entity with verb followed by noun (Who teaches Physics?)
					return 3;
				}
		*/
		return 1;
	}
	
	int checkQuestionType(String question) {

		splitQuestion = new ArrayList<String>(Arrays.asList(question.split(" ")));
		//Log.d("size -1", splitQuestion.get(splitQuestion.size() - 1).toString());
		//splitQuestion.get(splitQuestion.size() - 1).toString().replace("?", "");
		
		
		//Reasoning question
		if(splitQuestion.contains("why") || splitQuestion.contains("Why")) {
			return 3;
		}
		
		//Factoid question
		ArrayList<String> validWhWords = new ArrayList<String>();
		validWhWords.add("when");
		validWhWords.add("where");
		validWhWords.add("who");
		validWhWords.add("whose");
		validWhWords.add("whom");
		validWhWords.add("what");
		validWhWords.add("how");
		/*
		validWhWords.add("When");
		validWhWords.add("Where");
		validWhWords.add("Who");
		validWhWords.add("Whose");
		validWhWords.add("Whom");
		validWhWords.add("What");
		validWhWords.add("How");
		*/
		
		if(checkKeyword(splitQuestion, validWhWords))
			return 1;		
		
		// Y/N questions
		ArrayList<String> validYNWords = new ArrayList<String>();
		validYNWords.add("is");
		validYNWords.add("isn't");
		validYNWords.add("are");
		validYNWords.add("ain't");
		validYNWords.add("do");
		validYNWords.add("don't");
		validYNWords.add("does");
		validYNWords.add("doesn't");
		validYNWords.add("can");
		validYNWords.add("can't");
		validYNWords.add("am");
		
		validYNWords.add("was");
		validYNWords.add("wasn't");
		validYNWords.add("were");
		validYNWords.add("weren't");
		
		validYNWords.add("will");
		validYNWords.add("won't");
		validYNWords.add("would");
		validYNWords.add("wouldn't");
		validYNWords.add("should");
		validYNWords.add("shouldn't");
		validYNWords.add("could");
		validYNWords.add("couldn't");
		
		if(validYNWords.contains(splitQuestion.get(0).toString()))
			return 2;
		
		return 4;
	}
	
	
	//to validate whether the question has WH word in it, works with only small case wh words
	boolean validate()
	{
			
		String toValidate = question.getText().toString();
		//Toast.makeText(getApplicationContext(),toValidate,Toast.LENGTH_SHORT).show();
		if(toValidate.equalsIgnoreCase("ok thanks?"))
		{
			return true;
		}
		
		//No compound questions allowed
		//Just checks whether the first occurence and the last occurence of '?' is the same or not. Needs modification.
		if(toValidate.indexOf('?') != toValidate.lastIndexOf('?'))
		{
			TextView textView = (TextView) findViewById(R.id.textView1);
	    	textView.setText("Compound questions not supported at the moment." + "\n");
			return false;	
		}
		
		//tokens array is sorted so that we can use binary search. 
		String tokens[] = {"are","could","did","do","does","how","is","shall","should","was","what","when","where","which","who","whom","whose","why","will","would"};
		
		String valArr[] = toValidate.split(" ");
		
		//binary search each word of the user's quetion in tokens
		int index = -1;
		for(int i=0; i<valArr.length; i++)
		{
			index = Arrays.binarySearch(tokens, valArr[i]);
			if(index > -1)
				return true;
		}
		
		return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		CloseClient closeConnection = new CloseClient();
		closeConnection.execute();
		if(tts != null)
		{
			tts.stop();
			tts.shutdown();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(tts != null)
		{
			tts.stop();
			tts.shutdown();
		}
		
		super.onPause();
	}
		
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if(arg0 == check && arg1 == RESULT_OK)
		{
			ArrayList<String> results = arg2.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			question.setText(results.get(0)+"?");
		}
		
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         actionBarDrawerToggle.syncState();
    }
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
         // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@SuppressWarnings("rawtypes")
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			//Toast.makeText(MainActivity.this, position+""/*((TextView)view).getText()*/, Toast.LENGTH_SHORT).show();
			
			switch(position) {
            
            case 1:
            	Intent guideliness = new Intent(MainActivity.this, Guideliness.class);
            	startActivity(guideliness);
            	/*
            	//set up dialog
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.guideliness);
                dialog.setTitle("Guideliness");
                //dialog.setCancelable(true);
                //there are a lot of settings, for dialog, check them all out!

                //now that the dialog is set up, it's time to show it    
                dialog.show();
            	
//            	Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                */
                break;
            case 0:
            	Intent login = new Intent(MainActivity.this, Register.class);
            	startActivity(login);
            	break;
            	
            case 2:
            case 3:
            	Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                break;             
            default:
                break;
        }
			
			drawerLayout.closeDrawer(drawerListView);
		}
    }
    
    /*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/
}