package com.example.rentalz;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rentalz.Backend.SQLiteHelper;
import com.example.rentalz.Models.Property;
import com.example.rentalz.ViewHolder.PropertyListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class PropertyListActivity<property> extends AppCompatActivity {
    ListView mListView;
    ArrayList<Property> mList;

    public static final String DATABASE_NAME;

    static {
        DATABASE_NAME = "RECORDDB.sqlite";
    }

    PropertyListAdapter mAdapter = null;
    ImageView imageViewIcon;
    private EditText searchInput;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "PropertyListActivity";
    SQLiteHelper sqLiteHelper;
    CharSequence search="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        searchInput = findViewById(R.id.search_input);

        mListView = findViewById(R.id.list_view);

        mList = new ArrayList<>();
        mAdapter = new PropertyListAdapter(this, R.layout.property_display_layout, mList);
        mListView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(PropertyListActivity.this, AddPropertyActivity.class);
            startActivity(intent);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
                search = s;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //get all data from sqlite
        sqLiteHelper = new SQLiteHelper(this, DATABASE_NAME, null,1);
        final Cursor cursor = sqLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String propType = cursor.getString(1);
            String bedrooms = cursor.getString(2);
            String date = cursor.getString(3);
            String time = cursor.getString(4);
            String rentPrice = cursor.getString(5);
            String furnType = cursor.getString(6);
            String notes = cursor.getString(7);
            String reporter = cursor.getString(8);
            byte[] image = cursor.getBlob(9);

            //add to list

            mList.add(new Property(id, propType, bedrooms, date, time, rentPrice, furnType, notes, reporter, image));
        }

        mAdapter.notifyDataSetChanged();
        if (mList.size()==0){
            //if there is no record in the database which means listview is empty
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            //alert dialog to display options of update and delete
            final CharSequence[] items = {"Update Property", "Update Info", "Delete", "Additional Notes"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(PropertyListActivity.this);

            dialog.setTitle("Choose an action");
            dialog.setItems(items, (dialog1, i) -> {


                if (i == 0) {
                    //Update Rating
                    Cursor c = AddPropertyActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                    ArrayList<Integer> arrID = new ArrayList<>();
                    while (c.moveToNext()){
                        arrID.add(c.getInt(0));
                    }
                    //show update dialog
                    showDialogUpdateRating(PropertyListActivity.this, arrID.get(position));
                }

                if (i == 1) {
                    //Update Info
                    Cursor c = AddPropertyActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                    ArrayList<Integer> arrID = new ArrayList<>();
                    while (c.moveToNext()){
                        arrID.add(c.getInt(0));
                    }
                    //show update dialog
                    showDialogUpdateInfo(PropertyListActivity.this, arrID.get(position));
                }



                if (i == 2){
                    //Delete
                    Cursor c = AddPropertyActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                    ArrayList<Integer> arrID = new ArrayList<>();
                    while (c.moveToNext()) {
                        arrID.add(c.getInt(0));
                    }
                    showDialogDelete(arrID.get(position));
                }

                if (i == 3) {
                    //Additional Notes
                    Cursor c = AddPropertyActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                    ArrayList<Integer> arrID = new ArrayList<>();
                    while (c.moveToNext()){
                        arrID.add(c.getInt(0));
                    }
                    //show update dialog
                    showDialogNote(PropertyListActivity.this, arrID.get(position));
                }
            });
            dialog.show();
            return true;
        });



        mListView.setOnItemClickListener((adapterView, view, position, l) -> {
            Cursor c = sqLiteHelper.getData("SELECT id FROM RECORD");

            ArrayList<Integer> arrID = new ArrayList<>();
            while (c.moveToNext()){
                arrID.add(c.getInt(0));
            }

            arrID.get(position);
            Intent intent = new Intent(getApplicationContext(), PropertyDetailsActivity.class);
            intent.putExtra("id",arrID);


        });

    }

    private void showDialogUpdateInfo(Activity activity, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_info_dialog);
        dialog.setTitle("Update Info");
        final EditText txtPropType = dialog.findViewById(R.id.txt_upd_prop_type);
        final EditText txtBedroom = dialog.findViewById(R.id.txt_upd_bedroom);
        final EditText txtFurnType = dialog.findViewById(R.id.txt_upd_furnType);
        final EditText txtDate = dialog.findViewById(R.id.txt_upd_date);
        final EditText txtTime = dialog.findViewById(R.id.txt_upd_time);
        final EditText txtRentPrice = dialog.findViewById(R.id.txt_upd_monthly_rent);
        final EditText txtNotes = dialog.findViewById(R.id.txt_upd_notes);
        final EditText txtRepName = dialog.findViewById(R.id.txt_upd_rep_name);
        Button btnUpdate = dialog.findViewById(R.id.btn_update_info);

        //Set DatePicker Listener on EditText
        txtDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog1 = new DatePickerDialog(
                    PropertyListActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year,month,day);
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog1.show();
        });

        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

            String date = month + "/" + day + "/" + year;
            txtDate.setText(date);
        };

        //set TimePicker Listener On EditText

        txtTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(PropertyListActivity.this, (timePicker, hourOfDay, minutes) -> txtTime.setText(hourOfDay + ":" + minutes), 0, 0, false);
            timePickerDialog.show();

        });

        //get data of raw clicked from sqlite
        final Cursor cursor = AddPropertyActivity.mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        mList.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String propType = cursor.getString(1);
            txtPropType.setText(propType);
            String bedrooms = cursor.getString(2);
            txtBedroom.setText(bedrooms);
            String date = cursor.getString(3);
            txtDate.setText(date);
            String time = cursor.getString(4);
            txtTime.setText(time);
            String rentPrice = cursor.getString(5);
            txtRentPrice.setText(rentPrice);
            String furnType = cursor.getString(6);
            txtFurnType.setText(furnType);
            String notes = cursor.getString(7);
            txtNotes.setText(notes);
            String reporterName = cursor.getString(8);
            txtRepName.setText(reporterName);
            byte[] image = cursor.getBlob(9);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));// set image

            mList.add(new Property(id, propType, bedrooms, date, time, rentPrice, furnType, notes, reporterName, image ));

        }

        //set width of dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height of dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels* 0.8);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        btnUpdate.setOnClickListener(view -> {
            try {
                AddPropertyActivity.mSQLiteHelper.updateInfo(
                        txtDate.getText().toString().trim(),
                        txtTime.getText().toString().trim(),
                        txtRentPrice.getText().toString().trim(),
                        txtNotes.getText().toString().trim(),
                        txtRepName.getText().toString().trim(),
                        position
                );
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }
            catch (Exception error) {
                Log.e("Update error", error.getMessage());
            }
            updateRecordList();
        });

    }

    private void showDialogNote(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.note_dialog);
        dialog.setTitle("Add Note");
        final EditText txtAddNote = dialog.findViewById(R.id.txtAddNote);
        final Button btnSave = dialog.findViewById(R.id.btnSave);

        //set width of dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height of dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.4);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        btnSave.setOnClickListener(view -> btnSave.setOnClickListener(v -> {

            try {
                AddPropertyActivity.mSQLiteHelper.insertNote(
                        txtAddNote.getText().toString().trim(),
                        position
                );
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Note added", Toast.LENGTH_SHORT).show();
            }
            catch (Exception error){
                Log.e("Error", error.getMessage());
            }
            updateRecordList();
        }));

    }

    private void showDialogUpdateRating(Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update Property");

        imageViewIcon = dialog.findViewById(R.id.ImageViewRecord);
        final Spinner spnPropType = dialog.findViewById(R.id.spinner_prop_type);
        final EditText txtPropType = dialog.findViewById(R.id.txt_upd_prop_type);
        final EditText txtBedroom = dialog.findViewById(R.id.txt_upd_bedroom);
        final EditText txtRentPrice = dialog.findViewById(R.id.txt_upd_monthly_rent);
        final EditText txtFurnType = dialog.findViewById(R.id.txt_upd_furnType);
        final Spinner spnFurnType = dialog.findViewById(R.id.spinner_furn_type);


        Button btnUpdate = dialog.findViewById(R.id.btn_update);

        // Array adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.propertyTypes, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnPropType.setAdapter(adapter);
        spnPropType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                if (i == 0){
                    //Do nothing
                    txtPropType.setText("");
                }
                else {
                    txtPropType.setText(text);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Array adapter for spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.furnitureTypes, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnFurnType.setAdapter(adapter2);
        spnFurnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                if (i == 0){
                    //Do nothing
                    txtFurnType.setText("");
                }else {
                    txtFurnType.setText(text);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //get data of raw clicked from sqlite
        final Cursor cursor = AddPropertyActivity.mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        mList.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String propType = cursor.getString(1);
            txtPropType.setText(propType);
            String bedrooms = cursor.getString(2);
            String date = cursor.getString(3);
            String time = cursor.getString(4);
            String rentPrice = cursor.getString(5);
            String furnType = cursor.getString(6);
            String notes = cursor.getString(7);
            String reporterName = cursor.getString(8);
            byte[] image = cursor.getBlob(9);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));// set image

            mList.add(new Property(id, propType, bedrooms, date, time, rentPrice, furnType, notes, reporterName, image ));


        }

        //set width of dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height of dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels* 0.9);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        //in update dialog click image view to update image
        imageViewIcon.setOnClickListener(view -> {
            //check external storage permission
            ActivityCompat.requestPermissions(
                    PropertyListActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    888
            );
        });

        btnUpdate.setOnClickListener(view -> {
            try {
                AddPropertyActivity.mSQLiteHelper.updatePropertyData(
                        txtPropType.getText().toString().trim(),
                        txtBedroom.getText().toString().trim(),
                        txtRentPrice.getText().toString().trim(),
                        txtFurnType.getText().toString().trim(),
                        AddPropertyActivity.imageViewToByte(imageViewIcon),
                        position
                );
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }
            catch (Exception error) {
                Log.e("Update error", error.getMessage());
            }
            updateRecordList();
        });

    }


    private void showDialogDelete(final int idRecord) {

        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(PropertyListActivity.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Delete Property?");
        dialogDelete.setPositiveButton("OK", (dialogInterface, i) -> {
            try {
                AddPropertyActivity.mSQLiteHelper.deleteData(idRecord);
                Toast.makeText(PropertyListActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Log.e("error", e.getMessage());
            }
            updateRecordList();
        });

        dialogDelete.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        dialogDelete.show();

    }

    private void updateRecordList() {
        Cursor cursor = AddPropertyActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String propType = cursor.getString(1);
            String bedroom = cursor.getString(2);
            String date = cursor.getString(3);
            String time = cursor.getString(4);
            String rentPrice = cursor.getString(5);
            String furnType = cursor.getString(6);
            String notes = cursor.getString(7);
            String reporter = cursor.getString(8);
            byte[] image = cursor.getBlob(9);

            //add to list

            mList.add(new Property(id, propType, bedroom, date, time, rentPrice, furnType, notes, reporter, image));
        }
        mAdapter.notifyDataSetChanged();
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 888) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
            }
            else {
                Toast.makeText(this, "Don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 888 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                    .setAspectRatio(1,1)// image will be square
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //set image choose from gallery to image view
                imageViewIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}