package cn.rieon.idea.maven.search;

public class MavenSearchQuery {

  //===============================================================================
  // Fields
  //===============================================================================

  private String fuzzy;
  private String groupId;
  private String artifactId;
  private boolean allVersions;
  private String version;
  private String packaging;
  private String classifier;
  private String classname;

  private Mode mode;

  private int size;
  private int start;

  //===============================================================================
  // Constructors
  //===============================================================================

  public MavenSearchQuery() {
  }

  //===============================================================================
  // Methods
  //===============================================================================
  public String buildQueryString() {

    switch (mode) {
      case FUZZY:
        return fuzzy;

      case ADVANCED:

        StringBuilder sb = new StringBuilder();
        if (groupId != null && groupId.length() > 0) {
          sb.append("g:\"").append(groupId).append("\" ");
        }

        if (artifactId != null && artifactId.length() > 0) {
          if (sb.length() > 0) {
            sb.append(" AND ");
          }
          sb.append("a:\"").append(artifactId).append("\"");
        }

        if (version != null && version.length() > 0) {
          if (sb.length() > 0) {
            sb.append(" AND ");
          }
          sb.append("v:\"").append(version).append("\"");
        }

        if (classifier != null && classifier.length() > 0) {
          if (sb.length() > 0) {
            sb.append(" AND ");
          }
          sb.append("l:\"").append(classifier).append("\"");
        }

        if (packaging != null && packaging.length() > 0) {
          if (sb.length() > 0) {
            sb.append(" AND ");
          }
          sb.append("p:\"").append(packaging).append("\"");
        }

        return sb.toString();

      case CLASSNAME:
        return "fc:\"" + classname + "\""; //todo impl
      case HASH:
        return ""; //todo impl
      default:
        return "";
    }

  }

  public static MavenSearchQueryBuilder builder() {
    return new MavenSearchQueryBuilder();
  }

  //===============================================================================
  // Subclasses
  //===============================================================================
  public enum Mode {

    FUZZY,
    ADVANCED,
    CLASSNAME,
    HASH

  }
  //===============================================================================
  // Getters & Setters
  //===============================================================================

  public String getFuzzy() {
    return fuzzy;
  }

  public void setFuzzy(String fuzzy) {
    this.fuzzy = fuzzy;
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

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public boolean isAllVersions() {
    return allVersions;
  }

  public void setAllVersions(boolean allVersions) {
    this.allVersions = allVersions;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getPackaging() {
    return packaging;
  }

  public void setPackaging(String packaging) {
    this.packaging = packaging;
  }

  public String getClassifier() {
    return classifier;
  }

  public void setClassifier(String classifier) {
    this.classifier = classifier;
  }

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }
}

