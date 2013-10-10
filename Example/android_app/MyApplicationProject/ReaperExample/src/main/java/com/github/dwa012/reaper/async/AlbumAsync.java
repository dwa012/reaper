package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Album;
import com.github.dwa012.reaper.wrapper.AlbumWrapper;
import com.github.dwa012.reaper.database.AlbumDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlbumAsync {

  private Context context;
  private AsyncListener<Album> listener;

  private AlbumAsync() {
    // left blank
  }

  public AlbumAsync(Context context) {
    this.context = context;
  }

  public List<Album> getItemsFromDB(){
    AlbumDataSource db = new AlbumDataSource(context);
    db.open();

    List<Album> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<Album> listener){
    this.listener = listener;
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<Album>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Album> doInBackground(Object... unused) {

      //write the captures to the database
      AlbumDataSource db = new AlbumDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.ALBUM_URL,params);
      JsonParser parser = null;
      AlbumWrapper wrapper = new AlbumWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,AlbumWrapper.class);
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

      db = new AlbumDataSource(context);
      db.open();

      for ( Album item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<Album> result) {
      listener.retrievalFinished(result);
    }

  }
}

