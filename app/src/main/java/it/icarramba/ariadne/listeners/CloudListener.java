package it.icarramba.ariadne.listeners;

public interface CloudListener {

    //method that has to set up the activity, if needed,
    //for example makeing visible loagind animation
    void beforeCall();

    //method that has to implement
    //what to do after the server call
    void afterCall(String response);

    //method that has to implement a strategy when the server
    //call returns an error
    void onError();

}
