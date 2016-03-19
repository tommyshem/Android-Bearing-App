package com.example.bearing_android_app_22;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


/**
 * EditEntry class - provides the navigation for the cursor and added the menu item edit for
 * adding the new records
 */
@SuppressWarnings("deprecation")
public class EditEntry_Activity extends Activity {

    //cursor reference for the database
    Cursor c;
    //global variables for the gui references
    private EditText et_bearingNumber, et_id, et_od, edit_text_image_number_ref, et_width, et_type, et_location, et_comments;
    private ImageButton b_prev, b_next, b_last, b_first;
    private MenuItem mi_add, mi_save, mi_cancel, mi_delete;
    private TextView record_values_TextView_Ref;
    private EditText[] edit_texts;

    // ArrayList<EditText> arrayList = new ArrayList<EditText>();

    /**
     * Main method called when creating the activity.
     * setup the buttons and edit texts views
     *
     * @param savedInstanceState Android Object passed
     */
     @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlayout);
        //setup the edit text references
        setupEditTexts();
        //load data into the cursor
        c = MainActivityBearing.myDatabase.getAllRows();
        startManagingCursor(c);
        // c.move(MainActivityBearing.GetDatabaseRecordLocationNumber());
        //move to the first record - removed 16/5/2013
        //c.moveToFirst();
        //fill in  the edit text views with the data from the cursor
        UpdateAllEditTextViewsFromCursor();

    }


    /**
     * Setup Edit Text method for the gui
     */
    private void setupEditTexts() {
        //setup reference for the edit text views for use in java
        et_bearingNumber = (EditText) this.findViewById(R.id.editTextBearingNum);
        et_id = (EditText) this.findViewById(R.id.editTextID);
        et_od = (EditText) this.findViewById(R.id.editTextOD);
        et_width = (EditText) this.findViewById(R.id.editTextWidth);
        et_type = (EditText) this.findViewById(R.id.editTextType);
        et_location = (EditText) this.findViewById(R.id.editTextLocation);
        edit_text_image_number_ref = (EditText) this.findViewById(R.id.editTextImageNum);
        et_comments = (EditText) this.findViewById(R.id.editTextComments);

        //array for the edit texts.
        edit_texts = new EditText[8];
        edit_texts[0] = et_bearingNumber;
        edit_texts[1] = et_id;
        edit_texts[2] = et_od;
        edit_texts[3] = et_width;
        edit_texts[4] = et_type;
        edit_texts[5] = et_location;
        edit_texts[6] = edit_text_image_number_ref;
        edit_texts[7] = et_comments;

        //setup button reference for the views for use in java
        b_next = (ImageButton) this.findViewById(R.id.btnNext);
        b_prev = (ImageButton) this.findViewById(R.id.btnPrev);
        b_last = (ImageButton) this.findViewById(R.id.btnLast);
        b_first = (ImageButton) this.findViewById(R.id.btnFirst);
        //setup text view reference for the use in java
        record_values_TextView_Ref = (TextView) this.findViewById(R.id.RecordValues);

    }

    //setup options menu methods

    /**
     * for loading the xml menu on the activity
     *
     * @param menu reference to the menu
     * @return true if dealt with the callback else false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // save menu reference
        // this.menu = menu;  no longer used
        //inflate the menu from the xml file
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenueditentry, menu);

        // save options menu items reference for use in java
        mi_add = menu.findItem(R.id.mi_add);
        mi_cancel = menu.findItem(R.id.mi_cancel);
        mi_save = menu.findItem(R.id.mi_save);
        mi_delete = menu.findItem(R.id.mi_delete);

        return true;
    }

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
            case R.id.mi_add:
                //hide the nav buttons
                b_first.setVisibility(Button.INVISIBLE);
                b_last.setVisibility(Button.INVISIBLE);
                b_next.setVisibility(Button.INVISIBLE);
                b_prev.setVisibility(Button.INVISIBLE);


                // edit texts change focus
                for (EditText edit_text : edit_texts) {
                    edit_text.setFocusableInTouchMode(true);
                }

                //set the add menu item to invisible
                item.setVisible(false);
                //set the cancel menu item to enabled
                mi_cancel.setVisible(true);
                //is enabled
                mi_save.setVisible(true);
                //set the delete menu item to invisible
                mi_delete.setVisible(false);
                //clear all edit text values to nothing
                ClearAllEditTextValues();
                return true;

            case R.id.mi_cancel:
                //un hide the nav buttons
                UnHideAllNavButtons();

                //set the add menu item to visible and the other two menu items to not viable
                mi_add.setVisible(true);

                mi_cancel.setVisible(false);
                mi_save.setVisible(false);
                mi_delete.setVisible(true);
                UpdateAllEditTextViewsFromCursor();

                return true;

            case R.id.mi_save:
                UnHideAllNavButtons();

                Boolean result = InsertDataToCursor();

                //move to the last data record which is the one you have just inserted
                // if the data inserted in the cursor ok (result = true) also
                // change the state of the menu items.
                if (result) {
                    //update the cursor with the new data
                    c.requery();
                    //move to the last record in the cursor so you can see the new data
                    c.moveToLast();
                    //setup menu items
                    mi_save.setVisible(false);
                    mi_cancel.setVisible(false);
                    mi_add.setVisible(true);
                    mi_delete.setVisible(true);
                    UnHideAllNavButtons();
                    //update all the edit text fields in this activity
                    UpdateAllEditTextViewsFromCursor();
                    return true;
                }
            case R.id.mi_delete:
                int row_id_delete;
                row_id_delete = c.getInt(DBAdapter.COL_ROWID);
                if (row_id_delete != -1) {

                    MainActivityBearing.myDatabase.deleteRow(row_id_delete);
                    Toast.makeText(this, "Record " + row_id_delete + " Deleted Successfully", Toast.LENGTH_SHORT).show();
                    //update all the edit text fields in this activity
                    c = MainActivityBearing.myDatabase.getAllRows();
                    startManagingCursor(c);
                    UpdateAllEditTextViewsFromCursor();
                    //todo: needs testing above so deletes and updates in the correct place
                    //todo : needs dialog box yes or no before deleting the data
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void UnHideAllNavButtons() {
        //un hide the nav buttons
        b_first.setVisibility(Button.VISIBLE);
        b_last.setVisibility(Button.VISIBLE);
        b_next.setVisibility(Button.VISIBLE);
        b_prev.setVisibility(Button.VISIBLE);
        // edit texts change focus to false - disables the input to the edit boxes
        for (EditText edit_text : edit_texts) {
            edit_text.setFocusable(false);
        }

    }

    /**
     * InsertDataToCursor()
     *
     * @return true for the data inserted correctly else false not inserted
     */
    private Boolean InsertDataToCursor() {
        try {

            MainActivityBearing.myDatabase.insertRow(et_bearingNumber.getText().toString(),
                    Integer.parseInt(et_id.getText().toString()),
                    Integer.parseInt(et_od.getText().toString()),
                    Integer.parseInt(et_width.getText().toString()),
                    et_type.getText().toString(),
                    Integer.parseInt(edit_text_image_number_ref.getText().toString()),
                    et_location.getText().toString(),
                    et_comments.getText().toString());
        } catch (NumberFormatException nfe) {
            Toast.makeText(EditEntry_Activity.this, "Failed to Update Database", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(EditEntry_Activity.this, "Updated Database", Toast.LENGTH_SHORT).show();
        return true;
    }


    //Methods below are for the GUI call back methods for clicking on the buttons.

    /**
     * onClickFirstRecord method
     * call back method from the xml first record button
     * Moves cursor to the first record in the data set
     *
     * @param v view
     */
    public void onClickFirstRecord(View v) {
        if (c != null) {
            c.moveToFirst();
            UpdateAllEditTextViewsFromCursor();

        }
    }

    /**
     * onClickNextRecord method
     * call back method from the xml next record button
     * Moves cursor to the next record in the data set but if on the last record then do nothing
     *
     * @param v view
     */
    public void onClickNextRecord(View v) {
        if (c != null) {
            if (!c.isLast())
                c.moveToNext();
        }
        UpdateAllEditTextViewsFromCursor();
    }

    /**
     * onClickPrevRecord method
     * call back method from the xml prev record button
     * Moves cursor to the previous record in the data set but if at First then do nothing
     *
     * @param v view
     */
    public void onClickPrevRecord(View v) {
        if (c != null) {
            if (!c.isFirst())
                c.moveToPrevious();
        }
        UpdateAllEditTextViewsFromCursor();
    }

    /**
     * onClickLastRecord method
     * call back method from the xml last record button
     * Moves cursor to the last record in the data set
     *
     * @param v view
     */
    public void onClickLastRecord(View v) {
        if (c != null) {
            c.moveToLast();
        }
        UpdateAllEditTextViewsFromCursor();
    }

    /**
     * UpdateAllEditTextViewsFromCursor method
     * method to update all the edit text views
     * fields for the data set
     */
    private void UpdateAllEditTextViewsFromCursor() {
        if (c != null) {
            et_bearingNumber.setText(c.getString(DBAdapter.COL_BEARING_NUMBER));
            et_od.setText(c.getString(DBAdapter.COL_OD_SIZE));
            et_id.setText(c.getString(DBAdapter.COL_ID_SIZE));
            et_width.setText(c.getString(DBAdapter.COL_KEY_WIDTH));
            et_type.setText(c.getString(DBAdapter.COL_KEY_TYPE));
            edit_text_image_number_ref.setText(c.getString(DBAdapter.COL_KEY_IMAGENUMBER));
            et_location.setText(c.getString(DBAdapter.COL_KEY_LOCATION));
            et_comments.setText(c.getString(DBAdapter.COL_KEY_COMMENTS));

            //setup text for the record number and size of records
            String text_values = "Record " + (c.getPosition() + 1) + " of " + c.getCount();
            record_values_TextView_Ref.setText(text_values);


        }
    }

    /**
     * ClearAllEditTextValues
     * clears all the edit text values so the user can input info in the edit texts
     * Also hides the record text when in editing mode and change the record text value
     * to Editing New Entry so the user knows which mode we are in.
     */
    private void ClearAllEditTextValues() {
        //clear all the values to nothing so you can edit the edit text values
        et_bearingNumber.setText("");
        et_od.setText("");
        et_id.setText("");
        et_width.setText("");
        et_type.setText("");
        edit_text_image_number_ref.setText("");
        et_location.setText("");
        et_comments.setText("");

        //update the record text view to what is going on
        record_values_TextView_Ref.setText(R.string.EditingNewEntry);

    }


}