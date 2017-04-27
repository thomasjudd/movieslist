package com.example.tom.movielist;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private ListView listView;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadAsync();
        listView = (ListView) findViewById(android.R.id.list);
        Log.v("listitems", String.valueOf(listItems));
        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void downloadAsync() {
        new GetMoviesTask().execute("http://api.themoviedb.org/3/movie/now_playing?api_key=b1e885f7a1a0602d435d8c52dc0de5f0");
    }

    private class GetMoviesTask extends AsyncTask<String, Integer, JSONArray> {
        @Override
        protected void onPostExecute(JSONArray jsonObjects) {
            for(int i = 0; i < jsonObjects.length(); i++) {
                try {
                    JSONObject obj = (JSONObject) jsonObjects.get(i);
                    String movieTitle = (String) obj.get("original_title");
                    adapter.add(movieTitle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            return getJSON(params[0]);
        }

        public JSONArray getJSON(String urlString) {
            URL apiURL= null;
            JSONObject responseJSON;
            JSONArray moviesJSON;
            try {
                apiURL = new URL(urlString);
                URLConnection conn = apiURL.openConnection();

                InputStream is = conn.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead = 0;
                byte[] data = new byte[2048];
                while((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                String response = new String(buffer.toByteArray(),Charset.forName("UTF-8"));
                Log.v("string ", response);
                responseJSON= new JSONObject(response);
                moviesJSON = responseJSON.getJSONArray("results");
                return moviesJSON;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}