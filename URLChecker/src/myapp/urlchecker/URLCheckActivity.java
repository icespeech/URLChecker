package myapp.urlchecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("unused")
public class URLCheckActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_check);
        
        String urlString = null;
        try
        {
            urlString = this.getIntent().getDataString();
        }
        catch (Exception e)
        {
            Log.e(TAG, "(onCreate)" + e.toString());
        }
        
        if (DEBUG)
            Log.d(TAG, "(onCreate) urlString : " + urlString);
        
        try
        {
            new PostToBackendHelper().execute(urlString);
        }
        catch (Exception e)
        {
            Log.e(TAG, "(onCreate)" + e.toString());
        }
        
        initViews();
        setListeners();
    }
    
    private void initViews()
    {
        tvOriginURL = (TextView) findViewById(R.id.tv_origin_url);
        tvWebhost = (TextView) findViewById(R.id.tv_webhost);
        tvMimeType = (TextView) findViewById(R.id.tv_mimetype);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnOpenWithBrowsers = (Button) findViewById(R.id.btn_open_with_browsers);
    }
    
    private void setListeners()
    {
        try
        {
            btnOk.setOnClickListener(btnOkOnClickListener);
            btnOpenWithBrowsers.setOnClickListener(btnOpenWithBrowsersOnClickListener);
        }
        catch (Exception e)
        {
            Log.e(TAG, "(setListeners)" + e.toString());
        }
    }
    
    private void showResults()
    {
        try
        {
            tvOriginURL.setText(jsonMap.get(getString(R.string.json_key_url)));
            tvWebhost.setText(jsonMap.get(getString(R.string.json_key_webhost)));
            tvMimeType.setText(jsonMap.get(getString(R.string.json_key_mimetype)));
        }
        catch (Exception e)
        {
            Log.e(TAG, "(showResults)" + e.toString());
        }
    }
    
    // Listeners
    private OnClickListener btnOkOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            finish();
        }
    };
    
    private OnClickListener btnOpenWithBrowsersOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            try
            {
                Uri uri = Uri.parse(jsonMap.get(getString(R.string.json_key_url)));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            catch (Exception e)
            {
                Log.e(TAG, "(btnOpenWithBrowsers.onClick)" + e.toString());
            }
            
        }
    };
    
    // members
    private final String TAG = "URLChecker.URLCheckActivity";
    private final Boolean DEBUG = true;
    private TextView tvOriginURL;
    private TextView tvWebhost;
    private TextView tvMimeType;
    private Button btnOk;
    private Button btnOpenWithBrowsers;
    private HashMap<String, String> jsonMap;
    
    // member class
    private class PostToBackendHelper extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... arg0)
        {
            try
            {
                String urlString = arg0[0];
                
                if (DEBUG)
                    Log.d(TAG, "(doInBackground) urlString : " + urlString);
                
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getString(R.string.backend_server_url));
                
                if (DEBUG)
                    Log.d(TAG, "(doInBackground) httpPost was set.");
                
                // configure parameters for posting
                List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair(getString(R.string.backend_post_token),urlString));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                
                // post the URL in parameter to backend
                HttpResponse response = httpClient.execute(httpPost);
                @SuppressWarnings("resource")
                Scanner scanner = new Scanner(response.getEntity().getContent());
                
                String jsonString = "";
                while (scanner.hasNext())
                {
                    jsonString += scanner.next();
                }
                
                if (DEBUG)
                    Log.d(TAG, "(doInBackground) jsonString = " + jsonString);
                
                JSONObject jsonObject = new JSONObject(jsonString);
                
                jsonMap = new HashMap<String, String>();
                
                @SuppressWarnings("unchecked")
                Iterator<String> iterator = jsonObject.keys();
                
                while (iterator.hasNext())
                {
                    String key = (String) iterator.next();
                    String value = jsonObject.getString(key);
                    
                    jsonMap.put(key, value);
                }
                
                if (DEBUG)
                {
                    for (String key : jsonMap.keySet())
                    {
                        Log.d(TAG, "(doInBackground) hashMap[" + key + "] = " + jsonMap.get(key));
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "(doInBackground)" + e.toString());
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void unused)
        {
            Log.d(TAG, "(onPostExecute) calling showResults");
            showResults();
        }
    }
}
