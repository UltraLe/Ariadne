package it.icarramba.ariadne.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.entities.FogNode;
import it.icarramba.ariadne.listeners.CloudListener;

public class BootstrapTask extends AsyncTask<String, Void, String> {

    private CloudListener cl;

    public BootstrapTask(CloudListener cl){
        this.cl = cl;
    }

    @Override
    protected String doInBackground(String... jsonReqs) {

        String response = "";
        String jsonReq = jsonReqs[0];

        InputStream input;

        BufferedReader bis;

        Socket socket = null;
        try {
            socket = new Socket(Constants.Cloud.BOOTSTRAP_IP, Constants.Cloud.BOOTSTRAP_PORT);
            input = socket.getInputStream();

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

        //now saving the response in some way
        // TODO

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
