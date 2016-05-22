package com.android.prakharagarwal.popularmoviesp1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by prakharagarwal on 07/03/16.
 */
public class PosterAdapter extends ArrayAdapter<Movie> {
    public ArrayList<Movie> movieList;

    public PosterAdapter(Activity context, ArrayList<Movie> movieList) {

        super(context, 0, movieList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }


        ImageView posterView = (ImageView) convertView.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(movie.image).into(posterView);



        return convertView;
    }
    public static class ViewHolder {
        public final ImageView imageView;


        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.trailer_image);

        }
    }

}
