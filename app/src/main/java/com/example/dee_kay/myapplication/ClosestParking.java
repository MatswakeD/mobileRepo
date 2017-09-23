package com.example.dee_kay.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Parking;

import java.util.ArrayList;

public class ClosestParking extends Service {


    private GlobalVariables gv = (GlobalVariables)getApplication();
    public ClosestParking() {


    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    long futureTime = System.currentTimeMillis() + 1000;
                    long currentTime = System.currentTimeMillis();

                    while (currentTime < futureTime) {
                        synchronized (this) {
                            try {
                                // System.out.println("SERVICE: WAITING");

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
        locationNotification();

        return Service.START_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    private static final int notificationID = 100;
    NotificationManager manager;


    public void locationNotification() {
        //String msgText = "Hi " + session.getUsername() + ", " + session.traderName + " is in the same area as you are, we thought you might wanna check their store out.";

        PendingIntent pi = getPendingIntent();
        Notification.Builder builder = new Notification.Builder(this);

        long[] array = {1, 2, 3};
        builder
                //.setTicker("Neighbourgoods Market Johannesburg")
                .setContentTitle("Update from " + "")
                .setVibrate(array)
                .setContentText("Update from " + "")
                .setSmallIcon(R.drawable.ic_alarm_on_black_24dp)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis());
        //.addAction(R.drawable.ic_shopping_cart_black_36dp, "open app", pi);
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);
        Notification notification = new Notification.BigTextStyle(builder)
                .bigText("messgae goes here").build();

        //sending out notification to device
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(notificationID, notification);

        stopNotification = true;
    }

    boolean stopNotification = false;


    private void checkLocation() {


        ArrayList<Parking> parkingList = gv.ParkingLatLong;
        int parkingSize = parkingList.size();

        System.out.println("-----------: " + parkingSize);



        for (int x = 0; x < parkingSize; x++) {

            //System.out.println(o.locations.get(x).ProfileID +" :: " + o.locations.get(x).TraderName);
            //System.out.println("Lat: " + o.locations.get(x).Lat + "\nLong: "+ o.locations.get(x).Long + "\n\n");

            //double tempLat = session.latitude - o.locations.get(x).Lat; //current = 2
            //double tempLong = session.longitude - o.locations.get(x).Long;

            //double a = (Math.pow(Math.sin(tempLat / 2), 2) + Math.cos(o.locations.get(x).Lat) * Math.cos(session.latitude) * Math.pow(Math.sin(tempLong / 2), 2));
            //double c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
           // double r = 6373 * c;

            //System.out.print(session.latitude + " : " + session.longitude);
            //System.out.println("DISTANCE " + r);


//            if (r <= 15) {
//
//
//                //System.out.print("LOOP: " + o.locations.get(x).TraderName + ": " + r);
//                System.out.println();
//                //session.traderName = o.locations.get(x).TraderName;
//                //session.trLatitude = o.locations.get(x).Lat;
//                //session.trLongitude = o.locations.get(x).Long;
//
//
//
//                if (!stopNotification) {
//                    locationNotification();
//                }
//                return;
//            }
        }

    }


    //User session = null;
    public PendingIntent getPendingIntent() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        //for plotting the location after openning the notifaction
        //intent.putExtra("session", session);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }

}
