package com.imebrademoproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
    List<String> listData;
    Bitmap bitmap;
    ImageView mImageView;
    int mSize;
    float width;


    public RecyclerAdapter(Context context, List<String> listData, ImageView aImageView) {
        this.context = context;
        this.listData = listData;
        mImageView = aImageView;


        mSize= Resources.getSystem().getDisplayMetrics().widthPixels;
        Log.d("TAG","Width_pix  "+mSize);
        int mSizehah= Resources.getSystem().getDisplayMetrics().heightPixels;
        Log.d("TAG","HEight_pix  "+mSizehah);
    }





    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview ,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);
        ViewGroup.LayoutParams layoutParams = view.findViewById(R.id.cardview).getLayoutParams();

        layoutParams.width =  mSize/listData.size();
        Log.d("TAG","zze "+width);

        return viewHolder;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String imgname = listData.get(position);
        Log.d("TAG","image_NAme"+imgname);

        try {
            // CodecFactory.setMaximumImageSize(8000, 8000);

            File file = new File(Environment.getExternalStoragePublicDirectory("Kavitha"), imgname);


            Log.d("TAG", "file name ooo " + file);
            Log.d("ImageListDebug", file.getPath());

            file.setReadable(true);
            if(file.exists())
            {
                Log.d("TAG","file exists..................................1111");

                InputStream stream = new FileInputStream(file);
                PipeStream imebraPipe = new PipeStream(32000);
                Thread pushThread = new Thread(new PushToImebraPipe(imebraPipe, stream));
                pushThread.start();
                DataSet loadDataSet = CodecFactory.load(new StreamReader(imebraPipe.getStreamInput()));
                Image dicomImage = loadDataSet.getImageApplyModalityTransform(0);
                TransformsChain chain = new TransformsChain();
                VOIs vois = loadDataSet.getVOIs(); // Get a list of contrast settings
                if (!vois.isEmpty()) {
                    chain.addTransform(new VOILUT(vois.get(0))); // Use the contrast settings when rendering the image
                }
                DrawBitmap drawBitmap = new DrawBitmap(chain);
                Memory memory = drawBitmap.getBitmap(dicomImage, drawBitmapType_t.drawBitmapRGBA, 4);

                final Bitmap renderBitmap = Bitmap.createBitmap((int) dicomImage.getWidth(), (int) dicomImage.getHeight(), Bitmap.Config.ARGB_8888);
                byte[] memoryByte = new byte[(int) memory.size()];
                memory.data(memoryByte);
                ByteBuffer byteBuffer = ByteBuffer.wrap(memoryByte);
                renderBitmap.copyPixelsFromBuffer(byteBuffer);
                bitmap = renderBitmap;
                Log.d("TAG","rend_image"+renderBitmap);

                Glide.with(context)
                        .asBitmap()
                        .load(renderBitmap)
                        .into(holder.image);

                viewImage(position, imgname);

                Log.d("TAG","nameeeee_image"+imgname);



                holder.image.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        float horizontalOffset = event.getRawX();
                        Log.d("TAG","evrnt_kkeke  " + horizontalOffset);
                        width = horizontalOffset/(mSize/listData.size());
                        Log.d("TAG","widdddd "+width);
                        int  ipos = Math.round(width);
                        Log.d("TAG","246810  "+ ipos);
                        //  Log.d("TAG","987654  "+ listData.get(i));// viewImage(position,listData.get(i));
                        event.setAction(7);
                        if(event.getAction() == MotionEvent.ACTION_HOVER_MOVE && ipos<listData.size()){
                            Log.d("TAG","13579 "+position+listData.get(ipos));
                            viewImage(position,listData.get(ipos));
                            return true;
                        }
                        return false;
                    }
                });
            }else
                Log.d("TAG","file doesn't exists. . . . . .2222");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.d("TAG","size .. "+listData.size());
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;


        public  ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img);

        }
    }

    public void viewImage(int apos,String name) {
        File file = new File(Environment.getExternalStoragePublicDirectory("Kavitha"), name);
        Log.d("TAG", "file name ooo " + file);

        file.setReadable(true);
        if (file.exists()) {
            Log.d("TAG", "file exists..................................1111");

            InputStream stream = null;
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            PipeStream imebraPipe = new PipeStream(32000);
            Thread pushThread = new Thread(new PushToImebraPipe(imebraPipe, stream));
            pushThread.start();
            DataSet loadDataSet = CodecFactory.load(new StreamReader(imebraPipe.getStreamInput()));
            Image dicomImage = loadDataSet.getImageApplyModalityTransform(0);
            TransformsChain chain = new TransformsChain();
            VOIs vois = loadDataSet.getVOIs(); // Get a list of contrast settings
            if (!vois.isEmpty()) {
                chain.addTransform(new VOILUT(vois.get(0))); // Use the contrast settings when rendering the image
            }
            DrawBitmap drawBitmap = new DrawBitmap(chain);
            Memory memory = drawBitmap.getBitmap(dicomImage, drawBitmapType_t.drawBitmapRGBA, 4);

            final Bitmap renderBitmap = Bitmap.createBitmap((int) dicomImage.getWidth(), (int) dicomImage.getHeight(), Bitmap.Config.ARGB_8888);
            byte[] memoryByte = new byte[(int) memory.size()];
            memory.data(memoryByte);
            ByteBuffer byteBuffer = ByteBuffer.wrap(memoryByte);
            renderBitmap.copyPixelsFromBuffer(byteBuffer);
            bitmap = renderBitmap;
            Log.d("TAG", "rend_image" + renderBitmap);
            mImageView.setImageBitmap(renderBitmap);
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

}
