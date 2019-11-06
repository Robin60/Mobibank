package com.devpoint.realpros.mobibank;
import android.os.AsyncTask;
import android.util.Log;
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

public class OnAccount {
    private String finalbal,accname,finalbal_to;
    private String finalpin;
    private String server_res="";
    public void onAccountUpdate(){
        class OnUpdate extends AsyncTask<Void,Void,String>{
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.0.19/restApi/update_account.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("Anumber", "183063339"));
                    nameValuePairs.add(new BasicNameValuePair("Abalance", "2961"));

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        String res = out.toString();
                        if(res=="Success") {
                            server_res=res;
                        }
                        else{
                            server_res=res;
                        }
                    }
                    else {
                        server_res="Catastropic error";
                    }

                } catch (Exception e) {
                    server_res="Error updating account server";
                }
                Log.d("Devp",server_res);
                return server_res;
            }
            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
            }
        }
        OnUpdate onUpdate=new OnUpdate();
        onUpdate.execute();
    }

    public String onAccountUpdate(final String accno, final int accbal) {
        class OnUpdate extends AsyncTask<Void,Void,String> {
            String acc_bal=String.valueOf(accbal);
            @Override
            protected String doInBackground(Void... voids) {
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
    public String getBal(final String Anumber,final String Aid){
        class GetAccount extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.0.19/restApi/fetch_balance.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("Anumber", Anumber));
                    nameValuePairs.add(new BasicNameValuePair("Aid", Aid));

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        String balance = out.toString();
                        if(!(balance.isEmpty()) || !(balance.contains(null))) {
                            finalbal = balance;
                        }
                        else {
                            finalbal="0 results";
                        }
                    }
                    return finalbal;
                } catch (Exception ee) {
                    return "Network link failure, couldn't reach the server..";
                }
            }
            @Override
            protected void onPostExecute(String data){
                super.onPostExecute(data);
            }
        }
        GetAccount Gacc=new GetAccount();
        Gacc.execute();
        return finalbal;
    }
    public String getAname(final String aname){
        class GetAccount extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.0.19/restApi/check_account.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("Anumber", aname));

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        String resp = out.toString();
                        if(resp=="No results"){
                            accname="No results";
                        }
                        else if(!(resp.isEmpty()) || !(resp.contains(null))) {
                            accname = resp;
                        }
                        else {
                            accname="Error fetching name";
                        }
                    }
                    return accname;
                } catch (Exception ee) {
                    return "Network link failure, couldn't reach the server..";
                }
            }
            @Override
            protected void onPostExecute(String data){
                super.onPostExecute(data);
            }
        }
        GetAccount Gacc=new GetAccount();
        Gacc.execute();
        return accname;
    }
    public String getBal_to(final String accno){
        class GetAccount extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.0.19/restApi/check_balance.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("Anumber", accno));
                   // nameValuePairs.add(new BasicNameValuePair("Aname", acc_name));

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        String resp = out.toString();
                        if(!(resp.isEmpty()) || !(resp.contains(null))) {
                            finalbal_to = resp;
                        }
                        else if(resp=="No results"){
                            finalbal_to="No results";
                        }
                        else {
                            finalbal_to="Error fetching name";
                        }
                    }
                    return finalbal_to;
                } catch (Exception ee) {
                    return "Network link failure, couldn't reach the server..";
                }
            }
            @Override
            protected void onPostExecute(String data){
                super.onPostExecute(data);
            }
        }
        GetAccount Gacc=new GetAccount();
        Gacc.execute();
        return finalbal_to;
    }
    public int getRescode(String resechoed){
        int rescode;
        if(resechoed.isEmpty()){
            rescode=404;
        }
        else if(resechoed.contains("0 result")) {
            rescode = 406;
        }

        else if (resechoed.contains("==")){
            rescode=200;
        }
        else if (resechoed=="null"){
            rescode=400;
        }
        else {
            rescode=505;
        }
        return rescode;
    }
    public String getTranscode(){
        Random rand=new Random();
        int tcode=rand.nextInt(10000);
        String transcode="CWXTRE"+tcode;
        return  transcode;
    }
    public Timestamp getTime(){
        Date date=new Date();
        Timestamp transtime=new Timestamp(date.getTime());
        return transtime;
    }
}
