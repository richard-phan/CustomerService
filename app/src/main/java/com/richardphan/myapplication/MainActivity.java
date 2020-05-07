package com.richardphan.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// AIzaSyCClmzLquByGKLD4L1FGA41hnHj8L5cQ5k
public class MainActivity extends AppCompatActivity {
    private static HomeFragment home;
    private static PageFragment page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home = new HomeFragment();
        page = new PageFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, home).commit();
    }

    public static HomeFragment getHomeFragment() {
        return home;
    }

    public static PageFragment getPageFragment() {
        return page;
    }
}
