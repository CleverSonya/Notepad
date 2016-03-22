package src;

/**
 *  Notepad tab closing action
 *
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */

public class CloseAction {
  protected static void performCloseAction(){
    try {
      // if these was the last tab. - close a frame
      if (NoteFrame.tabbedPane.getTabCount() == 1) {
        // if app is currently closing, skip NoteFrame.exit() to avoid recursion
        if (!NoteFrame.closingState) {
          Log.LOGGER.trace("Last tab, app exit");
          NoteFrame.exit();
        }
      } else {
        NoteFrame.tabbedPane.removeSelectedTab();
      }

      Log.LOGGER.trace("Tab closed: action performed");
    } catch (Exception ex) {
      Log.getLog().error("Error during closing a tab: " + ex.toString());
      for (StackTraceElement stackLine : ex.getStackTrace())
        Log.LOGGER.error(stackLine);
    }
  }
}
