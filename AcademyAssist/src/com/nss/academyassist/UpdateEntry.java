package com.nss.academyassist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
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

public class UpdateEntry extends Activity implements OnClickListener {

	ParseOperations p;
	ProgressDialog loading;
	String objectID, className;
	//TableLayout t2;
	EditText tagName, tagEntry;
	String tagNameValue, tagEntryValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_entry);
		
		Intent i = getIntent();
		objectID = i.getStringExtra("ID");
		className = i.getStringExtra("Domain");
		
		TextView t1 = (TextView) findViewById(R.id.textView1);
		TextView t2 = (TextView) findViewById(R.id.textView2);
		
		t1.setText("Object id: " +  objectID);
		t2.setText("Class name: " +  className);

		tagName = (EditText) findViewById(R.id.editText3);
		tagEntry = (EditText) findViewById(R.id.editText4);
		
		TableLayout t21 = (TableLayout) findViewById(R.id.main_table);
		
		TableRow tr_head = new TableRow(this);
		tr_head.setId(10);
		tr_head.setLayoutParams(new LayoutParams());
		//tr_head.setPadding(5, 5, 5, 5);
		
		TextView name = new TextView(this);
		name.setId(20);
		name.setText("Tag\n");
		//textview.getTextColors(R.color.)
		tr_head.addView(name);
		//name.addView(textview);

		TextView ID = new TextView(this);
		ID.setId(21);
		ID.setText("Value\n");
		//textview.getTextColors(R.color.)
		tr_head.addView(ID);
		
		t21.addView(tr_head, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		  
      	p = new ParseOperations();
      	p.loadInAdminPanel();
		
      	loading = new ProgressDialog(UpdateEntry.this);
    	//loading.setCancelable(true);
    	loading.setMessage("Getting tags for the object...");
    	//loading.show();
		
    	//loading.setTitle("Getting answer...");
		loading.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		
	    loading.show();

    	p.getTags(objectID, className);
    	
      	new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	loading.dismiss();
            	
            	//TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        		

        		//tr_head.setPadding(5, 5, 5, 5);
        		
        		displayTags();	
            	
            }
        }, 6000);

      	Button updateEntry = (Button) findViewById(R.id.button1);
      	updateEntry.setOnClickListener(this);
      	
	}
	
	void displayTags() {
		
		for(int i=0; i<com.nss.academyassist.ParseOperations.tags.size(); i++)
		{
			TableLayout t3 = (TableLayout) findViewById(R.id.main_table);
			
			TableRow tr_head = new TableRow(this);
//			tr_head.setId(setIDValue++);
			tr_head.setLayoutParams(new LayoutParams());
			
			TextView tag = new TextView(this);
			tag.isClickable();
			tag.setTextIsSelectable(true);
			tag.setText(com.nss.academyassist.ParseOperations.tags.get(i));
			//textview.getTextColors(R.color.)
			tr_head.addView(tag);
			//name.addView(textview);

			TextView value = new TextView(this);
			value.setText(com.nss.academyassist.ParseOperations.values.get(i));
			//textview.getTextColors(R.color.)
			tr_head.addView(value);
			
			/*
			TextView domain = new TextView(this);
			domain.setText(com.nss.academyassist.ParseOperations.loadDomains.get(i));
			//textview.getTextColors(R.color.)
			tr_head.addView(domain);
			//name.addView(textview);
			*/
			
			t3.addView(tr_head, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}	
    	
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		tagNameValue = tagName.getText().toString();
		tagEntryValue = tagEntry.getText().toString();
		
		if(tagNameValue.trim().length() > 0 && tagEntryValue.trim().length() > 0)
		{
			loading.setMessage("Updating value...");
			loading.show();
			
			new ParseOperations().updateEntry(objectID, className, tagNameValue, tagEntryValue);
			
			new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	loading.dismiss();
	            	Toast.makeText(getApplicationContext(), "Update successful. Go back and refresh.", Toast.LENGTH_SHORT).show();
	        	}
	        }, 5000);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Please enter both tag and updated value for it.", Toast.LENGTH_SHORT).show();
		}
	}
		
}
