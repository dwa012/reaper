package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Artist;
import com.github.dwa012.reaper.wrapper.ArtistWrapper;
import com.github.dwa012.reaper.database.ArtistDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArtistAsync {

  private Context context;
  private AsyncListener<Artist> listener;

  private ArtistAsync() {
    // left blank
  }

  public ArtistAsync(Context context) {
    this.context = context;
  }

  public List<Artist> getItemsFromDB(){
    ArtistDataSource db = new ArtistDataSource(context);
    db.open();

    List<Artist> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<Artist> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<Artist>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Artist> doInBackground(Object... unused) {

      //write the captures to the database
      ArtistDataSource db = new ArtistDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.ARTIST_URL,params);
      JsonParser parser = null;
      ArtistWrapper wrapper = new ArtistWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,ArtistWrapper.class);
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

      db = new ArtistDataSource(context);
      db.open();

      for ( Artist item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<Artist> result) {
      listener.retrievalFinished(result);
    }

  }
}

