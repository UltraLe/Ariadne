package it.icarramba.ariadne.entities;

import java.util.Vector;

public class MonumentsReturnedServer {

    //                      !   !   !
    //The server into the cloud will return an array (itineraries)
    //of array (monuments) of this class elements, in json format

    public class ServerItineraries {

        public Vector<Vector<MonumentsReturnedServer>> Itineraries;

        public ServerItineraries(Vector<Vector<MonumentsReturnedServer>> itineraries){
            this.Itineraries = itineraries;
        }

    }

    public String Name;
    public String Coordinates;
    public Byte[] Picture;
    public int Position;
    public String ExpectedArrTime;

    public MonumentsReturnedServer(String  name, String coordinates, Byte[] picture,
                                   int position, String expectedArrTime){
        this.Coordinates = coordinates;
        this.ExpectedArrTime = expectedArrTime;
        this.Name = name;
        this.Picture =  picture;
        this.Position = position;
    }

    public String getName() {
        return Name;
    }

    public String getExpectedArrTime() {
        return ExpectedArrTime;
    }

    public String getCoordinates() {
        return Coordinates;
    }

    public Byte[] getPicture() {
        return Picture;
    }

    public int getPosition() {
        return Position;
    }
}
