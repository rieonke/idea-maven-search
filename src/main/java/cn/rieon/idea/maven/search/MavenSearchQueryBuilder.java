package cn.rieon.idea.maven.search;

import cn.rieon.idea.maven.search.MavenSearchQuery.Mode;

public class MavenSearchQueryBuilder {

  //===============================================================================
  // Fields
  //===============================================================================
  private String groupId;
  private String artifactId;
  private String fuzzy;
  private String version;
  private String packaging;
  private String classifier;
  private String classname;

  private boolean allVersions = false;
  private Mode mode;

  private int size = 20;
  private int start = 0;

  //===============================================================================
  // Constructors
  //===============================================================================

  //===============================================================================
  // Methods
  //===============================================================================

  public MavenSearchQueryBuilder fuzzy(String fuzzy) {

    this.fuzzy = fuzzy;
    this.mode = Mode.FUZZY;

    return this;
  }

  public MavenSearchQueryBuilder groupId(String g) {

    this.groupId = g;
    this.mode = Mode.ADVANCED;

    return this;
  }

  public MavenSearchQueryBuilder artifactId(String a) {
    this.artifactId = a;
    this.mode = Mode.ADVANCED;

    return this;
  }

  public MavenSearchQueryBuilder start(int s) {
    this.start = s;

    return this;
  }

  public MavenSearchQueryBuilder size(int s) {
    this.size = s;

    return this;
  }

  public MavenSearchQueryBuilder allVersions(boolean allVersions) {
    this.allVersions = allVersions;
    this.mode = Mode.ADVANCED;

    return this;
  }

  public MavenSearchQueryBuilder version(String version) {
    this.version = version;
    this.mode = Mode.ADVANCED;
    return this;
  }

  public MavenSearchQueryBuilder packaging(String packaging) {
    this.packaging = packaging;
    this.mode = Mode.ADVANCED;
    return this;
  }

  public MavenSearchQueryBuilder classifier(String classifier) {
    this.classifier = classifier;
    this.mode = Mode.ADVANCED;
    return this;
  }

  public MavenSearchQueryBuilder classname(String classname) {
    this.classname = classname;
    this.mode = Mode.CLASSNAME;
    return this;
  }


  public MavenSearchQuery build() {

    MavenSearchQuery q = new MavenSearchQuery();

    q.setMode(mode);
    q.setAllVersions(allVersions);
    q.setFuzzy(this.fuzzy);
    q.setArtifactId(this.artifactId);
    q.setGroupId(this.groupId);
    q.setVersion(version);
    q.setClassifier(classifier);
    q.setClassname(classname);
    q.setPackaging(packaging);

    q.setSize(size);
    q.setStart(start);

    return q;
  }

}
