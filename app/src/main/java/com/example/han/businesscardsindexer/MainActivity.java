package com.example.han.businesscardsindexer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    //public static final int MEDIA_TYPE_IMAGE = 1;

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick( View view ){
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
        takePicture.setOnClickListener( new ButtonClickHandler() );
        viewCards.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             startActivity(new Intent(MainActivity.this, CardDetails.class));
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

    }

    protected void startCameraActivity() {
        /*
        File file = new File( imgPath );
        Uri outputFileUri = Uri.fromFile( file );

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

        startActivityForResult( intent, 0 );
*/
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
        Log.i( "MakeMachine", "resultCode: " + resultCode );
        switch( resultCode ) {
            case 0:
                Log.i( "MakeMachine", "User cancelled" );
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }

    protected void onPhotoTaken()
    {
        taken = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        String imgPath = image.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile( imgPath, options );
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


        ///bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        TessBaseAPI baseApi = new TessBaseAPI();
        String DATA_PATH = getDir("bin", Context.MODE_PRIVATE).getAbsolutePath();
        baseApi.init(DATA_PATH, "eng");

        //baseApi.setImage(bitmap);

        Pix pixs = ReadFile.readBitmap(bitmap);

        //float skewDeg = -1* Skew.findSkew(pixs);
        //Log.d("skew: ", "" + skewDeg);
        //pixs = rotate(pixs, skewDeg);
        //Pix pixForOCR = Binarize.otsuAdaptiveThreshold(pixs);
        Pix pixForOCR = Binarize.otsuAdaptiveThreshold(pixs, 100, 100, 100, 100, 0.0F);
        //Pix pixForOCR = GrayQuant.pixThresholdToBinary(pixs, 50);
        baseApi.setImage(pixForOCR);
        //baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        TextView myTV = (TextView)findViewById(R.id.textView);
        myTV.setText(recognizedText);


        // bitmap = camera image
        // String recognizedText = text from image




        /*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile( imgPath, options );
        imageView.setImageBitmap(bitmap);
        */
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
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putBoolean( MainActivity.PHOTO_TAKEN, taken );
    }
    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState)
    {
        Log.i( "MakeMachine", "onRestoreInstanceState()");
        if( savedInstanceState.getBoolean( MainActivity.PHOTO_TAKEN ) ) {
            onPhotoTaken();
        }
    }
}
