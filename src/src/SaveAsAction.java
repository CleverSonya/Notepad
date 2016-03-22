
package src;

import javax.swing.text.DefaultStyledDocument;
import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/** Performs saving action when no path is chosen yet
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class SaveAsAction {
  protected static void performSaveAction(){
    TextArea selectedArea = NoteFrame.tabbedPane.getSelectedComponent();
    DefaultStyledDocument doc = selectedArea.getDoc();
    File previousFile = new File(selectedArea.getDocPath());

    // declare fileDialog
    FileDialog fileDialog = new FileDialog(
      NoteFrame.mainFrame,
      "My notepad - save as...",
      FileDialog.SAVE
    );
    fileDialog.setDirectory(
      previousFile.getAbsolutePath().substring(0, previousFile.getAbsolutePath().lastIndexOf(File.separator))
    );
    fileDialog.setVisible(true);

    String fileName = fileDialog.getFile();
    String fileDirectory = fileDialog.getDirectory();

    if (fileName != null){

      File file = new File(fileDirectory + File.separator + fileName);

      // check whether file format is valid
      boolean valid = false;
      if (fileName.contains(".")) {
        String currentExt = fileName.substring(fileName.lastIndexOf(".") + 1);

        for (String extention : NoteFrame.possibleExts)
          if (currentExt.equalsIgnoreCase(extention)) {
            valid = true; break;
          }
      }

      // if not - save it as txt file
      if (!valid) file = new File(file.toString() + ".txt");

      // saving data
      try {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(fos));

        try {
          int step = 100;
          for (int offset = 0; offset < doc.getLength() - 1; offset += 100) {
            if (doc.getLength() <= 100) {
              step = doc.getLength() - offset;
              if (step <= 0) break;
            }
            buff.write(doc.getText(offset, step));
            buff.flush();
          }
        } finally {
          try {
            buff.close();
            fos.close();
          } catch (IOException ioe){
            Log.getLog().error("Resource leak, cannot close outputStream: " + ioe.toString());
            for (StackTraceElement stackLine : ioe.getStackTrace())
              Log.LOGGER.error(stackLine);
          }
        }
      } catch  (Exception ex) {
        Log.getLog().error("Save as Action error: " + ex.toString());
        for (StackTraceElement stackLine : ex.getStackTrace())
          Log.LOGGER.error(stackLine);
      }
      FileMenu.activateSaveOption(false);
      NoteFrame.saveButton.setEnabled(false);
      NoteFrame.tabbedPane.setSaveState(false);

      // updating tabbedPane title
      String fileTitle = file.getName().substring(0, file.getName().lastIndexOf("."));
      NoteFrame.tabbedPane.updateSelectedTabComponent(fileTitle);

      // updating openedTab info
      int selectedTabIndex = NoteFrame.tabbedPane.getSelectedIndex();
      TabInfo previousFileInfo = TabbedPane.openedTabs.get(selectedTabIndex);
      TabbedPane.openedTabs.remove(selectedTabIndex);
      TabbedPane.openedTabs.add(new TabInfo(fileTitle, file.getAbsolutePath()));

      // if previous file was NewDocument - delete
      if (previousFileInfo.tabName.contains("New Document"))
        if (previousFileInfo.tabPath.contains(File.separator + "docs" + File.separator))
          if (!previousFile.delete())
            Log.LOGGER.error("Unable to delete an empty NewDocument : " + previousFile.getName());

      Log.LOGGER.trace("Document is saved as new, tab info updated");
    }

  }
}
