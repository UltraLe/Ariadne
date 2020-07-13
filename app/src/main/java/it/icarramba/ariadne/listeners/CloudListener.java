package it.icarramba.ariadne.listeners;

import java.util.ArrayList;

public interface CloudListener {

    //method that has to set up the activity, if needed,
    //for example making visible loading animation
    void beforeCall();

    //method that has to implement
    //what to do after the server call
    void afterCall(String response);

    //method that has to implement a strategy when the server
    //call returns an error
    void onError();

    //method that has to implement
    //what to do after the bootstrap call
    void afterBootstrapCall(ArrayList<String> fogIpList);

    //method that has to implement
    //what to do before the bootstrap call
    void beforeBootstrapCall();

}
