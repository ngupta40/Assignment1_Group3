package com.example.nitesh.assignment1_group3;


import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.AsyncTask;
import android.widget.Toast;
import android.widget.Button;
import android.os.Environment;
import android.content.Intent;
import android.graphics.Color;
import android.os.PowerManager;
import android.widget.EditText;
import android.content.Context;
import android.widget.RadioButton;
import android.content.ComponentName;
import android.widget.RelativeLayout;
import android.content.pm.ActivityInfo;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import java.net.URL;
import java.io.File;
import java.util.Date;
import java.util.Arrays;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

import static java.lang.Math.abs;


/**
 * Created by Nitesh Gupta on 01-10-2017.
 */

public class MainActivity extends AppCompatActivity {
    GraphView graphView;
    Handler threadHandle = new Handler();

    boolean buttonAlreadyClicked = false;
    boolean uploadButtonPress = false;
    boolean downloadButtonPress = false;
    boolean serviceFlag = false;

    float[] values = new float[50];

    SQLiteDatabase db;
    String path = Environment.getExternalStorageDirectory().getPath();
    private final String DATABASE_FILENAME = "assignment2_group3";
    private final String DATABASE = path + "/Android/Data/CSE535_ASSIGNMENT2/" + DATABASE_FILENAME;

    // Defining variables for Buttons
    Button runButton, stopButton, uploadButton, downloadButton;
    // Strings to store the Data
    String sName, sAge, sId, sSex;
    // Defining variables for TextBoxes
    EditText id, name, age;
    // Defining variables for Radio Button
    RadioButton rb_Male, rb_Female;

    MyDataBase handler;
    MyDataBase downloadHandler;
    String tablename;

    AccelService accelerometerService;
    Intent serviceIntent;
    ServiceConnection serve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        String[] VAxis = {"1.00", "0.75", "0.5", "0.25", "0"};
        String[] HAxis = {"0", "0.25", "0.5", "0.75", "1.00"};
        String title = "Patient Health Monitor - Group 3";

        float[] values = new float[10];
        runButton = (Button) findViewById(R.id.btnRun);
        stopButton = (Button) findViewById(R.id.btnStop);
        stopButton.setEnabled(false);
        uploadButton = (Button) findViewById(R.id.Upload);
        downloadButton = (Button) findViewById(R.id.Download);
        id = (EditText) findViewById(R.id.txtPatID);
        age = (EditText) findViewById(R.id.txtAge);
        name = (EditText) findViewById(R.id.txtPatName);
        rb_Male = (RadioButton) findViewById(R.id.rdbM);
        rb_Female = (RadioButton) findViewById(R.id.rdbF);

        graphView = new GraphView(MainActivity.this, values, values, values, title, HAxis, VAxis, true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.GraphLyout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        graphView.setBackgroundColor(Color.BLACK);
        layoutParams.addRule(relativeLayout.BELOW, R.id.parent);
        relativeLayout.addView(graphView, layoutParams);

        final Handler handler1;

        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                float x = msg.getData().getFloat("x");
                float y = msg.getData().getFloat("y");
                float z = msg.getData().getFloat("z");
                Date date = new java.util.Date();
                if (serviceFlag) {
                    handler.insertAccelValues(tablename, new Timestamp(date.getTime()), abs(x), abs(y), abs(z));
                }
            }
        };

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().isEmpty() || name.getText().toString().isEmpty()
                        || age.getText().toString().isEmpty()) {

                    Toast.makeText(MainActivity.this, "ENTER ALL DETAILS", Toast.LENGTH_SHORT).show();
                } else {
                    if (buttonAlreadyClicked == false) {
                        Toast.makeText(MainActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
                        buttonAlreadyClicked = true;
                        stopButton.setEnabled(true);
                        runButton.setEnabled(false);
                        serviceFlag = true;
                        handler = new MyDataBase(MainActivity.this);
                        handler.createDatabase();
                        if (rb_Male.isChecked()) {
                            sSex = "MALE";
                        } else {
                            sSex = "FEMALE";
                        }

                        sName = name.getText().toString().toUpperCase().replace(' ', '_');
                        sId = id.getText().toString().toUpperCase();
                        sAge = age.getText().toString();

                        tablename = sName + "_" + sId + "_" + sAge + "_" + sSex;
                        handler.createTable(tablename);


                        serviceIntent = new Intent(MainActivity.this.getBaseContext(), AccelService.class);
                        startService(serviceIntent);
                        serve = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                accelerometerService = ((AccelService.LocalBinder) service).getInstance();
                                accelerometerService.setHandler(handler1);
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {
                            }
                        };
                        bindService(serviceIntent, serve, Context.BIND_AUTO_CREATE);
                        plotGraph();
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runButton.setEnabled(true);
                stopButton.setEnabled(false);
                clearGraph();
                if (serviceFlag) {
                    unbindService(serve);
                    serviceFlag = false;
                }
                Toast.makeText(MainActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButton.callOnClick();
                processUploadClick();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButton.callOnClick();
                processDownloadClick();
            }
        });
    }

    public void plotGraph() {
        final Thread graphPlotterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (buttonAlreadyClicked == true) {
                    ArrayList<float[]> storedVals = handler.fetchLastTenValues(tablename);
                    float[] x_array = storedVals.get(0);
                    float[] y_array = storedVals.get(1);
                    float[] z_array = storedVals.get(2);

                    graphView.setValues(x_array, y_array, z_array);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threadHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            graphView.invalidate();
                        }
                    });

                }
            }
        });
        graphPlotterThread.start();
    }

    public void clearGraph() {
        if (buttonAlreadyClicked == true) {
            buttonAlreadyClicked = false;
            float[] xArray = new float[10];
            Arrays.fill(xArray, 0);
            float[] yArray = new float[10];
            Arrays.fill(yArray, 0);
            float[] zArray = new float[10];
            Arrays.fill(zArray, 0);
            graphView.setValues(xArray, yArray, zArray);
            graphView.invalidate();
            EditText et = (EditText) findViewById(R.id.txtPatID);
            EditText et1 = (EditText) findViewById(R.id.txtAge);
            EditText et2 = (EditText) findViewById(R.id.txtPatName);
        }
    }

    // Upload Asynchronously
    private class UploadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock wakeLock;
        private final String CRLF = "\r\n";

        public UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            FileInputStream input = null;
            DataOutputStream output = null;

            HttpURLConnection con = null;
            String sResponse = null;

            String URLBoundary = "???";

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            }};

            try {
                URL url = new URL(sUrl[0]);
                con = (HttpURLConnection) url.openConnection();

                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestProperty("uploaded_file", DATABASE_FILENAME);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + URLBoundary);
                con.setRequestProperty("ENCTYPE", "multipart/form-data");
                con.setRequestProperty("Connection", "Keep-Alive");


                input = new FileInputStream(DATABASE);
                output = new DataOutputStream(con.getOutputStream());

                output.writeBytes("--" + "???" + CRLF);
                output.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + DATABASE_FILENAME + "\"" + CRLF);
                output.writeBytes(CRLF);


                byte data[] = new byte[4096];

                while (input.read(data, 0, 4096) > 0) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    output.write(data, 0, 4096);
                }
                output.writeBytes(CRLF);
                output.writeBytes("--" + "???" + "--" + CRLF);

                if (con.getResponseCode() != 200)
                    sResponse = con.getResponseMessage();

            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
            }
            return sResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            wakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            wakeLock.release();
            if (result != null) {
                Toast.makeText(context, "Error in Uploading: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Database Uploaded", Toast.LENGTH_SHORT).show();
                if (uploadButtonPress) {
                    uploadButtonPress = false;
                }
            }
        }
    }

    private void processUploadClick() {
        final MainActivity.UploadTask uploadTask = new MainActivity.UploadTask(MainActivity.this);
        uploadTask.execute("http://10.218.110.136/CSE535Fall17Folder/UploadToServer.php");
        uploadButtonPress = true;
    }

    //Download Asynchronously
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock wakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        private String DBPATH = "";

        @Override
        protected String doInBackground(String... sUrl) {
            String sResponse = null;
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection con = null;

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                }
            }};

            try {
                URL url = new URL(sUrl[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + con.getResponseCode()
                            + " " + con.getResponseMessage();
                }

                int fileLength = con.getContentLength();
                input = con.getInputStream();
                // Create CSE535_ASSIGNMENT2_Extra Directory If it Doesnt Exist
                final File newFile = new File(path + "/Android/Data/CSE535_ASSIGNMENT2_Extra/");
                if (!newFile.exists()) {
                    newFile.mkdir();
                }
                DBPATH = newFile.getAbsoluteFile() + "/" + DATABASE_FILENAME;
                output = new FileOutputStream(DBPATH);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {

                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;

                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (con != null)
                    con.disconnect();
            }
            return sResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            wakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            wakeLock.release();
            if (result != null) {
                Toast.makeText(context, "Error in Downloading: " + result, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();

                if (downloadButtonPress) {
                    downloadButtonPress = false;
                }
            }

            plotGraph();
        }

        private void plotGraph() {
            graphStaticPlotterThread.start();
        }

        final Thread graphStaticPlotterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the values from textbox for which downloaded data needs to be displayed
                sName = name.getText().toString().toUpperCase().replace(' ', '_');
                sId = id.getText().toString().toUpperCase();
                sAge = age.getText().toString();
                tablename = sName + "_" + sId + "_" + sAge + "_" + sSex;

                //Initialize download button database handler (seperate from run button database handler)
                downloadHandler = new MyDataBase(MainActivity.this, DBPATH);
                downloadHandler.createDatabase();
                downloadHandler.createTable(tablename);
                ArrayList<float[]> storedVals = downloadHandler.fetchLastTenValues(tablename);
                float[] x_array = storedVals.get(0);
                float[] y_array = storedVals.get(1);
                float[] z_array = storedVals.get(2);
                graphView.setValues(x_array, y_array, z_array);
                threadHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        graphView.invalidate();
                    }
                });
            }
        });
    }

    private void processDownloadClick() {
        downloadButtonPress = true;
        final MainActivity.DownloadTask DownloadTask = new MainActivity.DownloadTask(MainActivity.this);
        DownloadTask.execute("http://10.218.110.136/CSE535Fall17Folder/" + DATABASE_FILENAME);
    }

}
