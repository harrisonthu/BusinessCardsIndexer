package com.example.han.businesscardsindexer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.Blob;

/**
 * Created by han on 11/13/16.
 */

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_CARD_TEXT = "cardText";
        public static final String COLUMN_IMAGE = "image";
    }
}