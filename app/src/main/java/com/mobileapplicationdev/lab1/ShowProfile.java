package com.mobileapplicationdev.lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class ShowProfile extends AppCompatActivity {

    private static final String TAG = "Lab1";
    private static final String NAME = "name";
    private static final String MAIL = "mail";
    private static final String BIO = "bio";
    private static final String IMAGE = "image";

    private Uri fileUri;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_profile);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "OnCreate");
    }//End of onCreate

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        //Restore saved values
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        restoreValues(sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.action_edit) {
            Intent editIntent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(editIntent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void restoreValues(SharedPreferences sharedPreferences) {
        String name = sharedPreferences.getString(NAME, null);
        String mail = sharedPreferences.getString(MAIL, null);
        String bio = sharedPreferences.getString(BIO, null);
        String uri = sharedPreferences.getString(IMAGE, null);

        if (name != null) {
            ((TextView) findViewById(R.id.name)).setText(name);
            ((TextView) findViewById(R.id.name)).setTextColor(getResources().getColor(R.color.black));
        }
        if (mail != null) {
            ((TextView) findViewById(R.id.mail)).setText(mail);
            ((TextView) findViewById(R.id.mail)).setTextColor(getResources().getColor(R.color.black));
        }
        if (bio != null) {
            ((TextView) findViewById(R.id.bio)).setText(bio);
            ((TextView) findViewById(R.id.bio)).setTextColor(getResources().getColor(R.color.black));
        }
        if (uri != null) {
            fileUri = Uri.parse(uri);
            setImage(fileUri);
        }
    }

    private void setImage(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            Log.d(TAG, "bitmap null");
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap small = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
            Bitmap rotated = Bitmap.createBitmap(small, 0, 0, small.getWidth(), small.getHeight(), matrix, true);
            ((ImageView) findViewById(R.id.image)).setImageBitmap(rotated);
            Log.d(TAG, "image set");
        }
    }

}
