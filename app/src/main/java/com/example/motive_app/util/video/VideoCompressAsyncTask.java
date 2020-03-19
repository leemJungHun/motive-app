package com.example.motive_app.util.video;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Locale;

public class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

    Context mContext;

    public VideoCompressAsyncTask(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... paths) {
        String filePath = null;
        try {
            Log.d("paths[0]",paths[0]);
            Log.d("paths[1]",paths[1]);
            filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("catch", " "+e.getMessage());
        }
        return  filePath;

    }


    @Override
    protected void onPostExecute(String compressedFilePath) {
        super.onPostExecute(compressedFilePath);
        File imageFile = new File(compressedFilePath);
        float length = imageFile.length() / 1024f; // Size in KB
        String value;
        if(length >= 1024)
            value = length/1024f+" MB";
        else
            value = length+" KB";
        String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", "성공", imageFile.getName(), value);
        Log.d("Silicompressor" , text);
        Log.i("Silicompressor", "Path: "+compressedFilePath);
    }


}