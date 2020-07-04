package it.icarramba.ariadne.tasks;

import android.app.Activity;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.bean.DBQueryBean;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;

//TODO check if used later, otherwise delete it
public class DBTask extends AsyncTask<DBQueryBean, Void, Void> {

    private WeakReference<Activity> reference;
    private DBManager dbManager;

    public DBTask(Activity activity){
        this.reference = new WeakReference<>(activity);
        this.dbManager = DBManager.getInstance(activity);
    }

    @Override
    protected Void doInBackground(DBQueryBean... dbQueries) {

       if(dbQueries[0].getQuery().equals(DBQueryBean.INSERT)){

           //Inserting the itineraries contained into the bean
           for (Itinerary itinerary : dbQueries[0].getItineraries()){
               System.out.println("Inserting values");
               try {
                   dbManager.insertItinerary(itinerary);
               }catch (SQLException e){
                   Toast.makeText(reference.get(), R.string.db_exception, Toast.LENGTH_SHORT).show();
               }
           }

       }else{

           //returning all the itineraries that has a specified type
           String itinType = dbQueries[0].getType();
           Itinerary[] itinerariesFound = null;
           try {
               itinerariesFound = dbManager.getItineraries(itinType);
           }catch(SQLException e){
               Toast.makeText(reference.get(), R.string.db_exception, Toast.LENGTH_SHORT).show();
           }
           dbQueries[0].setItineraries(itinerariesFound);

       }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
