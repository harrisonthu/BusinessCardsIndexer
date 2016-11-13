package com.example.han.businesscardsindexer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewCards extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cards);

        LinearLayout cardViewLayout = (LinearLayout) findViewById(R.id.cardViewLayout);
        byte[][] cardPics = getImageFile();
        for (int i = 0; i < cardPics.length; i++) {
            Bitmap img = BitmapFactory.decodeByteArray(cardPics[i], 0, cardPics[i].length);
            BitmapDrawable bd = new BitmapDrawable(getResources(), img);
            ImageView imageView = new ImageView(ViewCards.this);
            imageView.setBackgroundDrawable(bd);
            cardViewLayout.addView(imageView);
        }
    }

//    Bitmap getImageFile()
}
