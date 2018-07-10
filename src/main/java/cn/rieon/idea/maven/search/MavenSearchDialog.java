package cn.rieon.idea.maven.search;

import cn.rieon.idea.maven.search.DownloadFileDialog.DownloadData;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.jetbrains.idea.maven.dom.MavenDomUtil;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class MavenSearchDialog extends DialogWrapper {

  private JPanel contentPane;
  private JTable resultTable;
  private JPanel actionPannel;
  private JButton addBtn;
  private JButton viewBtn;
  private JButton downloadBtn;
  private JLabel loadingLabel;
  private JButton searchBtn;
  private JProgressBar progressBar;
  private JTextField fuzzyTx;
  private JTextField groupIdTx;
  private JTextField artifactIdTx;
  private JTextField versionTx;
  private JTextField packagingTx;
  private JTextField classifierTx;
  private JTextField classnameTx;
  private JButton moreBtn;
  private JTabbedPane tabbedPane;
  private JScrollPane tableScrollPane;
  private JLabel countLabel;

  private Project project;
  private int selected = -1;
  private boolean allVersions = false;

  private static int size = 20;
  private static int start = 0;
  private static int totalRecords = 0;
  private static int loadedRecords = 0;

  private static volatile boolean loading = false;

  private final MavenDocumentTableModel model = new MavenDocumentTableModel(new ArrayList<>());


  public MavenSearchDialog(@Nullable Project project, boolean canBeParent) {
    super(project, canBeParent);
    this.project = project;

    setModal(true);
    setTitle("Maven Search");

    actionPannel.setVisible(false);
    progressBar.setVisible(false);

    init();

    initSearchButton();

    initResultTable();

    initViewButton();

    initMoreVersionButton();

    initAddButton();

    initDownloadButton();

  }

  private void initDownloadButton() {

    downloadBtn.addActionListener(e -> {

      if (selected == -1) {
        System.out.println("No item selected");
        return; //TODO alert
      }

      Document document = model.getDocuments().get(selected);

      if (document == null) {
        System.out.println("No artifact found");
        return; //TODO alert
      }

      List<String> ec = document.getEc();
      Vector<DownloadData> data = new Vector<>();
      for (String s : ec) {
        DownloadData d = new DownloadData(document, s);
        data.add(d);
      }

      DownloadFileDialog dialog = new DownloadFileDialog(project, data);
      dialog.show();

    });

  }

  private void initAddButton() {

    addBtn.addActionListener((e) -> {

      if (selected == -1) {
        return; //todo alert
      }

      Document document = model.getDocuments().get(selected);
      if (document == null) {
        return;//todo alert
      }
      MavenId id = new MavenId(document.getGroupId(), document.getArtifactId(),
          document.getVersion());
      List<MavenProject> projects = MavenProjectsManager.getInstance(project).getProjects();

      for (MavenProject mp : projects) { // todo select project to add

        if (mp == null) {
          return;
        }
        final MavenDomProjectModel m = MavenDomUtil.getMavenDomProjectModel(project, mp.getFile());
        if (m == null) {
          return;
        }
        WriteCommandAction.writeCommandAction(project).withName("Add Maven Dependency").run(() -> {
          MavenDomDependency dependency = MavenDomUtil.createDomDependency(m, null, id);
        });

      }

    });

  }

  private void initMoreVersionButton() {
    moreBtn.addActionListener(e -> {

      if (selected == -1) {
        return; //todo alert
      }

      Document document = model.getDocuments().get(selected);
      if (document == null) {
        return;//todo alert
      }

      groupIdTx.setText(document.getGroupId());
      artifactIdTx.setText(document.getArtifactId());

      tabbedPane.setSelectedIndex(1);

      allVersions = true;
      searchBtn.doClick();
      allVersions = false;

    });
  }

  private void initViewButton() {
    viewBtn.addActionListener((e) -> {

      if (selected == -1) {
        System.out.println("No item selected");
        return; //TODO alert
      }

      Document document = model.getDocuments().get(selected);

      if (document == null) {
        System.out.println("No artifact found");
        return; //TODO alert
      }

      loading = true;
      showLoading();

      loadingLabel.setText("Downloading...");

      MavenSearch search = new MavenCentralNetSearch();

      new Thread(() -> {

        try {
          String pom = search
              .getPom(document.getGroupId(), document.getArtifactId(), document.getVersion());

          VirtualFile file = project.getBaseDir();

          if (file == null) {
            System.out.println("Virtual File is null");
            return; //TODO alert
          }

          loading = false;
          progressBar.setVisible(false);
          loadingLabel.setText("Done!");

          SwingUtilities.invokeLater(() -> {
            CodeViewerDialog dialog = new CodeViewerDialog(document.getArtifactId(), pom,
                XmlFileType.INSTANCE, project, true);
            dialog.show();
          });

        } catch (MavenSearchException e1) {
          e1.printStackTrace();
          loading = false;
          progressBar.setVisible(false);
          loadingLabel.setText(e1.getMessage());
        }
      }).start();
//      CodeViewerDialog viewer = new CodeViewerDialog(pom, XmlFileType.INSTANCE, project);
//      viewer.show();

//        LightVirtualFile vp = new LightVirtualFile(document.getArtifactId(),
//            XmlFileType.INSTANCE, pom);
//        vp.setWritable(false);
//        FileEditorManager.getInstance(project)
//            .openTextEditor(new OpenFileDescriptor(project, vp), true);

    });

  }

  private void initResultTable() {

    resultTable.setModel(model);
    resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    resultTable.getSelectionModel().addListSelectionListener(e1 -> {
      selected = resultTable.getSelectedRow();
      System.out.println("Selected row " + selected);
      actionPannel.setVisible(true);
    });

    JScrollBar sb = tableScrollPane.getVerticalScrollBar();

    sb.addAdjustmentListener(e -> {

      float total = sb.getMaximum() - sb.getHeight();
      float current = e.getValue();
      if (total == 0 || current == 0) {
        return;
      }
      float p = current / total;
      if (p >= 0.85 && !loading && loadedRecords < totalRecords) {

        start = start + size;

        searchAndLoad();

      }

    });

  }

  private void initSearchButton() {

    searchBtn.addActionListener(e -> {

      start = 0;
      size = 100;

      //clear the table and hide the action buttons
      model.clear();
      actionPannel.setVisible(false);

      searchAndLoad();
    });

  }

  private void searchAndLoad() {

    loading = true;

    showLoading();

    String fuzzyText = fuzzyTx.getText();
    String groupIdText = groupIdTx.getText();
    String artifactIdText = artifactIdTx.getText();
    String versionText = versionTx.getText();
    String classifierText = classifierTx.getText();
    String classnameText = classnameTx.getText();
    String packagingText = packagingTx.getText();

    MavenSearch search = new MavenCentralNetSearch();
    MavenSearchQuery query;

    int selectedIndex = tabbedPane.getSelectedIndex();

    switch (selectedIndex) {
      case 0:  //fuzzy mode
        query = MavenSearchQuery.builder()
            .fuzzy(fuzzyText)
            .start(start).size(size)
            .build();
        break;
      case 1:
        query = MavenSearchQuery.builder()
            .groupId(groupIdText)
            .artifactId(artifactIdText)
            .size(size).start(start)
            .version(versionText)
            .classifier(classifierText)
            .packaging(packagingText)
            .allVersions(allVersions)
            .build();
        break;
      case 2:
        query = MavenSearchQuery.builder()
            .classname(classnameText)
            .size(size).start(start)
            .build();
        break;
      default:
        return; //error!
    }

    //start query as background task
    new Thread(() -> {

      loadingLabel.setText("Searching....");
      SwingUtilities.updateComponentTreeUI(loadingLabel);

      try {
        List<Document> documents = search.search(query, true);

        int first = model.getDocuments().size();
//        model.getDocuments().addAll(documents);
        int last = model.getDocuments().size();
//        model.fireTableRowsInserted(first, last);
//        model.fireTableDataChanged();
        model.addDocuments(documents);
        totalRecords =
            documents.size() == 0 ? model.getDocuments().size() : documents.get(0).get_total();
        loadedRecords = model.getDocuments().size();
        countLabel.setText("Loaded: " + loadedRecords + "/ Total: " + totalRecords);
        loading = false;
        loadingLabel.setText("Done!");
        progressBar.setVisible(false);
      } catch (MavenSearchException e1) {
        e1.printStackTrace();

        model.clear();
        loading = false;
        loadingLabel.setText(e1.getMessage());
        progressBar.setVisible(false);
      }

    }).start();

  }


  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  private void showLoading() {

    new Thread(() -> {
      while (loading) {

        if (!progressBar.isVisible()) {
          progressBar.setVisible(true);
        }

        for (int i = 0; i <= 20; i++) {
          if (!loading) {
            break;
          }
          progressBar.setValue(i * 5);
          try {
            Thread.sleep(100);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          if (i == 20) {
            i = 0;
          }
        }

      }
    }).start();

  }

}
