package com.example.demo;

import org.json.JSONArray;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import java.util.concurrent.TimeUnit;


import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;

import com.jayway.jsonpath.spi.cache.Cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.lang.*;


public class HttpURLConnectionExample {

	private static String POST_URL_File = "http://localhost:8888/";
	private static String POST_URL_Db = "http://localhost:8888/";
	private static CacheAccess<String, String> cache = null;	

	public static void main(String[] args) throws IOException {
		
		long startTime,endTime;
		String fdomain = "AccountActivity";
		String fsubdomain = "default";
		String ftenant = "Bank1";
		String fkey = "accountActivityPending.maxRecords";
		
		startTime = System.nanoTime();
		
		String fvalue = getConfig(fdomain,fsubdomain,fkey,ftenant);
		
		endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime)/1000000 + " ms");
		System.out.println("For Domain : "+fdomain+" , SubDomain : "+fsubdomain+" , Tenant : "+ftenant+" , Key : "+fkey);
		System.out.println("Value Fetched is : " + fvalue);
	
		
		String domain = "Account";
		String subdomain = "Validation";
		String key = "useNewValidationFw";
		String tenant = "defaultAffiliate";

		
		startTime = System.nanoTime();
		
		String value = getConfig(domain,subdomain,key,tenant);
		
		endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime)/1000000 + " ms");
		System.out.println("For Domain : "+domain+" , SubDomain : "+subdomain+" , Tenant : "+tenant+" , Key : "+key);
		System.out.println("Value Fetched is : " + value);
		
		/*String tdomain = "rwd";
		String tsubdomain = "default";
		String ttenant = "jvm_1";
		String tkey = "clientSecret";

		
		startTime = System.nanoTime();
		
		String tvalue = getConfig(tdomain,tsubdomain,tkey,ttenant);
		
		endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime)/1000000 + " ms");
		System.out.println("For Domain : "+tdomain+" , SubDomain : "+tsubdomain+" , Tenant : "+ttenant+" , Key : "+tkey);
		System.out.println("Value Fetched is : " + tvalue);*/
	}


	private static String getConfig(String domain, String subdomain, String key, String tenant) throws IOException {
		String value = "";
		String cache_key = domain+subdomain+tenant+key;
		cache = JCS.getInstance("test");
		if(cache.get(cache_key) != null) {
			
			value = cache.get(cache_key);
			JCS.shutdown();
			System.out.println("From Cache");
			
		}
		else {
			POST_URL_File = POST_URL_File +domain+"/"+subdomain+"/"+tenant;
			URL obj = new URL(POST_URL_File);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("content-type", "application/json; charset=UTF-8");
			con.setDoOutput(true);
			
			//OutputStream os = con.getOutputStream();
			//os.write("aditya".getBytes());
			//os.write(POST_PARAMS.getBytes());
			//os.flush();
			//os.close();
			// For POST only - END
	
			int responseCode = con.getResponseCode();
	
			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				
				String jsonString = response.toString();
				// print result
				JsonArray sources = Json.parse(jsonString).asObject().get("propertySources").asArray();
				for (JsonValue source : sources) {
					try {
							value = source.asObject().get("source").asObject().getString(key, "unknown");
							if(!value.equals("unknown")) {
								cache.put(cache_key, value);
								JCS.shutdown();
								return value;
							}
					}
					catch (ArrayIndexOutOfBoundsException e){
						continue;
					}
				}
					
				}
			
				con = null;
				POST_URL_Db = POST_URL_Db +key+"/"+domain+subdomain+"/"+tenant;
				obj = new URL(POST_URL_Db);
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("content-type", "application/json; charset=UTF-8");
				con.setDoOutput(true);
				
				responseCode = con.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK) { //success
					BufferedReader in = new BufferedReader(new InputStreamReader(
							con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
		
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					String jsonString = response.toString();
					// print result
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject (jsonString);
						JSONArray one = new JSONArray(jsonObject.getJSONArray("propertySources").toString());
						JSONObject two = new JSONObject(one.get(0).toString());
						JSONObject three = new JSONObject(two.get("source").toString());
						value = three.get(key).toString();
						cache.put(cache_key, value);
						JCS.shutdown();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		       
				} else {
					System.out.println("POST request not worked");
				}
				
		}
		return value;
	}
	

}