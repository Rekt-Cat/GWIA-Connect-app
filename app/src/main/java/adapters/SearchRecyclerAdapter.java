package adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maid.gwiaconnect.R;
import com.maid.gwiaconnect.UserProfile;
import com.maid.gwiaconnect.myProfile_fragment;

import java.util.ArrayList;

import Models.SearchModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.viewHolder> {
    ArrayList<SearchModel> data;
    Context context;
    FirebaseUser firebaseUser;
    SelectedUser selectedUser;
    public SearchRecyclerAdapter(ArrayList<SearchModel> data, Context context,SelectedUser selectedUser) {
        this.data = data;
        this.context = context;
        this.selectedUser=selectedUser;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_items,parent,false);
        return new viewHolder(view, selectedUser);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SearchModel sm =data.get(position);
        holder.followBut.setVisibility(View.VISIBLE);
        holder.fullName.setText(sm.getfullName());
        isFollowing(sm.getId(),holder.followBut);
        Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/gwia-connect.appspot.com/o/icons8-female-profile-30.png?alt=media&token=84e03a0e-30cf-40ab-b22c-dfcec3f9b576").into(holder.profilePic);

        if(sm.getId().equals(firebaseUser.getUid())){
            holder.followBut.setVisibility(View.GONE);
            Log.d(TAG, "onBindViewHolder: "+sm.getId());
            Log.d(TAG, "onBindViewHolder: "+firebaseUser.getUid());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",sm.getId());
                editor.apply();
                selectedUser.gotoUser(sm.getId());
            }
        });
        holder.followBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.followBut.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(sm.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(sm.getId()).child("Followers").child(firebaseUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(sm.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(sm.getId()).child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        TextView fullName;
        CircleImageView profilePic;
        Button followBut;
        SelectedUser selectedUser;
        public viewHolder(@NonNull View itemView,SelectedUser selectedUser) {
            super(itemView);
            this.selectedUser=selectedUser;
            fullName =itemView.findViewById(R.id.username);
            profilePic=itemView.findViewById(R.id.profilePic);
            followBut=itemView.findViewById(R.id.followBut);
        }
    }
    private void isFollowing(String userId,Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userId).exists()){
                    button.setText("Following");
                }
                else{
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public interface SelectedUser{
        void gotoUser(String userId);
    }
}
