package cn.rieon.idea.maven.search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class MavenCentralNetSearch implements MavenSearch {

  private final static String HOST = "https://search.maven.org/";
  private final static int TIME_OUT = 10 * 1000;
  private static Gson gson;

  @Override
  public List<Document> search(MavenSearchQuery q, boolean simple) throws MavenSearchException {
    String query = q.buildQueryString();
    int rows = q.getSize();
    int start = q.getStart();
    String core = q.isAllVersions() ? "gav" : "";

    try {

      String sb = HOST + "solrsearch/select?"
          + "q=" + URLEncoder.encode(query, "UTF-8")
          + "&wt=json"
          + "&rows=" + rows
          + "&start=" + start
          + "&core=" + core;

      HttpURLConnection con = getConnection(sb);

      int code = con.getResponseCode();
      if (code == HttpURLConnection.HTTP_OK) {

        String response = getHttpResponseAsString(con);

        Gson gson = getGsonInstance();

        SearchResponse searchResponse = null;
        try {
          searchResponse = gson.fromJson(response, SearchResponse.class);
        } catch (JsonSyntaxException e) {
          e.printStackTrace();
          throw new MavenSearchException("Error: Invalid JSON response!");
        }

        return searchResponse.getResponse().getDocs();

      } else {
        throw new MavenSearchException("Error: " + code);
      }

    } catch (IOException e) {
      e.printStackTrace();
      throw new MavenSearchException("Error: " + e.getMessage());
    }

  }

  @Override
  public InputStream download(String groupId, String artifactId, String version, String type)
      throws MavenSearchException {

    try {

      String sb = HOST + "remotecontent?filepath=" + groupId.replaceAll("\\.", "\\/")
          + "/" + artifactId + "/"
          + version + "/" + artifactId + "-"
          + version + type;

      HttpURLConnection con = getConnection(sb);

      int code = con.getResponseCode();
      if (code == HttpURLConnection.HTTP_OK) {

        return con.getInputStream();

      } else {
        throw new MavenSearchException("Error: " + code);
      }

    } catch (IOException e) {
      e.printStackTrace();
      throw new MavenSearchException("Error: " + e.getMessage());
    }
  }

  @Override
  public String getPom(String groupId, String artifactId, String version)
      throws MavenSearchException {

    try {
      InputStream stream = download(groupId, artifactId, version, ".pom");
      String string = IOUtils.toString(stream, "UTF-8");
      stream.close();
      return string;
    } catch (IOException e) {
      e.printStackTrace();
      throw new MavenSearchException(e.getMessage());
    }

  }

  @Override
  public void downloadFile(MavenId id, String type, String path) throws MavenSearchException {
    download(id.getGroupId(), id.getArtifactId(), id.getVersion(), path);
  }

  @Override
  public void downloadFile(MavenId id, String type, String path, DownloadProgressListener listener)
      throws MavenSearchException {

    try {

      String sb = HOST + "remotecontent?filepath=" + id.getGroupId().replaceAll("\\.", "\\/")
          + "/" + id.getArtifactId() + "/"
          + id.getVersion() + "/" + id.getArtifactId() + "-"
          + id.getVersion() + type;

      HttpURLConnection con = getConnection(sb);

      int code = con.getResponseCode();
      if (code == HttpURLConnection.HTTP_OK) {

        long contentLength = con.getContentLengthLong();

        InputStream inputStream = con.getInputStream();

        FileOutputStream outputStream = new FileOutputStream(path);

        long read = 0;
        int bytesRead = -1;
        byte[] buffer = new byte[8192];

        while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
          read += bytesRead;

          if (listener != null) {
            int progress = Math.toIntExact((read / contentLength) * 100);
            listener.onProgressUpdate(progress);
          }
        }

        outputStream.close();
        inputStream.close();


      } else {
        throw new MavenSearchException("Error: " + code);
      }

    } catch (IOException e) {
      e.printStackTrace();
      throw new MavenSearchException("Error: " + e.getMessage());
    }
  }

  @Override
  public void downloadFile(String groupId, String artifactId, String version, String type,
      String path) throws MavenSearchException {

    try {
      InputStream stream = download(groupId, artifactId, version, type);

      FileOutputStream outputStream = null;
      outputStream = new FileOutputStream(path);
      int bytesRead = -1;
      byte[] buffer = new byte[8192];
      while ((bytesRead = stream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }

      outputStream.close();
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  private HttpURLConnection getConnection(String url)
      throws IOException {

    URL u = new URL(url);
    HttpURLConnection con = (HttpURLConnection) u.openConnection();

    con.setRequestMethod("GET");
    con.setRequestProperty("accept", "application/json");
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setUseCaches(false);
    con.setConnectTimeout(TIME_OUT);
    con.setRequestProperty("Charset", "UTF-8");

    return con;

  }

  private String getHttpResponseAsString(HttpURLConnection con) throws IOException {

    InputStream inputStream = con.getInputStream();
    String string = IOUtils.toString(inputStream, "UTF-8");
    inputStream.close();
    return string;
//    String line = "";
//    StringBuilder buffer = new StringBuilder();
//    while ((line = reader.readLine()) != null) {
//      buffer.append(line).append(System.lineSeparator());
//    }
//    reader.close();
//
//    return buffer.toString();

  }

  private Gson getGsonInstance() {

    if (gson == null) {

      GsonBuilder gb = new GsonBuilder();
      gb.registerTypeAdapter(Date.class,
          (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(
              json.getAsJsonPrimitive().getAsLong()));
      gson = gb.create();

    }

    return gson;

  }

}
