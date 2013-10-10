package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.MediaType;
import com.github.dwa012.reaper.wrapper.MediaTypeWrapper;
import com.github.dwa012.reaper.database.MediaTypeDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MediaTypeAsync {

  private Context context;
  private AsyncListener<MediaType> listener;

  private MediaTypeAsync() {
    // left blank
  }

  public MediaTypeAsync(Context context) {
    this.context = context;
  }

  public List<MediaType> getItemsFromDB(){
    MediaTypeDataSource db = new MediaTypeDataSource(context);
    db.open();

    List<MediaType> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<MediaType> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<MediaType>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<MediaType> doInBackground(Object... unused) {

      //write the captures to the database
      MediaTypeDataSource db = new MediaTypeDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.MEDIA_TYPE_URL,params);
      JsonParser parser = null;
      MediaTypeWrapper wrapper = new MediaTypeWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,MediaTypeWrapper.class);
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

      db = new MediaTypeDataSource(context);
      db.open();

      for ( MediaType item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<MediaType> result) {
      listener.retrievalFinished(result);
    }

  }
}

