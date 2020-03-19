package com.devpoint.realpros.mobibank;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

public class Terms_A extends Fragment {
    public Terms_A() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_terms, container, false);
        WebView webView=view.findViewById(R.id.webview);
            webView.loadUrl("http://192.168.0.19:8080/realpros/Terms&agreements.html");

        return view;
    }

}
