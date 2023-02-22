package com.example.rentalz;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rentalz.Backend.SQLiteHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddPropertyActivity extends AppCompatActivity {
    public static final String DATABASE_NAME;
    public static final String TABLE_NAME;

    static {
        TABLE_NAME = "RECORD";
        DATABASE_NAME = "RECORDDB.sqlite";
    }
    ImageView mImageView;
    private Spinner spnPropType, spnFurnType;
    private EditText txtBedrooms,txtPropType, txtFurnType, txtEntryDate, txtEntryTime, txtRentPrice, txtNotes, txtRepName;

    private Button btnCreate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "";
    private ProgressDialog loadingBar;
    private Uri imageUri;
    private TextView lblClose;

    final int REQUEST_CODE_GALLERY = 999;
    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mImageView = findViewById(R.id.add_prop_image);
        spnPropType = findViewById(R.id.spinner_prop_type);
        txtPropType = findViewById(R.id.txt_prop_type);
        txtBedrooms = findViewById(R.id.txt_prop_bedrooms);
        txtEntryDate = findViewById(R.id.txt_pick_date);
        txtEntryTime = findViewById(R.id.txt_pick_time);
        txtRentPrice = findViewById(R.id.txt_rent_price);
        spnFurnType = findViewById(R.id.spinner_furn_type);
        txtFurnType = findViewById(R.id.txt_furn_type);
        txtNotes = findViewById(R.id.txt_notes);
        txtRepName = findViewById(R.id.txt_rep_name);
        loadingBar = new ProgressDialog(this);
        btnCreate = findViewById(R.id.btn_create);
        lblClose = findViewById(R.id.lbl_close_add_property);

        lblClose.setOnClickListener((View view) -> finish());

        mImageView.setOnClickListener((View view) -> {
            //read external permission to select image from gallery
            //runtime permission for android 6.0 and above
            ActivityCompat.requestPermissions(
                    AddPropertyActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_GALLERY
            );
        });

        //creating database

        mSQLiteHelper = new SQLiteHelper(this, DATABASE_NAME, null, 1);

        //creating table in database

        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, propType VARCHAR, bedrooms VARCHAR, date VARCHAR, time VARCHAR, rentPrice VARCHAR, furnType VARCHAR, notes VARCHAR, addNote VARCHAR, reporter VARCHAR, image BLOB)");

        btnCreate.setOnClickListener((View view) -> validateData());
        dateTimePicker();
        SpinnerAdapters();
    }

    private void validateData()
    {
        String propType = spnPropType.getSelectedItem().toString();
        String propBedrooms = txtBedrooms.getText().toString();
        String entryDate = txtEntryDate.getText().toString();
        String entryTime = txtEntryTime.getText().toString();
        String rentPrice = txtRentPrice.getText().toString();
        String repName = txtRepName.getText().toString();

        if ( imageUri == null){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(propType))
        {
            Toast.makeText(this, "Please Select Property Type", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if (TextUtils.isEmpty(propBedrooms))
        {
            Toast.makeText(this, "Please Enter Bedroom Type", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if(TextUtils.isEmpty(entryDate))
        {
            Toast.makeText(this, "Please enter date", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if(TextUtils.isEmpty(entryTime))
        {
            Toast.makeText(this, "Please enter time", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if(TextUtils.isEmpty(rentPrice))
        {
            Toast.makeText(this, "Please enter monthly rent price", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if(TextUtils.isEmpty(repName))
        {
            Toast.makeText(this, "Please enter reporter name", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else
        {
            confirmData();
        }
    }

    private void confirmData() {
        final AlertDialog dialog = new AlertDialog.Builder(AddPropertyActivity.this)
                .setTitle("Confirm")
                .setMessage("Are You Sure?"+"\n" +"Property type: " +txtPropType.getText()+ "\n" +"Bedrooms: "
                        +txtBedrooms.getText() +"\n" +"Date: " +txtEntryDate.getText() +"\n" +"Time: "
                        +txtEntryTime.getText() +"\n" +"Monthly Rent Price: " +txtRentPrice.getText() +"\n"
                        +"Furniture Type: " +txtFurnType.getText() +"\n"
                        +"Notes: " +txtNotes.getText() +"\n" +"Reporter Name: " +txtRepName.getText() +"\n" )
                .setPositiveButton("ok", null)
                .setNegativeButton("cancel", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener((View view)->{
            try {
                mSQLiteHelper.insertData(
                        txtPropType.getText().toString().trim(),
                        txtBedrooms.getText().toString().trim(),
                        txtEntryDate.getText().toString().trim(),
                        txtEntryTime.getText().toString().trim(),
                        txtRentPrice.getText().toString().trim(),
                        txtFurnType.getText().toString().trim(),
                        txtNotes.getText().toString().trim(),
                        txtRepName.getText().toString().trim(),
                        imageViewToByte(mImageView));
                Toast.makeText(this, "Property Added Successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                e.printStackTrace();
            }
            dialog.dismiss();
            Intent intent = new Intent(AddPropertyActivity.this, PropertyListActivity.class);
            startActivity(intent);
        });
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void dateTimePicker() {

        //Set DatePicker Listener on EditText

        txtEntryDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    AddPropertyActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year,month,day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        });

        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

            String date = month + "/" + day + "/" + year;
            txtEntryDate.setText(date);
        };

        //set TimePicker Listener On EditText

        txtEntryTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddPropertyActivity.this, (timePicker, hourOfDay, minutes) -> txtEntryTime.setText(hourOfDay + ":" + minutes), 0, 0, false);
            timePickerDialog.show();

        });
    }

    private void SpinnerAdapters() {
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
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
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            imageUri = data.getData();
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
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}