package it.icarramba.ariadne.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.entities.BootstrapRequest;
import it.icarramba.ariadne.entities.FogNode;
import it.icarramba.ariadne.listeners.CloudListener;
import it.icarramba.ariadne.tasks.BootstrapTask;

public class CloudInteractor implements Response.ErrorListener, Response.Listener<String> {

    private List<String> headers;
    private String domain;
    private Context context;
    private CloudListener cl;

    public CloudInteractor(String domain, Context context, CloudListener cl){
        headers = new ArrayList<>();
        this.context = context;
        this.domain = domain;
        this.cl = cl;
    }

    public void addHeader(String headerName, String value){
        this.headers.add(headerName+","+value);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fogListRequest(int maxNumFog, String lat, String lon){

        System.out.println("REQUESTING NODES IPS");

        cl.beforeBootstrapCall();
        SharedPreferences sh = context.getSharedPreferences(context.getString(R.string.shared_pref),
                                                            Context.MODE_PRIVATE);

        String updateTime = sh.getString(context.getString(R.string.shared_key_date),null);
        //System.out.println("Date found: "+updateTime);
        //if shared prefs is not empty, extract the value of the date
        if(updateTime != null) {

            //System.out.println("Shared preferences found!");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(updateTime, formatter);

            if (date.compareTo(java.time.LocalDateTime.now()) > 0) {
                System.out.println("Bootstrap call  not needed!");
                //update is NOT needed yet.
                //Retrieving node list
                String nodeList = sh.getString(context.getString(R.string.shared_key_list), null);
                if(nodeList == null){
                    System.out.println("Something went wrong, date saved but not node list");
                    return;
                }
                //System.out.println("Old node list: "+nodeList);
                //converting them in array  style
                Gson gson = new Gson();
                FogNode[] nodes = gson.fromJson(nodeList, FogNode[].class);
                ArrayList<String> nodeIps = new ArrayList<>();
                for(FogNode node : nodes){
                    nodeIps.add(node.getIp());
                }

                //giving the array to the caller
                cl.afterBootstrapCall(nodeIps);
                return;
            }
        }

        System.out.println("No shared preferences found!");
        //if im here a new request has to be computed
        BootstrapRequest req = new BootstrapRequest(Constants.Cloud.FOG_LIST_REQ, lat, lon, maxNumFog);
        String jsonReq = (new Gson()).toJson(req);

        // create async task that sends the list request, receive the response, calls the cl.afterCall()
        // on post execute
        BootstrapTask bootTask = new BootstrapTask(this.cl, context);
        bootTask.execute(jsonReq);
    }

    public void sendRequest(String lat, String lon, String time, String  transport, int PORT){

        cl.beforeCall();

        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="http://"+this.domain+":"+PORT+"/?"+ Constants.Cloud.LAT+"="+lat+
                    "&"+Constants.Cloud.LON+"="+lon+"&"+Constants.Cloud.TIME+"="+time+
                    "&"+Constants.Cloud.TRA+"="+transport;

        System.out.println("REQUESTED URL: "+url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this) {
            //Adding headers, if any.
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                for(String header : headers){
                    String[] par = header.split(",");
                    params.put(par[0], par[1]);
                }
                return params;
            }
        };

        // Add the request to the RequestQueue.
        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        System.out.println("VOLLEY ERR: "+error.toString());
        cl.onError();
    }

    @Override
    public void onResponse(String response) {
        cl.afterCall(response);
    }
}
