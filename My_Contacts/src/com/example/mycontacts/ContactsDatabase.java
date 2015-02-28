package com.example.mycontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The Class for working with database of application.
 */
public class ContactsDatabase {

	/**
	 * Instantiates a new contacts database.
	 */
	public ContactsDatabase(Context ctx) {
		contactsCtx = ctx;
	}

	/**
	 * Open database connection.
	 */
	public void open() {
		dbHelp = new DataBaseHelper(contactsCtx, DATABASE_NAME, null,
				DATABASE_VERSION);
		myContDB = dbHelp.getWritableDatabase();
	}

	/**
	 * Close database connection.
	 */
	public void close() {
		if (dbHelp != null) {
			dbHelp.close();
		}
	}

	/**
	 * Select contact.
	 *
	 * @param id the id of  selected contact
	 * @return the contact data
	 */
	public ContactData selectContact(long id) {

		Cursor mCursor = myContDB.query(DATABASE_TABLE, null, COLUMN_ID
				+ " = ?", new String[] { String.valueOf(id) }, null, null,
				COLUMN_LASTNAME);

		mCursor.moveToFirst();
		String name = mCursor.getString(NUM_COLUMN_NAME);
		String lastname = mCursor.getString(NUM_COLUMN_LASTNAME);
		long date = mCursor.getLong(NUM_COLUMN_BIRTHDATE);
		String adress = mCursor.getString(NUM_COLUMN_ADRESS);
		String gender = mCursor.getString(NUM_COLUMN_GENDER);
		String image = mCursor.getString(NUM_COLUMN_IMAGE);
		mCursor.close();
		return new ContactData(id, name, lastname, date, adress, gender, image);
	}
	

	/**
	 * Select all contacts from database.
	 *
	 * @return the cursor
	 */
	public Cursor getAllData(){
		return myContDB.rawQuery("SELECT * FROM " 
				+ DATABASE_TABLE
				+ " ORDER BY CASE WHEN " 
				+ COLUMN_LASTNAME + " = '' THEN "
				+ COLUMN_NAME + " ELSE " 
				+ COLUMN_LASTNAME
				+ " END"
				, null);
	}

	/**
	 * Adds the contact.
	 *
	 * @param cd the contact data for new contact.
	 * @return command to insert contact data in database.
	 */
	public long addContact(ContactData cd) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, cd.getName());
		cv.put(COLUMN_LASTNAME, cd.getLastname());
		cv.put(COLUMN_BIRTHDATE, cd.getDate());
		cv.put(COLUMN_ADRESS, cd.getAdress());
		cv.put(COLUMN_GENDER, cd.getGender());
		cv.put(COLUMN_IMAGE, cd.getImage());
		return myContDB.insert(DATABASE_TABLE, null, cv);
	}
	
	/**
	 * Update contact.
	 *
	 * @param cd the contact data of selected contact
	 * @return command to update selected contact.
	 */
	public int update(ContactData cd) {
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, cd.getName());
		cv.put(COLUMN_LASTNAME, cd.getLastname());
		cv.put(COLUMN_BIRTHDATE, cd.getDate());
		cv.put(COLUMN_ADRESS, cd.getAdress());
		cv.put(COLUMN_GENDER, cd.getGender());
		cv.put(COLUMN_IMAGE, cd.getImage());
		return myContDB.update(DATABASE_TABLE, cv, COLUMN_ID + " = ?",
				new String[] { String.valueOf(cd.getId()) });
	}

	/**
	 * Delete the contact by id.
	 *
	 * @param id the id of deleting contact.
	 */
	public void delete(long id) {
		myContDB.delete(DATABASE_TABLE, COLUMN_ID + " = " + id, null);
	}

	/**
	 * Delete all contacts from database.
	 */
	public void deleteAll() {
		myContDB.delete(DATABASE_TABLE, null, null);
	}
		
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "myContactAppDB";
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 2;
	
	/** The Constant DATABASE_TABLE. */
	private static final String DATABASE_TABLE = "Contacts";

	/** The Constant COLUMN_ID. */
	final static String COLUMN_ID = "_id";
	
	/** The Constant COLUMN_NAME. */
	final static String COLUMN_NAME = "firstname";
	
	/** The Constant COLUMN_LASTNAME. */
	final static String COLUMN_LASTNAME = "lastname";
	
	/** The Constant COLUMN_BIRTHDATE. */
	final static String COLUMN_BIRTHDATE = "date_of_birth";
	
	/** The Constant COLUMN_ADRESS. */
	final static String COLUMN_ADRESS = "adress";
	
	/** The Constant COLUMN_IMAGE. */
	final static String COLUMN_IMAGE = "image";
	
	/** The Constant COLUMN_GENDER. */
	private static final String COLUMN_GENDER = "gender";

	/** The Constant NUM_COLUMN_ID. */
	final int NUM_COLUMN_ID = 0;
	
	/** The Constant NUM_COLUMN_NAME. */
	final int NUM_COLUMN_NAME = 1;
	
	/** The Constant NUM_COLUMN_LASTNAME. */
	final int NUM_COLUMN_LASTNAME = 2;
	
	/** The Constant NUM_COLUMN_BIRTHDATE. */
	int NUM_COLUMN_BIRTHDATE = 3;
	
	/** The Constant NUM_COLUMN_ADRESS. */
	final int NUM_COLUMN_ADRESS = 4;
	
	/** The Constant NUM_COLUMN_GENDER. */
	final int NUM_COLUMN_GENDER = 5;
	
	/** The Constant NUM_COLUMN_IMAGE. */
	final int NUM_COLUMN_IMAGE = 6;

	/** The Constant CREATE_DATABASE. */
	private static final String CREATE_DATABASE = "create table "
			+ DATABASE_TABLE + 
			" (" + COLUMN_ID + " integer primary key " + "autoincrement, "
			+ COLUMN_NAME + " text, "
			+ COLUMN_LASTNAME + " text,"
			+ COLUMN_BIRTHDATE + " long ," 
			+ COLUMN_ADRESS	+ " text,"
			+ COLUMN_GENDER + " text, " 
			+ COLUMN_IMAGE + " text );";

	private final Context contactsCtx;
	private DataBaseHelper dbHelp;	
	private SQLiteDatabase myContDB;

	/**
	 * The Class DataBaseHelper for work with database.
	 */
	private class DataBaseHelper extends SQLiteOpenHelper {

		/**
		 * Instantiates a new data base helper.
		 *
		 * @param context the context
		 * @param name the name of database
		 * @param factory is null
		 * @param version the database version
		 */
		public DataBaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, null, version);
		}

		/** 
		 * Creates database.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DATABASE);

		}

		/**
		 *  Upgrades database.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			if(oldVersion == 1 && newVersion == 2){
				db.beginTransaction();
				try{
					db.execSQL("ALTER TABLE " + DATABASE_TABLE 
							+ " RENAME TO " + DATABASE_TABLE + "_TMP");
					db.execSQL(CREATE_DATABASE);
					db.execSQL("INSERT INTO " + DATABASE_TABLE 
							+ "( "
							+ COLUMN_ID + ", "  
							+ COLUMN_NAME + ", "
							+ COLUMN_LASTNAME + ", "
							+ COLUMN_BIRTHDATE + ", "
							+ COLUMN_ADRESS + ", "
							+ COLUMN_GENDER + ", "
							+ COLUMN_IMAGE + ")"
							+" SELECT contact_id, "
							+ COLUMN_NAME + ", "
							+ COLUMN_LASTNAME + ", "
							+ COLUMN_BIRTHDATE + ", "
							+ COLUMN_ADRESS + ", "
							+ COLUMN_GENDER + ", "
							+ COLUMN_IMAGE 
							+ " FROM " + DATABASE_TABLE + "_TMP;");
					db.execSQL("DROP TABLE " + DATABASE_TABLE + "_TMP" );
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}

		}

	}

}
