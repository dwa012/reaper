package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.InvoiceLine;
import com.github.dwa012.reaper.wrapper.InvoiceLineWrapper;
import com.github.dwa012.reaper.database.InvoiceLineDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceLineAsync {

  private Context context;
  private AsyncListener<InvoiceLine> listener;

  private InvoiceLineAsync() {
    // left blank
  }

  public InvoiceLineAsync(Context context) {
    this.context = context;
  }

  public List<InvoiceLine> getItemsFromDB(){
    InvoiceLineDataSource db = new InvoiceLineDataSource(context);
    db.open();

    List<InvoiceLine> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<InvoiceLine> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<InvoiceLine>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<InvoiceLine> doInBackground(Object... unused) {

      //write the captures to the database
      InvoiceLineDataSource db = new InvoiceLineDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.INVOICE_LINE_URL,params);
      JsonParser parser = null;
      InvoiceLineWrapper wrapper = new InvoiceLineWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,InvoiceLineWrapper.class);
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

      db = new InvoiceLineDataSource(context);
      db.open();

      for ( InvoiceLine item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<InvoiceLine> result) {
      listener.retrievalFinished(result);
    }

  }
}

