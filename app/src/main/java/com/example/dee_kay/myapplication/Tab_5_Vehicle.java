package com.example.dee_kay.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.util.Locale;


/**
 * View or edit vehicle information
 */
public class Tab_5_Vehicle extends Fragment {


    private EditText et_registration_num,et_vehicle_type;
    private Button btn_change;
    private String editing = "false";
    Output output;
    Input input;


    Handler handler;
    private String User_id = "empty";


    public Tab_5_Vehicle() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        input = new Input();
        handler = new Handler();

        View v = inflater.inflate(R.layout.fragment_tab_5__vehicle, container, false);



        et_registration_num = (EditText) v.findViewById(R.id.et_registration_num);
        et_vehicle_type = (EditText) v.findViewById(R.id.et_vehicle_type);


        btn_change = (Button) v.findViewById(R.id.btn_edit_vehicle);


        GlobalVariables gv = ((GlobalVariables)getActivity().getApplicationContext());
        User_id = gv.getUserID();
        input.user.User_ID = User_id;

        if(this.User_id.equals("empty"))
        {
            Toast.makeText(getActivity(),"User is not logged in",Toast.LENGTH_LONG).show();
        }else {
            new myAsync().execute();
        }



        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(User_id.equals("empty"))
                {
                    Toast.makeText(getActivity(),"User is not logged in",Toast.LENGTH_LONG).show();
                }
                else {
                    editing = "true";
                    new myAsync().execute();

                    //Capturing user contact details
                    input.vehicle.Registration_Num = (et_registration_num.getText().toString());
                    input.vehicle.Vehicle_type =(et_vehicle_type.getText().toString());

                }
            }
        });


        return v;
    }


    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient(input.AZURE_URL);
            if(editing.equals("false"))
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","GetProfile"));
                //passing the input class as a parameter to the service
                client.addParameter("request",input);


                output = new Output();

                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else  //edit the user vehicle
            {

                client.configure(new Configurator("http://tempuri.org/","IService1","EditVehicle"));
                //passing the input class as a parameter to the service
                client.addParameter("request",input);

                output = new Output();
                editing = "false";
                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                input.user.User_ID = User_id;
                client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");
                client.configure(new Configurator("http://tempuri.org/","IService1","GetProfile"));
                //passing the input class as a parameter to the service
                client.addParameter("request",input);

                output = new Output();
                editing = "false";
                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return output;
        }

        @Override
        protected void onPostExecute( final Object o) {
            super.onPostExecute(o);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Output out = (Output)o;

                    if(!out.profile.Deactivate.toUpperCase(Locale.ENGLISH).equals("DEACTIVATED")) {
                        if (out != null) {
                            et_registration_num.setText(out.vehicle.Registration_Num);
                            et_vehicle_type.setText(out.vehicle.Vehicle_type);

                        } else {
                            Toast.makeText(getActivity(), "User is not logged in", Toast.LENGTH_LONG).show();
                        }
                    }else
                    {
                        Toast.makeText(getActivity(), "user profile is deactivated", Toast.LENGTH_LONG).show();
                    }

                }

            });
        }
    }

}
