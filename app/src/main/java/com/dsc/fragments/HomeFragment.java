package com.dsc.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsc.R;
import com.dsc.recyclerviewUtils.home_rv_adapter;
import com.dsc.recyclerviewUtils.home_rv_item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;


public class HomeFragment extends Fragment implements home_rv_adapter.listItemClickListener {

    RecyclerView recyclerView;
    home_rv_adapter recyclerView_adapter;
    static ArrayList<home_rv_item> feedItemsArrayList;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView errorTextView;
    SharedPreferences sharedPreferences;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_layout, container, false);

        feedItemsArrayList = new ArrayList<>();
        errorTextView = rootView.findViewById(R.id.error_textview);

        //swipe refresh layout setup
        swipeRefreshLayout = rootView.findViewById(R.id.home_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });

        // recycler view setup
        recyclerView = rootView.findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_adapter = new home_rv_adapter(getActivity(), feedItemsArrayList, this);
        recyclerView.setAdapter(recyclerView_adapter);

        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        }

        // fetches data

        fetchData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_frag_menu, menu);
        if (getActivity() != null) {
            // implementing search
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setIconifiedByDefault(true);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    recyclerView_adapter.getFilter().filter(query);

                    return true;

                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    recyclerView_adapter.getFilter().filter(newText);

                    return true;

                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {

        }
        return super.onOptionsItemSelected(item);
    }

    // fetches data from api link provided
    private void fetchData() {
        if (isNetworkConnected()) {
            errorTextView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Accept-Encoding", "UTF-8")
                    .url("https://wayhike.com/dsc/demo_app_api.php")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    handleError(e.getMessage());
                }

                @Override
                public void onResponse(final Response response) {
                    if (!response.isSuccessful()) {
                        handleError("Request Failed");
                    } else {
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String output = response.body().string();
                                        JSONObject raw_json_result = new JSONObject(output);
                                        JSONArray event_titles_array = raw_json_result.getJSONArray("event_titles");
                                        parseJSONToItems(event_titles_array, true);
                                    } catch (JSONException | IOException e) {
                                        handleError("Output Error");
                                    }
                                }
                            });

                    }
                }
            });
        } else {
            handleError(getString(R.string.no_internet_string));
        }
    }

    // methoid to handle error i.e to show error messages etc..
    private void handleError(String errorExtra) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(MessageFormat.format("{0}{1}{2}", getString(R.string.error_expression), "\n", errorExtra));
        swipeRefreshLayout.setRefreshing(false);
        if (errorExtra.equals(getString(R.string.no_internet_string))) {
            String offlineData = sharedPreferences.getString(getString(R.string.offline_data), null);
            if (offlineData != null) {
                errorTextView.setText(MessageFormat.format("{0}{1}{2}{3}", getString(R.string.error_expression), "\n", errorExtra, "\nShowing Offline Data"));

                handleOfflineData(offlineData);
            }
        }
    }

    // parses offline data
    private void handleOfflineData(String offlineData) {
        Gson gson = new Gson();
        Type jsonTypeToken = new TypeToken<JSONArray>() {
        }.getType();
        if (offlineData != null) {
            JSONArray raw_offline_data = gson.fromJson(offlineData, jsonTypeToken);
            parseJSONToItems(raw_offline_data, false);
        }
    }

    // parses json and add elements to adapter list
    private void parseJSONToItems(JSONArray values, Boolean saveOffline) {
        try {
            if (saveOffline)
                saveDataOffline(values);
            feedItemsArrayList.clear();
            for (int i = 0; i < values.length(); i++) {
                feedItemsArrayList.add(new home_rv_item(values.getString(i)));
            }
        } catch (JSONException e) {
            handleError("Output Error");
        }

        swipeRefreshLayout.setRefreshing(false);
        recyclerView_adapter.notifyDataSetChanged();
    }


    // saves recently fetched data oto use in offline
    private void saveDataOffline(JSONArray dataArray) {
        Gson gson = new Gson();
        Type jsonTypeToken = new TypeToken<JSONArray>() {
        }.getType();
        String jsonDataString = gson.toJson(dataArray, jsonTypeToken);
        sharedPreferences.edit().putString(getString(R.string.offline_data), jsonDataString).apply();
        Log.e("TAGGG", "saveDataOffline: " + jsonDataString);
    }

    @Override
    public void onListItemClick(int clickInt) {
        // handle recycler view item click here
    }

    // check if network connection is there
    private boolean isNetworkConnected() {
        boolean Wifi = false;
        boolean Mobile = false;

        if (getActivity() != null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        Wifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        Mobile = true;
            }
        }
        return Wifi || Mobile;
    }


}