
package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An {@link StoryAdapter} knows how to create a list item layout for each item
 * in the data source (a list of {@link Story} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class StoryAdapter extends ArrayAdapter<Story> {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = StoryAdapter.class.getSimpleName();

    /**
     * The part of the location string from the USGS service that we use to determine
     * whether or not there is a location offset present ("5km N of Cairo, Egypt").
     */
    private static final String LOCATION_SEPARATOR = " of ";

    /**
     * Constructs a new {@link StoryAdapter}.
     *
     * @param context of the app
     * @param stories is the list of stories, which is the data source of the adapter
     */
    public StoryAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
    }

    /**
     * Returns a list item view that displays information about the item at the given position
     * in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Find the item at the given position in the list
        Story currentStory = getItem(position);

        // Make a new ViewHolder
        ViewHolder holder;

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.story_list_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }

        // Get the tag of the current list item holder
        holder = (ViewHolder) listItemView.getTag();

        // Display story title
        holder.titleTextView.setText(currentStory.getTitle());

        // Display story author
        holder.authorTextView.setText(currentStory.getAuthorsAsString());

        // Display story publish date
        Date date = currentStory.getDate();
        if (date != null)
            holder.dateTextView.setText(formatDate(date));

        // Display story section
        holder.sectionTextView.setText(currentStory.getSection());

        // Display story thumbnail if available
        String imageUrl = currentStory.getThumnailUrl();
        if (imageUrl != null) {
            // Display the image of the current book that View
            Picasso.with(getContext())
                    .load(imageUrl)
                    .into(holder.thumbnailImageView);
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMM, yyyy");
        return dateFormat.format(dateObject);
    }

    /* Implement ViewHolder pattern for increased performance */
    static class ViewHolder {
        @BindView(R.id.story_title)
        TextView titleTextView;
        @BindView(R.id.story_author)
        TextView authorTextView;
        @BindView(R.id.story_date)
        TextView dateTextView;
        @BindView(R.id.story_section)
        TextView sectionTextView;
        @BindView(R.id.story_thumbnail)
        ImageView thumbnailImageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}