package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maid.gwiaconnect.R;

import java.util.ArrayList;

import Models.FeedModel;

public class UserGridAdapter extends BaseAdapter {
    ArrayList<FeedModel> data;
    Context context;
    public UserGridAdapter(ArrayList<FeedModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.gridimage,null);
        ImageView img=view.findViewById(R.id.profilePost);


        FeedModel fm = data.get(position);
        Log.d("peesh",fm.getImage());
        Glide.with(context).load(fm.getImage()).into(img);

        return view;
    }
}
