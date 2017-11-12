package com.example.n5050.imageviewerrxjava;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by n5050 on 11/12/2017.
 */

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {

    View view;
    private  List<String> list;
    private Context context;

    public ImageViewAdapter(Context context,List<String> list){
        this.context=context;
        this.list=list;
    }

    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        context=viewGroup.getContext();
        view= LayoutInflater.from(context).inflate(R.layout.image_item,viewGroup,false);
        ImageViewHolder imageViewHolder=new ImageViewHolder(view);
        return imageViewHolder;

    }

    public void onBindViewHolder(ImageViewHolder viewHolder,int i){

        final String imageUrl=list.get(i);

        Picasso.with(context).load(list.get(i)).resize(400,400).into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Toast.makeText(view.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,ImageFullView.class);
                intent.putExtra("path",imageUrl);
                context.startActivity(intent);

            }
        });

    }

    public int getItemCount(){
        if(list==null){
            return 0;
        }
        else{
            return list.size();
        }


    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;




        public ImageViewHolder(View v){
            super(v);
            imageView=(ImageView) v.findViewById(R.id.flagImage);

        }


    }
}
