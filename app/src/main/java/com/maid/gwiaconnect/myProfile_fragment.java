package com.maid.gwiaconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import Models.FeedModel;
import adapters.GridAdapter;

public class myProfile_fragment extends Fragment {
    GridView gridView;
    ImageButton signOut;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    TextView name, mail;
    ArrayList<FeedModel> arr;
    GridAdapter gridAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arr = new ArrayList<>();
        gridView = view.findViewById(R.id.dataGrid);
        name = view.findViewById(R.id.name);
        mail = view.findViewById(R.id.mail);
        mail = view.findViewById(R.id.mail);
        signOut = view.findViewById(R.id.signOut);
        setProfile();
        getGridImage();

        gridAdapter = new GridAdapter(arr,getContext());
        gridView.setAdapter(gridAdapter);



        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), SignIn.class));
            }
        });
    }


    private void setProfile() {
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isComplete()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        name.setText(String.valueOf(dataSnapshot.child("fullName").getValue()));
                        mail.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                    } else {
                        Toast.makeText(getContext(), "User does not exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed loading the data!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getGridImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FeedModel fm = new FeedModel();
                    if (dataSnapshot.child("Publisher").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
                gridAdapter.notifyDataSetChanged();
                Log.d("sheesh",""+arr.size());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
