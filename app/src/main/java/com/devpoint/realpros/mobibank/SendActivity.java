package com.devpoint.realpros.mobibank;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SendActivity extends AppCompatActivity implements ToSend.onAccValidatioListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(findViewById(R.id.fragment_send)!=null){
            if(savedInstanceState!=null){
                return;
            }
            ToSend toSend=new ToSend();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_send,toSend,null).commit();
        }
    }

    @Override
    public void onAccValid(String accno_fro, String accno_to,String accname_to, int bal_fro, String pin) {
        SendMoney sendMoney=new SendMoney();
        Bundle bundle=new Bundle();
        bundle.putString("Account_from",accno_fro);
        bundle.putString("Account_to",accno_to);
        bundle.putString("Account_name_to",accname_to);
        bundle.putInt("Account_from_bal",bal_fro);
        bundle.putString("Account_pin",pin);
        sendMoney.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragment_send, sendMoney,null);
        ft.addToBackStack(null);
        ft.commit();
    }
}
