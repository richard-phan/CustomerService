package com.richardphan.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PageFragment extends Fragment {
    private View view;

    private Button btnBack;
    private TextView tvName;
    private TextView tvLoc;
    private TextView tvWebsite;
    private ArrayList<String> names;
    private ArrayList<String> locations;

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_page, container, false);

        btnBack = view.findViewById(R.id.back);
        tvName = view.findViewById(R.id.tvName);
        tvLoc = view.findViewById(R.id.tvLoc);
        tvWebsite = view.findViewById(R.id.website);

        Bundle args = getArguments();
        names = args.getStringArrayList("nameList");
        locations = args.getStringArrayList("locList");

        tvName.setText(args.getString("name"));
        tvLoc.setText(args.getString("loc"));
        tvWebsite.setText(args.getString("website"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("nameList", names);
                bundle.putStringArrayList("locList", locations);
                Fragment home = new HomeFragment();
                home.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, home).commit();
            }
        });

        return view;
    }
}
