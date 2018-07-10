package cn.rieon.idea.maven.search;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import org.jetbrains.annotations.Nullable;

public class DownloadFileDialog extends DialogWrapper {

  private JPanel contentPane;
  private JList downloadList;
  private JProgressBar downloadProgress;
  private JLabel loadingLabel;

  private Project project;
  private Vector<DownloadData> data;

  private static volatile boolean downloading = false;

  public DownloadFileDialog(@Nullable Project project, Vector<DownloadData> data) {
    super(project, false);

    this.data = data;
    this.project = project;
    setModal(true);
    setTitle("Maven Search");
    contentPane.setVisible(true);
    downloadProgress.setVisible(false);

    init();

    downloadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    downloadList.setModel(new DefaultComboBoxModel<>(data));

  }

  @Override
  protected void doOKAction() {

    List<DownloadData> list = downloadList.getSelectedValuesList();

    if (list == null || list.size() < 1) {
      close(0);
      return;
    }

    VirtualFile vf = FileChooser
        .chooseFile(new FileChooserDescriptor(false, true, false, false, false, false),
            project, project.getBaseDir());

    if (vf == null) {
      return;
    }

    MavenSearch search = new MavenCentralNetSearch();
    for (DownloadData d : list) {

      MavenId id = new MavenId(d.getDocument().getGroupId(), d.getDocument().getArtifactId(),
          d.getDocument().getVersion());

      String fileName = id.getArtifactId() + "-" + id.getVersion() + d.getType();

      String path = vf.getPath() + "/" + fileName;
      VirtualFile f = LocalFileSystem.getInstance().findFileByPath(path);
      if (f != null && f.exists()) {
        //todo alert
        return;
      }

      System.out.println("Start download " + fileName);
      loadingLabel.setText("Downloading...");

      downloading = true;

      new Thread(() -> {
        while (downloading) {

          if (!downloadProgress.isVisible()) {
            downloadProgress.setVisible(true);
          }

          for (int i = 0; i <= 100; i++) {
            if (!downloading) {
              downloadProgress.setVisible(false);
              break;
            }
            downloadProgress.setValue(i);
            try {
              Thread.sleep(10);
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            }
            if (i == 100) {
              i = 0;
            }
          }

        }
      }).start();

      new Thread(() -> {

        try {
          downloadProgress.setVisible(true);
          search.downloadFile(id, d.getType(), path, null);
//          (p) -> {
//            new Thread(() -> {
//              downloadProgress.setValue(p);
//            }).start();
//          }
        } catch (MavenSearchException e) {
          downloading = false;
          e.printStackTrace();
          loadingLabel.setText(e.getMessage());
          downloadProgress.setVisible(false);
          return;
        }

        downloading = false;
        loadingLabel.setText("Done!");
        downloadProgress.setVisible(false);
        vf.refresh(true, true);

      }).start();


    }

  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  public static class DownloadData {

    private Document document;
    private String type;

    public DownloadData(Document document, String type) {
      this.document = document;
      this.type = type;
    }

    @Override
    public String toString() {
      return document.getArtifactId() + "-" + document.getVersion() + type;
    }

    public Document getDocument() {
      return document;
    }

    public void setDocument(Document document) {
      this.document = document;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
