package it.icarramba.ariadne.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class DBTask extends AsyncTask<String, String, String> {

    private WeakReference<Activity> reference;

    public DBTask(Activity activity){
        this.reference = new WeakReference<>(activity);
    }

    @Override
    protected String doInBackground(String... strings) {

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //May be useful later
        //((MyTextView)(reference.get().findViewById(R.id....))).setText(...);
    }
}
