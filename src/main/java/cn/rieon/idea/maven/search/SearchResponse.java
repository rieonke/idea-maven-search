package cn.rieon.idea.maven.search;

import java.util.List;
import java.util.Map;

public class SearchResponse {

  private Response response;
  private Map<String, Object> responseHeader;
  private Map<String, Object> spellcheck;

  public static class Response {

    private int numFound;
    private int start;
    private List<Document> docs;

    public int getNumFound() {
      return numFound;
    }

    public void setNumFound(int numFound) {
      this.numFound = numFound;
    }

    public int getStart() {
      return start;
    }

    public void setStart(int start) {
      this.start = start;
    }

    public List<Document> getDocs() {
      docs.forEach(d -> d.set_total(numFound));
      return docs;
    }

    public void setDocs(List<Document> docs) {
      this.docs = docs;
    }
  }

  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

  public Map<String, Object> getResponseHeader() {
    return responseHeader;
  }

  public void setResponseHeader(Map<String, Object> responseHeader) {
    this.responseHeader = responseHeader;
  }

  public Map<String, Object> getSpellcheck() {
    return spellcheck;
  }

  public void setSpellcheck(Map<String, Object> spellcheck) {
    this.spellcheck = spellcheck;
  }

}
