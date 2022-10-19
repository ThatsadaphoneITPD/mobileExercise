package com.example.exercise1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    ArrayList<Bitmap> imageUrls;
    Button addImage, backImg, nextImg, cameraPic, checkUrl;
    TextView totalImg;
    EditText urlInput;
    //get to Store constant data as global valuable
    int index = 0;
    Bitmap theImg = null, convertByteToBitmap;
    String URL;

    //this C_R_C for result camera
    private static final int CAMERA_REQ_CODE = 100;
    //Library tool converter
    Picasso.Builder mPicassoBuilder;
    //use DBHelper
    PictureDBHelper picDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        backImg = findViewById(R.id.backImage);
        nextImg = findViewById(R.id.nextImage);
        imageView = findViewById(R.id.myImageView);
        urlInput = findViewById(R.id.urlInput);
        addImage = findViewById(R.id.AddUrl);
        checkUrl = findViewById(R.id.CHeckURL);
        cameraPic = findViewById(R.id.Camera);
        totalImg = findViewById(R.id.TotalImg);
        mPicassoBuilder = new Picasso.Builder(this);
        picDB = new PictureDBHelper(this);
        imageUrls = new ArrayList<>();
        // 1.get data for DB cursor
        storeDisPlayDataInArrays();
        //4. display img by byte to bitmap from DB
        displayImage();




        if (index == 0 ){
            backImg.setVisibility(View.GONE);
        }
        if (imageUrls.size() <= 1 ){
            nextImg.setVisibility(View.GONE);
        }

        totalImg.setText("Total TB Image URL:  "+ imageUrls.size());
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousPic();
            }
        });
        nextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPic();
            }
        });
        //Check URL
        checkUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPicFromURL();
                addImage.setText("Add By URL??");
            }
        });
        //add Image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPicBaseOnImageView();
            }
        });
        cameraPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prepare permission
                Intent lCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(lCamera, CAMERA_REQ_CODE);
            }
        });

    }
    //Refresh Data when get image after Camera take picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when capture then will put upload
        if(resultCode == RESULT_OK ) {
            if (requestCode == CAMERA_REQ_CODE) {
                //for camera result data by Bitmap
                Bundle extra = data.getExtras();
                theImg = (Bitmap) extra.get("data");
                //Store img in imageview to Preview
                imageView.setImageBitmap(theImg);
                //send Extra of camera input data
                addImage.setText("Add Pic from Camera?");
            }
        }
    }
    //Create each option menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //this method will select to trigger each menu action in List menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //select case in menu
        if(item.getItemId() == R.id.delete_all){
            confirmDialog("Delete ALl Images Table?", true);
        }
        return super.onOptionsItemSelected(item);
    }
    void confirmDialog(String title, boolean deleteALl){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( title);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (deleteALl == true){
                    //then Message delete done
                    Toast.makeText(MainActivity.this, "Delete All", Toast.LENGTH_SHORT).show();
                    PictureDBHelper IDB = new PictureDBHelper(MainActivity.this);
                    IDB.deleteImages();
                    //Refresh Activity
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //this will not execute any action
            }
        });
        builder.create().show();
    }
    void AddPicBaseOnImageView(){
        theImg = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        //save image bitmap to DB BLOB
        byte [] inputByte = Utils.getBytes(theImg);
        //convert to byte array for BLOB type
        picDB.addBitmap(inputByte);
        //reset input bitmap after add to db
        theImg = null;
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);

    }
    void storeDisPlayDataInArrays() {
        Cursor cursor = picDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            //The URL list will be "" empty
        } else {
            //Map or read out all data from cursor to MainActivity
            while (cursor.moveToNext()) {
                //convert byte from cursor to bitmap obj
                imageUrls.add(Utils.getImage(cursor.getBlob(1)));
            }
            //The Empty URL list will be fill after DB have data URL
            convertByteToBitmap = imageUrls.get(index);
        }
    }
    void displayImage() {
        //app will read initiated URL as String "" then display as None ImageView if Arraylist = 0
        //else if URL's fill will fill would display Imageview
        imageView.setImageBitmap(convertByteToBitmap);

    }
    void nextPic(){
        index++;
        convertByteToBitmap = imageUrls.get(index);
        //Refresh ImageView
        imageView.setImageBitmap(convertByteToBitmap);
        //RefreshSet to hide/unHide button
        if (index > 0) {

            backImg.setVisibility(View.VISIBLE);
            backImg.setText("BACK");
            nextImg.setText("Img "+index + " >>");
        }
        if (index  == imageUrls.size()-1 ) {
            nextImg.setVisibility(View.GONE);
        }
    }
    void previousPic(){
        index--;
        convertByteToBitmap = imageUrls.get(index);
        //Refresh ImageView
        imageView.setImageBitmap(convertByteToBitmap);
        //RefreshSet to hide/unHide button
        if (index == 0 || index == 1) {
            backImg.setVisibility(View.GONE);
        }
        if (index < imageUrls.size()){
            nextImg.setVisibility(View.VISIBLE);
            nextImg.setText("NEXT");
            backImg.setText("<< "+index + " Img");
        }
    }

    void  CheckPicFromURL(){
        if (urlInput.length() == 0) {
            ToastCustom("u'r in the big Trouble, Error URL's Empty");
            addImage.setVisibility(View.GONE);
        } else {
            URL = urlInput.getText().toString().trim();
            mPicassoBuilder.build().load(URL).resize(900, 900).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // loaded bitmap is here (bitmap)
                    imageView.setImageBitmap(bitmap);
                    addImage.setVisibility(View.VISIBLE);
                }
                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    //check URl if URL is invalid
                    ToastCustom("Issue Error URL is invalid");
                    addImage.setVisibility(View.GONE);
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            });

        }
    }

    @SuppressLint("ShowToast")
    public void ToastCustom(String textMessage) {
        Toast toast = Toast.makeText(this, textMessage, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView text = view.findViewById(android.R.id.message);
        text.setTextSize(30);
        text.setTextColor(Color.WHITE);
        //toast.setGravity(Gravity.CENTER, 0, 200);
        //toast.setGravity(Gravity.TOP, 0, 200);
        view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        //Then Pop up
        toast.show();
    }
}