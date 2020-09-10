package com.dsc;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dsc.fragments.AboutFragment;
import com.dsc.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawermenu;
    ActionBarDrawerToggle menu_toggle;
    NavigationView navigationView;
    MenuItem prevmenuItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar setup
        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        //drawer menu setup
        drawermenu = findViewById(R.id.main_activity_drawer);
        menu_toggle = new ActionBarDrawerToggle(this, drawermenu, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawermenu.addDrawerListener(menu_toggle);
        menu_toggle.syncState();
        navigationView = findViewById(R.id.main_activity_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // showing Home on app opening
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
        prevmenuItem = navigationView.getMenu().getItem(0);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (prevmenuItem != null) {
            prevmenuItem.setChecked(false);
        }
        Fragment selectedFragment=new HomeFragment();

        switch (menuItem.getItemId()) {
            case R.id.dm_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.dm_about_us:
                selectedFragment = new AboutFragment();
                break;
        }
        menuItem.setChecked(true);
        prevmenuItem = menuItem;
        drawermenu.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (menu_toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}