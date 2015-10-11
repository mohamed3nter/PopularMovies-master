package com.example.mohamedanter.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedanter.popularmovies.Data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Anter on 9/29/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final String DETAIL_URI = "URI";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int LOADER = 0;
    private Uri mUri;
    private Uri mfavUriWithId;
    private TextView t1;
    private ImageView img;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private String SelectedMovieID ="0";
    private Movie SelectedItem;
    private Button fav_button;
    View rootView,headerView;
    ListView detailList;
    private ArrayList<Review> ReviewData;
    private ArrayList<Trailer> TrailerData;
    private DetailAdaptor detailAdaptor;
    private boolean isFavorite=false;
    private static final String SHARE_HASHTAG = " #PopularMoviesApp";

    public DetailFragment() {
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        headerView = inflater.inflate(R.layout.detail_item, null, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        if(mUri != null){
            SelectedMovieID = mUri.getPathSegments().get(1);
        }
        detailList =(ListView)rootView.findViewById(R.id.detail_list_view);
        t1 = (TextView) headerView.findViewById(R.id.Movie_name);
        img = (ImageView) headerView.findViewById(R.id.image);
        t2 = (TextView) headerView.findViewById(R.id.Year);
        t3 = (TextView) headerView.findViewById(R.id.Lenth);
        t4 = (TextView) headerView.findViewById(R.id.rate);
        t5 = (TextView) headerView.findViewById(R.id.review);
        fav_button=(Button)headerView.findViewById(R.id.favorite);
        TrailerData=new ArrayList<Trailer>();
        ReviewData=new ArrayList<Review>();
        detailList.addHeaderView(headerView);
        detailAdaptor=new DetailAdaptor(getActivity());
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) instanceof Trailer) {
                    Trailer temp= (Trailer) parent.getItemAtPosition(position);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + temp.TrailerKey));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {

                    }
                }
            }
        });
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            String TrailerLink=null;
            if (TrailerData.size()>0)
            {
                TrailerLink="http://www.youtube.com/watch?v="+TrailerData.get(0).TrailerKey;
                Intent ShareIntent =createShareIntent(TrailerLink);
                startActivity(ShareIntent);
            }
            else
            {
                Toast.makeText(getActivity(), "This Movie don't have any Trailer", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Intent createShareIntent(String TrailerLink) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,TrailerLink+SHARE_HASHTAG);
        return shareIntent;
    }



    void ChangeMovies() {

        Uri uri = mUri;
        if (null != uri) {
            getLoaderManager().restartLoader(LOADER, null, this);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst())
            return;
        ContentValues Content_Value = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(data, Content_Value);
        if (Content_Value!=null) {
            SelectedItem = new Movie(Content_Value.getAsLong(MovieContract.COLUMN_MOVIE_ID),
                    Content_Value.getAsString(MovieContract.COLUMN_TITLE),
                    Content_Value.getAsString(MovieContract.COLUMN_ORIGINAL_TITLE),
                    Content_Value.getAsString(MovieContract.COLUMN_OVERVIEW),
                    Content_Value.getAsLong(MovieContract.COLUMN_VOTE_COUNT),
                    Content_Value.getAsDouble(MovieContract.COLUMN_VOTE_AVERAGE),
                    Content_Value.getAsString(MovieContract.COLUMN_RELEASE_DATE),
                    Content_Value.getAsString(MovieContract.COLUMN_POSTER_PATH),
                    Content_Value.getAsString(MovieContract.COLUMN_Trailer_JSON_STR),
                    Content_Value.getAsString(MovieContract.COLUMN_REVIEW_JSON_STR));
            try {
                if (SelectedItem.REVIEW_JSON_STR !=null)
                ReviewData = getReviewDataFromJson(SelectedItem.REVIEW_JSON_STR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (SelectedItem.Trailer_JSON_STR !=null)
                    TrailerData = getTrailerDataFromJson(SelectedItem.Trailer_JSON_STR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (TrailerData.size()>0)
                detailAdaptor.addHeader("Trailers");
            if (TrailerData != null) {
                for (Trailer trailer : TrailerData)
                    detailAdaptor.addItem(trailer);
            }
            if(ReviewData.size()>0)
                detailAdaptor.addHeader("Reviews");
            if (ReviewData != null) {
                for (Review review : ReviewData)
                    detailAdaptor.addItem(review);
            }
            mfavUriWithId = MovieContract.FavoriteMoviesEntry.buildMoviesUriWithMovieId(Long.valueOf(SelectedMovieID));
            Cursor CheckIfFavorite = getActivity().getContentResolver().query(mfavUriWithId, null, null, null, null);
            if(CheckIfFavorite != null && CheckIfFavorite.getCount() > 0){
                fav_button.setBackgroundResource(R.drawable.favorite);
                isFavorite=true;
            }
            fav_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isFavorite){
                        fav_button.setBackgroundResource(R.drawable.favorite);
                        ContentValues MovieValues =new ContentValues();
                        MovieValues.put(MovieContract.COLUMN_MOVIE_ID, SelectedItem.MOVIE_ID);
                        MovieValues.put(MovieContract.COLUMN_TITLE, SelectedItem.TITLE);
                        MovieValues.put(MovieContract.COLUMN_ORIGINAL_TITLE, SelectedItem.ORIGINAL_TITLE);
                        MovieValues.put(MovieContract.COLUMN_OVERVIEW, SelectedItem.OVERVIEW);
                        MovieValues.put(MovieContract.COLUMN_VOTE_COUNT, SelectedItem.VOTE_COUNT);
                        MovieValues.put(MovieContract.COLUMN_VOTE_AVERAGE, SelectedItem.VOTE_AVARAGE);
                        MovieValues.put(MovieContract.COLUMN_RELEASE_DATE, SelectedItem.RELEASE_DATE);
                        MovieValues.put(MovieContract.COLUMN_POSTER_PATH, SelectedItem.POSTER_PATH);
                        MovieValues.put(MovieContract.COLUMN_Trailer_JSON_STR, SelectedItem.REVIEW_JSON_STR);
                        MovieValues.put(MovieContract.COLUMN_REVIEW_JSON_STR, SelectedItem.REVIEW_JSON_STR);
                        getActivity().getContentResolver().insert(MovieContract.FavoriteMoviesEntry.CONTENT_URI, MovieValues);
                        Toast.makeText(getActivity(), "Movie Added To Favorite Movies", Toast.LENGTH_LONG).show();
                        isFavorite=true;
                    }
                    else{
                        fav_button.setBackgroundResource(R.drawable.nonfavorite);
                        getActivity().getContentResolver().delete(mfavUriWithId, null, null);
                        Toast.makeText(getActivity(), "Removed From Favorites", Toast.LENGTH_LONG).show();
                        isFavorite=false;
                    }
                }
            });
            t1.setText(SelectedItem.ORIGINAL_TITLE);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185//" + SelectedItem.POSTER_PATH)
                    .resize(MainActivity.img_width,(int)(MainActivity.img_width*1.5)).into(img);
            if(SelectedItem.RELEASE_DATE!=null) {
                String Time[] = SelectedItem.RELEASE_DATE.split("-");
                t2.setText((CharSequence) Time[0]);
            }
            t3.setText("120min");
            if (SelectedItem.VOTE_AVARAGE!=null)
                t4.setText(SelectedItem.VOTE_AVARAGE + "/10");
            if (SelectedItem.OVERVIEW!=null)
                t5.setText(SelectedItem.OVERVIEW);
            detailList.setAdapter(detailAdaptor);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    private ArrayList<Review> getReviewDataFromJson(String forecastJsonStr)
            throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        Log.d(LOG_TAG, "//////////////////////////////////////////////////" + forecastJsonStr);
        JSONArray DataArray = forecastJson.getJSONArray("results");
        ArrayList<Review> ReviewData =new ArrayList<Review>();
        for (int i = 0; i < DataArray.length(); i++) {
            JSONObject object = DataArray.getJSONObject(i);
            String REVIEW_AUTHOR = object.getString("author");
            String REVIEW_CONTENT = object.getString("content");
            Review temp = new Review(REVIEW_AUTHOR, REVIEW_CONTENT);
            ReviewData.add(temp);
        }
        return ReviewData;
    }
    private ArrayList<Trailer> getTrailerDataFromJson(String forecastJsonStr)
            throws JSONException {
        Log.d(LOG_TAG, "//////////////////////////////////////////////////" + forecastJsonStr);
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray DataArray = forecastJson.getJSONArray("results");
        ArrayList<Trailer> TrailerData =new ArrayList<Trailer>();
        for (int i = 0; i < DataArray.length(); i++) {
            JSONObject object = DataArray.getJSONObject(i);
            String TrailerName = object.getString("name");
            String TrailerKey = object.getString("key");
            Trailer temp = new Trailer(TrailerName, TrailerKey);
            TrailerData.add(temp);
        }
        return TrailerData;
    }
}