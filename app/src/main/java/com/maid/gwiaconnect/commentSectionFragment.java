package com.maid.gwiaconnect;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import Models.CommentModel;
import adapters.CommentRecyclerAdapter;


public class commentSectionFragment extends Fragment {

    EditText typeComment;
    ImageButton postComment;

    String postId,publisherId;

    RecyclerView recyclerView;
    CommentRecyclerAdapter commentRecyclerAdapter;
    ArrayList<CommentModel> data;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout=view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readComment();
                commentRecyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        data= new ArrayList<>();
        recyclerView=view.findViewById(R.id.commentsRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentRecyclerAdapter= new CommentRecyclerAdapter(data,getContext());

        recyclerView.setAdapter(commentRecyclerAdapter);


        typeComment=view.findViewById(R.id.typeComment);
        postComment=view.findViewById(R.id.postComment);

        Bundle bundle = this.getArguments();
        postId=bundle.getString("postId");
        publisherId=bundle.getString("publisherId");

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeComment.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Comment box is empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    addComment();
                }
            }
        });
        readComment();


    }
    private void addComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("comment",typeComment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());
        reference.push().setValue(hashMap);
    }
    private void readComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        Log.d("lol",postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("lol",snapshot.getChildren().toString());
                data.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Log.d("lol",snapshot1.toString());
                    Log.d("lol",snapshot1.child("comment").getValue().toString());
                    CommentModel cm = new CommentModel();
                    cm.setPublisher(snapshot1.child("publisher").getValue().toString());
                    cm.setUserComment(snapshot1.child("comment").getValue().toString());
                    data.add(cm);
                }
                for(int i =0;i<data.size();i++){
                    Log.d("poo", "onDataChange: "+data.get(i).toString());
                }
                commentRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}