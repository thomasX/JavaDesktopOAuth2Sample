package info.margreiter.oauth2;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

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
	public  void printResponseHeaders(HttpResponse httpRequest) throws IOException {
	      String token = null;
	        HttpHeaders headers = httpRequest.getHeaders();
	        System.out.println("ResponseHeaders: " + headers.toString());
	       
	}
	
}
