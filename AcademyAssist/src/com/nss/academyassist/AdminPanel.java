package com.nss.academyassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nss.academyassist.ParseOperations;

public class AdminPanel extends Activity implements OnClickListener {

	ParseOperations p;
	ProgressDialog loading;
	EditText enteredID, enteredDomain;
	String enteredIDValue, enteredDomainValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_panel);
		
		TableLayout tl = (TableLayout) findViewById(R.id.main_table);
		
		TableRow tr_head = new TableRow(this);
		tr_head.setId(10);
		tr_head.setLayoutParams(new LayoutParams());
		//tr_head.setPadding(5, 5, 5, 5);
		
		TextView name = new TextView(this);
		name.setId(20);
		name.setText("Name\n");
		//textview.getTextColors(R.color.)
		tr_head.addView(name);
		//name.addView(textview);

		TextView objectID = new TextView(this);
		objectID.setId(21);
		objectID.setText("Object ID\n");
		//textview.getTextColors(R.color.)
		tr_head.addView(objectID);
		
		TextView domain = new TextView(this);
		domain.setId(21);
		domain.setText("Domain\n");
		//textview.getTextColors(R.color.)
		tr_head.addView(domain);
		//name.addView(textview);
		
		tl.addView(tr_head, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		  
      	p = new ParseOperations();
      	p.loadInAdminPanel();
		
      	loading = new ProgressDialog(AdminPanel.this);
    	//loading.setCancelable(true);
    	loading.setMessage("Fetching values");
    	//loading.show();
		
    	//loading.setTitle("Getting answer...");
		loading.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		
	    loading.show();
      	
      	new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	loadValues();
            	loading.dismiss();
            }
        }, 6000);

      	Button add = (Button) findViewById(R.id.button1);
      	Button update = (Button) findViewById(R.id.button2);
      	Button delete = (Button) findViewById(R.id.button3);
      	Button refresh = (Button) findViewById(R.id.button4);

      	//add.setOnClickListener(this);
      	update.setOnClickListener(this);
      	delete.setOnClickListener(this);
      	refresh.setOnClickListener(this);
      	
      	enteredID = (EditText) findViewById(R.id.editText1); 
      	enteredDomain = (EditText) findViewById(R.id.editText2); 
	}
		
	void loadValues() {
		
		TableLayout tl = (TableLayout) findViewById(R.id.main_table);
		

		//tr_head.setPadding(5, 5, 5, 5);
		
		for(int i=0; i<com.nss.academyassist.ParseOperations.loadObjectIDs.size(); i++)
		{

			TableRow tr_head = new TableRow(this);
//			tr_head.setId(setIDValue++);
			tr_head.setLayoutParams(new LayoutParams());
			
			TextView name = new TextView(this);
			name.setText(com.nss.academyassist.ParseOperations.loadNames.get(i));
			//textview.getTextColors(R.color.)
			tr_head.addView(name);
			//name.addView(textview);

			TextView objectID = new TextView(this);
			objectID.isClickable();
			objectID.setTextIsSelectable(true);
			objectID.setText(com.nss.academyassist.ParseOperations.loadObjectIDs.get(i));
			//textview.getTextColors(R.color.)
			tr_head.addView(objectID);
			
			TextView domain = new TextView(this);
			domain.setText(com.nss.academyassist.ParseOperations.loadDomains.get(i));
			//textview.getTextColors(R.color.)
			tr_head.addView(domain);
			//name.addView(textview);
			
			tl.addView(tr_head, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
		}		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch(v.getId())
		{
		    case R.id.button1: //add
				break;
				
		    case R.id.button2: //update
		    	enteredIDValue = enteredID.getText().toString();
		    	enteredDomainValue = enteredDomain.getText().toString();
		    	if(enteredIDValue.length() > 0 && enteredDomainValue.length() > 0)
		    	{	
		    		Intent i = new Intent(AdminPanel.this, UpdateEntry.class);
//			    	finish();
		    		i.putExtra("ID", enteredIDValue);
		    		i.putExtra("Domain", enteredDomainValue);
			    	startActivity(i);
	 
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplicationContext(), "Please enter both the Object ID & it's corresponding domain", Toast.LENGTH_SHORT).show();
		    	}
				break;
			
		    case R.id.button3: //delete
		    	enteredIDValue = enteredID.getText().toString();
		    	enteredDomainValue = enteredDomain.getText().toString();
		    	if(enteredIDValue.length() > 0 && enteredDomainValue.length() > 0)
		    	{

		    		loading.setMessage("Deleting...");
		    		loading.show();

		    		p.deleteObject(enteredIDValue, enteredDomainValue);
		    		
		          	new Handler().postDelayed(new Runnable() {
		                @Override
		                public void run() {
		                	Toast.makeText(getApplicationContext(), "Operation successful. Refresh list.", Toast.LENGTH_SHORT).show();
		                	loading.dismiss();
		                }
		            }, 5000);
		    		
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplicationContext(), "Please enter both the Object ID & it's corresponding domain", Toast.LENGTH_SHORT).show();
			    }
				break;
			
		    case R.id.button4: //refresh
		    	/*
		    	loading.show();

		      	p.loadInAdminPanel();
		      	
		       	new Handler().postDelayed(new Runnable() {
		             @Override
		             public void run() {
		             	loadValues();
		             	loading.dismiss();
		             }
		         }, 6000);
		       	*/

		    	Intent intent = getIntent();
		    	finish();
		    	startActivity(intent);
		    	
				break;
		}
	}
}
