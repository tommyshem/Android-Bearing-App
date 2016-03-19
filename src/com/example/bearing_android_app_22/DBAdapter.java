// ------------------------------------ DBADapter.java ---------------------------------------------
// TO USE:
// Change the package (at top) to match your project.

package com.example.bearing_android_app_22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DataBase info: it's name
    //  Setup your database here:
    public static final String DATABASE_NAME = "MyDb";
    // the table we are using (only one at this time).
    public static final String DATABASE_TABLE = "mainTable";

    // DataBase Fields  go here
    public static final String KEY_ROW_ID = "_id";
    public static final int COL_ROW_ID = 0;

    //  Setup your fields here:
    public static final String KEY_BEARING_NUMBER = "name";
    public static final String KEY_OD_SIZE = "odsize";
    public static final String KEY_ID_SIZE = "idsize";
    public static final String KEY_WIDTH = "widthsize";
    public static final String KEY_TYPE = "type";
    public static final String KEY_IMAGE_NUMBER = "imagenumber";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_COMMENTS = "comments";

    //  Setup your field numbers here (0 = KEY_ROW_ID, 1=...)
    public static final int COL_BEARING_NUMBER = 1;
    public static final int COL_OD_SIZE = 2;
    public static final int COL_ID_SIZE = 3;
    public static final int COL_KEY_WIDTH = 4;
    public static final int COL_KEY_TYPE = 5;
    public static final int COL_KEY_IMAGE_NUMBER = 6;
    public static final int COL_KEY_LOCATION = 7;
    public static final int COL_KEY_COMMENTS = 8;

    //  make sure all the fields are filled in at the end:
    public static final String[] ALL_KEYS = new String[]{KEY_ROW_ID,
            KEY_BEARING_NUMBER,
            KEY_OD_SIZE,
            KEY_ID_SIZE,
            KEY_WIDTH,
            KEY_TYPE,
            KEY_IMAGE_NUMBER,
            KEY_LOCATION,
            KEY_COMMENTS};


    // Track DataBase version if a new version of your app changes the format.
    //numbers start at 1 and every time you change your database change the number by +1
    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROW_ID + " integer primary key autoincrement, "

                    //	- {type} is one of: text, integer, real, blob
                    //		(http://www.sqlite.org/datatype3.html)
                    
                    + KEY_BEARING_NUMBER + " string not null,"
                    + KEY_OD_SIZE + " integer not null, "
                    + KEY_ID_SIZE + " integer not null, "
                    + KEY_WIDTH + " integer not null, "
                    + KEY_TYPE + " string not null,"
                    + KEY_IMAGE_NUMBER + " integer not null, "
                    + KEY_LOCATION + " string not null, "
                    + KEY_COMMENTS + " string not null"

// Rest  of creation:
                    + ");";

    //database reference
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////
    //constructor which creates a database in the data/data directory
    public DBAdapter(Context context) {
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        sqlDB = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String bnumber, int odsize, int idsize, int widthsize, String type, int imagenumber, String location, String comments) {
		/*
		 * CHANGE 3:
		 */
        //  Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();

        //  Update data in the row with new fields.
        initialValues.put(KEY_BEARING_NUMBER, bnumber);
        initialValues.put(KEY_OD_SIZE, odsize);
        initialValues.put(KEY_ID_SIZE, idsize);
        initialValues.put(KEY_WIDTH, widthsize);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_IMAGE_NUMBER, imagenumber);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_COMMENTS, comments);
        // Insert it into the database.
        return sqlDB.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete a row from the database, by rowId (primary key)
     * @param rowId pass in database row id to delete that record
     * @return result
     */
    public boolean deleteRow(long rowId) {
        String where = KEY_ROW_ID + "=" + rowId;
        return sqlDB.delete(DATABASE_TABLE, where, null) != 0;
    }

    /**
     *  Delete all records in the database
     */
    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROW_ID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    /**
     * Return all data in the database.
     * @return database cursor
     */
    public Cursor getAllRows() {
        String where = null;
        Cursor c = sqlDB.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * FindValveInTable -finds the value in the table passed to it
     *
     * @param Field_name    field name goes here
     * @param value_to_find value you want to find in the column
     * @return cursor reference
     */
    public Cursor FindValueInTable(String Field_name, String value_to_find) {
        //field name
        String where = Field_name + "='" + value_to_find + "'";

        Cursor c = sqlDB.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * SearchBearingSizesInTable
     *
     * @param id    value to find in the database table - can be zero
     * @param od    value to find in the database table - can be zero
     * @param width value to find in the database table - can be zero
     * @return Cursor from the database with the search items
     */
    public Cursor SearchBearingSizesInTable(String id, String od, String width) {
        //field name
        List<String> sqlList = new ArrayList<String>();
        if (!id.equals("0")) sqlList.add(KEY_ID_SIZE + "='" + id + "'");
        if (!od.equals("0")) sqlList.add(KEY_OD_SIZE + "='" + od + "'");
        if (!width.equals("0")) sqlList.add(KEY_WIDTH + "='" + width + "'");
        String where = "";

        if (sqlList.size() == 0) {
            where = null;
        } else if (sqlList.size() == 1) {
            where = sqlList.get(0);
            Log.d("search1 items", sqlList.get(0));
        } else if (sqlList.size() == 2) {
            where = sqlList.get(0) + " AND " + sqlList.get(1);
            Log.d("search 2 items", where);

        } else if (sqlList.size() == 3) {
            where = sqlList.get(0) + " AND " + sqlList.get(1) + " AND " + sqlList.get(2);
            Log.d("search 3 items", where);

        }

        Cursor c = sqlDB.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * @param rowId Get a specific row
     * @return result
     */
    public Cursor getRow(long rowId) {
        String where = KEY_ROW_ID + "=" + rowId;
        Cursor c = sqlDB.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Update an existing row to be equal to new data.
     * @param rowId             pass in row id
     * @param bearing_number    pass in bearing number
     * @param od_size           pass in outer diameter
     * @param id_size           pass in inner diameter
     * @param width_size        pass in width diameter
     * @param type              pass in type of bearing eg. zz 2rs
     * @param image_number      pass in image number
     * @param location          pass in location of the bearing
     * @param comments          pass in ant comments
     * @return result 
     */
    public boolean updateRow(long rowId, String bearing_number, int od_size,
                             int id_size, int width_size, String type,
                             int image_number, String location, String comments) {

        String where = KEY_ROW_ID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
        //  Update data in the row with new fields.
        // Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_BEARING_NUMBER, bearing_number);
        newValues.put(KEY_OD_SIZE, od_size);
        newValues.put(KEY_ID_SIZE, id_size);
        newValues.put(KEY_WIDTH, width_size);
        newValues.put(KEY_TYPE, type);
        newValues.put(KEY_IMAGE_NUMBER, image_number);
        newValues.put(KEY_LOCATION, location);
        newValues.put(KEY_COMMENTS, comments);
        // Insert it into the database.
        return sqlDB.update(DATABASE_TABLE, newValues, where, null) != 0;
    }


    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        //constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //callback when the database does not exist and it will create empty one
        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        //callback method when the version number is higher than the one stored
        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            //todo copy data from the old database and then update the database table  -as only deletes all contexts
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
