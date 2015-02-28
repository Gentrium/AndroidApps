package com.example.mycontacts;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class of main screen activity, with list of all contacts.
 * 
 *
 * @author      Suchkov Maxim
 * @version     $Revision: 1.00 $, $Date: 2014/05/28 $
 */

public class MainScreen extends FragmentActivity implements LoaderCallbacks<Cursor>{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		contactDB = new ContactsDatabase(this);
		contactDB.open();
		mContext = this;
		
		mainScreenLV = (ListView) findViewById(R.id.listView1);
	    contactAdapter = new ContactAdapter(this, null, 0);
		mainScreenLV.setAdapter(contactAdapter);

		registerForContextMenu(mainScreenLV);
		
		mainScreenLV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactData cd = contactDB.selectContact(id);
				Intent intent = new Intent(mContext, OpenContact.class);
				intent.putExtra("ContactData", cd);
				startActivity(intent);

			}
		});	
			getSupportLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Creates context menu.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXT_EDIT_ID, 0, R.string.edit_button);
		menu.add(0, CONTEXT_DELETE_ID, 0, R.string.delete_button);

	}
	/**
	 * Processes selected item of context menu.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case CONTEXT_EDIT_ID:
			Intent intent = new Intent(mContext, AddingActivity.class);
			ContactData cd = contactDB.selectContact(acmi.id);
			intent.putExtra("ContactData", cd);
			startActivityForResult(intent, EDIT_ACTIVITY);
			getSupportLoaderManager().getLoader(0).forceLoad();
			return true;
		case CONTEXT_DELETE_ID:
			dialogTrigger = acmi.id;
			showDialog();
			return true;
		default:
			break;

		}
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Creats options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Processes selected option menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add:
			Intent intent = new Intent(this, AddingActivity.class);
			startActivityForResult(intent, ADD_ACTIVITY);
			getSupportLoaderManager().getLoader(0).forceLoad();
			return true;
		case R.id.deleteAll:
			dialogTrigger = -1;
			showDialog();
			return true;
		case R.id.exit:
			onDestroy();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		return new MyCursorLoader(this, contactDB);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		contactAdapter.swapCursor(cursor);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	/**
	 * Starts activity to add new contact.
	 */
	void addBtnClick(View view) {
		Intent intent = new Intent(this, AddingActivity.class);
		startActivityForResult(intent, ADD_ACTIVITY);
	}
	
	
	/**
	 * Aborts deleting data. 
	 */
	void doNegativeClick() {
		Toast.makeText(this, "Действие отменено", Toast.LENGTH_SHORT)
				.show();
	}
	
	/**
	 * Continues deleting data.
	 */
	void doPositiveClick() {
		if (dialogTrigger == -1) {
			contactDB.deleteAll();
			Toast.makeText(this, "Все контакты удалены", Toast.LENGTH_SHORT).show();
			} else {
		contactDB.delete(dialogTrigger);
		Toast.makeText(this, "Контакт удален", Toast.LENGTH_SHORT).show();
			}
		getSupportLoaderManager().getLoader(0).forceLoad();
	}
	
	/**
	 * Processes result from editing or adding activities.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			ContactData cd = (ContactData) data.getExtras().getSerializable(
					"ContactData");
			if (requestCode == EDIT_ACTIVITY) {
				contactDB.update(cd);
			} else {
				contactDB.addContact(cd);
			}
			getSupportLoaderManager().getLoader(0).forceLoad();
		}
	}
	
	/**
	 * Opens database connection if closed by other activity
	 * and update list of contacts.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getSupportLoaderManager().getLoader(0).forceLoad();
	}
	
	/**
	 * Closes database connection when activity destroyed.
	 */
	@Override
	protected void onDestroy() {
		contactDB.close();
		getSupportLoaderManager().destroyLoader(0);
		super.onDestroy();
		
		 try {
	            trimCache(this);
	        } catch (Exception e) {
	            e.printStackTrace();
        }

    }
	/**
	 * Delete cache.
	 * @param context
	 */
    public static void trimCache(Context context) {
        try {
           File dir = context.getCacheDir();
           if (dir != null && dir.isDirectory()) {
              deleteDir(dir);
           }
        } catch (Exception e) {
        	e.printStackTrace();
        }
     }

     public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
           String[] children = dir.list();
           for (int i = 0; i < children.length; i++) {
              boolean success = deleteDir(new File(dir, children[i]));
              if (!success) {
                 return false;
              }
           }
        }
        return dir.delete();
     }
		
	/**
	 * Shows alert dialog.
	 */
	private void showDialog() {
		DialogFragment newFragment;
		if(dialogTrigger == -1) {
			newFragment = MyAlertDialogFragment
				.newInstance(R.string.deleting_all_data_dialog);}
		else {
			newFragment = MyAlertDialogFragment
					.newInstance(R.string.deleting_selected_data_dialog);
		}
		newFragment.show(getFragmentManager(), "dialog");
	}
	
	ContactsDatabase contactDB;
	ListView mainScreenLV;
	ContactAdapter contactAdapter;
	Context mContext;
	
	private long dialogTrigger;
	
	/** The  ADD_ACTIVITY. */
	static final int ADD_ACTIVITY = 0;
	
	/** The Constant EDIT_ACTIVITY. */
	static final int EDIT_ACTIVITY = 1;
	
	/** The Constant CONTEXT_EDIT_ID. */
	private static final int CONTEXT_EDIT_ID = 1;
	
	/** The Constant CONTEXT_DELETE_ID. */
	private static final int CONTEXT_DELETE_ID = 2;
	/**
	 * 
	 * Class to reorganize filling of contact list.
	 * 
	 * @author maks
	 *
	 */
	class ContactAdapter extends CursorAdapter{

		public ContactAdapter(Context context, Cursor c, int flags) {
				super(context, c, flags);
				mLayoutInflater = LayoutInflater.from(context);			
			}			
				private LayoutInflater mLayoutInflater;
			
				/**
				 * Creats list items.
				 */
			@Override
			public void bindView(View view, Context ctx, Cursor curs) {
				ImageView photoIV = (ImageView) view
						.findViewById(R.id.imgForLV);
				TextView tvName = (TextView) view
						.findViewById(R.id.nameTextViewForLV);
				LinearLayout background = (LinearLayout) view
						.findViewById(R.id.contact_mainScreen_layout);
				/**
				 * Sets Name and Lastname of contact in one textbox.
				 */
				if (curs.getString(2).equals("")) {
					tvName.setText(curs.getString(1));
				} else {
				tvName.setText(curs.getString(2)
						+ ' ' 
						+ curs.getString(1));
				}
				/** 
				 * Sets image and clears unused images
				 */
				try{
					  Bitmap photo = MediaStore.Images.Media
							  .getBitmap(getContentResolver(),
									  Uri.parse(curs.getString(6)));
					  Bitmap photoScaled = Bitmap
							  .createScaledBitmap(photo, 46, 46, true);
					  photoIV.setImageBitmap(photoScaled);
					  if (photo != photoScaled) {
						  photo.recycle();
					  }
				} catch (Exception e) {
					photoIV.setImageResource(R.drawable.person);
				}
				
				/**
				 * Set's background color of contact.				
				 */
				float a = (float) 0.9;
				if (curs.getString(5).startsWith("М")) {
					background.setBackgroundColor(getResources().getColor(
							R.color.SkyBlue));
					background.setAlpha(a);
				} else {
					background.setBackgroundColor(getResources().getColor(
							R.color.DeepPink));
					background.setAlpha(a);
				}
				
			}

			@Override
			public View newView(Context context,
					Cursor cursor, ViewGroup parent) {
				return  mLayoutInflater
						.inflate(R.layout.contacts_at_main, parent, false);
			}
	}
	
	/**
	 * Loads cursor in background to better performance.
	 */
	static class MyCursorLoader extends CursorLoader{
				public MyCursorLoader(Context context, ContactsDatabase db) {
					super(context);
					this.db = db;
				}
		
				ContactsDatabase db;

				@Override
				public Cursor loadInBackground(){
					Cursor cursor = db.getAllData();
					return cursor;
				}
			}
		
	/**
	 * The Class for creating Alert Dialog.
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
		 * Creates Alert Dialog when deleting one or all contacts.
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
									((MainScreen) getActivity())
											.doPositiveClick();
 
								}
							})
					.setNegativeButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int whichButton) {
									((MainScreen) getActivity())
											.doNegativeClick();
								}
							}).create();
		}
	}
}
