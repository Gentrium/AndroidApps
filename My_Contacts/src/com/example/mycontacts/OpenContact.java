package com.example.mycontacts;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Open extended contact information activity.
 */
public class OpenContact extends Activity {

	
	/** 
	 * Declaration of variables for graphical elements.  
	 */
	ImageView contImage;
	TextView tvName;
	TextView tvLastname;
	TextView tvAdress;
	TextView tvGender;
	TextView tvDate;
	ContactData tempContData;
	
	/** The contact background. */
	ScrollView backgrndScrollView;

	/** Request code of editing contact. */
	static final int EDIT_CONTACT = 2;

	/** Declaration of database. */
	ContactsDatabase contactDB;

	/**
	 * Creates activity and connect to database.
	 */
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watch_contact);
		
		contactDB = new ContactsDatabase(this);
		contactDB.open();

		tvName = (TextView) findViewById(R.id.tvShowName);
		tvLastname = (TextView) findViewById(R.id.tvShowLastname);
		tvAdress = (TextView) findViewById(R.id.tvShowAdress);
		tvGender = (TextView) findViewById(R.id.tvShowGender);
		tvDate = (TextView) findViewById(R.id.tvShowDate);

		contImage = (ImageView) findViewById(R.id.showImage);

		backgrndScrollView = (ScrollView) findViewById(R.id.scroll_open_contact);
		getIntent().getExtras();
		ContactData cd = (ContactData) getIntent().getSerializableExtra(
				"ContactData");
		fillData(cd);
		tempContData = cd;
	}

	/**
	 * Creates options menu.  
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Switches between chosen action from options menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.delete:
			new AlertDialog.Builder(this)
		    .setTitle(R.string.deleting_selected_data_dialog)
		    .setIcon(getResources().getDrawable(R.drawable.alert_dialog_icon))
		    .setPositiveButton(R.string.alert_dialog_ok,
		    		new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	deleteRecord();
		        }
		     })
		    .setNegativeButton(R.string.alert_dialog_cancel,
		    		new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
			return true;
		case R.id.edit:
			editRecord();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Opens activity to edit contact.
	 */
	public void onBtnEditClick( View view) {
		editRecord();
	}

	/**
	 * Deletes contact from database and ends activity.
	
	 */
	public void onBtnDeleteClick(View view) {
		new AlertDialog.Builder(this)
	    .setTitle(R.string.deleting_selected_data_dialog)
	    .setIcon(getResources().getDrawable(R.drawable.alert_dialog_icon))
	    .setPositiveButton(R.string.alert_dialog_ok,
	    		new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	deleteRecord();
	        }
	     })
	    .setNegativeButton(R.string.alert_dialog_cancel,
	    		new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
		
	}

	

	/** 
	 * Re-fill activity when edited.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			ContactData cd = (ContactData) data.getExtras().getSerializable(
					"ContactData");
			contactDB.update(cd);
			fillData(cd);
			tempContData = cd;
		}
	}
	
	/**
	 * Closes database connection when activity destroyed. 
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		contactDB.close();
	}

	/**
	 * Delete record.
	 */
	private void deleteRecord() {
		getIntent().getExtras();
		ContactData cd = (ContactData) getIntent().getSerializableExtra(
				"ContactData");
		contactDB.delete(cd.getId());
		finish();
		onDestroy();

	}

	/**
	 * Edits the record.
	 */
	private void editRecord() {
		Intent intent = new Intent(this, AddingActivity.class);
		intent.putExtra("ContactData", tempContData);
		startActivityForResult(intent, EDIT_CONTACT);
	}

	/**
	 * Fill activity with data.
	 *
	 * @param cd Contact data
	 */
	private void fillData(ContactData cd) {
		/**
		 * Formating data to readable string format.
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",
				Locale.getDefault());

		tvDate.setText("Дата рождения \n" + dateFormat.format(cd.getDate()));
		tvName.setText("Имя \n" + cd.getName());
		tvLastname.setText("Фамилия \n" + cd.getLastname());
		tvAdress.setText("Адрес \n" + cd.getAdress());
		tvGender.setText("Пол \n" + cd.getGender());
		try{
			contImage.setImageURI(Uri.parse(cd.getImage()));
			} catch (Exception e){
				contImage.setImageResource(R.drawable.person);
			}
				
		/**
		 * Switches background color depends on gender. 
		 */
		if (cd.getGender().startsWith("М")) {
			backgrndScrollView.setBackgroundColor(getResources().getColor(
					R.color.SkyBlue));
		} else {
			backgrndScrollView.setBackgroundColor(getResources().getColor(
					R.color.DeepPink));
		}
	}

}