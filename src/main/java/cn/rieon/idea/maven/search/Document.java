package cn.rieon.idea.maven.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

  private String id;

  private int _total;

  @SerializedName("g")
  private String groupId;

  @SerializedName("a")
  private String artifactId;

  @SerializedName("p")
  private String packaging;

  @SerializedName("timestamp")
  private Date updated;

  @SerializedName("v")
  private String version;

  private int versionCount;

  private String latestVersion;

  private String repositoryId;

  private List<String> text;

  private List<String> ec;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getPackaging() {
    return packaging;
  }

  public void setPackaging(String packaging) {
    this.packaging = packaging;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public int getVersionCount() {
    return versionCount;
  }

  public void setVersionCount(int versionCount) {
    this.versionCount = versionCount;
  }

  public String getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(String latestVersion) {
    this.latestVersion = latestVersion;
  }

  public String getVersion() {
    if (version == null) {
      return latestVersion;
    }
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(String repositoryId) {
    this.repositoryId = repositoryId;
  }

  public List<String> getText() {
    return text;
  }

  public void setText(List<String> text) {
    this.text = text;
  }

  public List<String> getEc() {
    return ec;
  }

  public void setEc(List<String> ec) {
    this.ec = ec;
  }

  public int get_total() {
    return _total;
  }

  public void set_total(int _total) {
    this._total = _total;
  }
}
