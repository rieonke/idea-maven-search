package cn.rieon.idea.maven.search;

import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.WindowWrapper.Mode;
import com.intellij.openapi.ui.WindowWrapperBuilder;
import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class CodeViewerDialog {

  @NotNull
  private final Project project;
  @NotNull
  private final JPanel editorPanel;
  @NotNull
  private final Mode windowMode;
  private WindowWrapper windowWrapper;

  private String title;


  public CodeViewerDialog(String title, String text, FileType type, @NotNull Project project,
      boolean dialog) {
    this.project = project;
    this.title = title;

    windowMode = dialog ? Mode.MODAL : Mode.FRAME;
    editorPanel = new ScrollablePanel();
    editorPanel.setSize(new Dimension(600, 500));
    editorPanel.setPreferredSize(new Dimension(600, 500));
    editorPanel.setLayout(new GridLayout(0, 1));
    DocumentEx document = new DocumentImpl(text, true, false);
    EditorTextField field = new EditorTextField(document, this.project, type, true, false) {
      @Override
      protected EditorEx createEditor() {
        EditorEx editor1 = super.createEditor();
        editor1.setVerticalScrollbarVisible(true);
        editor1.setHorizontalScrollbarVisible(true);
        return editor1;

      }
    };
    field.setBorder(new ColoredSideBorder(JBColor.LIGHT_GRAY,
        JBColor.LIGHT_GRAY, JBColor.LIGHT_GRAY,
        JBColor.LIGHT_GRAY, 2));
    field.setSize(600, 500);
    field.setPreferredSize(new Dimension(600, 500));
    editorPanel.add(field);


  }

  public void show() {
    if (windowWrapper == null) {
      windowWrapper = new WindowWrapperBuilder(windowMode, editorPanel)
          .setProject(project)
          .setTitle(title)
          .setPreferredFocusedComponent(editorPanel)
          .setDimensionServiceKey(CodeViewerDialog.class.getName())
          .build();
    }
    windowWrapper.show();
  }

}
