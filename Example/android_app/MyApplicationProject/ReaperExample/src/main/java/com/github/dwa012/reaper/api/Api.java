package com.github.dwa012.reaper.api;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Joiner;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.util.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class Api {
  public static final String BASE_API_URL = "http://10.0.2.2:3000/api/v1/";
  public static final String ALBUM_URL = BASE_API_URL + "album";
  public static final String ARTIST_URL = BASE_API_URL + "artist";
  public static final String CUSTOMER_URL = BASE_API_URL + "customer";
  public static final String EMPLOYEE_URL = BASE_API_URL + "employee";
  public static final String GENRE_URL = BASE_API_URL + "genre";
  public static final String INVOICE_LINE_URL = BASE_API_URL + "invoice_line";
  public static final String INVOICE_URL = BASE_API_URL + "invoice";
  public static final String MEDIA_TYPE_URL = BASE_API_URL + "media_type";
  public static final String PLAYLIST_URL = BASE_API_URL + "playlist";
  public static final String TRACK_URL = BASE_API_URL + "track";

  // if result == "{}", there was an error
  public static String GET(String url,Map<String, String> params) {
    String json = "{}";

    String paramString = Joiner.on("&").withKeyValueSeparator("=").useForNull("").join(params);

    HttpGet request = new HttpGet(url + "?" + paramString);
    HttpClient httpClient = new DefaultHttpClient();
    HttpResponse response;

    try {
      response = httpClient.execute(request);
      Response result = new Response(response.getStatusLine().getStatusCode(),
                                     response.getStatusLine().getReasonPhrase(),
                                     EntityUtils.toString(response.getEntity()));

      json = result.getData();

    } catch (ClientProtocolException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }

    return json;
  }

  public static String POST(String url, Map<String, Object> params) {
    String json = "{}";

    String jsonEncodedString = "";

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonFactory jsonFactory = Common.getJsonFactory();
    JsonGenerator jsonGenerator = null;
    try {
        jsonGenerator = jsonFactory.createJsonGenerator(outputStream, JsonEncoding.UTF8);
        jsonGenerator.writeStartObject();

        //add the params to the body
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            jsonGenerator.writeObjectField(pairs.getKey().toString(), pairs.getValue());
        }
        //---------------------

        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        jsonEncodedString = outputStream.toString();
    } catch (IOException e) {
        e.printStackTrace();
    }


    HttpPost request = new HttpPost(url);
    request.setEntity(new ByteArrayEntity(jsonEncodedString.getBytes()));
    request.addHeader("Content-Type", "application/json");

    HttpClient httpClient = new DefaultHttpClient();
    HttpResponse response;

    try {
        response = httpClient.execute(request);
        Response httpResult = new Response(response.getStatusLine().getStatusCode(),
                                           response.getStatusLine().getReasonPhrase(),
                                           EntityUtils.toString(response.getEntity()));

        json = httpResult.getData();
    } catch (ClientProtocolException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }

    return json;
  }

}
