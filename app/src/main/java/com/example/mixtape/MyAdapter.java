package com.example.mixtape;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixtape.ui.past.PastFragment;
import com.example.mixtape.ui.profile.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
//        holder.recCourse.setText(dataList.get(position).getDataArtists());
//        holder.recProf.setText(dataList.get(position).getDataTracks());
//        holder.recTime.setText(dataList.get(position).getDataUsername());
        //holder.recCourse.setText("mixtape");
        //holder.recProf.setText("username");
        holder.recTime.setText(dataList.get(position).getDataArtists());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("TOP ARTISTS", dataList.get(holder.getLayoutPosition()).getDataArtists());
//                intent.putExtra("username", dataList.get(holder.getLayoutPosition()).getDataTracks());
//                intent.putExtra("Key", dataList.get(holder.getLayoutPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList (ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recCourse, recProf, recTime;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        //recImage = itemView.findViewById(R.id.recImage);
        //recCourse = itemView.findViewById(R.id.recMixtape);
        //recProf = itemView.findViewById(R.id.recUsername);
        recTime = itemView.findViewById(R.id.recTop);
        recCard = itemView.findViewById(R.id.recCard);

    }
}