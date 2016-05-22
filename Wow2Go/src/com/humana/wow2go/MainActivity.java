package com.humana.wow2go;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;

import org.apache.cordova.DroidGap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.util.Random;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends DroidGap {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private int MY_CALLBACK_ID = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        URI myUri = null;
		try 
		{
			myUri = new URI("storage/emulated/0/Wow2Go/");
		} 
		catch (URISyntaxException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, myUri);
        startActivityForResult(intent, MY_CALLBACK_ID);*/
    }
    
    public void onActivityResult(int  requestId, int resultCode, Intent data) 
    {
    	if (requestId == MY_CALLBACK_ID) 
    	{
    	   if (resultCode == Activity.RESULT_CANCELED) 
    	   {
    	       onCancelled();
    	   } else if (resultCode == Activity.RESULT_OK) 
    	   {
    	      onFinishedPickingMedia();
    	   }
    	}
    }*/

    private void onCancelled() {
		// TODO Auto-generated method stub
    	Log.d("AAAAAAAAHHHHHHHHHHH", "This is broke!!!");
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Oops!");
    	builder.setMessage("Something went wrong....Come back later!");
    	builder.setPositiveButton("OK", null);
    	AlertDialog dialog = builder.show();
		
	}

	private void onFinishedPickingMedia() {
		// TODO Auto-generated method stub
		//Find the last picture 
        String[] projection = new String[]{ MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.MIME_TYPE }; 
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"); 
        String imageLocation = "";
        if (cursor.moveToFirst()) 
        { 
        	imageLocation = cursor.getString(1); File imageFile = new File(imageLocation); 
        	if (imageFile.exists()) 
        	{ 
        		// TODO: is there a better way to do this? 
        		Bitmap bm = BitmapFactory.decodeFile(imageLocation); 
        	} 
        }
        Log.d("AAAAAAAAHHHHHHHHHHH", imageLocation);
        
        SendFeedTask myObj = new SendFeedTask(imageLocation);
        myObj.execute();
     
        super.loadUrl("file:///android_asset/www/index.html");
	}

	//@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

class SendFeedTask extends AsyncTask
{
	int serverResponseCode = 0;
	TextView tv;
	public String imageLocation = "";
	
	public SendFeedTask(String imageLocal)
	{
		imageLocation = imageLocal;
	}

	@Override
	protected Object doInBackground(Object... arg0) 
	{
		// TODO Auto-generated method stub
		uploadFile(imageLocation);
		return null;
	}
	
	public int uploadFile(String sourceFileUri) 
	{
        String upLoadServerUri = "http://192.81.216.122:5000/api";
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        if (!sourceFile.isFile()) 
        {
	         Log.e("AAAAAAAAHHHHHHHHHHH", "Source File Does not exist");
	         return 0;
        }
        try // open a URL connection to the Servlet
        { 
             FileInputStream fileInputStream = new FileInputStream(sourceFile);
             URL url = new URL(upLoadServerUri);
             conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
             conn.setRequestProperty("Connection", "Keep-Alive");
             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             conn.setRequestProperty("uploaded_file", fileName);
             try
             {
            	 dos = new DataOutputStream(conn.getOutputStream());
             }
             catch (Exception e)
             {
            	 Log.e("AAAAAAAAHHHHHHHHHHH", "Failed on Data Output Stream");
            	 Log.e("AAAAAAAAHHHHHHHHHHH", e.toString());
             }
   
             dos.writeBytes(twoHyphens + boundary + lineEnd); 
             dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
             dos.writeBytes(lineEnd);
   
             bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
   
             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             buffer = new byte[bufferSize];
   
             // read file and write it into form...
             bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
               
             while (bytesRead > 0) 
             {
	               dos.write(buffer, 0, bufferSize);
	               bytesAvailable = fileInputStream.available();
	               bufferSize = Math.min(bytesAvailable, maxBufferSize);
	               bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
              }
   
             // send multipart form data necesssary after file data...
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
             
             String serverResponseMessage = null;
             // Responses from the server (code and message)
             try
             {
            	 serverResponseCode = conn.getResponseCode();
             }
             catch (Exception e)
             {
            	 Log.d("AAAAAAAAHHHHHHHHHHH", "ERROR ON CODE!");
             }
             try
             {
            	 serverResponseMessage = conn.getResponseMessage();
             }
             catch (Exception e)
             {
            	 Log.d("AAAAAAAAHHHHHHHHHHH", "ERROR ON MESSAGE!");
             }
              
             Log.i("AAAAAAAAHHHHHHHHHHH", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
             if(serverResponseCode == 200)
             {
            	 Log.d("AAAAAAAAHHHHHHHHHHH", "Success!");
                 //runOnUiThread(new Runnable() 
                 //{
                      //public void run() {
                          //tv.setText("File Upload Completed.");
                          //Toast.makeText(MainActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                      //}
                  //});               
             }
             else if(serverResponseCode != 200)
             {
            	 Log.d("AAAAAAAAHHHHHHHHHHH", "You Recieved an Error Code!");
             }
             
             //close the streams //
             fileInputStream.close();
             dos.flush();
             dos.close();
        } 
        catch (MalformedURLException ex) 
        {   
            ex.printStackTrace();
            //Toast.makeText(SendFeedTask.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
            Log.e("AAAAAAAAHHHHHHHHHHH", "error: " + ex.getMessage(), ex);  
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            //Toast.makeText(SendFeedTask.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("AAAAAAAAHHHHHHHHHHH", "Exception : " + e.getMessage(), e);  
        }
        Log.d("AAAAAAAAHHHHHHHHHHH", "Finished!");   
        return serverResponseCode;
        
    } 
}

