package cn.rieon.idea.maven.search;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

public class MavenSearchAction extends DumbAwareAction {

  private Project project;

  @Override
  public void actionPerformed(AnActionEvent e) {
    project = e.getProject();

    MavenSearchDialog dialog = new MavenSearchDialog(project, true);
    dialog.show();

  }
}
