package com.devpoint.realpros.mobibank;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Random;

public class WdawMoney extends Fragment {
    private TextView display;
    int Accbal,amount;
    Random rand;
    String Accpin,Accno,txtamount;
    AlertDialog.Builder dialogue ;
    private Button wdaw;
    OnAccount onAccount=new OnAccount();
    onAccountUpdateListener onAccountUpdateListener;

    public interface onAccountUpdateListener{
        String onAccountUpdate(String accno,int accbal);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.frag_two, container, false);
       display=view.findViewById(R.id.dispAcc);
       wdaw=view.findViewById(R.id.btdwdaw);
       Bundle bundle=getArguments();
       if(bundle!=null) {
           Accno = bundle.getString("Account_number");
           Accbal = bundle.getInt("Account_balance");
           Accpin = bundle.getString("Account_pin");
           onAccountUpdateListener.onAccountUpdate(Accno,Accbal);
       }
       else{
           Accno="null";
           Accbal =0-1;
           Accpin ="null";
       }

        display.setText("Account number: \n"+Accno);
        display.setTextColor(Color.rgb(254,254,254));

       wdaw.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              if(Accpin.equals("null") || Accbal<0){
                  Toast.makeText(getActivity(),"Please try again later",Toast.LENGTH_SHORT).show();
              }
              else {
                      android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
                      builder.setTitle("Enter amount to withdraw");
                      final EditText edtamt=new EditText(getActivity());
                      edtamt.setInputType(InputType.TYPE_CLASS_NUMBER);
                      builder.setView(edtamt);
                      builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
                              builder.setTitle("Enter Pin number");
                              final EditText edtpin=new EditText(getActivity());
                              edtpin.setInputType(InputType.TYPE_CLASS_NUMBER);
                              edtpin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                              builder.setView(edtpin);
                              builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialogInterface, int i) {
                                      String Pin = edtpin.getText().toString().trim();
                                      txtamount=edtamt.getText().toString();
                                      String rawpin = StringEn.decrypt(Accpin);
                                      if(Pin.equals(rawpin)) {
                                      int charges = 0;
                                      amount=Integer.parseInt(txtamount);
                                      if (amount <= 500) {
                                          charges = 12;
                                      } else if (500 < amount & amount <= 2500) {
                                          charges = 25;
                                      } else if (2500 < amount & amount <= 5000) {
                                          charges = 37;
                                      } else if (5000 < amount & amount <= 10000) {
                                          charges = 56;
                                      } else if (10000 < amount & amount <= 20000) {
                                          charges = 102;
                                      } else {
                                          charges = 200;
                                      }
                                      if (amount > Accbal) {
                                          Toast.makeText(getActivity(),"!!..Not enough float to withdraw this amount",Toast.LENGTH_SHORT).show();

                                      } else if (amount < 100) {
                                          Toast.makeText(getActivity(),"!..cannot withdraw less than 100",Toast.LENGTH_SHORT).show();

                                      } else if (amount > 70000) {
                                          Toast.makeText(getActivity(),"!..can only withdraw  ksh.70000 or less per transanction.",Toast.LENGTH_SHORT).show();

                                      }
                                      else {
                                          amount += charges;
                                          if (amount > Accbal) {
                                              Toast.makeText(getActivity(), "!!..Not enough float to send Ksh."+amount, Toast.LENGTH_SHORT).show();
                                          }
                                          else{
                                          AlertDialog.Builder confirm = new AlertDialog.Builder(getActivity());
                                          confirm.setTitle("Confirm Dialogue");
                                          confirm.setCancelable(false);
                                          confirm.setMessage("Confirm withdrawal of this amount Ksh." + txtamount + ", charges cost of Ksh." + charges);
                                          confirm.setPositiveButton("Withdraw", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialogInterface, int i) {
                                                  Accbal -= amount;
                                                  dialogue = new AlertDialog.Builder(getActivity());
                                                  if (Accbal >= 0) {
                                                      String check_update = onAccountUpdateListener.onAccountUpdate(Accno, Accbal);
                                                      String update_res = String.valueOf(check_update);
                                                      if (update_res == "null") {
                                                          Toast.makeText(getActivity(), "update not working", Toast.LENGTH_SHORT).show();
                                                      } else if (update_res.contains("Success")) {

                                                          dialogue.setMessage(onAccount.getTranscode() + ", Confirmed withdrawal of Ksh. " + txtamount + " was successful,\n" +
                                                                  "New balance is Ksh." + Accbal + " on " + onAccount.getTime());
                                                          Accpin="null";
                                                      } else {
                                                          dialogue.setMessage("Sorry, failed to execute operation, please try again later");
                                                      }
                                                  } else {
                                                      dialogue.setMessage("Sorry operation did not complete successfully at this time, \n" +
                                                              "Please try again later");
                                                  }
                                                  dialogue.show();

                                              }
                                          });
                                          confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialogInterface, int i) {
                                                  // to be coded
                                              }
                                          });
                                          confirm.show();
                                      }

                                      }
                                  }
                                      else {
                                         Toast.makeText(getActivity(),"Wrong pin was detected.. at this time",Toast.LENGTH_SHORT).show();
                                      }

                                  }
                              });
                              builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialogInterface, int i) {

                                  }
                              });
                              builder.show();
                          }
                      });
                      builder.show();
              }
           }
       });
        return view;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity=(Activity)context;
        try{
            onAccountUpdateListener=(WdawMoney.onAccountUpdateListener) activity;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(),"Casting error detected"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
