package com.devpoint.realpros.mobibank;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Home_fragment extends Fragment {
    private static Home_fragment instance = null;
    Home_fragment.onButtonClick onButtonClick;
    private String finalpin="null";
    private TextView title,progview;
    IntentFilter intentFilter;
    private ProgressBar progressBar;
    private Button readT;
    private EditText accnumber, accid, accpin;
    private Button login;
    private CheckBox check;
    private int count=1;
    public Home_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.home_fragment, container, false);
        readT=view.findViewById(R.id.bterms);
        progressBar=view.findViewById(R.id.progressBar);
        progview=view.findViewById(R.id.pview);
        progressBar.setVisibility(View.INVISIBLE);
        title=view.findViewById(R.id.tittle);
        check=view.findViewById(R.id.checkT);
        title.setText("WELCOME TO INTERNET BANKING \n LOGIN TO MOBIBANK");
        title.setTextColor(Color.rgb(100,020,255));
        login= view.findViewById(R.id.btnlog);
        accnumber= view.findViewById(R.id.edtaccno);
        accid= view.findViewById(R.id.edtid);
        accpin= view.findViewById(R.id.edtpin);
        accid.requestFocus();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check.isChecked()){
                    /*progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(20);
                    progview.setText("Progress...");
                    fetchPin(view);*/
                    String Anumber = accnumber.getText().toString();
                    String Aid = accid.getText().toString();
                    getPin(Anumber,Aid);

                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             login();
            }
        });
        readT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onButtonClick.onRead();
                accnumber.setText("");
                accid.setText("");
                accpin.setText("");
                check.setChecked(false);
            }
        });
        return view;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity=(Activity)context;
        try{
            onButtonClick =(Home_fragment.onButtonClick) activity;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(),"Casting error detected"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
    public  void login() {
        if(!(check.isChecked())){
            Toast.makeText(getActivity(),"You must accept terms to proceed",Toast.LENGTH_SHORT).show();
        }
        else {
            String Anumber = accnumber.getText().toString();
            String Aid = accid.getText().toString();
            String Pin = accpin.getText().toString().trim();
            String encrypPin= getPin(Anumber,Aid);
            String Apin = String.valueOf(encrypPin);
            if (Apin.contains("==")) {
                String rawPin = StringEn.decrypt(Apin);
                if (Pin.equals(rawPin)) {
                    onButtonClick.onLogin(Anumber, Aid, Apin);
                    accnumber.setText("");
                    accid.setText("");
                    accpin.setText("");
                    check.setChecked(false);
                } else {
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), finalpin, Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public String getPin(final String accno,final String accid) {
        progressBar.setVisibility(View.VISIBLE);
        class GetAccount extends AsyncTask<Integer, Integer, String> {
            @Override
            protected String doInBackground(Integer... params) {
                for (; count <= params[0]; count++) {
                    try {
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost("http://192.168.0.19/restApi/fetch_pin.php");
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("Anumber", accno));
                        nameValuePairs.add(new BasicNameValuePair("Aid", accid));
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                        post.setEntity(entity);
                        HttpResponse response = client.execute(post);
                        StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                            if (out.toString().contains("Connection failure")) {
                                finalpin = "null";
                            } else if (out.toString().contains("No results found")) {
                                finalpin = "0 result";
                            } else {
                                finalpin = out.toString();
                            }
                            publishProgress(count);
                        } else {
                            finalpin = "Uknown exception occurred";
                        }

                    } catch (Exception ee) {
                        finalpin = "Network link failure, couldn't reach the server..";
                    }
                }
                return finalpin;

            }

            @Override
            protected void onPostExecute(String result) {
                String returns;
                progressBar.setVisibility(View.GONE);
                if(result.contains("==")){
                    returns="fetching data complete";
                }
                else{
                    returns="fetching returns fail";
                }
                progview.setText(returns);
            }

            @Override
            protected void onPreExecute() {
                progressBar.setMax(20);
                progview.setText("Progress...");
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progview.setText("Running..." + values[0]);
                progressBar.setProgress(values[0]);
            }

        }
        GetAccount Gacc = new GetAccount();
        Gacc.execute(10);
        return finalpin;
    }

    public interface onButtonClick {
        void onLogin(String Ano,String Aid,String Pin);
        void onRead();
    }

    @Override
    public void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction("Fetching pin");
        getActivity().registerReceiver(intentReceiver, intentFilter);


    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(intentReceiver);
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String returns;
            if(intent.getAction().equals("Fetching pin")){
                String pin=intent.getStringExtra("result");
                finalpin=pin;
                if(pin.contains("==")){
                    returns="fetching data complete";
                    stopFetching();
                }
                else{
                    returns="fetching returns fail";
                }
                progview.setText(returns);

            }
        }
    };
    public void stopFetching(){
        Intent intent=new Intent(getActivity(),OnlineDbListener1.class);
        getActivity().stopService(intent);
        progressBar.setVisibility(View.GONE);
    }

}
