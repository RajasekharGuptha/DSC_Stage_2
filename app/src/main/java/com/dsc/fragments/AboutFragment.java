package com.dsc.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dsc.R;


public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_about_layout, container, false);

        final String dsc_web_link="https://dscsastra.com/";
        Button dscWebsiteBtn=rootView.findViewById(R.id.visit_dsc_web_btn);
        dscWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(dsc_web_link));
                if (getActivity()!=null && webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(webIntent);
                }

            }
        });
        return rootView;
    }
}