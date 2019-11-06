package com.devpoint.realpros.mobibank;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Main extends AppCompatActivity implements Home_fragment.onLoginClick,Fragment_Main.onListSelectedListener, ToWdaw.onAccountSentListener, WdawMoney.onAccountUpdateListener {
   private String server_res;
   ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(findViewById(R.id.fragment_container)!=null){
            if(savedInstanceState!=null){
                return;
            }
            Home_fragment frag_home=new Home_fragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,frag_home,null).commit();
        }
    }

    @Override
    public void onAccountSent(String accno,int bal,String pin) {
        WdawMoney wdawMoney =new WdawMoney();
        Bundle bundle=new Bundle();
        bundle.putString("Account_number",accno);
        bundle.putInt("Account_balance",bal);
        bundle.putString("Account_pin",pin);
        wdawMoney.setArguments(bundle);

        FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, wdawMoney,null);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBalance(String accpin,int accbal) {
        ToBalance toBalance=new ToBalance();
        Bundle bundle=new Bundle();
        bundle.putString("Account_pin",accpin);
        bundle.putInt("Account_bal",accbal);
        toBalance.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toBalance,null);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onWithdraw(String accnumber,int accbal,String pin) {
        ToWdaw toWdaw =new ToWdaw();
        Bundle bundle=new Bundle();
        bundle.putString("Account number",accnumber);
        bundle.putInt("Account balance",accbal);
        bundle.putString("Account pin",pin);
        toWdaw.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, toWdaw,null);
        ft.addToBackStack(null);
        ft.commit();
    }

  /*  @Override
    public void onSend(String accno,String accpin,int accbal) {
        ToSend frag_send=new ToSend();
        Bundle bundle=new Bundle();
        bundle.putString("Account_number",accno);
        bundle.putString("Account_pin",accpin);
        bundle.putInt("Account_bal",accbal);
        frag_send.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag_send,null);
        ft.addToBackStack(null);
        ft.commit();

    }*/
    @Override
    public String onAccountUpdate(final String accno,final int accbal) {
        class OnUpdate extends AsyncTask<Void,Void,String> {
            @Override
            protected String doInBackground(Void... voids) {
                String acc_bal=String.valueOf(accbal);
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.0.19/restApi/update_account.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("Anumber", accno));
                    nameValuePairs.add(new BasicNameValuePair("Abalance", acc_bal));

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        String res = out.toString();
                        if(res=="Success" ) {
                            server_res="Success";
                        }
                        else {
                            server_res=res;
                        }
                    }
                    else {
                        server_res="Server unreacheable";
                    }

                } catch (Exception e) {
                    server_res="Error updating account server";
                }
                return server_res;
            }
            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
            }
        }
        OnUpdate onUpdate=new OnUpdate();
        onUpdate.execute();
        return server_res;
        }
    public void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        //appending it to string builder
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                }
                catch (Exception e) {
                    return "Network failure, could not reach server...";
                }

            }

        }
        GetJSON gJSON=new GetJSON();
        gJSON.execute();
    }
    public void loadIntoListView(String json) throws JSONException {
        //creating a new json array from the json string..
        JSONArray jsonArray=new JSONArray(json);

        //creating string array for the listview..
        String []accounts=new String[jsonArray.length()];

        //looping through all elements in json array..
        for(int i=0; i<=jsonArray.length(); i++){
            JSONObject jobject=jsonArray.getJSONObject(i);
            accounts[i]=jobject.getString("Aname");

        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,accounts);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public void onLogin(String Ano,String Aid,String Pin) {
        Fragment_Main fragment_main=new Fragment_Main();
        Bundle bundle=new Bundle();
        bundle.putString("Anumber",Ano);
        bundle.putString("Aid",Aid);
        bundle.putString("Apin",Pin);
        fragment_main.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment_main,null);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void sendMessage(String msg){
        int permissioncheck= ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissioncheck==PackageManager.PERMISSION_GRANTED){
            try {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("3342", null, "Hello was testting", null, null);
                Toast.makeText(this, "sms sent", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(this, "sms failed", Toast.LENGTH_SHORT).show();

            }
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        }
    }

  /*  @Override
    public void onRequestPermissionResult(int requestcode, @Nullable String[]permission,int []requescode){
        super.onRequestPermissionsResult(requestcode,permission,requescode);

    }*/
}
