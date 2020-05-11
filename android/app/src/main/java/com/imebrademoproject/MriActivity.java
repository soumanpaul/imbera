package com.imebrademoproject;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.ReactActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MriActivity extends ReactActivity {
    ImageView viewimg;
    List<String> datalist = new ArrayList<String>();
    @Override
    protected String getMainComponentName() {
        return "ImebraDemoProject";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.loadLibrary("imebra_lib");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mri);
        viewimg = (ImageView)findViewById(R.id.imgviewer) ;

        loadDcimImages();


    }
    private void loadDcimImages() {
        List<String> dcimList = new ArrayList<String>();

        File sdcardPath = new File(Environment.getExternalStoragePublicDirectory("Kavitha").getPath());
        Log.d("ImageListDebug", sdcardPath.getPath());

        int imageCount = sdcardPath.listFiles().length;
        for (int count = 0; count <= imageCount - 1; count++) {

            String imagePath = sdcardPath.listFiles()[count].getAbsolutePath();
            Log.d("ImageListDebug", "ImagePath[" + count + "]: " + imagePath);
            dcimList.add(imagePath);

            String[] iname = imagePath.split("/");
            datalist.add(iname[iname.length - 1]);

            Log.d("TAG", "name_solit     " + iname[iname.length - 1]);

        }
        Log.d("TAG", "name_solit    lis " +datalist);
        // need to create the logic to display the image in grid view

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false){
            public boolean canScrollHorizontally() {
                return false;
            }

        };
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerAdapter adapter = new RecyclerAdapter(this, datalist, viewimg);

        recyclerView.setAdapter(adapter);




        recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollX; int scrollY;
                scrollX=recyclerView.getScrollX();
                scrollY=recyclerView.getScrollY();
                Log.d("TAG","newxxxxxxxxxxxxxxxxxxx  "+scrollX);
            }
        });
    }
}

