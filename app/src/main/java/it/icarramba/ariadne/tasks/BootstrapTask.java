package it.icarramba.ariadne.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.entities.FogNode;
import it.icarramba.ariadne.listeners.CloudListener;

public class BootstrapTask extends AsyncTask<String, Void, String> {

    private CloudListener cl;
    private Context context;

    public BootstrapTask(CloudListener cl, Context context){
        this.cl = cl;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String... jsonReqs) {

        String response = "";
        String jsonReq = jsonReqs[0];

        BufferedReader bis;

        Socket socket = null;
        try {
            socket = new Socket(Constants.Cloud.BOOTSTRAP_IP, Constants.Cloud.BOOTSTRAP_PORT);

            //sending request to the bootstrap
            OutputStream outstream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outstream);
            out.print(jsonReq);
            // append and flush in logical chunks
            out.flush();

            bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            response = bis.readLine();
            bis.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //now saving the response in shared preferences
        //the value will be the date value
        //the values will be saved only if a non void json array is received
        if(response.length() > 5) {
            LocalDateTime now = java.time.LocalDateTime.now();
            System.out.println(now.toString());

            //the request to the bootstrap will be done again after
            //'HOURS_BEFORE_NEW_BOOTREQ' hours
            now = now.plusHours(Constants.Cloud.HOURS_BEFORE_NEW_BOOTREQ);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String nowString = now.format(formatter);

            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //updating date
            editor.putString(context.getString(R.string.shared_key_date), nowString);
            //updating node list value
            editor.putString(context.getString(R.string.shared_key_list), response);
            editor.apply();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        ArrayList<String> nodeIps = new ArrayList<>();
        Gson gson = new Gson();
        if(response.length() > 0){
            FogNode[] nodes = gson.fromJson(response, FogNode[].class);

            for(FogNode node : nodes){
                nodeIps.add(node.getIp());
            }
        }
        cl.afterBootstrapCall(nodeIps);
    }
}
