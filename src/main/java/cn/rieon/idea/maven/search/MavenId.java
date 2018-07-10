package cn.rieon.idea.maven.search;

import java.io.Serializable;

public class MavenId implements Serializable {

  //===============================================================================
  // Fields
  //===============================================================================

  private String groupId;
  private String artifactId;
  private String version;

  //===============================================================================
  // Constructors
  //===============================================================================

  public MavenId(String groupId, String artifactId, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  //===============================================================================
  // Getters & Setters
  //===============================================================================

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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}