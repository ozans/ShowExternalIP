package com.ozan.showexternalip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
		        	String URL="http://ipecho.net/plain";
		        	HttpClient httpclient = new DefaultHttpClient();
		        	HttpResponse response = httpclient.execute(new HttpGet(URL));
		        	final StatusLine statusLine = response.getStatusLine();
		        	if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        		ByteArrayOutputStream out = new ByteArrayOutputStream();
		        		response.getEntity().writeTo(out);
		        		out.close();
		        		final String responseString = out.toString();

		        		InetAddress ia = InetAddress.getByName(responseString);
		        		final String hostname=ia.getCanonicalHostName();

		        		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		        		WifiInfo info = manager.getConnectionInfo();

		        		final String macaddress = info.getMacAddress();
		        		runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            	String str="External IP Address: " + responseString + "\n" + "Hostname: " + hostname;
                            	if (macaddress != null) {
                            		str+="\n" + "WiFi MAC Address: " + macaddress;
                            	}

                            	Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                           }
                       });
		        	} else{
		        		response.getEntity().getContent().close();
		        		runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Error: " + statusLine.getReasonPhrase(),
                      	              Toast.LENGTH_LONG).show();
                           }
                       });
		        		throw new IOException(statusLine.getReasonPhrase());
		        	}
		        }
		        catch (final Exception e)
		        {
		         	runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        	Toast.makeText(getBaseContext(), e.toString(),
                    	              Toast.LENGTH_LONG).show();
                       }
                   });
		        }
			}
		 }).start();
	}

}
