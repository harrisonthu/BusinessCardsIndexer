package com.example.han.businesscardsindexer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.googlecode.leptonica.android.Rotate.rotate;
import static com.googlecode.leptonica.android.Skew.findSkew;

public class MainActivity extends Activity {
    /*
        public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
        public static final String ALLOW_KEY = "ALLOWED";
        public static final String CAMERA_PREF = "camera_pref";
    */
    Context context;
    Button takePicture;
    Button viewCards;
    ImageView imageView;
    //File imgPath;
    //File imagesFolder;
    File image;
    Boolean taken = false;
    protected static final String PHOTO_TAKEN = "photo_taken";
    public FeedReaderDbHelper mDbHelper;
    public static final int MEDIA_TYPE_IMAGE = 1;

    //DBHelper mydb;

    FeedReaderContract frc;
    //FeedReaderContract.FeedReaderDbHelper frdbh = new FeedReaderDbHelper();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            startCameraActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the elements
        takePicture = (Button) findViewById(R.id.buttonTakePicture);
        viewCards = (Button) findViewById(R.id.buttonViewCards);
        imageView = (ImageView) findViewById(R.id.image_view);
        takePicture.setOnClickListener(new ButtonClickHandler());
        viewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, CardDetails.class));

            }
        });
        /*
        String folder_main = "images";
        File tmpf = new File(getDir("bin", Context.MODE_PRIVATE).getAbsolutePath(), folder_main);
        if (!tmpf.exists()) {
            tmpf.mkdirs();
        }

        imgPath = getDir("bin", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + "images" + File.separator + "pic.png";
        */
        context = getApplicationContext();
        String folder_main = "tessdata";
        File tmpf = new File(getDir("bin", Context.MODE_PRIVATE).getAbsolutePath(), folder_main);
        if (!tmpf.exists()) {
            tmpf.mkdirs();
        }
        folder_main = "eng";
        tmpf = new File(getDir("bin", Context.MODE_PRIVATE).getAbsolutePath(), folder_main);
        if (!tmpf.exists()) {
            tmpf.mkdirs();
        }
        //copyAssets();
        copyAssetFolder(getAssets(), "tessdata", getDir("bin", Context.MODE_PRIVATE).getAbsolutePath() + "/tessdata");

        // initialize database
        //mydb = new DBHelper(this);
        //frc = new FeedReaderContract();

        mDbHelper = new FeedReaderDbHelper(context);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void startCameraActivity() {
        /*
        File file = new File( imgPath );
        Uri outputFileUri = Uri.fromFile( file );

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

        startActivityForResult( intent, 0 );
*/
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        //imagesFolder = new File(context.getFilesDir(), "images");
        //imagesFolder.mkdirs();
        File imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        image = new File(imagePath, "pic.png");
        imagePath.mkdirs();
        //image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "pic.png");
        Uri uriSavedImage = Uri.fromFile(image);
        Log.d("image path: ", image.getAbsolutePath());
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, 0);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }

    protected void onPhotoTaken() {
        taken = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        String imgPath = image.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        imageView.setImageBitmap(bitmap);

        /*try {
            ExifInterface exif = new ExifInterface(imgPath);

            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate != 0) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                Log.d("rotation: ", "" + rotate);
                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }
        }
        catch (Exception e) {
            Log.e("exception: ", e.getMessage());
        }*/
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        TessBaseAPI baseApi = new TessBaseAPI();
        String DATA_PATH = getDir("bin", Context.MODE_PRIVATE).getAbsolutePath();
        baseApi.init(DATA_PATH, "eng");

        //baseApi.setImage(bitmap);

        Pix pixs = ReadFile.readBitmap(bitmap);
        //float skewDeg = -1* Skew.findSkew(pixs);
        //Log.d("skew: ", "" + skewDeg);
        //pixs = rotate(pixs, skewDeg);
        //Pix pixForOCR = Binarize.otsuAdaptiveThreshold(pixs);
        Pix pixForOCR = GrayQuant.pixThresholdToBinary(pixs, 50);
        baseApi.setImage(pixForOCR);

        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        TextView myTV = (TextView) findViewById(R.id.textView);
        myTV.setText(recognizedText);



        /*mydb.insertText(recognizedText,getBytes(bitmap));
        ArrayList<String> contacts = mydb.getAllContacts();
        String s = "";
        for (String contact : contacts) {
            s = s + contact;
        }
        Log.d("This is my print out",s);
*/
        // bitmap = camera image
        // String recognizedText = text from image


        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_CARD_TEXT, recognizedText);
        values.put(FeedReaderContract.FeedEntry.COLUMN_IMAGE, getBytes(bitmap));

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);


        /*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile( imgPath, options );
        imageView.setImageBitmap(bitmap);
        */

    }

    public Cursor getImageFile() {


        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_IMAGE
        };

// Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_CARD_TEXT;

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_IMAGE;

        Cursor c = db.rawQuery("select image from contacts", null);


        return  c;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    /*
    private void copyAssets() {
        Log.d("tag", "starting copy files");
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("eng");
            Log.d("info", "Length of files = " + files.length);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("eng/" + filename);
                File outFile = new File(Environment.getDataDirectory().getAbsolutePath() + "/eng", filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                Log.d("copied file", filename);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }*/


    private static boolean copyAssetFolder(AssetManager assetManager,
                                           String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            Log.d("assets", "num files = " + files.length);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            Log.d("assetcp: ", toPath);
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MainActivity.PHOTO_TAKEN, taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }
}
