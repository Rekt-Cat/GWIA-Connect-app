package com.maid.gwiaconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import Models.SearchModel;
import adapters.SearchRecyclerAdapter;


public class search_fragment extends Fragment implements SearchRecyclerAdapter.SelectedUser{

    EditText search;
    RecyclerView recyclerView;
    SearchRecyclerAdapter searchRecyclerAdapter;
    ArrayList<SearchModel> arr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search=view.findViewById(R.id.userSearchBox);
        search.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                .getSystemService(getContext().INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

        recyclerView = view.findViewById(R.id.searchRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        arr= new ArrayList<>();
        searchRecyclerAdapter= new SearchRecyclerAdapter(arr,getContext(),this);
        recyclerView.setAdapter(searchRecyclerAdapter);
        // readUser();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase(Locale.ROOT));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    private void searchUser(String s){
        Query query= FirebaseDatabase.getInstance().getReference("Users").orderByChild("fullName").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!search.getText().toString().equals("")){
                    arr.clear();
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        SearchModel sm = snapshot1.getValue(SearchModel.class);
                        arr.add(sm);
                    }
                    searchRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void gotoUser(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("UserId",userId);
        getParentFragmentManager().setFragmentResult("UserData",bundle);
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain,new UserProfile()).addToBackStack(null).commit();
    }
//    private void readUser(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(search.getText().toString().equals("")){
//                    arr.clear();
//                    for(DataSnapshot snapshot1:snapshot.getChildren()){
//                        SearchModel sm = snapshot1.getValue(SearchModel.class);
//                        arr.add(sm);
//                    }
//                    searchRecyclerAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}