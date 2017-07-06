package com.example.dee_kay.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.User;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class Registration extends Fragment {

    private EditText et_FirstName, et_LastName,et_Email,et_Password,et_ReEnter;
    Button btn_SignUp;
    private TextView tv_registtrationStatus;

    private Input input;

    Output output;
    private User user;
    private Handler handler;

    public Registration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_registration, container, false);

        getActivity().setTitle("Registration ");

        handler = new Handler();


        tv_registtrationStatus = (TextView) v.findViewById(R.id.tv_registrationStatus);

        et_FirstName = (EditText) v.findViewById(R.id.et_firstName);
        et_LastName = (EditText) v.findViewById(R.id.et_LastName);
        et_Email = (EditText) v.findViewById(R.id.et_EmailAddress);
        et_Password = (EditText) v.findViewById(R.id.et_Password);
        et_ReEnter = (EditText) v.findViewById(R.id.et_reEnterPassword);

        btn_SignUp = (Button) v.findViewById(R.id.btn_SignUp);

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input = new Input();
                user = new User();

                String password = "";
                String reEnteredPassword = "";
                String firstName = "";
                String LastName = "";

                password = et_Password.getText().toString();
                reEnteredPassword = et_ReEnter.getText().toString();

                firstName = et_FirstName.getText().toString();
                LastName = et_LastName.getText().toString();

                //Check if edit text are not empty
                if(!(firstName.equals("")) && !(LastName.equals(""))) {

                    //Check for password similarities
                    if (password.equals(reEnteredPassword)) {

                        user.FirstName = et_FirstName.getText().toString();
                        user.LastName = et_LastName.getText().toString();
                        user.Email = et_Email.getText().toString();
                        user.Password = password;

                        //packaging
                        input.user = user;
                        new myAsync().execute();
                    } else {
                        Toast.makeText(getActivity(), "Passwords do not match !!", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    tv_registtrationStatus.setVisibility(View.VISIBLE);
                    tv_registtrationStatus.setText("User details are required to complete registration");
                }


            }
        });




        return v;
    }

    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");
            client.configure(new Configurator("http://tempuri.org/","IService1","Register"));

            //passing the input class as a parameter to the service
            client.addParameter("request",input);


            output = new Output();

            try {
                output = client.call(output);
            } catch (Exception e) {
                e.printStackTrace();
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
                   String comfirmation = ((Output) o).Comfirmation;

                    if(comfirmation.equals("True"))
                    {
                        //handle a session
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        ((Activity) getActivity()).overridePendingTransition(0,0);

                        Toast.makeText(getActivity(),"Welcome !!",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        tv_registtrationStatus.setVisibility(View.VISIBLE);
                        tv_registtrationStatus.setText("Something went wrong during registration process, Try again!!");
                    }

                }
            });
        }
    }

}
