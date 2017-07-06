package com.example.dee_kay.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.Snackbar;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_1 extends Fragment {


    private TextView tv_email;
    private EditText et_firstname,et_lastname,et_contacts,et_address;

    private Button btn_Edit_profile;
    Output output;
    Input input;

    Handler handler;


    private String User_id = "empty";

    private String editing ="false";
    //Empty contractor
    public Tab_1() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_1, container, false);

        input = new Input();
        handler = new Handler();

        et_firstname = (EditText) v.findViewById(R.id.et_userFirstname);
        et_lastname = (EditText) v.findViewById(R.id.et_userLastName);
        et_contacts = (EditText) v.findViewById(R.id.et_ContactNumber);
        et_address = (EditText) v.findViewById(R.id.et_UserAddress);

        btn_Edit_profile = (Button)v.findViewById(R.id.btn_edit);
        tv_email = (TextView) v.findViewById(R.id.tv_userEmail) ;




        btn_Edit_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                 if(User_id.equals("empty"))
                 {
                     Snackbar.make(v, "User not logged in", Snackbar.LENGTH_LONG).show();
                 }
                else {
                     editing = "true";
                     new myAsync().execute();

                     //Capturing User details
                     input.user.User_ID = User_id;
                     input.user.FirstName = et_firstname.getText().toString();
                     input.user.LastName = et_lastname.getText().toString();

                     //Capturing user contact details
                     input.contact.Contact_Number = et_contacts.getText().toString();
                     input.contact.Address = et_address.getText().toString();

                 }
            }
        });

        GlobalVariables gv = ((GlobalVariables)getActivity().getApplicationContext());
        User_id = gv.getUserID();

        input.user.User_ID = User_id;

        if(this.User_id.equals("empty"))
        {
            Snackbar.make(v, "User not logged in", Snackbar.LENGTH_LONG).show();
        }else {
            new myAsync().execute();
        }
        return v;
    }

    /*
* Used for getting parking(s) from the data-store
* */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {


            if(editing.equals("false"))
            {
                FireExitClient client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");
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
            else  //edit the user profile
            {

                FireExitClient client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");
                client.configure(new Configurator("http://tempuri.org/","IService1","editProfile"));
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

                    if(out != null)
                    {
                        tv_email.setText(out.user.Email);
                        et_firstname.setText(out.user.FirstName);
                        et_lastname.setText(out.user.LastName);
                        et_contacts.setText(out.contact.Contact_Number);
                        et_address.setText(out.contact.Address);

                    }else
                    {
                        //Toast.makeText(getActivity(),"User is not logged in",Toast.LENGTH_LONG).show();
                    }

                }

            });
        }
    }


}
