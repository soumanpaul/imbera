package com.imebrademoproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.ReactActivity;
import com.imebra.CodecFactory;
import com.imebra.DataSet;
import com.imebra.DrawBitmap;
import com.imebra.Image;
import com.imebra.Memory;
import com.imebra.PipeStream;
import com.imebra.StreamReader;
import com.imebra.TransformsChain;
import com.imebra.VOILUT;
import com.imebra.VOIs;
import com.imebra.drawBitmapType_t;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class ExampleActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "ImebraDemoProject";
    }
    private  int STORAGE_PERMISSION_CODE =1;
    private ImageView mImageView;
    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
          System.loadLibrary("imebra_lib");
//        System.loadLibrary("imebra_lib");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        mImageView = findViewById(R.id.imageView);
        if (ContextCompat.checkSelfPermission(ExampleActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ExampleActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
            loadDicomFileClicked();
        } else {
            requestStoragePermission();
        }

    }

    public void loadDicomFileClicked() {

        try {


            CodecFactory.setMaximumImageSize(8000, 8000);

            File file = new File(Environment.getExternalStoragePublicDirectory("Kavitha"), "002.dcm");
            //  File file = new File("/storage/emulated/0/Amit/img.dcm");
            Log.d("ImageListDebug", file.getPath());

            file.setReadable(true);
            InputStream stream = new FileInputStream(file);
            PipeStream imebraPipe = new PipeStream(32000);
            Thread pushThread = new Thread(new PushToImebraPipe(imebraPipe, stream));
            pushThread.start();
            DataSet loadDataSet = CodecFactory.load(new StreamReader(imebraPipe.getStreamInput()));
            Image dicomImage = loadDataSet.getImageApplyModalityTransform(0);
            TransformsChain chain = new TransformsChain();
            VOIs vois = loadDataSet.getVOIs(); // Get a list of contrast settings
            if(!vois.isEmpty()) {
                chain.addTransform(new VOILUT(vois.get(0))); // Use the contrast settings when rendering the image
            }
            DrawBitmap drawBitmap = new DrawBitmap(chain);
            Memory memory = drawBitmap.getBitmap(dicomImage, drawBitmapType_t.drawBitmapRGBA, 4);

            Bitmap renderBitmap = Bitmap.createBitmap( (int)dicomImage.getWidth(), (int)dicomImage.getHeight(), Bitmap.Config.ARGB_8888);
            byte[] memoryByte = new byte[(int)memory.size()];
            memory.data(memoryByte);
            ByteBuffer byteBuffer = ByteBuffer.wrap(memoryByte);
            renderBitmap.copyPixelsFromBuffer(byteBuffer);
            mImageView.setImageBitmap(renderBitmap);
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        catch(IOException e) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage(e.getMessage());
            dlgAlert.setTitle("Error");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                } } );
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            String test = "Test";
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to access the File")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ExampleActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                loadDicomFileClicked();
            } else {
                requestStoragePermission();
            }
        }
    }
}
