package com.github.dwa012.reaperexample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.dwa012.reaper.model.Album;
import com.github.dwa012.reaperexample.R;

import java.util.List;

public class AlbumListAdapter extends ArrayAdapter<Album> {
    private Context context;

    public AlbumListAdapter(Context context, int resource, List<Album> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Album album = this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.album_list_item, parent, false);
        }

        TextView title = (TextView)convertView.findViewById(R.id.text_view);
        title.setText(album.getTitle());

        return convertView;
    }
}
