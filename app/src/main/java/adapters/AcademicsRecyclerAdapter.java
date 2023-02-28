package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maid.gwiaconnect.R;

import java.util.ArrayList;

import Models.AcademicsModel;

public class AcademicsRecyclerAdapter extends RecyclerView.Adapter<AcademicsRecyclerAdapter.viewHolder>{
    ArrayList<AcademicsModel> data;
    Context context;

    public AcademicsRecyclerAdapter(ArrayList<AcademicsModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.academicsitem,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AcademicsModel am= data.get(position);
        holder.time.setText(am.getTime());
        holder.subject.setText(am.getSubject());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView time,subject;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            subject=itemView.findViewById(R.id.subject);
        }
    }
}
