package com.devpoint.realpros.mobibank;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ToBalance extends Fragment {
    private String Apin;
    int balance=0-1;
    private EditText apin;
    private Button ok,cancel;
    private OnAccount onAccount=new OnAccount();
    public ToBalance() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        if(bundle!=null){
            String Accpin=bundle.getString("Account_pin");
            int Accbal=bundle.getInt("Account_bal");
            Apin=Accpin;
            balance=Accbal;
        }
        View view=inflater.inflate(R.layout.frag_balance, container, false);
        apin=view.findViewById(R.id.balPin);
        cancel=view.findViewById(R.id.btncancel);
        ok=view.findViewById(R.id.btnbal);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Pin = apin.getText().toString();
                int rescode = onAccount.getRescode(Apin);
                if (rescode == 200) {
                    String rawPin = StringEn.decrypt(Apin);
                    if (Pin.equals(rawPin)) {
                        try {
                            SmsManager sm = SmsManager.getDefault();
                            sm.sendTextMessage("0766898713", null, "get sms", null, null);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            if (balance >= 0) {
                                dialog.setMessage(onAccount.getTranscode()+",Confirmed, your account balance is Ksh. " + balance+" on "+onAccount.getTime());
                                sendMessage();
                            } else {
                                dialog.setMessage("Sorry,encountered problem with result number" + 404
                                );
                            }
                            dialog.show();

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Sending sms failed", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Incorrect pin", Toast.LENGTH_SHORT).show();
                    }
                    apin.setText("");
                    apin.requestFocus();
                }
                else {
                    Toast.makeText(getActivity(), "Couldn't perform operation at this time, returns code no."+rescode, Toast.LENGTH_SHORT).show();
                    apin.setText("");
                }
            }
        });
        return view;
    }

    public void sendMessage(){
        int permissioncheck= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        if (permissioncheck== PackageManager.PERMISSION_GRANTED){
            try {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("3342", null, "Hello was testing", null, null);
                Toast.makeText(getActivity(), "sms sent", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "sms failed", Toast.LENGTH_SHORT).show();

            }
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},0);
        }
    }

}
