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
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Class represent user wallet
 */
public class Tab_2 extends Fragment {


    private TextView tv_creditStatus;
    private EditText et_credits;
    private Button btnLoadCredits;
    Output output;
    Input input;

    Handler handler;

    private String User_id = "empty";

    private String editing ="false";


    public Tab_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        input = new Input();
        handler = new Handler();


        View v = inflater.inflate(R.layout.fragment_tab_2, container, false);

        tv_creditStatus =(TextView) v.findViewById(R.id.tv_credit_status);
        et_credits = (EditText) v.findViewById(R.id.et_credits);
        btnLoadCredits = (Button) v.findViewById(R.id.btn_load);

        btnLoadCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User_id.equals("empty"))
                {
                    Toast.makeText(getActivity(),"User is not logged in",Toast.LENGTH_LONG).show();
                }
                else {


                    //Capturing user contact details
                    try {
                        input.wallet.Balance = Double.parseDouble(et_credits.getText().toString());
                        editing = "true";
                        new myAsync().execute();
                    }catch (NumberFormatException e)
                    {
                        Toast.makeText(getActivity(),"Please provide a valid amount",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        //Getting the user ID
        GlobalVariables gv = ((GlobalVariables)getActivity().getApplicationContext());
        User_id = gv.getUserID();

        input.user.User_ID = User_id;

        if(this.User_id.equals("empty"))
        {
            Toast.makeText(getActivity(),"User is not logged in",Toast.LENGTH_LONG).show();
        }else {
            new myAsync().execute();
        }


        return v;

    }


    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {
            if(editing.equals("false"))
            {
                FireExitClient client = new FireExitClient(Input.AZURE_URL);
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

                FireExitClient client = new FireExitClient(Input.AZURE_URL);
                client.configure(new Configurator("http://tempuri.org/","IService1","EditWallet"));
                //passing the input class as a parameter to the service
                client.addParameter("request",input);

                output = new Output();
                editing = "false";
                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                //For the displaying the edited amount
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

                            et_credits.setText("R " + out.wallet.Balance);
                            if(out.Comfirmation.equals("true"))
                            {
                                tv_creditStatus.setText("Low on credits, please re-load");
                            }

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
