package com.example.madcamp_week1;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Fragment1 extends Fragment {

    private ArrayList<Item> mArrayList;
    private DictionaryAdapter mAdapter;

    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_id);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        // parsing json
        AssetManager assetManager = getActivity().getAssets();

        try {
            InputStream is = assetManager.open("jsonDirectory/restaurantList.json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line!=null){
                buffer.append(line+"\n");
                line = reader.readLine();
            }

            String jsonData = buffer.toString();

            JSONArray jsonArray = new JSONArray(jsonData);

            for(int i = 0; i < jsonArray.length(); i++){
                switch (i){
                    case 0:
                        mArrayList.add(new Item("한식"));
                        break;
                    case 4:
                        mArrayList.add(new Item("일식"));
                        break;
                    case 13:
                        mArrayList.add(new Item("양식"));
                        break;
                    case 15:
                        mArrayList.add(new Item("기타 외국 음식"));
                        break;
                    case 17:
                        mArrayList.add(new Item("Pub & Bar"));
                        break;
                }

                JSONObject jo = jsonArray.getJSONObject(i);

                String name = jo.getString("name");
                String contact = jo.getString("contact");

                Dictionary data = new Dictionary(i+"", name, contact);
                mArrayList.add(new Item(data));
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new DictionaryAdapter(mArrayList);

        mAdapter.setOnItemClickListener(new DictionaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String phoneNumber = mArrayList.get(position).dict.getContact();
                phoneNumber = phoneNumber.replace("-", "");
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }
}