package com.nss.academyassist;

import com.nss.academyassist.ParseOperations;
import com.parse.ParseAnalytics;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends ActionBarActivity {

	EditText etUID, etFullName, etBranch, etBatch, etDegree, etNumber, etEmail, etAbout, etStudentBody, etDesignation;
	String UID, fullName, branch, batch, degree, number, email, about, studentBody, designation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		//final String tag = "CheckMe";
		//Log.v(tag, "register...");
		
		//back button on action bar
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		//Now add the parent activity metadata in AndroidManifest

	    ParseAnalytics.trackAppOpenedInBackground(getIntent());

	    TextView secretPanel = (TextView) findViewById(R.id.adminPanel);	    
	    secretPanel.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Actvitating Secret Panel...", Toast.LENGTH_SHORT).show();
				
				// Create Object of Dialog class
				final Dialog login = new Dialog(Register.this);
				// Set GUI of login screen
				login.setContentView(R.layout.login_dialog);
				login.setTitle("Admin Panel Login");
				//Dialog box styling
				login.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
				
				// Init button of login GUI
				Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
				Button btnCancel = (Button) login.findViewById(R.id.btnCancel);
				final EditText txtUsername = (EditText)login.findViewById(R.id.txtUsername);
				final EditText txtPassword = (EditText)login.findViewById(R.id.txtPassword);

				// Attached listener for login GUI button
				btnLogin.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(txtUsername.getText().toString().trim().length() > 0 && txtPassword.getText().toString().trim().length() > 0)
						{
						// Validate Your login credential here than display message
							if(txtUsername.getText().toString().equals("a") && txtPassword.getText().toString().equals("a"))
							{
								Toast.makeText(getApplicationContext(), "Login Sucessful! Welcome admin.", Toast.LENGTH_SHORT).show();
								// Redirect to dashboard / home screen.
								Intent loginPanel = new Intent(Register.this, AdminPanel.class);
								startActivity(loginPanel);
								
								finish();
								login.dismiss();
							}
							else
							{
								Toast.makeText(getApplicationContext(), "Wrong credentials.", Toast.LENGTH_SHORT).show();

								// Redirect to dashboard / home screen.
								login.dismiss();
							}
						}
						else
						{
							Toast.makeText(getApplicationContext(),	"Enter both username and password.", Toast.LENGTH_SHORT).show();
						}
					}
				});
				btnCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						login.dismiss();
					}
				});

				// Make dialog box visible.
				login.show();

				return true;
			}
	    	
	    });
	    
	    etUID = (EditText) findViewById(R.id.editText1);
	    etFullName = (EditText) findViewById(R.id.editText2);
	    etBranch = (EditText) findViewById(R.id.editText3);
	    etBatch = (EditText) findViewById(R.id.editText4);
	    etDegree = (EditText) findViewById(R.id.editText5);
	    etNumber = (EditText) findViewById(R.id.editText6);
	    etEmail = (EditText) findViewById(R.id.editText7);
	    etAbout = (EditText) findViewById(R.id.editText8);
	    etStudentBody = (EditText) findViewById(R.id.editText9);
	    etDesignation = (EditText) findViewById(R.id.editText10);
	    
	    Button register = (Button) findViewById(R.id.register);
	    register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				UID = etUID.getText().toString();
				fullName = etFullName.getText().toString();
				branch = etBranch.getText().toString();
				batch = etBatch.getText().toString();
				degree = etDegree.getText().toString();
				number = etNumber.getText().toString();
				email = etEmail.getText().toString();
				about = etAbout.getText().toString();
				studentBody = etStudentBody.getText().toString();
				designation = etDesignation.getText().toString();
				
				//Individually check each field	so that you can tell user which field is missing and add focus and style to the missing field
				if(individualValidate())
				{
					//All fine, so now do the registration process	
					final ParseOperations p = new ParseOperations();

					//Log.v(tag, "Before delay...");
					p.checkUID(UID);
					
					//Display loading dialog box for 4 secondds
					final Dialog loading = new Dialog(Register.this);
					loading.setTitle("Loading...");
					loading.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
					
				    loading.show();
					
					//This handler is required because querying the database takes around 3 seconds
					new Handler().postDelayed(new Runnable() {
	                      @Override
	                      public void run() {
	                    	  
	                    	  loading.dismiss();
	                    	  
	                    	  if(com.nss.academyassist.ParseOperations.exists)
		      					{
		      						//true then UID already exists
		      						Toast.makeText(getApplicationContext(), "UID already exists.", Toast.LENGTH_SHORT).show();				
		      					}
		      					else
		      					{
		      						p.addPerson(UID, fullName, branch, batch, degree, number, email, about, studentBody, designation);
		      						
		      						Toast.makeText(getApplicationContext(), "Registration sucessful.", Toast.LENGTH_SHORT).show();
		      						finish();	
		      					}
	                    	  
	                      }
	                  }, 4000);

					//Log.v(tag, "After delay...");
						
				}
				else
				{
					//Don't do anything as this case is handled individually in the individualValidate function
				}
				
			}
		});
	}
	
	public boolean individualValidate() {
		if(UID.trim().length() > 0)
		{
			if(fullName.trim().length() > 0)
			{
				if(branch.trim().length() > 0)
				{
					if(batch.trim().length() > 0)
					{
						if(degree.trim().length() > 0)
						{
							if(number.trim().length() > 0)
							{
								if(email.trim().length() > 0)
								{
									if(about.trim().length() > 0)
									{
										//Either add both Student Body and Designation or don't add any of the two.
										if((studentBody.trim().length() == 0 && designation.trim().length() == 0) || (studentBody.trim().length() != 0 && designation.trim().length() != 0))
										{
											//Toast.makeText(getApplicationContext(), "All valid", Toast.LENGTH_SHORT).show();
											return true;
										}
										else
										{
											Toast.makeText(getApplicationContext(), "Either enter both Student Body and Designation details or don't enter any.", Toast.LENGTH_LONG).show();
											etStudentBody.requestFocus();
										}
									}
									else
									{
										Toast.makeText(getApplicationContext(), "Enter About", Toast.LENGTH_SHORT).show();
										etAbout.requestFocus();
									}
								}
								else
								{
									Toast.makeText(getApplicationContext(), "Enter Branch", Toast.LENGTH_SHORT).show();
									etBranch.requestFocus();
								}
							}
							else
							{
								Toast.makeText(getApplicationContext(), "Enter Number", Toast.LENGTH_SHORT).show();
								etNumber.requestFocus();
							}
						}
						else
						{
							Toast.makeText(getApplicationContext(), "Enter Degree", Toast.LENGTH_SHORT).show();
							etDegree.requestFocus();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Enter Batch", Toast.LENGTH_SHORT).show();
						etBatch.requestFocus();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Enter Branch", Toast.LENGTH_SHORT).show();
					etBranch.requestFocus();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Enter Full Name", Toast.LENGTH_SHORT).show();
				etFullName.requestFocus();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Enter Unique ID", Toast.LENGTH_SHORT).show();
			etUID.requestFocus();
		}
		
		return false;
	}
}
