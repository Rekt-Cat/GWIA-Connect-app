package com.maid.gwiaconnect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import Models.FeedModel;
import adapters.GridAdapter;
import adapters.UserGridAdapter;

public class UserProfile extends Fragment {

    DatabaseReference reference;
    TextView name,mail;
    ArrayList<FeedModel> arr;
    UserGridAdapter userGridAdapter;
    GridView gridView;
    String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView=view.findViewById(R.id.userDataGrid);
        arr=new ArrayList<>();
        getParentFragmentManager().setFragmentResultListener("UserData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                userId= result.getString("UserId");
                name=view.findViewById(R.id.user_name);
                mail=view.findViewById(R.id.user_mail);
                reference= FirebaseDatabase.getInstance().getReference("Users");
                reference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isComplete()){
                            if(task.getResult().exists()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                name.setText(String.valueOf(dataSnapshot.child("fullName").getValue()));
                                mail.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                            }
                            else{
                                Toast.makeText(getContext(), "User does not exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "Failed loading the data!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        getGridImage();
        userGridAdapter = new UserGridAdapter(arr,getContext());
        gridView.setAdapter(userGridAdapter);


    }
    private void getGridImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FeedModel fm = new FeedModel();
                    if (dataSnapshot.child("Publisher").getValue().toString().equals(userId)) {
                        Log.d("sheesh",""+dataSnapshot.child("Publisher").getValue().toString()+"="+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Log.d("sheesh",""+dataSnapshot.child("PostImage").getValue().toString());
                        fm.setImage(dataSnapshot.child("PostImage").getValue().toString());
                        arr.add(fm);
                    }
                }
                for (int i = 0; i < arr.size(); i++) {
                    Object obj = arr.get(i);
                    Log.d("sheesh", ""+obj);
                }
                Collections.reverse(arr);
                userGridAdapter.notifyDataSetChanged();
                Log.d("sheesh",""+arr.size());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}