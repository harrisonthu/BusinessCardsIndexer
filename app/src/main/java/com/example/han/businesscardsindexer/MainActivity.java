package com.example.han.businesscardsindexer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import static com.googlecode.leptonica.android.Rotate.rotate;
import static com.googlecode.leptonica.android.Skew.findSkew;

public class MainActivity extends Activity {

    Context context;
    Button takePicture;
    Button viewCards;
    Button clearDatabase;
    ImageView imageView;
    File image;
    Boolean taken = false;
    protected static final String PHOTO_TAKEN = "photo_taken";
    public static FeedReaderDbHelper mDbHelper;

    //public static final int MEDIA_TYPE_IMAGE = 1;

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
        clearDatabase = (Button) findViewById(R.id.buttonClearDatabase);
        imageView = (ImageView) findViewById(R.id.image_view);
        takePicture.setOnClickListener(new ButtonClickHandler());
        viewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CardDetails.class));

            }
        });
        clearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDatabase();
            }
        });

        //imgPath = getDir("bin", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + "images" + File.separator + "pic.png";


        String folder_main = "tessdata";
        File tmpf = new File(getDir("bin", Context.MODE_PRIVATE).getAbsolutePath(), folder_main);
        if (!tmpf.exists()) {
            tmpf.mkdirs();
        }
        copyAssetFolder(getAssets(), "tessdata", getDir("bin", Context.MODE_PRIVATE).getAbsolutePath() + "/tessdata");
        context = getApplicationContext();
        mDbHelper = new FeedReaderDbHelper(context);
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
        imagePath.mkdirs();
        image = new File(imagePath, "pic.png");
        //image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "pic.png");
        Uri uriSavedImage = Uri.fromFile(image);
        Log.d("image path: ", image.getAbsolutePath());
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, 0);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Picture", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("Picture", "User cancelled");
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }

    protected void onPhotoTaken() {
        taken = true;

        String imgPath = image.getAbsolutePath();

        //get the full size image for processing
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

        //get a 1/4th size image for the preview
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmapPreview = BitmapFactory.decodeFile(imgPath, options);

        imageView.setImageBitmap(bitmapPreview);

        /*
        try {
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
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        */

        TessBaseAPI baseApi = new TessBaseAPI();
        String DATA_PATH = getDir("bin", Context.MODE_PRIVATE).getAbsolutePath();
        baseApi.init(DATA_PATH, "eng", TessBaseAPI.OEM_TESSERACT_ONLY);

        TessBaseAPI baseApi2 = new TessBaseAPI();
        baseApi2.init(DATA_PATH, "eng", TessBaseAPI.OEM_TESSERACT_ONLY);

        //baseApi.setImage(bitmap);

        /*
        Preprocessing:
            - Binarize
            - Detect/Correct skew
         */


        Pix pixs = ReadFile.readBitmap(bitmap);
        //read the file
        //Pix pixs = ReadFile.readFile(image);
        Pix pixForOCR1 = Binarize.otsuAdaptiveThreshold(pixs, 100, 100, 100, 100, 0.1F);
        Log.d("OCR", "preprocessing done for first method");
        Pix pixForOCR2 = GrayQuant.pixThresholdToBinary(pixs, 50);
        Log.d("OCR", "preprocessing done for second method");
        //float skewDeg = -1* Skew.findSkew(pixs);
        //Log.d("skew: ", "" + skewDeg);
        //pixs = rotate(pixs, skewDeg);
        //Pix pixForOCR = Binarize.otsuAdaptiveThreshold(pixs);

        //Pix pixForOCR = GrayQuant.pixThresholdToBinary(pixs, 50);
        baseApi.setImage(pixForOCR1);
        String recognizedText = baseApi.getUTF8Text();
        Log.d("OCR", "OCR done for first method");

        baseApi2.setImage(pixForOCR2);
        String recognizedText2 = baseApi2.getUTF8Text();
        Log.d("OCR", "OCR done for second method");

        baseApi.end();
        TextView myTV = (TextView) findViewById(R.id.textView);
        myTV.setText(recognizedText);
        Log.d("OCR1", recognizedText);
        Log.d("OCR2", recognizedText2);

        // bitmap = camera image
        // String recognizedText = text from image


        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(FeedReaderContract.FeedEntry.COLUMN_CARD_TEXT, recognizedText);
        //storing the 1/4th size image in the database for storege-saving reasons
        values.put(FeedReaderContract.FeedEntry.COLUMN_IMAGE, getBytes(bitmapPreview));

        //values.put("cardText", recognizedText);
        //values.put("image", getBytes(bitmap));
        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        db.close();
    }

    public static void clearDatabase() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(FeedReaderContract.getDeleteString());
        db.execSQL(FeedReaderContract.getCreateString());
        db.close();
    }

    public static Cursor getImageFile() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //SELECT image, FROM contacts
        String queryString = "SELECT " + FeedReaderContract.FeedEntry.COLUMN_IMAGE + " FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;

        Cursor c = db.rawQuery(queryString, null);
        return  c;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

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
            out.flush();
            out.close();
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
