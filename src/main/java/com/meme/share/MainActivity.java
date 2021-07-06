package com.meme.share;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    String url;
    ImageView image;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMeme();

    }
    public void loadMeme() {
        url = "https://meme-api.herokuapp.com/gimme";
        RequestQueue queue = Volley.newRequestQueue(this);
        TextView t = (TextView) findViewById(R.id.t);
        image = (ImageView) findViewById(R.id.i);
        ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        image.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {


                        t.setText("");

                        try {
                            imageUrl = response.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }                                                    //listener for volley
                        Glide.with(getApplicationContext()).load(imageUrl).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                image.setVisibility(View.VISIBLE);
                                pb.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                image.setVisibility(View.VISIBLE);
                                pb.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }
                        ).into(image);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        t.setText("No internet connection!!!");
                        t.setTextColor(Color.parseColor("#F40B0B"));
                        pb.setVisibility(View.VISIBLE);
                        image.setVisibility(View.INVISIBLE);

                        // image2.setBackgroundResource(R.drawable.loading);


                    }
                });

// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }
    public void shareLink()
    {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,"The meme is:"+imageUrl);
        Intent chooser=Intent.createChooser(sharingIntent,"Share via:");
        startActivity(chooser);

    }
    public void shareImage()
    {
        StrictMode.VmPolicy.Builder bulider=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(bulider.build());
        BitmapDrawable drawable=(BitmapDrawable)image.getDrawable();
        Bitmap bitmap=drawable.getBitmap();
        File f=new File(getExternalCacheDir()+"/"+getResources().getString(R.string.app_name)+".png");
        Intent shareint=null;
        try {
            FileOutputStream outputStream =new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            shareint=new Intent(Intent.ACTION_SEND);
            shareint.setType("image/*");
            shareint.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));
            shareint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
           e.printStackTrace();
        }
        startActivity(Intent.createChooser(shareint,"share image"));
    }

   public void share(View view) {


       AlertDialog.Builder a=new AlertDialog.Builder(MainActivity.this);
       a.setMessage("Share meme in form of")
               .setPositiveButton("Link", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       shareLink();
                   }
               })
               .setNegativeButton("Image", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       shareImage();
                   }
               });
                AlertDialog c=a.create();
                c.show();

    }
    public void next(View view) {

        loadMeme();
    }
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder a=new AlertDialog.Builder(MainActivity.this);
        a.setMessage("Are you sure you want to quit")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No",null);
        AlertDialog c=a.create();
        c.show();
    }

   }
