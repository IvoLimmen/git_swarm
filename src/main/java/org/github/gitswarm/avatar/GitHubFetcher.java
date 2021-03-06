package org.github.gitswarm.avatar;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.IOUtils;
import static org.github.gitswarm.avatar.AbstractAvatarFetcher.getImage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubFetcher extends AbstractAvatarFetcher {

   private final static Logger LOGGER = LoggerFactory.getLogger(GitHubFetcher.class);

   static {
      TrustManager[] trustAllCerts = new TrustManager[]{
         new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
               return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
         }
      };

      // Activate the new trust manager
      try {
         SSLContext sc = SSLContext.getInstance("SSL");
         sc.init(null, trustAllCerts, new SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      }
      catch (Exception e) {
      }
   }

   @Override
   public String fetchUserImage(String username) {

      String json = searchUser(username);
      if (json == null) {
         return null;
      }
      JSONObject object = new JSONObject(json);
      JSONArray array = object.optJSONArray("items");
      if (array == null || array.isNull(0)) {
         return null;
      }
      JSONObject person = array.getJSONObject(0);
      String avatar = person.optString("avatar_url");

      try {
         return getImage(username, new URL(avatar));
      }
      catch (MalformedURLException e) {
         return null;
      }
   }

   private String searchUser(String email) {
      String json = null;
      URI uri;
      try {
         uri = new URI("https://api.github.com/search/users?q=" + email);
         HttpURLConnection httpConn = (HttpURLConnection) uri.toURL().openConnection();
         int responseCode = httpConn.getResponseCode();
         if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            StringWriter stringWriter = new StringWriter(4096);
            try (InputStream inputStream = httpConn.getInputStream()) {
               IOUtils.copy(inputStream, stringWriter, "UTF-8");
            }
            json = stringWriter.toString();
         }
         httpConn.disconnect();
      }
      catch (URISyntaxException | IOException ex) {
         LOGGER.error("", ex);
      }

      return json;
   }
}
