package com.example.dee_kay.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.example.dee_kay.myapplication.WcfObjects.User;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Service that checks for near parking around the user, and notify them
 */
public class ClosestParking extends Service {

    private Handler handler;
    private Output output;

    public ClosestParking() {

        this.handler = new Handler();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private User userCurrentLocation = null;
    private GlobalVariables gv = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //Getting user current location
        gv = (GlobalVariables) getApplication();
        userCurrentLocation = new User();

        userCurrentLocation.lat = gv.lat;
        userCurrentLocation.lng = gv.lng;

        System.out.println("SERVICE: " + userCurrentLocation.lat);
        System.out.println("SERVICE: " + userCurrentLocation.lng);


        //Starting the service
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    long futureTime = System.currentTimeMillis() + 2000;
                    long currentTime = System.currentTimeMillis();

                    while (currentTime < futureTime) {
                        synchronized (this) {
                            try {
                                System.out.println("SERVICE: WAITING");

                                wait(futureTime - currentTime);
                                checkLocation();

                            } catch (Exception e) {

                            }
                        }
                    }
                }
            }
        };

        Thread notificationThread = new Thread(r);
        notificationThread.start();

        return Service.START_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private static final int notificationID = 100;
    NotificationManager manager;

    /**
     * Near by parking notification
     */
    public void locationNotification() {
        String msgText = "Hi " + gv.getFirstname() + " '" + parking.Parking_Name + " Parking'" + " is in the same area as you are, if you looking for a parking.";

        PendingIntent pi = getPendingIntent();
        Notification.Builder builder = new Notification.Builder(this);

        long[] array = {1, 2, 3};
        builder
                .setTicker("E-Parking")
                .setContentTitle("E-Parking")
                .setVibrate(array)
                .setContentText("Update from " + "Near by Parking")
                .setSmallIcon(R.drawable.ic_place_black_24dp)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis());
        //.addAction(R.drawable.ic_shopping_cart_black_36dp, "open app", pi);
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);
        Notification notification = new Notification.BigTextStyle(builder)
                .bigText(msgText).build();

        //sending out notification to device
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(notificationID, notification);

        stopNotification = true;
    }

    boolean stopNotification = false;
    private Parking parking = null;


    /**
     *
     */
    private void checkLocation() {


        //getting parking coordinates
        new myAsync().execute();

        List<Parking> parkingList = output.parkingList;
        int parkingSize = parkingList.size();

        System.out.println("-------------: " + parkingSize);

        for (int x = 0; x < parkingSize; x++) {

            //System.out.println(o.locations.get(x).ProfileID +" :: " + o.locations.get(x).TraderName);
            //System.out.println("Lat: " + o.locations.get(x).Lat + "\nLong: "+ o.locations.get(x).Long + "\n\n");

//            double tempLat = session.latitude - o.locations.get(x).Lat; //current = 2
//            double tempLong = session.longitude - o.locations.get(x).Long;


            //Getting the difference between the user's location and the parking around them
            double tempLat = userCurrentLocation.lat - parkingList.get(x).Coordinates_ltd;
            double tempLong = userCurrentLocation.lng - parkingList.get(x).Coordinates_lng;

            //Calculating user's radius
            double a = (Math.pow(Math.sin(tempLat / 2), 2) + Math.cos(parkingList.get(x).Coordinates_ltd) * Math.cos(userCurrentLocation.lat) * Math.pow(Math.sin(tempLong / 2), 2));
            double c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
            double r = 6373 * c;

            //System.out.print(session.latitude + " : " + session.longitude);
            System.out.println("DISTANCE " + r);


            //Storing the name of the parking, to display on the notification
            parking = new Parking();
            if (r <= 15) {

                System.out.print("LOOP: " + parkingList.get(x).Parking_Name + ": " + r);
                System.out.println();

                parking.Parking_Name = parkingList.get(x).Parking_Name;
                parking.Parking_City = parkingList.get(x).Parking_City;
                parking.Coordinates_ltd = parkingList.get(x).Coordinates_ltd;
                parking.Coordinates_lng = parkingList.get(x).Coordinates_lng;

                //Saving the parking coordinates
                gv.ParkingName = parking.Parking_Name;
                gv.parkingLat = parking.Coordinates_ltd;
                gv.parkingLng = parking.Coordinates_lng;

                //session.traderName = o.locations.get(x).TraderName;
                //session.trLatitude = o.locations.get(x).Lat;
                //session.trLongitude = o.locations.get(x).Long;


                //Sending out the notification with the parking name
                if (!stopNotification) {
                    locationNotification();
                }
                return;
            }
        }

    }


    /**
     * For handling on click notification, and plot the parking that is around the user location
     *
     * @return
     */
    public PendingIntent getPendingIntent() {

        Intent intent = new Intent(getApplicationContext(), NearByParking.class);

        //for plotting the location after openning the notifaction
        //intent.putExtra("session", session);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }


    /**
     * Used for getting data from the database
     * Getting the parking that might be possibly around the user
     */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);
            //Calling the function from the service, to give a list parking(s)
            client.configure(new Configurator("http://tempuri.org/", "IService1", "GetParkings"));

            //passing the input class as a parameter to the service
            client.addParameter("request", "");


            output = new Output();

            try {
                output = client.call(output);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return output;
        }

        @Override
        protected void onPostExecute(final Object o) {
            super.onPostExecute(o);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Output out = (Output) o;

                    if (out.parkingList.size() != 0) {


                    } else {
                        //If we did not get any parking, try again
                        new myAsync().execute();
                    }


                }
            });
        }
    }
}
