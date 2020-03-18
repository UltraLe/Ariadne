package it.icarramba.ariadne.constants;

public class Constants {

    public static class DBConstants {

        public static String DBName = "AriadneDB";
        public static int DBVersion = 1;

        public static class Itineraries{

            public static String TableName = "Itineraries";

            public static String ID = "ID";
            public static String Type = "Type";
            public static String Departure = "Departure";
            public static String MeansOfTransp = "MeansOfTransp";

            public static String CreateQuery = "CREATE TABLE \""+TableName+"\" (" +
                                                    "\""+ID+"\" TEXT NOT NULL, " +
                                                    "\""+Type+"\" TEXT NOT NULL, " +
                                                    "\""+Departure+"\" TEXT NOT NULL, " +
                                                    "\""+MeansOfTransp+"\" TEXT NOT NULL, " +
                                                    "PRIMARY KEY(\""+ID+"\"))";

        }

        public static class ItineraryMonuments{

            public static String TableName = "ItineraryMonuments";

            public static String ItineraryID = "ItineraryID";
            public static String MonumentName = "MonumentName";
            public static String Position = "Position";
            public static String ExpectedArrTime = "ExpectedArrTime";

            public static String CreateQuery = "CREATE TABLE \""+TableName+"\" ("+
                                                            "\""+ItineraryID+"\" INTEGER NOT NULL, "+
                                                            "\""+MonumentName+"\" TEXT NOT NULL, "+
                                                            "\""+Position+"\"\tINTEGER NOT NULL, "+
                                                            "\""+ExpectedArrTime+"\" TEXT NOT NULL, "+
                                                            "FOREIGN KEY(\""+MonumentName+"\") REFERENCES \""+Monuments.TableName+"\"(\""+Monuments.Name+"\")"+
                                                            "PRIMARY KEY(\""+ItineraryID+"\",\"MonumentName\"), "+
                                                            "FOREIGN KEY(\""+ItineraryID+"\") REFERENCES \""+Itineraries.TableName+"\"(\""+Itineraries.ID+"\"))";

        }

        public static class Monuments{

            public static String TableName = "Monuments";

            public static String Name = "Name";
            public static String Picture = "Picture";
            public static String Coordinates = "Coordinates";

            public static String CreateQuery = "CREATE TABLE \""+TableName+"\" ( " +
                                                    "\""+Name+"\"\tTEXT NOT NULL, " +
                                                    "\""+Picture+"\"\tBLOB, " +
                                                    "\""+Coordinates+"\" TEXT NOT NULL, \"Description\" TEXT, " +
                                                    "PRIMARY KEY(\""+Name+"\"))";

        }

    }


}
