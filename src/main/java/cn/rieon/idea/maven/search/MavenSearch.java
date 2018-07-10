package cn.rieon.idea.maven.search;

import java.io.InputStream;
import java.util.List;

public interface MavenSearch {

  List<Document> search(MavenSearchQuery q, boolean simple) throws MavenSearchException;

  InputStream download(String groupId, String artifactId, String version, String type)
      throws MavenSearchException;

  void downloadFile(String groupId, String artifactId, String version, String type,
      String path) throws MavenSearchException;

  String getPom(String groupId, String artifactId, String version) throws MavenSearchException;


  void downloadFile(MavenId id, String type, String path) throws MavenSearchException;

  void downloadFile(MavenId id, String type, String path,DownloadProgressListener listener) throws MavenSearchException;
}
