package com.devpoint.realpros.mobibank;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class SendMoney extends Fragment {
    private String Accpin, Accno_fro,Accno_to,Accname_to;
    private String txtamount;
    int Accbal_fro,Accbal_to,amount;
    AlertDialog.Builder dialogue;
    OnAccount onAccount = new OnAccount();
    private TextView display;
    private Button cancel,ok;

    public SendMoney() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        if(bundle!=null){
            String accno_fro=bundle.getString("Account_from");
            String accno_to=bundle.getString("Account_to");
            String accname_to=bundle.getString("Account_name_to");
            int accbal_fro=bundle.getInt("Account_from_bal");
            String accpin=bundle.getString("Account_pin");
            Accno_fro=accno_fro;
            Accno_to=accno_to;
            Accname_to=accname_to;
            Accbal_fro=accbal_fro;
            Accpin=accpin;
            onAccount.onAccountUpdate(Accno_fro, Accbal_fro);
        }
        else{
            Toast.makeText(getActivity(),"Missing arguments",Toast.LENGTH_LONG).show();

        }
        accountBal();
        View view=inflater.inflate(R.layout.frag_send, container, false);
        display=view.findViewById(R.id.txtView);
        display.setText("Please confirm that,\n");
        display.append("You are about to send money to this account"+"\n"+Accno_to+","+Accname_to+"\n");
        ok=view.findViewById(R.id.btn2send);

        cancel=view.findViewById(R.id.btn2cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Accbal_to=accountBal();
                if(Accbal_to<0){
                    String abalance=onAccount.getBal_to(Accno_to);
                    String bal=String.valueOf(abalance);
                    Toast.makeText(getActivity(),bal,Toast.LENGTH_LONG).show();
                }
                else{
                    if(Accpin.equals("null")){
                        Toast.makeText(getActivity(),"Please try again later",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Enter amount to Send");
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
                                            onAccount.onAccountUpdate(Accno_to, Accbal_to);
                                            String Pin = edtpin.getText().toString().trim();
                                            txtamount = edtamt.getText().toString();
                                            String rawpin = StringEn.decrypt(Accpin);
                                            if (Pin.equals(rawpin)) {
                                                int charges = 0;
                                                amount = Integer.parseInt(txtamount);
                                                final int send_to = Integer.valueOf(txtamount);
                                                if (amount <= 500) {
                                                    charges = 10;
                                                } else if (500 < amount & amount <= 2500) {
                                                    charges = 15;
                                                } else if (2500 < amount & amount <= 5000) {
                                                    charges = 27;
                                                } else if (5000 < amount & amount <= 10000) {
                                                    charges = 42;
                                                } else if (10000 < amount & amount <= 20000) {
                                                    charges = 87;
                                                } else {
                                                    charges = 113;
                                                }
                                                if (amount < 50) {
                                                    Toast.makeText(getActivity(), "!..cannot send less than 50", Toast.LENGTH_SHORT).show();

                                                } else if (amount > 100000) {
                                                    Toast.makeText(getActivity(), "!..can only send  maximum of ksh.100000  per transanction.", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    amount += charges;
                                                    if (amount > Accbal_fro) {
                                                        Toast.makeText(getActivity(), "!!..Not enough float to send Ksh." + amount, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        AlertDialog.Builder confirm = new AlertDialog.Builder(getActivity());
                                                        confirm.setTitle("Confirm Dialogue");
                                                        confirm.setCancelable(false);
                                                        confirm.setMessage(Accname_to+", account number"+Accno_to+"will receive amount Ksh."+send_to+ " @ charges cost of Ksh." + charges + ". Confirm?");
                                                        confirm.setPositiveButton("send", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Accbal_fro -= amount;
                                                                Accbal_to += send_to;
                                                                dialogue = new AlertDialog.Builder(getActivity());
                                                                String check_update = onAccount.onAccountUpdate(Accno_fro, Accbal_fro);
                                                                String check_update_to = onAccount.onAccountUpdate(Accno_to, Accbal_to);
                                                                String update_res = String.valueOf(check_update);
                                                                String update_res_to = String.valueOf(check_update_to);
                                                                if (update_res == "null" || update_res_to== "null") {
                                                                    Toast.makeText(getActivity(), "update not working", Toast.LENGTH_SHORT).show();
                                                                } else if (update_res.contains("Success") && update_res_to.contains("Success")) {
                                                                    dialogue.setMessage(onAccount.getTranscode() + ", Confirmed amount Ksh. " +txtamount+ " was successfully sent to "+Accname_to+",\n" +
                                                                            "New balance is Ksh." + Accbal_fro + " on " + onAccount.getTime());
                                                                     Accpin="null";
                                                                } else {
                                                                    dialogue.setMessage("Sorry, failed to execute operation, please try again later");
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
                                            } else {
                                                Toast.makeText(getActivity(), "Wrong pin was detected.. at this time", Toast.LENGTH_SHORT).show();
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
            }
        });
        return view;
    }
    public int accountBal(){
        String abalance=onAccount.getBal_to(Accno_to);
        String bal=String.valueOf(abalance);
        int balance;
        if (bal.contains("Network link failure")) {
            balance=0-1;
        } else if (bal=="null") {
            balance=0-1;
        }
        else if(bal.contains("Error fetching name") ||bal.contains("No results")){
            balance=0-1;
        }
        else{
            int accbal=Integer.parseInt(bal);
            balance=accbal;
        }
        return balance;
    }

}
