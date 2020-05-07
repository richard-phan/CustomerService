package com.richardphan.myapplication;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// AIzaSyCClmzLquByGKLD4L1FGA41hnHj8L5cQ5k
public class HomeFragment extends Fragment implements LocationListener {
    private View view;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btnSearch;
    private SeekBar sbRadius;
    private TextView tvRadius;
    private ListView listPlaces;

    private ArrayList<String> names;
    private ArrayList<String> locations;
    private ArrayList<String> ids;
    private ArrayAdapter adapter;

    private double lattitude;
    private double longitude;
    private double radius;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btnSearch = view.findViewById(R.id.search);
        sbRadius = view.findViewById(R.id.seekBar);
        tvRadius = view.findViewById(R.id.radius_label);
        listPlaces = view.findViewById(R.id.list_places);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Bundle args = getArguments();
        if (args != null && args.containsKey("nameList")) {
            names = args.getStringArrayList("nameList");
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names);
            listPlaces.setAdapter(adapter);
        }

        if (args != null && args.containsKey("locList")) {
            locations = args.getStringArrayList("locList");
        }


        listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeId = ids.get(position);
                String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&fields=name,vicinity,website&key=AIzaSyCClmzLquByGKLD4L1FGA41hnHj8L5cQ5k";
                final int pos = position;

                OkHttpClient client = new OkHttpClient();
                Request onRequest = new Request.Builder().url(url).build();

                client.newCall(onRequest).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        final Bundle bundle = new Bundle();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject Jobject = new JSONObject(res);

                                    Iterator<String> key = Jobject.keys();
                                    for (Iterator<String> it = key; it.hasNext(); ) {
                                        String k = it.next();
                                        System.out.println(k);
                                    }
                                    JSONObject obj = (JSONObject) Jobject.get("result");
                                    System.out.println(obj);
                                    String website = (String) obj.get("website");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", names.get(pos));
                                    bundle.putString("loc", locations.get(pos));
                                    bundle.putStringArrayList("nameList", names);
                                    bundle.putStringArrayList("locList", locations);
                                    bundle.putString("website", website);

                                    Fragment page = new PageFragment();
                                    page.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, page).commit();
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            }
                        });
                    }
                });
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radius = convertMilesToMeters(sbRadius.getProgress() + 1);
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lattitude + "," + longitude + "&radius=" + radius + "&type=food,restaurant&fields=name,vicinity,website&opennow=true&key=AIzaSyCClmzLquByGKLD4L1FGA41hnHj8L5cQ5k";

                // WHITEBOX TEST with incorrect API KEY
                //url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lattitude + "," + longitude + "&radius=" + radius + "&type=food,restaurant&fields=name,vicinity,website&opennow=true&key=AIzaSyCClmzLquByGKLD4L1F4A41hnHj8L5cQ5k";
                System.out.println(url);
                System.out.println("Location: " + lattitude + " " + longitude);

                OkHttpClient client = new OkHttpClient();

                Request onRequest = new Request.Builder().url(url).build();

                client.newCall(onRequest).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.err.println("Failed to get a url response");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject Jobject = new JSONObject(res);

                                    try {
                                        String error = Jobject.getString("error_message");
                                        System.err.println(error);
                                        return;
                                    } catch (Exception e) {

                                    }

                                    JSONArray Jarray = Jobject.getJSONArray("results");

                                    System.out.println(Jarray);

                                    names = new ArrayList<>();
                                    locations = new ArrayList<>();
                                    ids = new ArrayList<>();
                                    for (int i = 1; i < Jarray.length(); i++) {
                                        JSONObject obj = (JSONObject) Jarray.get(i);

                                        String name = (String) obj.get("name");
                                        String loc = (String) obj.get("vicinity");
                                        String id = (String) obj.get("place_id");
                                        names.add(name);
                                        locations.add(loc);
                                        ids.add(id);
                                    }
                                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names);
                                    listPlaces.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });

        tvRadius.setText("Radius: " + (sbRadius.getProgress() + 1) + " miles");
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadius.setText("Radius: " + (progress + 1) + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    public double convertMilesToMeters(double miles) {
        return miles * 1609.34;
    }

    @Override
    public void onLocationChanged(Location location) {
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}