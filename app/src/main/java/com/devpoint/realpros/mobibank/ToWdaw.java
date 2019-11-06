package com.devpoint.realpros.mobibank;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ToWdaw extends Fragment {
    public OnAccount onAccount;
    private Button ok,test;
    private EditText agno;
    private AlertDialog.Builder dialog;
    private int Accountbalance=0-1;
    private String Accnumber,Accountpin;
    onAccountSentListener onAccountSentListener;

    public interface onAccountSentListener{
        void onAccountSent(String accno,int bal, String pin);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int balance;
        final String pin,anumber;
        Bundle bundle=getArguments();
        if(bundle!=null){
            anumber=bundle.getString("Account number");
            balance=bundle.getInt("Account balance");
            pin=bundle.getString("Account pin");
                if (balance >= 0) {
                    Accnumber=anumber;
                    Accountbalance = balance;
                    Accountpin=pin;
                }
                else if(pin.isEmpty()){
                    Toast.makeText(getActivity(), "No pin was fetched, try again", Toast.LENGTH_SHORT).show();
                }
            }
        View view=inflater.inflate(R.layout.frag_1, container, false);
        ok=view.findViewById(R.id.btnbal);
        agno=view.findViewById(R.id.edtAgno);
        test=view.findViewById(R.id.btntest1);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Acc_no=agno.getText().toString();
                if(Acc_no.equals("70900")){
                    onAccountSentListener.onAccountSent(Accnumber,Accountbalance,Accountpin);
                }
                else{
                    dialog=new AlertDialog.Builder(getActivity());
                    dialog.setMessage("Invalid agent number, cannot perform this operation.");
                    dialog.show();
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
            onAccountSentListener=(onAccountSentListener) activity;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(),"Casting error detected"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        agno.setText("");
    }
}