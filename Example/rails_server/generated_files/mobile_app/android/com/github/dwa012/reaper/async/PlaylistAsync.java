package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Playlist;
import com.github.dwa012.reaper.wrapper.PlaylistWrapper;
import com.github.dwa012.reaper.database.PlaylistDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlaylistAsync {

  private Context context;
  private AsyncListener<Playlist> listener;

  private PlaylistAsync() {
    // left blank
  }

  public PlaylistAsync(Context context) {
    this.context = context;
  }

  public List<Playlist> getItemsFromDB(){
    PlaylistDataSource db = new PlaylistDataSource(context);
    db.open();

    List<Playlist> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<Playlist> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<Playlist>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Playlist> doInBackground(Object... unused) {

      //write the captures to the database
      PlaylistDataSource db = new PlaylistDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.PLAYLIST_URL,params);
      JsonParser parser = null;
      PlaylistWrapper wrapper = new PlaylistWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,PlaylistWrapper.class);
      } catch (JsonParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      } finally {
          try {
              parser.close();
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (java.lang.NullPointerException e) {

          }
      }

      db = new PlaylistDataSource(context);
      db.open();

      for ( Playlist item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<Playlist> result) {
      listener.retrievalFinished(result);
    }

  }
}

