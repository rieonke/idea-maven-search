package cn.rieon.idea.maven.search;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class MavenDocumentTableModel extends AbstractTableModel {

  List<Document> documents;

  public MavenDocumentTableModel(List<Document> documents) {
    this.documents = documents;
  }

  /**
   * 表头（列名）
   */
  private Object[] columnNames = {"Group Id", "Artifact Id", "Version", "Update", "Type"};

  /**
   * 返回总行数
   */
  @Override
  public int getRowCount() {
    return documents.size();
  }

  /**
   * 返回总列数
   */
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * 返回列名称（表头名称），AbstractTableModel 中对该方法的实现默认是以 大写字母 A 开始作为列名显示，所以这里需要重写该方法返回我们需要的列名。
   */
  @Override
  public String getColumnName(int column) {
    return columnNames[column].toString();
  }

  /**
   * 返回指定单元格的显示的值
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Document d = documents.get(rowIndex);
    if (d == null) {
      return null;
    }

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    switch (columnIndex) {
      case 0:
        return d.getGroupId();
      case 1:
        return d.getArtifactId();
      case 2:
        return d.getVersion();
      case 3:
        return fmt.format(d.getUpdated());
      case 4:
        return d.getPackaging();
      default:
        return "NULL";
    }

  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  public void setDocuments(List<Document> documents) {
    this.documents = documents;
    this.fireTableDataChanged();
  }

  public void addDocuments(List<Document> documents) {
    this.documents.addAll(documents);
    this.fireTableDataChanged();
  }

  public void clear() {
    this.documents.clear();
    this.fireTableDataChanged();
  }

  public List<Document> getDocuments() {
    return documents;
  }
}
