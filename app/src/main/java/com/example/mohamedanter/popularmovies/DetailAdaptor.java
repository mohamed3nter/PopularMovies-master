package com.example.mohamedanter.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Anter on 10/5/2015.
 */
public class DetailAdaptor extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int VIEW_TYPE_TRAILER= 0;
    private static final int VIEW_TYPE_REVIEW = 1;
    private static final int VIEW_TYPE_HEADER= 2;
    List ListItems;
    Context context;
    public DetailAdaptor (Context context) {
        this.context = context;
        ListItems = new ArrayList();
    }
    @Override
    public int getCount() {
        return ListItems.size();
    }
    @Override
    public Object getItem(int position) {
        return ListItems.get(position);
    }
    public void addItem(Object item) {
        this.ListItems.add(item);
    }
    public void addHeader(Object item){
        this.ListItems.add(item);
    }
    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Trailer)
            return VIEW_TYPE_TRAILER;
        else if (getItem(position) instanceof Review)
            return VIEW_TYPE_REVIEW;
        else
            return VIEW_TYPE_HEADER;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder ;
        int type = getItemViewType(position);
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            switch (type) {
                case VIEW_TYPE_HEADER:
                    convertView = inflater.inflate(R.layout.header_item,parent,false);
                    viewHolder.header = (TextView) convertView.findViewById(R.id.header);
                    break;
                case VIEW_TYPE_TRAILER:
                    convertView = inflater.inflate(R.layout.trailer_item, parent, false);
                    viewHolder.trailername = (TextView) convertView.findViewById(R.id.Trailername);
                    break;
                case VIEW_TYPE_REVIEW:
                    convertView = inflater.inflate(R.layout.review_item, parent, false);
                    viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                    viewHolder.author = (TextView) convertView.findViewById(R.id.name);
                    break;
                default:
                    break;
            }
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();
        if (type == VIEW_TYPE_TRAILER) {
            Trailer trailer= (Trailer) ListItems.get(position);
            viewHolder.trailername.setText(trailer.TrailerName);
        }
        if (type == VIEW_TYPE_REVIEW) {
            Review review = (Review)ListItems.get(position);
            viewHolder.author.setText(review.AuthorName);
            viewHolder.content.setText(review.Textcontent);
        }
        if (type == VIEW_TYPE_HEADER) {
            String header = (String) ListItems.get(position);
            viewHolder.header.setText(header);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView trailername;
        TextView header;
        TextView content;
        TextView author;

    }

}
