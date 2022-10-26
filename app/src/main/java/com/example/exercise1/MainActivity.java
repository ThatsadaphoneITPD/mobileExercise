package com.example.exercise1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
    ArrayList<String> imageUrls;
    Button addImage, backImg, nextImg, cameraPic, checkUrl;
    TextView totalImg;
    EditText urlInput;
    //get to Store constant data as global valuable
    int index = 0;

    String URL;
    String InputURL;
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
        totalImg = findViewById(R.id.TotalImg);
        urlInput = findViewById(R.id.urlInput);
        addImage = findViewById(R.id.AddUrl);
        checkUrl = findViewById(R.id.CHeckURL);
        mPicassoBuilder = new Picasso.Builder(this);
        picDB = new PictureDBHelper(this);
        imageUrls = new ArrayList<>();

        // 1.get data for DB cursor
        storeDisPlayDataInArrays();
        //Display image
        if (imageUrls.size() > 0 ){
            displayImage();
        }
        if (index == 0 ){
            backImg.setVisibility(View.GONE);
        }
        if (imageUrls.size() <= 1 ){
            nextImg.setVisibility(View.GONE);
        }
        if (urlInput.length() == 0) {
            addImage.setVisibility(View.GONE);
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
            }
        });
        //add Image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlInput.length() == 0) {
                    ToastCustom("u'r in the big Trouble, Error URL's Empty");
                    addImage.setVisibility(View.GONE);
                } else {AddPicBaseOnImageView();}
            }
        });
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

    void nextPic(){
        index++;
        URL = imageUrls.get(index);
        //Refresh ImageView
        mPicassoBuilder.build().load(URL).resize(900, 900).into(imageView);
        //RefreshSet to hide/unHide button
        if (index > 0) {

            backImg.setVisibility(View.VISIBLE);
            backImg.setText("BACK");
            nextImg.setText("Img "+index + " >>");
        }
        if (index  == imageUrls.size() - 1 ) {
            nextImg.setVisibility(View.GONE);
        }
    }
    void previousPic(){
        index--;
        URL =  imageUrls.get(index);
        //Refresh ImageView
        mPicassoBuilder.build().load(URL).resize(900, 900).into(imageView);
        //RefreshSet to hide/unHide button
        if (index == 0 ) {
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
                    addImage.setText("Add By URL??");
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
    void AddPicBaseOnImageView(){
        InputURL = urlInput.getText().toString().trim();
        picDB.addURL(InputURL);
        totalImg.setText("Total TB Image URL:  "+ imageUrls.size());
        if (imageUrls.size() > 1 ){
            nextImg.setVisibility(View.VISIBLE);
        }
        if (imageUrls.size() >= 1  ){
            backImg.setVisibility(View.VISIBLE);
        }
        if (index == 0|| index == 1 ){
            backImg.setVisibility(View.GONE);
        }
        if (index  == imageUrls.size()-1 ) {
            nextImg.setVisibility(View.GONE);
        }
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
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
                imageUrls.add(cursor.getString(1));
            }
            //The Empty URL list will be fill after DB have data URL
        }
    }

    void displayImage() {
        //app will read initiated URL as String "" then display as None ImageView if Arraylist = 0
        //else if URL's fill will fill would display Imageview
        URL = imageUrls.get(index);
        mPicassoBuilder.build().load(URL).resize(900, 900).into(imageView);

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
}