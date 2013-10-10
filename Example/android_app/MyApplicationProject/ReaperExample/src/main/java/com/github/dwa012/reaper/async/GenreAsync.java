package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Genre;
import com.github.dwa012.reaper.wrapper.GenreWrapper;
import com.github.dwa012.reaper.database.GenreDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenreAsync {

  private Context context;
  private AsyncListener<Genre> listener;

  private GenreAsync() {
    // left blank
  }

  public GenreAsync(Context context) {
    this.context = context;
  }

  public List<Genre> getItemsFromDB(){
    GenreDataSource db = new GenreDataSource(context);
    db.open();

    List<Genre> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<Genre> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<Genre>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Genre> doInBackground(Object... unused) {

      //write the captures to the database
      GenreDataSource db = new GenreDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.GENRE_URL,params);
      JsonParser parser = null;
      GenreWrapper wrapper = new GenreWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,GenreWrapper.class);
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

      db = new GenreDataSource(context);
      db.open();

      for ( Genre item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<Genre> result) {
      listener.retrievalFinished(result);
    }

  }
}

