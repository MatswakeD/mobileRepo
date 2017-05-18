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
public class Login extends Fragment {

    public static final String USER_ID = "ID";

    private EditText et_EmailAddress, et_Password;
    private TextView tv_loginStatus, tv_hearFirstname;
    Button btn_SignIn;


    private User user;
    Input input;
    Output output;
    Handler handler;

    private String User_id = "";
    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle("Log in");
        handler = new Handler();


        et_EmailAddress = (EditText) v.findViewById(R.id.et_EmailAddress);
        et_Password = (EditText) v.findViewById(R.id.et_Password);



        tv_loginStatus = (TextView) v.findViewById(R.id.tv_loginStatus);
        //tv_hearFirstname = (TextView) v.findViewById(R.id.tv_headerFirstname);

        btn_SignIn = (Button) v.findViewById(R.id.btn_SignIn);

        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Instantiate input class for sending
                input = new Input();

                String email = et_EmailAddress.getText().toString();
                String password = et_Password.getText().toString();

                user = new User();
                user.EmailAddress = email;
                user.Password = password;




                if(!(email.equals("")) && !(password.equals("")))
                {
                    //Packaging login inside input for sending
                    input.user = user;

                    new myAsync().execute();
                }
                else
                {
                    Toast.makeText(getActivity(),"Log in details are required", Toast.LENGTH_LONG).show();
                    tv_loginStatus.setVisibility(View.VISIBLE);
                    tv_loginStatus.setText("Log in details are required");

                }

            }
        });



        return v ;
    }

    class myAsync extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");
            client.configure(new Configurator("http://tempuri.org/","IService1","SignIn"));

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


                    if(out.Comfirmation.equals("true"))
                    {

                        tv_loginStatus.setVisibility(View.VISIBLE);
                        tv_loginStatus.setText("Welcome ");


                        if(out.User_ID > 0)
                        {
                            User_id = out.User_ID + "";
                            Bundle b = new Bundle();
                            b.putString(USER_ID,  User_id);


                            GlobalVariables gv = ((GlobalVariables)getActivity().getApplicationContext());

                            gv.setUserID(User_id);

                            Home_Map fragB = new Home_Map();
                            fragB.setArguments(b);
                            getFragmentManager().beginTransaction().replace(R.id.content_main, fragB).commit();

                        }
                        Toast.makeText(getActivity(),"Welcome "+ out.user.LastName,Toast.LENGTH_LONG).show();
                    }
                    else {
                        tv_loginStatus.setVisibility(View.VISIBLE);
                        tv_loginStatus.setText("Incorrect Password Or User name");
                    }
                }
            });
        }
    }


}
