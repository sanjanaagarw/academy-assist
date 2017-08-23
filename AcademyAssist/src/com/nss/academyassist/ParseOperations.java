package com.nss.academyassist;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Application;
import android.util.Log;

import com.nss.academyassist.MainActivity;

public class ParseOperations extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Initialize Crash Reporting.
	    ParseCrashReporting.enable(this);

	    // Enable Local Datastore.
	    Parse.enableLocalDatastore(this);

	    // Add your initialization code here
	    //Parse.initialize(this);
	    Parse.initialize(this, "EQPgJlycgSGMJSjCJvJoF9M0UqoLlcPEg61Sl7Zl", "jxn9vq22MiXIE0UaJ9wRjWnFsa5qRrhzjqQxwVbD");
	    
		ParseUser.enableAutomaticUser();
		
		ParseACL defaultACL = new ParseACL();
	    // Optionally enable public read access.
	    defaultACL.setPublicReadAccess(true);
	    ParseACL.setDefaultACL(defaultACL, true);
	    
	    answer.add("This is a default answer. Something went wrong. Report it please. :)");
	}
	
	public static boolean exists;
	
	public void checkUID(String UID) {
		
		exists = false; //initialize to false

		//final String tag = "CheckMe";

	    ParseQuery<ParseObject> query = ParseQuery.getQuery("person");
		query.whereEqualTo("UID", UID);
	
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> docList, ParseException e) {
		        if (e == null) {
		        	if(docList.size() != 0)
			        {
		        		//Log.v(tag, "I am here now.. "+docList.size()+"");
		        		exists = true; //Since inner class cannot return value, manipulate using class boolean value	
			        }
		        }
		    }
		});
		
		//return exists;//pref.getBoolean("flag", false);
	}
	
	static ArrayList<String> answer = new ArrayList<String>();
	
	public void getDocumentList(String className, String entity, final String tag) {
		answer.set(0, "Sorry, didn't catch that. Ask again.");
		
		Log.d("inside class is ", className);
		Log.d("inside entity is ", entity);
		Log.d("inside tag is ", tag);
		
	    ParseQuery<ParseObject> query2 = ParseQuery.getQuery(className);
		query2.whereExists(tag).whereEqualTo("fullName", entity);
		//query2.;
		
		Log.d("After query execution", "After query execution");
		
		query2.findInBackground(new FindCallback<ParseObject>() {

			@Override
		    public void done(List<ParseObject> docList, ParseException e) {

				Log.d("inside void run function", "inside void run function");
		        if (e == null) {
					Log.d("inside if statement", "inside if statement");
		        	if(docList.size() == 0)
			        {
		        		Log.d("I am here", "I am here now.. "+docList.size()+"");
		        		//answer.set(0, "Woah there! Something is fishy with what you're asking. I'll have to look into it.");
		        		answer.set(0, "No documents retrieved.");
			        }
		        	else
		        		if (docList.size() == 1)
		        		{
		        			answer.set(0, docList.get(0).getString(tag));
				        }
		        		else
		        		{
		        			answer.set(0, docList.get(0).getString(tag));
		        			for(int i=1; i<docList.size(); i++)
		        			{
		        				answer.add(docList.get(i).getString(tag));
		        			}
		        		}		        	
		        }
		        else
		        {
		        	Log.d("Error e quals null", e.toString());
		        }
		    }
		});
		
		Log.d("After findInBackground", "After findInBackground");
		
	}
	
	public void addPerson(String UID, String fullName, String branch, String batch, String degree, String number, String email, String about, String studentBody, String designation){
		
		final ParseObject user = new ParseObject("person");
		
		user.put("UID", UID);
		user.put("fullName", fullName);
		user.put("branch", branch);
		user.put("batch", batch);
		user.put("degree", degree);
		user.put("number", number);
		user.put("email", email);
		user.put("about", about);
		
		if(!studentBody.equals(""))
		{
			user.put("studentBody", studentBody);
			user.put("designation", designation);
		}
		
		user.saveEventually();
	    
	    //Toast.makeText(getApplicationContext(), "Added to db?", Toast.LENGTH_SHORT).show();
	}
	

    boolean entityFound = false;
    public String nameER = "";
    public String firstString, middleString, lastString;
    public int firstIndex, middleIndex, lastIndex;

    public int decider = 0;
    String[] words;
    public String fl1="";
    public String fl2="";
    
	public void forNER(String className, final String NER) {
		com.nss.academyassist.MainActivity.entityIsFound = false;
		com.nss.academyassist.MainActivity.namedEntityClass = "";
		
        //Log.d("Parse App NER is", "" + NER);
        nameER = NER;
        words = nameER.split("\\s+");
        //Log.d("Array of words is", Arrays.toString(words));
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
        //Log.d("Query is: ",""+ query);
        query.orderByAscending("fullName");
        //query.whereEqualTo("fullName", NER); //Use this when you want to search
        if(words.length>1|(words.length==1 & (query.getClassName().equals("utility") | query.getClassName().equals("room")))){
    //	if(words.length>1|(words.length==1 & (query.getClassName().equals("utility")))){
    	        query.findInBackground(new FindCallback<ParseObject>() {
	            @Override
	        	public void done(List<ParseObject> objectList, ParseException e) {
	
	                if (e == null) {
	                    firstIndex = 0;
	                    lastIndex = objectList.size() - 1;
	                    //Log.d("lastIndex: ", "" + lastIndex);
	                    middleIndex = (firstIndex + lastIndex) / 2;
	                    //Log.d("middleIndex: ", "" + middleIndex);
	                    firstString = objectList.get(firstIndex).getString("fullName");//.toLowerCase();
	                    lastString = objectList.get(lastIndex).getString("fullName");//.toLowerCase();
	                    middleString = objectList.get(middleIndex).getString("fullName");//.toLowerCase();
	                //    firstString.toLowerCase();
	                  //  middleString.toLowerCase();
	                   // lastString.toLowerCase();
	                    Log.d("firstName0", firstString);
	                    Log.d("midName0", middleString);
	                    Log.d("lastName0", lastString);
	                    
	                    while (firstIndex <= lastIndex) {
	                       // Log.d("mid str in while is", middleString);
	                        //Log.d("NER str in while is", nameER);
	                        fl1 = middleString.substring(0, 1);
	                        fl2 = nameER.substring(0, 1);
	                        //Log.d("mid str f1", fl1);
	                        //Log.d("ner fl2", fl2);
	
	                        decider = middleString.compareToIgnoreCase(nameER);
	                        //     Log.d("mid str f1",fl1);
	                        //   Log.d("ner fl2", fl2);
	
	                        Log.d("Decider is", "" + decider);
	                        if (decider < 0) {
	                            firstIndex = middleIndex + 1;
	                            firstString = objectList.get(firstIndex).getString("fullName");
	                            Log.d("firstName1: ", firstString);
	                            Log.d("midName1: ", middleString);
	                            Log.d("lastName1: ", lastString);
	                        } else if (middleString.compareToIgnoreCase(nameER) == 0) {
	                            Log.d("String status: ", "" + middleString + " Found at position " + (middleIndex + 1));
	
	                            //            Log.d("firstName2: ", ""+firstString);
	                            //          Log.d("midName2: ", ""+middleString);
	                            Log.d("lastName2: ", "" + lastString);
	                            
	                            com.nss.academyassist.MainActivity.entityIsFound = true;
	                            
	                            com.nss.academyassist.MainActivity.namedEntityClass = query.getClassName();
	                            
	                            Log.d("Value of entity ki class found", "" + com.nss.academyassist.MainActivity.namedEntity);
	                            Log.d("Value of entity found", "" + com.nss.academyassist.MainActivity.entityIsFound+"");
	                            Log.d("Class name is",query.getClassName());
	                             //objectList.getClass().toString()
	                            //return true;
	                            break;
	                        }
	                        else {
		                        	if(middleIndex > 0)
		                        	{
		                        		lastIndex = middleIndex - 1;
			                            lastString = objectList.get(lastIndex).getString("fullName");
			                            Log.d("firstName3: ", "" + firstString);
			                            Log.d("midName3: ", "" + middleString);
			                            Log.d("lastName3: ", "" + lastString);
			                        	
		                        	}
		                        	else
		                        	{
		                        		Log.d("String status: ", "Not found");
		    	                        Log.d("firstName5: ", "" + firstString);
		    	                        Log.d("midName5: ", "" + middleString);
		    	                        Log.d("lastName5: ", "" + lastString);
		    	                        break;
		                        	}
	                        	}
	                        middleIndex = (firstIndex + lastIndex) / 2;
	                        middleString = objectList.get(middleIndex).getString("fullName");
	                        Log.d("firstName4: ", "" + firstString);
	                        Log.d("midName4: ", "" + middleString);
	                        Log.d("lastName4: ", "" + lastString);
	
	                    }
	                    Log.d("Value outside While", "" + com.nss.academyassist.MainActivity.entityIsFound+"");
	                    if (firstIndex > lastIndex) {
	                        Log.d("String status: ", "Not found");
	                        Log.d("firstName5: ", "" + firstString);
	                        Log.d("midName5: ", "" + middleString);
	                        Log.d("lastName5: ", "" + lastString);
	                    }
	
	                } else {
	                    Log.d("name", "Error: " + e.getMessage());
	                }
	            }
	        });
        }
        else if(words.length == 1) {
            String word = words[0];
            Log.d("WORD is",word);
            query.whereContains("fullName",word);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        //    query.whereExists(words);
                        for(int i=0;i<parseObjects.size();i++)
                        Log.d("Retrieved", "" + parseObjects.get(i).getString("fullName"));

                    } else
                        Log.d("name", "Error: " + e.getMessage());
                }
            });
            //Log.d("The named entity is:",query.getClassName());
        }
        //   else  & (query.getClassName().equals("person")|query.getClassName().equals("room"))
    }
	
	
	static ArrayList<String> loadNames;// = new ArrayList<String>();
	static ArrayList<String> loadDomains;// = new ArrayList<String>();
	static ArrayList<String> loadObjectIDs;// = new ArrayList<String>();
	
	void loadInAdminPanel() {
		
		 loadNames = new ArrayList<String>();
		 loadDomains = new ArrayList<String>();
		 loadObjectIDs = new ArrayList<String>();
		 
		ParseQuery<ParseObject> query3 = ParseQuery.getQuery("person");
		
		query3.findInBackground(new FindCallback<ParseObject>() {

			@Override
		    public void done(List<ParseObject> docList, ParseException e) {
				
		        if (e == null) {

		        	for(int i=0; i<docList.size(); i++)
        			{
		        		loadDomains.add("person");
        				loadNames.add(docList.get(i).getString("fullName"));
        				loadObjectIDs.add(docList.get(i).getObjectId());
        			}	
		        	
		        }
		        else
		        {
		        	Log.d("Error e quals null", e.toString());
		        }
		    }
		});
		
		ParseQuery<ParseObject> query4 = ParseQuery.getQuery("room");
		
		query4.findInBackground(new FindCallback<ParseObject>() {

			@Override
		    public void done(List<ParseObject> docList, ParseException e) {
				
		        if (e == null) {

		        	for(int i=0; i<docList.size(); i++)
        			{
		        		loadDomains.add("room");
        				loadNames.add(docList.get(i).getString("fullName"));
        				loadObjectIDs.add(docList.get(i).getObjectId());
        			}	
		        	
		        }
		        else
		        {
		        	Log.d("Error e quals null", e.toString());
		        }
		    }
		});
		
		ParseQuery<ParseObject> query5 = ParseQuery.getQuery("utility");
		
		query5.findInBackground(new FindCallback<ParseObject>() {

			@Override
		    public void done(List<ParseObject> docList, ParseException e) {
				
		        if (e == null) {

		        	for(int i=0; i<docList.size(); i++)
        			{
		        		loadDomains.add("utility");
        				loadNames.add(docList.get(i).getString("fullName"));
        				loadObjectIDs.add(docList.get(i).getObjectId());
        			}	
		        	
		        }
		        else
		        {
		        	Log.d("Error e quals null", e.toString());
		        }
		    }
		});
		
	}
	
	void deleteObject(String ID, String className) {
		
		ParseQuery<ParseObject> query6 = ParseQuery.getQuery(className);
		 
		// Retrieve the object by id
		query6.getInBackground(ID, new GetCallback<ParseObject>() {
		  public void done(ParseObject toDelete, ParseException e) {
		    if (e == null) {
		    	toDelete.deleteInBackground();
		    }
		  }
		});
	}
	
	static ArrayList<String> tags;
	static ArrayList<String> values;
	
	void getTags(String ID, String className) {
		
		tags = new ArrayList<String>();
		values = new ArrayList<String>();
		
		ParseQuery<ParseObject> query7 = ParseQuery.getQuery(className);
		 
		// Retrieve the object by id
		query7.getInBackground(ID, new GetCallback<ParseObject>() {
		  public void done(ParseObject tagsObject, ParseException e) {
		    if (e == null) {
		    	tags.addAll(tagsObject.keySet());
		    	for(int i=0; i<tags.size(); i++)
				{
			    	values.add(tagsObject.getString(tags.get(i)));
				}
		    	//toDelete.deleteInBackground();
		    }
		  }
		});
		
	}

	public void updateEntry(String objectID, String className,
			final String tagNameValue, final String tagEntryValue) {
		// TODO Auto-generated method stub
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
		 
		// Retrieve the object by id
		query.getInBackground(objectID, new GetCallback<ParseObject>() {
		  public void done(ParseObject gameScore, ParseException e) {
		    if (e == null) {
		      // Now let's update it with some new data. In this case, only cheatMode and score
		      // will get sent to the Parse Cloud. playerName hasn't changed.
		      gameScore.put(tagNameValue, tagEntryValue);
		      gameScore.saveInBackground();
		    }
		  }
		});
	}
	
}
