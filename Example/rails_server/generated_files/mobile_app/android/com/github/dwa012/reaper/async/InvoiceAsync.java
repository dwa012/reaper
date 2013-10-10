package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Invoice;
import com.github.dwa012.reaper.wrapper.InvoiceWrapper;
import com.github.dwa012.reaper.database.InvoiceDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceAsync {

  private Context context;
  private AsyncListener<Invoice> listener;

  private InvoiceAsync() {
    // left blank
  }

  public InvoiceAsync(Context context) {
    this.context = context;
  }

  public List<Invoice> getItemsFromDB(){
    InvoiceDataSource db = new InvoiceDataSource(context);
    db.open();

    List<Invoice> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<Invoice> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<Invoice>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Invoice> doInBackground(Object... unused) {

      //write the captures to the database
      InvoiceDataSource db = new InvoiceDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.INVOICE_URL,params);
      JsonParser parser = null;
      InvoiceWrapper wrapper = new InvoiceWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,InvoiceWrapper.class);
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

      db = new InvoiceDataSource(context);
      db.open();

      for ( Invoice item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<Invoice> result) {
      listener.retrievalFinished(result);
    }

  }
}

