/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package info.margreiter.oauth2;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * A sample application that demonstrates how the Google OAuth2 library can be used to authenticate
 * against Daily Motion.
 *
 * @author Ravi Mistry
 */
public class DailyMotionSample {

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/ferps_fff");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  /** OAuth 2 scope. */
//  private static final String SCOPE = "read";
//  private static final String SCOPE = "read";
//  private static final String[] SCOPE = new String[]{"openid","https://www.googleapis.com/auth/userinfo.email",};
  private static final String[] SCOPE = new String[]{"profile","email","openid",};
//  "email", 
//  "https://www.googleapis.com/auth/userinfo.email"
//  "profile",
//dfadsfa
  
  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  static final JsonFactory JSON_FACTORY = new JacksonFactory();

//  private static final String TOKEN_SERVER_URL = "https://api.dailymotion.com/oauth/token";
//  private static final String TOKEN_SERVER_URL = "https://accounts.google.com/o/oauth2/v4/token";
  private static final String TOKEN_SERVER_URL = "https://accounts.google.com/o/oauth2/token";

//  private static final String AUTHORIZATION_SERVER_URL ="https://api.dailymotion.com/oauth/authorize";
//  private static final String AUTHORIZATION_SERVER_URL ="https://accounts.google.com/o/oauth2/v2/auth";
  private static final String AUTHORIZATION_SERVER_URL ="https://accounts.google.com/o/oauth2/auth";

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    OAuth2ClientCredentials.errorIfNotSpecified();
    // set up authorization code flow
    AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
        .authorizationHeaderAccessMethod(),
        HTTP_TRANSPORT,
        JSON_FACTORY,
        new GenericUrl(TOKEN_SERVER_URL),
        new ClientParametersAuthentication(
            OAuth2ClientCredentials.API_KEY, OAuth2ClientCredentials.API_SECRET),
        OAuth2ClientCredentials.API_KEY,
        AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList( SCOPE))
        .setDataStoreFactory(DATA_STORE_FACTORY).build();
        
    // authorize
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(
        OAuth2ClientCredentials.DOMAIN).setPort(OAuth2ClientCredentials.PORT).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  private static void run(HttpRequestFactory requestFactory) throws IOException {
	    DailyMotionUrl url = new DailyMotionUrl("https://www.felder-group.com");
	    url.setFields("id,tags,title,url");

	    HttpRequest request = requestFactory.buildGetRequest(url);
	    HttpResponse response = request.execute();
	    
//	    System.out.println("und iatz ? " + response.getStatusCode());
//	    System.out.println("TOKEN-REQUEST:");
	    String tk = new TokenService().getToken(request);
	    
	    UserInfoURL userInfo = new  UserInfoURL("https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
	    HttpRequest idRequest = requestFactory.buildGetRequest(userInfo);
	    HttpResponse idResponse = idRequest.execute();
	    System.out.println("ID-REQUEST:");
	    new TokenService().printResponse(idResponse);
	  System.out.println("alles perfekt !");
  }
  public static void main(String[] args) {
    try {
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
      final Credential credential = authorize();
      HttpRequestFactory requestFactory =
          HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
              credential.initialize(request);
              request.setParser(new JsonObjectParser(JSON_FACTORY));
            }
          });
      run(requestFactory);
      // Success!
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }
}
