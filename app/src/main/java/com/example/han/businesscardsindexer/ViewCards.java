package com.example.han.businesscardsindexer;

import android.database.Cursor;
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
        Cursor c = MainActivity.getImages();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            byte[] myBlob = c.getBlob(c.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_IMAGE));
            Bitmap img = BitmapFactory.decodeByteArray(myBlob, 0, myBlob.length);
            BitmapDrawable bd = new BitmapDrawable(getResources(), img);
            ImageView imageView = new ImageView(ViewCards.this);
            imageView.setBackgroundDrawable(bd);
            cardViewLayout.addView(imageView);
            c.moveToNext();
        }
    }
}