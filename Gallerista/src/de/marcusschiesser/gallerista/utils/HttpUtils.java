package de.marcusschiesser.gallerista.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;

/**
 * Helper class to do the REST communication
 * Can also perform JSON-parsing using Jackson.
 * 
 * @author Marcus
 */
public class HttpUtils {

	private DefaultHttpClient mClient;
	private ResponseHandler<String> mResponseHandler;
	private ObjectMapper mMapper;

	private int mServerPort;
	private String mServerName;
	private String mPathPrefix;

	public HttpUtils(String serverName, String pathPrefix, int serverPort) {
		super();
		mClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(mClient.getParams(), 30000);
		mResponseHandler = new BasicResponseHandler();
		mMapper = new ObjectMapper(); // can reuse, share globally
		this.mServerName = serverName;
		this.mPathPrefix = pathPrefix;
		this.mServerPort = serverPort;
	}

	public HttpUtils(String serverName, String pathPrefix) {
		this(serverName, pathPrefix, 80);
	}

	public String doGet(String query) throws IOException {
		return doGet("", query);
	}

	public <T> T doGet(String query, Class<T> valueType) throws IOException {
		try {
			String responseText = doGet(query);
			return mMapper.readValue(responseText, valueType);
		} catch (JsonParseException e) {
			throw new IOException("parse error");
		} catch (JsonMappingException e) {
			throw new IOException("mapping error");
		}
	}

	public String doGet(String path, String query) throws IOException {
		try {
			URI uri;
			uri = createURI(path, query);
			HttpGet get = new HttpGet(uri);
			HttpResponse response = mClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return mResponseHandler.handleResponse(response);
			} else {
				throw new IOException("wrong http status: " + statusCode);
			}
		} catch (URISyntaxException e) {
			throw new IOException("uri syntax error");
		} catch (ClientProtocolException e) {
			throw new IOException("protocol error");
		}

	}

	private URI createURI(String path, String query) throws URISyntaxException {
		URI uri = URIUtils.createURI("http", mServerName, mServerPort, mPathPrefix
				+ path, query, null);
		Log.v(HttpUtils.class.getSimpleName(), "Requesting URI: " + uri.toString());
		return uri;
	}

	public boolean doPut(String path, Object object) throws IOException {
		try {
			String json = mMapper.writeValueAsString(object);
			URI uri = createURI(path, null);
			HttpPut put = new HttpPut(uri);
			put.addHeader("Accept", "application/json");
			put.addHeader("Content-Type", "application/json");
			StringEntity entity = new StringEntity(json, "UTF-8");
			entity.setContentType("application/json");
			put.setEntity(entity);
			HttpResponse response = mClient.execute(put);
			int statusCode = response.getStatusLine().getStatusCode();
			return statusCode == HttpStatus.SC_OK;
		} catch (URISyntaxException e) {
			throw new IOException("uri syntax error");
		} catch (ClientProtocolException e) {
			throw new IOException("protocol error");
		}
	}

	public String doPutFile(final String path, final File file)
			throws URISyntaxException, HttpException, IOException {
		URI uri = createURI(path, null);
		HttpPut put = new HttpPut(uri);
		String mimeType = "binary/octet-stream";
		if (file.getName().matches(".*\\.(jpeg|jpg)"))
			mimeType = "image/jpeg";
		FileEntity reqEntity = new FileEntity(file, mimeType);
		put.setEntity(reqEntity);
		HttpResponse response = mClient.execute(put);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			return mResponseHandler.handleResponse(response);
		} else {
			throw new IOException("wrong http status: " + statusCode);
		}
	}

	public String doPost(final String path, final String POSTText)
			throws URISyntaxException, HttpException, IOException {
		URI uri = createURI(path, null);
		HttpPost httpPost = new HttpPost(uri);
		StringEntity entity = new StringEntity(POSTText, "UTF-8");
		BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json");
		httpPost.getParams().setBooleanParameter(
				"http.protocol.expect-continue", false);
		entity.setContentType(basicHeader);
		httpPost.setEntity(entity);
		HttpResponse response = mClient.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			return mResponseHandler.handleResponse(response);
		} else {
			throw new IOException("wrong http status: " + statusCode);
		}
	}

	public boolean doDelete(final String path) throws HttpException,
			IOException, URISyntaxException {
		URI uri = createURI(path, null);
		HttpDelete httpDelete = new HttpDelete(uri);
		httpDelete.addHeader("Accept",
				"text/html, image/jpeg, *; q=.2, */*; q=.2");
		HttpResponse response = mClient.execute(httpDelete);
		int statusCode = response.getStatusLine().getStatusCode();
		return statusCode == HttpStatus.SC_OK ? true : false;
	}

}