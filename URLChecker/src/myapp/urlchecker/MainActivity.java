package myapp.urlchecker;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

@SuppressWarnings("unused")
public class MainActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void initViews()
    {
        btnCheckSubmit = (Button) findViewById(R.id.btn_url_submit);
        etURL = (EditText) findViewById(R.id.et_url);
    }
    
    private void setListeners()
    {
        try
        {
            btnCheckSubmit.setOnClickListener(urlCheckListener);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
    
    private View.OnClickListener urlCheckListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            try
            {
                String urlString = etURL.getText().toString();
                
                /*
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, URLCheckActivity.class);
                
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.intent_key_url), urlString);
                
                intent.putExtras(bundle);
                */
                
                Uri uri = Uri.parse(urlString);
                
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, URLCheckActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    };
    
    
    // members
    private final String TAG = "URLChecker.MainActivity";
    private Button btnCheckSubmit;
    private EditText etURL;
}
