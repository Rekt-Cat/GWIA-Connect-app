package adapters;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maid.gwiaconnect.MainActivity;
import com.maid.gwiaconnect.NewPostFragment;
import com.maid.gwiaconnect.R;
import com.maid.gwiaconnect.commentSectionFragment;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import Models.FeedModel;
import Models.SearchModel;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.viewHolder> {

    ArrayList<FeedModel> data = new ArrayList<>();
    Context context;
    SelectedUser selectedUser;

    public FeedRecyclerAdapter(ArrayList<FeedModel> data, Context context, SelectedUser selectedUser) {
        this.data = data;
        this.context = context;
        this.selectedUser=selectedUser;
    }
    private void isLiked(String postId,ImageView imageview){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageview.setImageResource(R.drawable.ic_action_name);
                    imageview.setTag("liked");
                }
                else{
                    imageview.setImageResource(R.drawable.ic_like);
                    imageview.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void likes(TextView likes,String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(TextView username,String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SearchModel sm = snapshot.getValue(SearchModel.class);
                username.setText(sm.getfullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getComments(String postId,TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.feeditems,parent,false);
        return new viewHolder(v,selectedUser);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if(position%1==0 && position!=1){

            AdLoader.Builder builder = new AdLoader.Builder(
                    context, "ca-app-pub-3940256099942544/2247696110");

            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    holder.templateView.setNativeAd(unifiedNativeAd);
                }
            });
            final AdLoader adLoader = builder.build();
            adLoader.loadAd(new AdRequest.Builder().build());
            holder.templateView.setVisibility(View.VISIBLE);
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FeedModel fm = data.get(position);
        Log.d("pubRec",fm.getImage());
        Glide.with(context).load(fm.getImage()).into(holder.pic);

        if(fm.getCaption().equals("")){
            holder.description.setVisibility(View.GONE);

        }
        else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(fm.getCaption());
        }
        publisherInfo(holder.userName,fm.getPublisher());


        isLiked(fm.getPostId(),holder.like);
        likes(holder.likes, fm.getPostId());
        getComments(fm.getPostId(), holder.comments);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(fm.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(fm.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle= new Bundle();
                bundle.putString("postId",fm.getPostId());
                bundle.putString("publisherId",fm.getPublisher());
                commentSectionFragment csFragment = new commentSectionFragment();
                csFragment.setArguments(bundle);
                ((MainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, csFragment).addToBackStack(null).commit();
            }
        });
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUser.gotoUser(fm.getPublisher());
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size()  ;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView pic,like,comment;
        TextView userName,comments,likes;
        ExpandableTextView description;
        TemplateView templateView;
        SelectedUser selectedUser;
        public viewHolder(@NonNull View itemView,SelectedUser selectedUser) {
            super(itemView);
            this.selectedUser=selectedUser;
            templateView=itemView.findViewById(R.id.my_template);
            comment=itemView.findViewById(R.id.comment);
            like=itemView.findViewById(R.id.like);
            pic = itemView.findViewById(R.id.pic);
            userName = itemView.findViewById(R.id.userName);
            comments=itemView.findViewById(R.id.comments);
            likes=itemView.findViewById(R.id.likes);
            description=itemView.findViewById(R.id.description);
        }
    }
    public static class adViewHolder extends RecyclerView.ViewHolder {

        public adViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
    public interface SelectedUser{
        void gotoUser(String userId);
    }
}
