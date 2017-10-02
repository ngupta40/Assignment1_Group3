package com.example.nitesh.assignment1_group3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Environment;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


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
    float[] values = new float[50];
    SQLiteDatabase db;
    String path = Environment.getExternalStorageDirectory().getPath();
    private final String DATABASE_FILENAME = "/assignment2_group3";
    private final String DATABASE = path + DATABASE_FILENAME;
    Button runButton;
    Button stopButton;
    Button uploadButton;
    Button downloadButton;

    String Sname;
    String Sage;
    String Sid;
    String Ssex;

    EditText id;
    EditText name;
    EditText age;
    RadioButton rb_Male;
    RadioButton rb_Female;
    MyDataBase handler;

    String tablename;
    Boolean serviceFlag = false;
    AccelService accelerometerService;
    Intent serviceIntent;
    ServiceConnection serve;
    Context ctx=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        String[] verlabels = {"1","2","3","4","5","6"};
        String[] horlabels=  {"1","2","3","4","5","6"};
        String title = "Patient health monitor";

        float[] values = new float[10];
        runButton = (Button) findViewById(R.id.btnRun);
        stopButton = (Button) findViewById(R.id.btnStop);
        uploadButton = (Button) findViewById(R.id.Upload);
        downloadButton = (Button) findViewById(R.id.Download);
        id = (EditText) findViewById(R.id.txtPatID);
        age = (EditText) findViewById(R.id.txtAge);
        name = (EditText) findViewById(R.id.txtPatName);
        rb_Male = (RadioButton) findViewById(R.id.rdbM);
        rb_Female = (RadioButton) findViewById(R.id.rdbF);

        graphView = new GraphView(MainActivity.this,values,values,values,title,horlabels,verlabels,true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.GraphLyout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(relativeLayout.BELOW, R.id.parent);
        relativeLayout.addView(graphView, layoutParams);


        final Handler handler1;

        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //msg.arg1
                float x = msg.getData().getFloat("x");
                float y = msg.getData().getFloat("y");
                float z = msg.getData().getFloat("z");
                Date date = new java.util.Date();
                if(serviceFlag){
                    handler.insertAccelValues(tablename, new Timestamp(date.getTime()), abs(x),abs(y), abs(z));
                }
            }
        };

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.getText().toString().isEmpty() || name.getText().toString().isEmpty()
                        || age.getText().toString().isEmpty()) {

                    Toast.makeText(MainActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (buttonAlreadyClicked == false) {
                        buttonAlreadyClicked = true;
                        serviceFlag = true;
                        handler = new MyDataBase(MainActivity.this);
                        handler.createDatabase();
                        if (rb_Male.isChecked()) {
                            Ssex = "MALE";
                        } else {
                            Ssex = "FEMALE";
                        }
                        Sname = name.getText().toString().toUpperCase().replace(' ', '_');
                        Sid = id.getText().toString().toUpperCase();
                        Sage = age.getText().toString().toUpperCase();
                        tablename = Sname + "_" + Sid + "_"  + Sage + "_" + Ssex;
                        handler.createTable(tablename);


                        serviceIntent = new Intent(MainActivity.this.getBaseContext(),AccelService.class);
                        startService(serviceIntent);
                        serve = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                accelerometerService =((AccelService.LocalBinder)service).getInstance();
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
            }});

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearGraph();
                if(serviceFlag) {
                    unbindService(serve);
                    serviceFlag = false;
                }
                Toast.makeText(MainActivity.this, "Service Stop Request Executed", Toast.LENGTH_SHORT).show();
                System.out.println(ctx.getDatabasePath(DATABASE));
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
                    ArrayList<float[]>storedVals=handler.fetchLastTenValues(tablename);
                    float[] x_array = storedVals.get(0);
                    float[] y_array = storedVals.get(1);
                    float[] z_array = storedVals.get(2);
                    System.out.println(x_array);
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
            EditText et1 = (EditText)findViewById(R.id.txtAge);
            EditText et2 = (EditText) findViewById(R.id.txtPatName);
        }
    }

    private void processUploadClick(){

        final MainActivity.UploadTask uploadTask = new MainActivity.UploadTask(MainActivity.this);
        uploadTask.execute("https://impact.asu.edu/CSE535Spring17Folder/UploadToServer.php");
        uploadButtonPress = true;

    }

    private void processDownloadClick() {
        final MainActivity.DownloadTask DownloadTask = new MainActivity.DownloadTask(MainActivity.this);
        DownloadTask.execute("https://impact.asu.edu/CSE535Spring17Folder/" + DATABASE_FILENAME);
        downloadButtonPress = true;
    }




    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String responseString = null;

            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                }
            } };

            try {
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                //downloadButton.setText(Integer.toString(fileLength));
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(DATABASE);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
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

                if (connection != null)
                    connection.disconnect();
            }
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during upload
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "Error in Uploading: " + result, Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(context, "Downloaded Complete", Toast.LENGTH_SHORT).show();

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
                ArrayList<float[]>storedVals=handler.fetchLastTenValues(tablename);
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


    // Upload Async Task.

    private class UploadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            FileInputStream input = null;
            DataOutputStream output = null;
            HttpsURLConnection connection = null;
            String responseString = null;

            String URLBoundary = "***";

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            } };

            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + URLBoundary);
                connection.setRequestProperty("uploaded_file", DATABASE_FILENAME);

                input = new FileInputStream(DATABASE);
                output = new DataOutputStream(connection.getOutputStream());

                output.writeBytes("--" + "***" + "\r\n");
                output.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + DATABASE_FILENAME + "\"" + "\r\n");
                output.writeBytes("\r\n");

                // Uploading the file
                byte data[] = new byte[4096];

                // Read the Database File Data in Bytes

                while (0 < input.read(data, 0, 4096)) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    output.write(data, 0, 4096);
                }

                // End Multipart form data necessary after file data.
                output.writeBytes("\r\n");
                output.writeBytes("--" + "***" + "--" + "\r\n");

                // Responses from the server (code and message)
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                Log.w("Database Upload", "HTTP Response Code:" + responseCode + ":" + responseMessage);

                if(200 != responseCode) {
                    responseString =  responseMessage;
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

            }
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during upload
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null){
                Toast.makeText(context,"Upload error: "+result, Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(context,"Database Uploaded", Toast.LENGTH_SHORT).show();

                if(uploadButtonPress){
                    uploadButtonPress = false;
                }
            }
        }
    }
}
