package info.margreiter.oauth2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

public class TokenService {
	//TODO Test 10.01.2018
	
	public  String getToken(HttpRequest httpRequest) throws IOException {
	      String token = null;
	        HttpHeaders headers = httpRequest.getHeaders();
	        System.out.println("Headers: " + headers.toString());
	        String authorizationHeader = headers.get("authorization").toString();
	        System.out.println("authHeader:" + authorizationHeader);
//	        final String authorizationHeader = httpRequest.getHeader("authorization");
	        if (authorizationHeader == null) {
	            throw new IOException("Unauthorized: No Authorization header was found");
	        }

	        String[] parts = authorizationHeader.split(" ");
	        if (parts.length != 2) {
	            throw new IOException("Unauthorized: Format is Authorization: Bearer [token]");
	        }

	        String scheme = parts[0];
	        String credentials = parts[1];

	        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
	        if (pattern.matcher(scheme).matches()) {
	            token = credentials;
	        }
	        return token;
	}
	public  void printResponse(HttpResponse httpRequest) throws IOException {
	        HttpHeaders headers = httpRequest.getHeaders();
//	        System.out.println("ResponseHeaders: " + headers.toString());
//	       System.out.println(httpRequest.getContentEncoding());
//	       System.out.println(httpRequest.getContentType());
//	       System.out.println(httpRequest.getContent());
	       
//	       IOUtils.toString(httpRequest.getContent(), "UTF-8");
	       
	       String jsonString = IOUtils.toString(httpRequest.getContent());
//	       System.out.println("jsionString" + jsonString);
	       JsonReader dtoReader = new JsonReader(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
	       JsonObject json = (JsonObject) dtoReader.readObject();
	       
//	       System.out.println(" soooooooo: " + httpRequest.getContent());
		    System.out.println("UI:             "+json.get("email"));
//		    System.out.println(dtoReader);
	}
	public  String getEmailAddress(HttpResponse httpRequest) throws IOException {
		String jsonString = IOUtils.toString(httpRequest.getContent());
		JsonReader dtoReader = new JsonReader(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
		JsonObject json = (JsonObject) dtoReader.readObject();
		return (String) json.get("email");
	}
	
}
