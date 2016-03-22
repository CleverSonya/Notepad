
package src;

import javax.swing.text.DefaultStyledDocument;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/** Performs saving a document with a previously chosen path.
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class SaveAction {
  protected static void performSaveAction(){
    try {
      TextArea selectedArea = NoteFrame.tabbedPane.getSelectedComponent();
      DefaultStyledDocument doc = selectedArea.getDoc();
      String docPath = selectedArea.getDocPath();

      // if doc is NewDocument - perform save as
      int selectedTabIndex = NoteFrame.tabbedPane.getSelectedIndex();
      TabInfo fileInfo = TabbedPane.openedTabs.get(selectedTabIndex);
      if (fileInfo.tabName.contains("New Document")) {
        SaveAsAction.performSaveAction();
        NoteFrame.saveButton.setEnabled(false);
        NoteFrame.tabbedPane.setSaveState(false);
        return;
      }

      FileOutputStream fos = new FileOutputStream(docPath);
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

    } catch (Exception ex) {
      Log.getLog().error("Save Button error: " + ex.toString());
      for (StackTraceElement stackLine : ex.getStackTrace())
        Log.LOGGER.error(stackLine);
    }
    NoteFrame.saveButton.setEnabled(false);
    FileMenu.activateSaveOption(false);
    NoteFrame.tabbedPane.setSaveState(false);
    Log.LOGGER.trace("Document is saved");
  }
}
