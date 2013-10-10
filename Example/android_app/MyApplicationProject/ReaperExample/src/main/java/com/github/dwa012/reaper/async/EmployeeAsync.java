package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Employee;
import com.github.dwa012.reaper.wrapper.EmployeeWrapper;
import com.github.dwa012.reaper.database.EmployeeDataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAsync {

  private Context context;
  private AsyncListener<Employee> listener;

  private EmployeeAsync() {
    // left blank
  }

  public EmployeeAsync(Context context) {
    this.context = context;
  }

  public List<Employee> getItemsFromDB(){
    EmployeeDataSource db = new EmployeeDataSource(context);
    db.open();

    List<Employee> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<Employee> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<Employee>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<Employee> doInBackground(Object... unused) {

      //write the captures to the database
      EmployeeDataSource db = new EmployeeDataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.EMPLOYEE_URL,params);
      JsonParser parser = null;
      EmployeeWrapper wrapper = new EmployeeWrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,EmployeeWrapper.class);
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

      db = new EmployeeDataSource(context);
      db.open();

      for ( Employee item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<Employee> result) {
      listener.retrievalFinished(result);
    }

  }
}

