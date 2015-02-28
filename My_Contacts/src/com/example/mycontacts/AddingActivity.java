package com.example.mycontacts;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * The Class for adding or editing contacts from database.
 */
public class AddingActivity extends Activity {
	
	/**
	 * On create.
	 *
	 * Creates a activity and connect variables with fields.
	 * 
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);

		etName = (EditText) findViewById(R.id.etNameAdd);
		etName.setText("");
		etLastname = (EditText) findViewById(R.id.etLastnameAdd);
		etLastname.setText("");
		etAdress = (EditText) findViewById(R.id.etAdressAdd);
		etAdress.setText("");

		ivImage = (ImageView) findViewById(R.id.imgAddView);
		ivImage.setImageResource(R.drawable.person);

		dpBirthDate = (DatePicker) findViewById(R.id.AddDate);
		dpBirthDate.setMaxDate(Calendar.getInstance().getTime().getTime());

		rgGender = (RadioGroup) findViewById(R.id.rdGrAddGender);

		btnCancel = (Button) findViewById(R.id.cancelAddBtn);
		btnConfirm = (Button) findViewById(R.id.confirmAddBtn);
		
		etLastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (!hasFocus) {
		            if(!etLastname.getText().toString().equals("")) {
		            	etName.setHint("Поле желательно для заполнения");
		            }
		        }
		    }
		});
		
		etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (!hasFocus) {
		            if(!etName.getText().toString().equals("")) {
		            	etLastname.setHint("Поле желательно для заполнения");
		            }
		        }
		    }
		});
		
		
	/**
	 * Sets data if Activity called for editing. 	 
	 */
		if (getIntent().hasExtra("ContactData")) {
			ContactData cd = (ContactData) getIntent().getSerializableExtra(
					"ContactData");
			Date d = new Date(cd.getDate());
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(d);

			dpBirthDate.updateDate(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
			etName.setText(cd.getName());
			etLastname.setText(cd.getLastname());
			etAdress.setText(cd.getAdress());
			if (cd.getGender().startsWith("М")) {
				rgGender.check(R.id.rdAddMale);
			} else {
				rgGender.check(R.id.rdAddFemale);
			}
			
			try {
				Bitmap photo = MediaStore.Images.Media
						.getBitmap(this.getContentResolver(),Uri.parse(cd.getImage()));
				Bitmap photoScaled = Bitmap.createScaledBitmap(photo, 70, 70, true);
				ivImage.setImageBitmap(photoScaled);
				contactImageUri = Uri.parse(cd.getImage());
			} catch (Exception e) {
				contactImageUri = Uri
						.parse("android.resource://com.example.mycontacts/drawable/person");
				ivImage.setImageURI(contactImageUri);
			}
			contactDataId = cd.getId();
		} else {
			contactDataId = -1;

		}

	}
	
	/**
	 * Starts activity to pick image from gallery.
	 */
	public void btnImgAddClick(View view) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);
	}

	/**
	 * Returns to activity to fill all data.
	 */
	public void doNegativeClick() {
		Toast.makeText(this, "Заполните все нужные поля", Toast.LENGTH_SHORT)
				.show();
	}
	
	/**
	 * Continue to adding contact with not all data.
	 */
	public void doPositiveClick() {
		//Shows message that contact added to database.  
		Toast.makeText(this, "Контакт сохранен", Toast.LENGTH_SHORT).show();
		addRecord();
	}

	/**
	 * Accepting editing or adding contact.
	 */
	public final void onBtnAcceptClick(View view) {
		aName = etName.getText().toString();
		aLastname = etLastname.getText().toString();
		aAdress = etAdress.getText().toString();
		
		/**
		 * Checks if no image picked.
		 */
		try{
			aImage = contactImageUri.toString(); 
		} catch (NullPointerException e) {
			contactImageUri = Uri.parse("android.resource://com.example.mycontacts/drawable/person");
			aImage = contactImageUri.toString();
		}
		
		getGender();
		//Set gender.

		GregorianCalendar cal = new GregorianCalendar(dpBirthDate.getYear(),
				dpBirthDate.getMonth(), dpBirthDate.getDayOfMonth());
		aDate = cal.getTime().getTime();

		if (aName.equalsIgnoreCase("") || aLastname.equalsIgnoreCase("")
				|| aAdress.equalsIgnoreCase("")) {
		if (aName.equalsIgnoreCase("") && aLastname.equalsIgnoreCase("")) {
			Toast.makeText(this, "Не введены личные данные", Toast.LENGTH_SHORT).show();

		} else {
			showDialog();
		}
		} else {
			addRecord();
		}
	}

	/**
	 * On btn cancel click.
	 *
	 * @param view the view
	 */
	public void onBtnCancelClick(final View view) {
		setResult(RESULT_CANCELED, new Intent());
		finish();
	}

	
	/**
	 * Shows alert dialog that not all data filled.
	 */
	void showDialog() {
		DialogFragment newFragment = MyAlertDialogFragment
				.newInstance(R.string.not_enoughth_information);
		newFragment.show(getFragmentManager(), "dialog");
	}


	/**
	 * Returns Uri of picked image for contact.
	 */
	protected void onActivityResult( int requestCode,
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				contactImageUri = data.getData();
			}
			break;
		default:
			break;
		}
		try{
		Bitmap photo = Media
				.getBitmap(getContentResolver(), contactImageUri);
		Bitmap photoScaled = Bitmap.createScaledBitmap(photo, 70, 70, false);
		ivImage.setImageBitmap(photoScaled);
		} catch (Exception e) {
			Toast.makeText(this, "Ошибка чтения изображения", Toast.LENGTH_LONG)
			.show();
		} catch(OutOfMemoryError e) {
			Toast.makeText(this, "Изображение слишком большое", Toast.LENGTH_LONG)
			.show();
		}
		
	}
	
	/**
	 * Gets the picked gender from Radio Group.
	 *
	 * @return the gender
	 */
	private String getGender() {
		switch (rgGender.getCheckedRadioButtonId()) {
		case R.id.rdAddMale:
			aGender = "Мужчина";
			break;
		case R.id.rdAddFemale:
			aGender = "Женщина";
			break;
		default:
			break;
		}
		return aGender;
	}
	
	/**
	 * Adds the record to database and finishing the activity.
	 */
	private void addRecord() {
		ContactData cd = new ContactData(contactDataId, aName, aLastname,
				aDate, aAdress, aGender, aImage);
		Intent intent = getIntent();
		intent.putExtra("ContactData", cd);
		setResult(RESULT_OK, intent);
		finish();
		onDestroy();
	}
	
	/**
	 * List of parameters for contact.
	 * 
	 * @param aAdress postal adress from contact.
	 * @param aGender contact gender.
	 * @param aImage path to contact image.
	 * @param aLastname contact Lastname.
	 * @param aName contact name.
	 * @param aDate date of contact birthday.
	 * @param contactDataId contact id from database.
	 * @param contactImageUri URI path to contact image.
	 */
	String aAdress;
	String aGender;
	String aImage;
	String aLastname;
	String aName;
	long aDate;
	long contactDataId;
	Uri contactImageUri;

	/** 
	 * Declaration of variables for graphical elements.  
	 */
	Button btnCancel;
	Button btnConfirm;
	DatePicker dpBirthDate;
	EditText etAdress;
	EditText etImage;
	EditText etLastname;
	EditText etName;
	ImageView ivImage;
	RadioGroup rgGender;

	/**
	 * The Class for Alert Dialog.
	 */
	public static class MyAlertDialogFragment extends DialogFragment {

		/**
		 * New instance.
		 *
		 * @param title the title
		 * @return the my alert dialog fragment
		 */
		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		/** 
		 * Creates Alert Dialog when not all data filled.
		 */
		@Override
		public final Dialog onCreateDialog(final Bundle savedInstanceState) {
			int title = getArguments().getInt("title");

			return new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle(title)
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int whichButton) {
									((AddingActivity) getActivity())
											.doPositiveClick();

								}
							})
					.setNegativeButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int whichButton) {
									((AddingActivity) getActivity())
											.doNegativeClick();
								}
							}).create();
		}
	}
}
