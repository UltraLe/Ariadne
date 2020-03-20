package it.icarramba.ariadne.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import it.icarramba.ariadne.control.DBManager;

public class DBTask extends AsyncTask<String, String, String> {

    private WeakReference<Activity> reference;
    private DBManager dbManager;

    public DBTask(Activity activity){
        this.reference = new WeakReference<>(activity);
        this.dbManager = DBManager.getInstance(activity);
    }

    @Override
    protected String doInBackground(String... strings) {
        //this method has to perform db queries

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //May be useful later
        //((MyTextView)(reference.get().findViewById(R.id....))).setText(...);
    }

}
