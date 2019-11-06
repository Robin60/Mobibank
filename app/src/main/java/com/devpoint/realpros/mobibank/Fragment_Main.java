package com.devpoint.realpros.mobibank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Main extends ListFragment implements AdapterView.OnItemClickListener {
    int balance=0-1;
    onListSelectedListener onListSelectedListener;
    private String Anumber;
    private String Aid;
    private String Apin;
    private int Abal;
    private  ProgressBar prog;
    private TextView status;
    private OnAccount onAccount=new OnAccount();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        final String Accno;
        final String Accid;
        final String Accpin;
        Bundle bundle=getArguments();
        if(bundle!=null){
            Accno=bundle.getString("Anumber");
            Accid=bundle.getString("Aid");
            Accpin=bundle.getString("Apin");
            Anumber=Accno;
            Aid=Accid;
            Apin=Accpin;
        }
        else{
            Log.d("Devp","There are missing argument");
        }
        accountBal();
       View view= inflater.inflate(R.layout.fragment_main, container, false);
       prog=view.findViewById(R.id.prog);
       prog.setProgress(3);
       status=view.findViewById(R.id.txtstatus);

       return  view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.buttons, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity=(Activity)context;
        try{
            onListSelectedListener=(Fragment_Main.onListSelectedListener) activity;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(),"Casting error detected"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //onListSelectedListener=null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int listpos, long l) {
        if(listpos==0){
            Abal=accountBal();
            onListSelectedListener.onBalance(Apin,Abal);
          //  balanceWindow();
        }
        else if(listpos==1){
            if(accountBal()<0){
                Toast.makeText(getActivity(),"Missing arguments",Toast.LENGTH_SHORT).show();
            }
            else {
                onListSelectedListener.onWithdraw(Anumber, accountBal(), Apin);
            }
        }
        else if(listpos==2){
            Abal=accountBal();
            Intent intent=new Intent(getActivity(),SendActivity.class);
            intent.putExtra("Account_number",Anumber);
            intent.putExtra("Account_pin",Apin);
            intent.putExtra("Account_bal",Abal);
            startActivity(intent);
        }
    }

    public void balanceWindow(){
            android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Enter Pin number");
            final EditText apin=new EditText(getActivity());
            apin.setInputType(InputType.TYPE_CLASS_NUMBER);
            apin.setTransformationMethod(PasswordTransformationMethod.getInstance());
            builder.setView(apin);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String Pin = apin.getText().toString().trim();
                    int resultcode = onAccount.getRescode(Apin);
                    if(resultcode == 200) {
                        String rawPin = StringEn.decrypt(Apin);
                        if (Pin.equals(rawPin)) {
                            String bal=String.valueOf(accountBal());
                            if (bal.contains("Network link failure")) {
                                Toast.makeText(getActivity(), bal, Toast.LENGTH_LONG).show();
                            } else if (bal == "null") {
                                Toast.makeText(getActivity(), "Could not fetch account balance at this time, try again later", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    SmsManager sm = SmsManager.getDefault();
                                    sm.sendTextMessage("0766898713", null, "get sms", null, null);
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                    int accbal = Integer.parseInt(bal);
                                    if (accbal >= 0) {
                                        balance = accbal;
                                        dialog.setMessage("AXSA123,Confirmed, your account balance is Ksh. " + balance);
                                    } else {
                                        dialog.setMessage("Sorry,encountered problem with result number" +
                                                "" + accbal + " as invalid number.");
                                    }
                                    dialog.show();

                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Sending sms failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Incorrect pin", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }

    public int accountBal(){
        String abalance=onAccount.getBal(Anumber,Aid);
        String bal=String.valueOf(abalance);
        if (bal.contains("Network link failure")) {
            balance=0-1;
        } else if (bal=="null") {
            balance=0-1;
        }
        else{
            int accbal=Integer.parseInt(bal);
            balance=accbal;
        }
        return balance;
    }

    public interface onListSelectedListener{
        void onBalance(String accpin,int abal);
         void onWithdraw(String accno,int accbal,String accpin);
    }

}
