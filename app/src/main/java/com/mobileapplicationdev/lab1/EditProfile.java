package com.mobileapplicationdev.lab1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfile extends Activity {

    private static final String TAG = "Lab1";
    private static final String NAME = "name";
    private static final String MAIL = "mail";
    private static final String BIO = "bio";
    private static final String IMAGE = "image";
    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;

    private Uri fileUri;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Launching");
        setContentView(R.layout.content_edit_profile);

        //Restore saved values
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        restoreValues(sharedPreferences);

        //Add a listener on the button to take a photo
        Button takePictureButton = (Button) findViewById(R.id.take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            private Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick");
                fileUri = getOutputMediaFileUri(); //Create a file to store the photo
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //Set the image file name
                startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE); //Launch the camera app
            }
        }); //End of the listener

        //Add a listener on the button to choose a picture
        Button choosePictureButton = (Button) findViewById(R.id.choose_picture);
        choosePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePictureIntent.setType("image/*");
                startActivityForResult(choosePictureIntent, CHOOSE_PICTURE_REQUEST_CODE);
            }
        }); //End of listener

        //Add a listener to the "Save" button
        Button switchButton = (Button) findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Saving");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(NAME, ((EditText)findViewById(R.id.name)).getText().toString());
                editor.putString(MAIL, ((EditText)findViewById(R.id.mail)).getText().toString());
                editor.putString(BIO, ((EditText) findViewById(R.id.bio)).getText().toString());
                if (fileUri != null)
                    editor.putString(IMAGE, fileUri.toString());
                editor.commit();
                Log.d(TAG, "Saved");
                finish();
            }
        });//End of listener
        Log.d(TAG, "onCreate finished");
    } //End of onCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if(resultCode == RESULT_OK) {
            Log.d(TAG, "result_ok");
            if (requestCode == CHOOSE_PICTURE_REQUEST_CODE) {
                fileUri = data.getData();
            }
            setImage(fileUri);
        } else if(resultCode == RESULT_CANCELED) {
            Log.d(TAG, "result canceled");
        }

    }

    private void setImage(Uri uri) {
        Log.d(TAG, "uri = " + uri);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null)
            Log.d(TAG, "bitmap null");
        else {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap small = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
            Bitmap rotated = Bitmap.createBitmap(small, 0, 0, small.getWidth(), small.getHeight(), matrix, true);
            ((ImageView) findViewById(R.id.image)).setImageBitmap(rotated);
            Log.d(TAG, "image set");
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Lab1");

        if(!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG" + timeStamp + ".jpg");
        return mediaFile;
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putString(NAME, ((EditText) findViewById(R.id.name)).getText().toString());
        b.putString(MAIL, ((EditText) findViewById(R.id.mail)).getText().toString());
        b.putString(BIO, ((EditText) findViewById(R.id.bio)).getText().toString());
        if (fileUri != null) {
            b.putString(IMAGE, fileUri.toString());
        } else {
            b.putString(IMAGE, null);
        }
        Log.d(TAG, "Values saved");
    }

    @Override
    public void onRestoreInstanceState(Bundle b) {
        ((EditText)findViewById(R.id.name)).setText(b.get(NAME).toString());
        ((EditText)findViewById(R.id.mail)).setText(b.get(MAIL).toString());
        ((EditText)findViewById(R.id.bio)).setText(b.get(BIO).toString());
        Log.d(TAG, "Fields restored");
        Object obj = b.get(IMAGE);
        if (obj != null) {
            fileUri = Uri.parse(obj.toString());
            Log.d(TAG, "uri restored = " + fileUri);
            setImage(fileUri);
        }
    }

    public void restoreValues(SharedPreferences sharedPreferences) {
        //Restore saved values
        Log.d(TAG, "preferences file found");
        String name = sharedPreferences.getString(NAME, null);
        String mail = sharedPreferences.getString(MAIL, null);
        String bio = sharedPreferences.getString(BIO, null);
        String uri = sharedPreferences.getString(IMAGE, null);
        Log.d(TAG, "preferences file read");

        if (name != null)
            ((EditText)findViewById(R.id.name)).setText(name);
        if (mail != null)
            ((EditText)findViewById(R.id.mail)).setText(mail);
        if (bio != null)
            ((EditText)findViewById(R.id.bio)).setText(bio);
        if (uri != null & fileUri == null) {
            fileUri = Uri.parse(uri);
            setImage(fileUri);
        }
        Log.d(TAG, "values set");
    }
}
