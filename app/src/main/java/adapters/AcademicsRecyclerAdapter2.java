package adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maid.gwiaconnect.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Locale;

import Models.AcademicsModel2;

public class AcademicsRecyclerAdapter2 extends  RecyclerView.Adapter<AcademicsRecyclerAdapter2.viewHolder> {
    ArrayList<AcademicsModel2> data;
    Context context;

    public AcademicsRecyclerAdapter2(ArrayList<AcademicsModel2> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public AcademicsRecyclerAdapter2.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.academicitems2,parent,false);
        return new AcademicsRecyclerAdapter2.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AcademicsModel2 am= data.get(position);
        holder.subject.setText(am.getSubject());
        holder.pi.addPieSlice(new PieModel(am.getSubject(),am.getPercent(), Color.parseColor("#f76363")));
        holder.pi.addPieSlice(new PieModel("full",100-am.getPercent(), Color.parseColor("#FFFFFF")));
        holder.pi.startAnimation();
        holder.pi.setInnerValueString(String.valueOf(am.getPercent())+"%");
        holder.pi.setUseCustomInnerValue(true);
        holder.pi.setValueTextSize(100f);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView subject;
        PieChart pi;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            subject=itemView.findViewById(R.id.Rsubject);
            pi=itemView.findViewById(R.id.pi);

        }
    }
}
