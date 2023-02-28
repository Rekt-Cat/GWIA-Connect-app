package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maid.gwiaconnect.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Models.CommentModel;
import Models.FeedModel;
import Models.SearchModel;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.viewHolder> {
    ArrayList<CommentModel> data;
    Context context;

    FirebaseUser firebaseUser;

    public CommentRecyclerAdapter(ArrayList<CommentModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        CommentModel cm = data.get(position);
        Log.d("lolp", "onBindViewHolder: "+cm.getUserComment());
        holder.userComment.setText(cm.getUserComment());
        getUserInfo(holder.user,cm.getPublisher());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        public TextView user;
        public TextView userComment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            user=itemView.findViewById(R.id.user);
            userComment=itemView.findViewById(R.id.userComment);
        }
    }
    private void getUserInfo(TextView user,String publisherId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SearchModel fm = snapshot.getValue(SearchModel.class);
                user.setText(fm.getfullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
