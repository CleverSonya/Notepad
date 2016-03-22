
package src;

import java.awt.FileDialog;
import java.io.File;
/** Performs opening new document in notepad
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.2
 */
public class OpenAction {
  protected static void performOpenAction(){
    // read info about previous file
    int tabNum = NoteFrame.tabbedPane.getTabCount() - 1;
    String previousDirPath = TabbedPane.openedTabs.get(tabNum).tabPath;

    // declare fileDialog
    FileDialog fileDialog = new FileDialog(
      NoteFrame.mainFrame,
      "My notepad - open...",
      FileDialog.LOAD
    );
    fileDialog.setDirectory(
      previousDirPath.substring(0, previousDirPath.lastIndexOf(File.separator))
    );
    fileDialog.setVisible(true);

    // process result
    String fileName = fileDialog.getFile();
    if (fileName != null){
      fileName = fileDialog.getDirectory() + fileName;
      File file = new File(fileName);

      Log.getLog().trace("Prepare to open new document: file choosen");
      fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
      String filePath = file.getAbsolutePath();

      // adding new tab to tabbedPane
      NoteFrame.tabbedPane.addTab(fileName, new TextArea(file));
      NoteFrame.tabbedPane.setTabComponentAt(tabNum + 1, new ClosePanel(fileName));
      TabbedPane.openedTabs.add(
        NoteFrame.tabbedPane.indexOfTab(fileName), new TabInfo(fileName, filePath));
      NoteFrame.tabbedPane.setSelectedIndex(NoteFrame.tabbedPane.indexOfTab(fileName));
      NoteFrame.mainFrame.repaint();
      Log.getLog().trace("Selected document opened in MyNotepad");
    }
  }
}
