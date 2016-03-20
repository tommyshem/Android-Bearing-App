package com.example.bearing_android_app_22;
//todo:landscape layout needs xml file doing.
//todo:real database data needs loading into database
//todo: finish new layout layout file with comments field and scroll view
//todo: main activity when edit is pressed should  go to edit entry with that position
//todo: edit needs adding to edit entry activity
//todo: delete on edit screen needs dialog for are you sure yes or no ?


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.app.Activity;

@SuppressWarnings("deprecation")
public class MainActivityBearing extends Activity implements SeekBar.OnSeekBarChangeListener {
    //database reference
    public static DBAdapter myDatabase;
    private static int DatabaseRecordLocationNumber = 0;
    //stores the references from the layout xml so java can change the widgets and get info from them
    private EditText InnerDiameter_editText_Ref;
    private SeekBar InnerDiameter_SliderSwitch_Ref;
    private EditText OuterDiameter_editText_Ref;
    private EditText WidthDiameter_editText_Ref;
    private SeekBar OuterDiameter_SliderSwitch_Ref;
    private SeekBar WidthDiameter_SliderSwitch_Ref;
    private ImageView bearing_ImageView_Ref;
    // bearing text view reference
    private TextView bearing_number_TextView_Ref;
    private TextView bearing_description_TextView_Ref;

    /**
     * GetDatabaseRecordLocationNumber
     * return  DatabaseRecordLocationNumber which is the database record location number
     * picked from the list
     */
    static public int GetDatabaseRecordLocationNumber() {

        return DatabaseRecordLocationNumber;
    }

    /**
    *UI create
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //open this apps database
        openThisAppsDatabase();

        //setup references and listeners
        InitContentViewReferencesAndListeners();

        //populate list from the database
        populateListViewFromDB(myDatabase.getAllRows());

    }//end onCreate

    /**
     * setup references and listeners
     * InitContentReferences
     * Get References for the text view and number edit boxes
     * and the slider widgets
     * reference for the inner diameter edit text
     */
    private void InitContentViewReferencesAndListeners() {
        //reference for the inner diameter edit text
        InnerDiameter_editText_Ref = (EditText) findViewById(R.id.etxt_ID);
        InnerDiameter_editText_Ref.setText("0");  //set to zero

        //Reference for the inner diameter slider switch
        InnerDiameter_SliderSwitch_Ref = (SeekBar) findViewById(R.id.seekBar_ID);

        //Reference for the outer diameter edit text
        OuterDiameter_editText_Ref = (EditText) findViewById(R.id.etxt_OD);
        OuterDiameter_editText_Ref.setText("0");

        //Reference for the outer diameter slider switch
        OuterDiameter_SliderSwitch_Ref = (SeekBar) findViewById(R.id.seekBar_OD);

        //Reference for the width diameter edit text
        WidthDiameter_editText_Ref = (EditText) findViewById(R.id.etxt_Width);
        WidthDiameter_editText_Ref.setText("0");

        //Reference for the width diameter  text
        WidthDiameter_SliderSwitch_Ref = (SeekBar) findViewById(R.id.seekBar_Width);

        //Reference for the image view bearing picture
        bearing_ImageView_Ref = (ImageView) findViewById(R.id.iv_BearingImage);


        //bearing info which needs updating reference
        bearing_number_TextView_Ref = (TextView) findViewById(R.id.tv_BearingNumber);
        bearing_description_TextView_Ref = (TextView) findViewById(R.id.tv_BearingDescription);

        //sets the listener for the seek bars all of them changed - this means methods in this class
        ////callback method onProgressChanged - onStartTrackingTouch and onStopTrackingTouch
        InnerDiameter_SliderSwitch_Ref.setOnSeekBarChangeListener(this);
        OuterDiameter_SliderSwitch_Ref.setOnSeekBarChangeListener(this);
        WidthDiameter_SliderSwitch_Ref.setOnSeekBarChangeListener(this);

        ListView myList = (ListView) findViewById(R.id.listViewBearingsList);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long idInDB) {
                //display the info in a toast popup
                displayToastForId(idInDB);
                //update the text values for bearing info in the middle section.
                UpdateBearingText(idInDB);
            }
        });


        //register for context menu callback method onContextItemSelected on the image - (long press menu)
        registerForContextMenu(bearing_ImageView_Ref);
    }

    /**
     * display the record
     *
     * @param idInDB Pass in id to be displayed.
     */
    private void displayToastForId(long idInDB) {
        Cursor cursor = myDatabase.getRow(idInDB);
        if (cursor.moveToFirst()) {
            long idDB = cursor.getLong(DBAdapter.COL_ROW_ID);
            String bearing_number = cursor.getString(DBAdapter.COL_BEARING_NUMBER);
            int size = cursor.getInt(DBAdapter.COL_ID_SIZE);
            int od = cursor.getInt(DBAdapter.COL_OD_SIZE);
            int width = cursor.getInt(DBAdapter.COL_KEY_WIDTH);
            String location = cursor.getString(DBAdapter.COL_KEY_LOCATION);
            String comments = cursor.getString((DBAdapter.COL_KEY_COMMENTS));

            String message = "Record ID: " + idDB + "\n"
                    + "Name: " + bearing_number + "\n"
                    + "I/D: " + size + "mm\n"
                    + "O/D: " + od + "mm\n"
                    + "Width: " + width + "mm\n"
                    + "Location: " + location + "\n"
                    + "Comments: " + comments;
            DatabaseRecordLocationNumber = (int) idInDB;
            Toast.makeText(MainActivityBearing.this, message, Toast.LENGTH_LONG).show();
        }
        cursor.close();
    }


    /*
    *database methods
     */

    private void UpdateBearingText(long DB_rowid) {
        Cursor cursor = myDatabase.getRow(DB_rowid);
        //if cursor not null then get the info
        if (cursor.moveToFirst()) {

            //get the info from the cursor from the record which the list selection pressed with the finger
            String bearing_number = cursor.getString(DBAdapter.COL_BEARING_NUMBER);
            //update the gui bearing number text and description
            String message = "I/D : " + cursor.getInt(DBAdapter.COL_ID_SIZE) +
                             "mm, O/D : " + cursor.getInt(DBAdapter.COL_OD_SIZE) + "mm,\n " +
                             "Width : " + cursor.getInt(DBAdapter.COL_KEY_WIDTH) + "mm";

            bearing_number_TextView_Ref.setText(bearing_number);
            bearing_description_TextView_Ref.setText(message);
        }
        cursor.close();
    }

    /**
     * open this apps database by using the DBAdapter class
     */
    private void openThisAppsDatabase() {
        myDatabase = new DBAdapter(this);
        myDatabase.open();

    }

    /*
     * Context menu methods (long press menu)
     */

    /**
     * populate the list view from the database cursor
     * call this method when you need to update the list view
     */
    private void populateListViewFromDB(Cursor cursor) {
        //get all data from the database
        //Cursor cursor = myDatabase.getAllRows();

        // Allow activity to manage lifetime of the cursor.
        // DEPRECATED! Runs on the UI thread, OK for small/short queries.
        startManagingCursor(cursor);

        // Setup mapping from cursor to view fields:
        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_BEARING_NUMBER, DBAdapter.KEY_OD_SIZE, DBAdapter.KEY_ID_SIZE, DBAdapter.KEY_WIDTH};
        int[] toViewIDs = new int[]
                {R.id.txt_list_bearingnumber, R.id.txt_od_list_value, R.id.txt_id_list_value, R.id.txt_list_width_value};

        // Create adapter to may columns of the DB onto elemesnt in the UI.
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,        // Context
                        R.layout.custom_listitem_layout,    // Row layout template
                        cursor,                    // cursor (set of DB records to map)
                        fromFieldNames,            // DB Column names
                        toViewIDs                // View IDs to put information in
                );
        // Set the adapter for the list view
        ListView myList = (ListView) findViewById(R.id.listViewBearingsList);
        myList.setAdapter(myCursorAdapter);

    }

    /**
     * override method for the context menu create with an xml resource file
     *
     * @param menu     default
     * @param v        default
     * @param menuInfo default
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu, menu);
    }

    /*
    *Options menu methods (menu on the bottom of the activity)
     */

    /**
     * callback method for item selected on the context menu - find out which one is pressed
     *
     * @param item menu item pressed
     * @return boolean return true if done the method
     */
    //
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //find out which menu item was pressed
        switch (item.getItemId()) {
            //search menu item - find with all fields
            case R.id.contextmenuoption1:

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityBearing.this);

                alert.setTitle("Find Bearing"); //Set Alert dialog title here
                alert.setMessage("Enter Your Bearing Number To Find Here"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(MainActivityBearing.this);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string
                        String srt = input.getEditableText().toString();
                        Cursor c = myDatabase.FindValueInTable(DBAdapter.KEY_BEARING_NUMBER, srt);
                        //if cursor is not null then display in the list view
                        if (c != null) populateListViewFromDB(c);
                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled. close the dialog
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
       /* Alert Dialog Code End*/

                return true;
            //set to zero menu
            case R.id.contextmenuoption2:
                //zero menu - set the slidders to zero and edit text box to zero
                InnerDiameter_SliderSwitch_Ref.setProgress(0);
                OuterDiameter_SliderSwitch_Ref.setProgress(0);
                WidthDiameter_SliderSwitch_Ref.setProgress(0);
                InnerDiameter_editText_Ref.setText("0");
                OuterDiameter_editText_Ref.setText("0");
                WidthDiameter_editText_Ref.setText("0");
                return true;
            default:
                return false;
        }
    }

    /**
     * for loading the xml menu on the activity
     *
     * @param menu reference to the menu
     * @return true if dealt with the callback else false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

      /*
       *Seekbar callback methods
       */

    /**
     * callback method event from the activity class
     * menu item selected check which menu item was pressed
     *
     * @param item menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //
            case R.id.MenuitemEdit:
                //start new splash screen
                Intent k = new Intent();
                k.setClass(this, EditEntry_Activity.class);
                startActivity(k);
                return true;

            case R.id.MenuitemAbout:
                //start new splash screen
                Intent i = new Intent();
                i.setClass(this, SplashScreenActivity.class);
                startActivity(i);
                return true;

            case R.id.MenuitemSearch:
                Log.d("menu", "search menu item pressed");
                Cursor c = myDatabase.SearchBearingSizesInTable(InnerDiameter_editText_Ref.getText().toString(), OuterDiameter_editText_Ref.getText().toString(), WidthDiameter_editText_Ref.getText().toString());
                //if cursor is not null then display in the list view
                if (c != null) populateListViewFromDB(c);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * callback method from the setOnSeekBarChangeListener on SeekBar.
     * setOnSeekBarChangeListener implements methods must be over ridden,
     * the methods that can be over-ridin are onProgressChanged, onStartTrackingTouch and onStopTrackingTouch
     *
     * @param seekbar  The SeekBar whose progress has changed
     * @param progress value of the slider between min and max numbers setup on the SeekBar
     * @param fromuser True if the progress change was initiated by the user.
     */
    @SuppressLint("SetTextI18n")
    @Override //  Notify that the progress level has changed.
    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromuser) {

        //send progress number from the seek bar to to the edit text
        //use if statements to find out which seek bar is being updated then update the edit text number

        if (seekbar.getId() == R.id.seekBar_ID) {
            InnerDiameter_editText_Ref.setText("" + progress);
        } else if (seekbar.getId() == R.id.seekBar_OD) {
            OuterDiameter_editText_Ref.setText("" + progress);
        } else if (seekbar.getId() == R.id.seekBar_Width) {
            WidthDiameter_editText_Ref.setText("" + progress);
        }
    }

    /**
     * used for the seekbar implements methods
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //  Auto-generated method stub
        //not used at present.
    }

    /**
     * used for the seekbar implements methods.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //  Auto-generated method stub
        //not used at present
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //close the database when activity is closed
        myDatabase.close();
    }

    /*
    *Button Onclick  call back methods
     */
    public void SearchOnClick(View v) {
        Log.d("Button", "search button pressed");

        switch (v.getId()) {
            //search button pressed
            case R.id.btn_search:
                Log.d("button", "search width pressed");
                //search database for the bearing sizes from the values passed
                Cursor c = myDatabase.SearchBearingSizesInTable(InnerDiameter_editText_Ref.getText().toString(), OuterDiameter_editText_Ref.getText().toString(), WidthDiameter_editText_Ref.getText().toString());
                //if cursor is not null then display in the list view
                if (c != null) populateListViewFromDB(c);
                break;


        }


    }
}//end class