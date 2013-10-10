package com.github.dwa012.reaperexample;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

import com.github.dwa012.reaper.async.AlbumAsync;
import com.github.dwa012.reaper.async.AsyncListener;
import com.github.dwa012.reaper.model.Album;
import com.github.dwa012.reaperexample.adapters.AlbumListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<Album> albums;
    private AlbumListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albums = new ArrayList<Album>();
        ListView list = (ListView) findViewById(R.id.list);

        adapter = new AlbumListAdapter(this, R.layout.album_list_item, albums);
        list.setAdapter(adapter);

        AlbumAsync async = new AlbumAsync(this);
        async.fetchItems(new AsyncListener<Album>() {
            @Override
            public void retrievalFinished(List<Album> items) {
                albums.clear();
                albums.addAll(items);
                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
