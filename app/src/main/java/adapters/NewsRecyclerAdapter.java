package adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maid.gwiaconnect.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import Models.NewsModel;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.viewHolder> {


    ArrayList<NewsModel> data;
    Context context;

    public NewsRecyclerAdapter(ArrayList<NewsModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.newsitem,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NewsModel nm = data.get(position);
        holder.cat.setText(nm.getCat());
        holder.title.setText(nm.getTitle());
        holder.date.setText(nm.getDate());
        holder.text.setText(nm.getText());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView cat;
        TextView title;
        TextView date;
        ExpandableTextView text;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

           cat=itemView.findViewById(R.id.cat);
           title=itemView.findViewById(R.id.postTitle);
           date=itemView.findViewById(R.id.date);
           text= itemView.findViewById(R.id.expand_text_view);

        }
    }
}
