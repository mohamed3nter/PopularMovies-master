package com.example.mohamedanter.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v4.widget.CursorAdapter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Mohamed Anter on 8/22/2015.
 */
public class CustomAdaptor extends CursorAdapter {
    ViewHolder holder = null;
    int width,height,img_width;
    public CustomAdaptor(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        if (width<600)
            img_width=width/2;
        else
            img_width=(width/2)/2;
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        width=(int)context.getResources().getDimension(R.dimen.image_width);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (ViewHolder) view.getTag();
        String x = cursor.getString(PlaceholderFragment.COL_POSTER_PATH);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + cursor.getString(PlaceholderFragment.COL_POSTER_PATH))
                .resize(img_width,(int)(img_width*1.5)).into(holder.PosterView);
        //holder.PosterView.setImageResource(R.drawable.img);
    }

    public static class ViewHolder {
        private ImageView PosterView;

        public ViewHolder(View view) {
            PosterView = (ImageView) view.findViewById(R.id.image);
        }
    }
}