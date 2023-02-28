package com.maid.gwiaconnect;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Models.FeedModel;
import adapters.FeedRecyclerAdapter;
import adapters.NewsRecyclerAdapter;

public class feed_fragment extends Fragment implements FeedRecyclerAdapter.SelectedUser {
    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;
    ArrayList<FeedModel> arr;
    ArrayList<String> followingList;
    FirebaseAuth auth;
    ImageButton searchBox,newPost;
    DatabaseReference reference;
    FeedRecyclerAdapter feedRecyclerAdapter;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isZeroFollowers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.feed_fragment, container, false);

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // swipeRefreshLayout=view.findViewById(R.id.feedRefresh);


        arr = new ArrayList<>();
        auth= FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        nestedScrollView=view.findViewById(R.id.feedScrollView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    isZeroFollowers =true;
                    if(isZeroFollowers){
                        nestedScrollView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_notfollowing));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);

        //boolean connected = isNetworkAvailable();
        //Log.d("lolt", "Internet "+connected);


            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar = view.findViewById(R.id.feedProgressBar);
                            progressBar.setVisibility(View.VISIBLE);

                        }
                    });
                    recyclerView = view.findViewById(R.id.recyclerView);

                    Log.d(TAG, "lol: "+firebaseUser);
                    isFollowing();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            feedRecyclerAdapter = new FeedRecyclerAdapter(arr,getContext(),feed_fragment.this::gotoUser);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(feedRecyclerAdapter);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });

//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    ExecutorService service = Executors.newSingleThreadExecutor();
//                    service.execute(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    swipeRefreshLayout.setRefreshing(true);
//
//                                }
//                            });
//
//                            //isFollowing();
//                            getFragmentManager().beginTransaction().detach(feed_fragment.this).attach(feed_fragment.this).commit();
//                            //feedRecyclerAdapter.notifyDataSetChanged();
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    swipeRefreshLayout.setRefreshing(false);
//                                }
//                            });
//
//                        }
//
//
//                    });
//
//
//                }
//            });

            searchBox=view.findViewById(R.id.searchBox);
            searchBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new search_fragment()).addToBackStack(null).commit();
                }
            });
            newPost=view.findViewById(R.id.newPost);
            newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new NewPostFragment()).addToBackStack(null).commit();
                }
            });
            firebaseUser.reload();
            if(!firebaseUser.isEmailVerified()){
                showAlertDialoge();
            }
    }
    private void isFollowing(){
        followingList= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());
                }
                for (int i = 0; i < followingList.size(); i++) {
                    Log.d(TAG, "onDataChange: "+followingList.get(i));
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    FeedModel fm = new FeedModel();
                    fm.setPublisher(dataSnapshot.child("Publisher").getValue().toString());
                    Log.d("ds", ""+dataSnapshot.getValue().toString());
                    Log.d("dms", ""+fm.toString());
                    for(String id: followingList){
                        Log.d("pub",fm.getPublisher());
                        if(fm.getPublisher().equals(id)){
                            fm.setImage(dataSnapshot.child("PostImage").getValue().toString());
                            fm.setCaption(dataSnapshot.child("Description").getValue().toString());
                            fm.setPostId(dataSnapshot.child("PostId").getValue().toString());

//                            Log.d("pub",fm.getPublisher());
//                            Log.d("pub",fm.getUserName());
                            Log.d("pub",fm.getImage());
//                            Log.d("pub",fm.getCaption());
                            arr.add(fm);
                        }
                        for(int i =0;i<arr.size();i++){
                            Log.d("Dsa", "onDataChange: "+arr.get(i));
                        }
                    }
                }
                Collections.reverse(arr);

                feedRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void showAlertDialoge(){
        firebaseUser.sendEmailVerification();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email not verified!");
        builder.setMessage("Please verify your email now. You cannot login next time without email verification. Please check your Spam folder if you cannot find the verification mail.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_APP_EMAIL);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void gotoUser(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("UserId",userId);
        getParentFragmentManager().setFragmentResult("UserData",bundle);
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain,new UserProfile()).addToBackStack(null).commit();
    }
}
