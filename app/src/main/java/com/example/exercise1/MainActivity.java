package com.example.exercise1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    //Library tool converter
    Picasso.Builder mPicassoBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backImg = findViewById(R.id.backImage);
        nextImg = findViewById(R.id.nextImage);
        imageView = findViewById(R.id.myImageView);
        totalImg = findViewById(R.id.TotalImg);
        mPicassoBuilder = new Picasso.Builder(this);
        imageUrls = new ArrayList<>();

        imageUrls.add("https://res.cloudinary.com/dp3zeejct/image/upload/v1664004661/Payment/minGun_p7ur9t.jpg");
        imageUrls.add("https://res.cloudinary.com/dp3zeejct/image/upload/v1664004660/Payment/Mother_Earth_Day-bro_krr996.png");
        imageUrls.add("https://res.cloudinary.com/dp3zeejct/image/upload/v1664004659/Payment/Gundamwitch_apoi1b.jpg");
        imageUrls.add("https://res.cloudinary.com/dp3zeejct/image/upload/v1663144442/Payment/CiwCwTaVEAA2tXX_4_rybbh5.jpg");
        //Display image
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
    }

    void displayImage() {
        //app will read initiated URL as String "" then display as None ImageView if Arraylist = 0
        //else if URL's fill will fill would display Imageview
//        imageView.setImageURI();
        URL = imageUrls.get(index);
        mPicassoBuilder.build().load(URL).resize(900, 900).into(imageView);

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
        if (index  == imageUrls.size()-1 ) {
            nextImg.setVisibility(View.GONE);
        }
    }
    void previousPic(){
        index--;
        URL =  imageUrls.get(index);
        //Refresh ImageView
        mPicassoBuilder.build().load(URL).resize(900, 900).into(imageView);
        //RefreshSet to hide/unHide button
        if (index == 0) {
            backImg.setVisibility(View.GONE);
        }
        if (index < imageUrls.size()){
            nextImg.setVisibility(View.VISIBLE);
            nextImg.setText("NEXT");
            backImg.setText("<< "+index + " Img");
        }
    }
}