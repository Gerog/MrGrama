package de.smileCrew.MrGrama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class PhraseParser {
	public static ArrayList<String> getPhrases(String url, String word) {
		ArrayList<String> phrases = new ArrayList<String>();
		
		try {
			String site = "";
			

			HttpClient client = new HttpClient();
			GetMethod method = new GetMethod(url + "/" + URLEncoder.encode(word,"UTF-8"));
			HttpMethodParams params = method.getParams();
			params.setParameter("http.useragent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.15) Gecko/20080623 Firefox/2.0.0.15");
			method.setParams(params);
			@SuppressWarnings("unused")
			int result = client.executeMethod(method);
			//System.out.println(result);

			//site = method.getResponseBodyAsString();
			site = new String(method.getResponseBody(), "UTF-8");
			method.releaseConnection();
			
			
			Pattern p = Pattern.compile("<span class=illustration>[^<]*</span>");
			Matcher m = p.matcher(site);

			while ( m.find() ) {
				String phrase = site.substring(m.start()+25, m.end()-7).trim();
				phrase = phrase.substring(0, 1).toLowerCase(Locale.GERMAN) + phrase.substring(1,phrase.length());
			    phrases.add(phrase);
			    //System.out.println(phrase);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 				
		
		return phrases;
	}

	public boolean wordExists(String url, String word) {
		
		try {
			HttpClient client = new HttpClient();
			GetMethod method = new GetMethod(url + "/" + URLEncoder.encode(word,"UTF-8"));
			HttpMethodParams params = method.getParams();
			params.setParameter("http.useragent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.15) Gecko/20080623 Firefox/2.0.0.15");
			method.setParams(params);
			client.executeMethod(method);
//			int result = client.executeMethod(method);
			//System.out.println(result);

			InputStream  is = method.getResponseBodyAsStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {	
				if (line.contains("Try Google search")) return false;
				if (line.contains("<span class=hw>")) return true;
			}
			method.releaseConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return false;
	}

}
