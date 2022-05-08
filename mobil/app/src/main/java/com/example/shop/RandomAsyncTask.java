package com.example.shop;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class RandomAsyncTask extends AsyncTask<Void, Void, String> {
    private WeakReference<TextView> fTextView;

    public RandomAsyncTask(TextView textView) {
        fTextView = new WeakReference<>(textView);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Random random = new Random();
        int number = random.nextInt(15);
        int ms = number*700;
        try{
            Thread.sleep(ms);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Login as guest, after " + ms + " ms";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        fTextView.get().setText(s);
    }
}
