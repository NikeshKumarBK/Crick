package in.co.crickon.cachii.crickon;

/**
 * Created by User on 7/2/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Department";

    // Login table name
    private static final String TABLE_USER = "users";

    // Login Table Columns names
    private static final String KEY_ID = "Id";
    private static final String KEY_PLAYERID = "PlayerId";
    private static final String KEY_NAME = "Name";
    private static final String KEY_TEAMID = "Teamid";
    private static final String KEY_PHONENUMBER = "Phno";
    private static final String KEY_PINCODE = "Pincode";
    private static final String KEY_ROLE = "Role";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  + KEY_PLAYERID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_TEAMID + " TEXT," + KEY_PHONENUMBER + " TEXT,"
                + KEY_ROLE + " TEXT,"
                + KEY_PINCODE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addPlayer(String keyid, String name, String phno, String teamid, String pincode, String role) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
       // values.put(KEY_ID,id);
        values.put(KEY_PLAYERID, keyid); // id
        values.put(KEY_NAME, name); // name
        values.put(KEY_PHONENUMBER, phno); // phone number
        values.put(KEY_TEAMID, teamid); //teamid
        values.put(KEY_PINCODE, pincode); //pincode
        values.put(KEY_ROLE, role); //role

        // Inserting Row
        long tid = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + tid);
    }
    public void addTeam(String name, String pincode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put(KEY_ID,id);
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PINCODE, pincode); // pincode

        // Inserting Row
        long tid = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + tid);
    }


    public String getRole(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_ROLE + " FROM " + TABLE_USER + " WHERE " +
                KEY_ID + "=1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String i = c.getString(0);

        return i;
    }

    public String getPincode(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_PINCODE + " FROM " + TABLE_USER + " WHERE " +
                KEY_ID + "=1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String i = c.getString(0);

        return i;
    }

    public String getTeamid(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_TEAMID + " FROM " + TABLE_USER + " WHERE " +
                KEY_ID + "=1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String i = c.getString(0);

        return i;
    }

    public String getPlayerID(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_PLAYERID + " FROM " + TABLE_USER + " WHERE " +
                KEY_ID + "=1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String i = c.getString(0);

        return i;
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id",cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("phonenumber", cursor.getString(3));
            user.put("pincode", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
/**
    public String getDept(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_DEPT + " FROM " + TABLE_USER + " WHERE " +
                KEY_ID + "=1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String i = c.getString(0);

        return i;
    }

  **/  /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }



}