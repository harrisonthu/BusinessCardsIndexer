package com.example.han.businesscardsindexer;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_CARD_TEXT = "cardText";
        public static final String COLUMN_IMAGE = "image";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_CARD_TEXT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_IMAGE + " BLOB" + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static String getCreateString() {return SQL_CREATE_ENTRIES;}
    public static String getDeleteString() {return SQL_DELETE_ENTRIES;}
}