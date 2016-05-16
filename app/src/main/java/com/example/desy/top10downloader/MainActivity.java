package com.example.desy.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final static String URL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
    private Button btnParse;
    private ListView listApps;
    private String mFileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnParse = (Button) findViewById(R.id.btnParse);
        listApps = (ListView) findViewById(R.id.xmlListView);

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseApplication parseApplication = new ParseApplication(mFileContents);
                parseApplication.process();
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                  MainActivity.this, R.layout.list_item, parseApplication.getApplications());
                listApps.setAdapter(arrayAdapter);
            }
        });

        DownloadData downloadData = new DownloadData();
        downloadData.execute(URL);
    }


    private class DownloadData extends AsyncTask<String,Void,String> {

        private String LOG = DownloadData.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLFILE(params[0]);
            if (mFileContents == null) {
                Log.d(LOG, "Error downloading");
            }

            return mFileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(LOG, "Result was: " + s);
        }

        private String downloadXMLFILE(String urlPath) {
            StringBuilder tempBuffer = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(LOG, "The response code was " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer,0,charRead));
                }

                return tempBuffer.toString();

            } catch (IOException e){
                Log.d(LOG, "IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.d(LOG, "Security exception. Need permission?" + e.getMessage());
            }

            return null;
        }
    }
}
