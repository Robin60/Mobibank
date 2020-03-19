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
import android.widget.TextView;
import android.widget.Toast;

public class ToSend extends Fragment {
    private String Accpin, Accno;
    int Accbal=0-1;
    OnAccount onAccount = new OnAccount();
    private TextView txtacc;
    private Button ok;
    onAccValidatioListener onAccValidatioListener;
    public ToSend() {
    }
    public interface onAccValidatioListener{
        void onAccValid(String accno_fro,String accno_to,String accname_to,int bal_fro,String pin);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle=getActivity().getIntent().getExtras();
        if(bundle!=null){
            String accno=bundle.getString("Account_number");
            String accpin=bundle.getString("Account_pin");
            int accbal=bundle.getInt("Account_bal");
            Accno=accno;
            Accpin=accpin;
            Accbal=accbal;
        }
        else{
            Toast.makeText(getActivity(),"There are missing argument",Toast.LENGTH_LONG).show();
        }

        View view = inflater.inflate(R.layout.frag_tosend, container, false);
        ok=view.findViewById(R.id.btn_ok);
        txtacc=view.findViewById(R.id.txt_Acc);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Accno_to=txtacc.getText().toString();
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                if(Accno_to.isEmpty() ||Accno_to.equals(null)){
                    txtacc.requestFocus();
                }
                else{

                onAccount.getAname(Accno_to);
                String Aname=String.valueOf(onAccount.getAname(Accno_to));
                if(Aname.contains("No results")||Aname.contains("Error fetching name")){
                    dialog.setMessage("Invalid account number, cannot send money to this account.");
                    txtacc.requestFocus();
                }
                else if(Aname.contains("null")){
                    Toast.makeText(getActivity(),"Please try again",Toast.LENGTH_LONG).show();
                    txtacc.requestFocus();
                }
                else if(Accbal<0){
                    Toast.makeText(getActivity(),"Please try again",Toast.LENGTH_LONG).show();
                    txtacc.requestFocus();
                }
                else if(Accno_to.equals(Accno)){
                    dialog.setMessage("! Cannot send money to your own account !");
                    txtacc.requestFocus();
                }
                else{
                    txtacc.setText("");
                    onAccValidatioListener.onAccValid(Accno,Accno_to,Aname,Accbal,Accpin);
                }
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
            onAccValidatioListener=(ToSend.onAccValidatioListener) activity;
        }
        catch (ClassCastException e){
            Toast.makeText(getActivity(),"Casting error detected"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
