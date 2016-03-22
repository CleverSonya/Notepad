
package src;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/** Simple notepad with multiple tabs and extended font edition (color, size, font family).
 *  Runs on JVM 1.8
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class NoteFrame {

  protected static JPanel mainPanel = new JPanel();
  protected static JPanel menuToolPanel = new JPanel();
  protected static JFrame mainFrame = new JFrame("");
  protected static JToolBar editToolBar = new JToolBar();
  protected static JToolBar styleToolBar = new JToolBar();
  protected static boolean closingState = false;
  private static Thread noteFrameCloseThread = new Thread();
  protected static String[] possibleExts = new String[]{"txt", ".xml", "rtf", "java", "doc", "au3", "rtf"};
  protected static JLabel boldButton, italicButton, underlineButton;
  protected static NoteButton undoButton = new NoteButton("","UndoActive.gif","UndoActive.gif","");
  protected static NoteButton saveButton = new NoteButton("","UndoActive.gif","UndoActive.gif","");
  protected static NoteButton saveAsButton = new NoteButton("","UndoActive.gif","UndoActive.gif","");
  protected static NoteButton redoButton = new NoteButton("","UndoActive.gif","UndoActive.gif","");
  public static TabbedPane tabbedPane;

  public static void main(String[] args) {

    // read preferences.xml
    UserData.read();

    // LookAndFeel setting
   try {
      UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
      Log.LOGGER.trace("WebLookAndFeel is set");
    } catch (Exception e) {
      Log.LOGGER.error("Cannot change LookAndFeel: " + e.toString());
      for (StackTraceElement stackLine : e.getStackTrace())
        Log.LOGGER.error(stackLine);
      Log.LOGGER.trace("Using Default LAF");
    }

    // initiating mainPanel
    try {
      mainPanel = new JPanel();
      mainPanel.setLayout(new BorderLayout());

      Log.LOGGER.trace("MainPanel is declared");
    } catch (Exception e) {
      Log.LOGGER.fatal("Cannot initiate MainPanel: " + e.toString());
      for (StackTraceElement stackLine : e.getStackTrace())
        Log.LOGGER.fatal(stackLine);
      System.exit(1);
    }

    // initiating editToolBar
    try {
      SwingUtilities.invokeAndWait(() -> {
        try {
          editToolBar = new JToolBar();
          editToolBar.setLayout(new BoxLayout(editToolBar, BoxLayout.X_AXIS));
          editToolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
          editToolBar.setFloatable(false);
          editToolBar.setVisible(true);
          editToolBar.setBorderPainted(false);
          editToolBar.setFocusable(false);

          Log.LOGGER.trace("editToolBar is declared");
        } catch (Exception e) {
          Log.LOGGER.fatal("Cannot initiate editToolBar: " + e.toString());
          for (StackTraceElement stackLine : e.getStackTrace())
            Log.LOGGER.fatal(stackLine);
        }
      });
    } catch (Exception e) {
      Log.LOGGER.fatal("Cannot initiate editToolBar: " + e.toString());
      for (StackTraceElement stackLine : e.getStackTrace())
        Log.LOGGER.fatal(stackLine);
    }

    editToolBar.add(new NewButton().getButton(
      (MouseClicked) e -> NewButton.performNewAction()
    ));
    editToolBar.add(Box.createHorizontalStrut(10));

    saveButton = new NoteButton(
      "Save", "SaveActiveSmall.gif", "SaveInactiveSmall.gif",
      "Save"
    );
    editToolBar.add(saveButton.getButton(
      (MouseClicked) e -> SaveAction.performSaveAction()
    ));
    editToolBar.add(Box.createHorizontalStrut(10));

    saveAsButton = new NoteButton(
      "Save As...", "SaveAsActiveSmall.gif", "SaveAsInactiveSmall.gif",
      "Save As..."
    );
    saveAsButton.setEnabled(true);
    editToolBar.add(saveAsButton.getButton(
      (MouseClicked) e -> SaveAsAction.performSaveAction()
    ));
    editToolBar.add(Box.createHorizontalStrut(10));

    editToolBar.add(new NoteButton(
      "Open", "OpenSmall.gif", "OpenSmall.gif", "Open new document"
    ).getButton(
      (MouseClicked) e -> OpenAction.performOpenAction()
    ));
    editToolBar.add(Box.createHorizontalStrut(10));

    undoButton = new NoteButton(
      "Undo", "UndoActiveSmall.gif", "UndoInactiveSmall.gif", "Undo");
    editToolBar.add(undoButton.getButton((MouseClicked) e ->
      tabbedPane.getSelectedComponent()
        .editorPane
        .undoManager
        .undo()));
    editToolBar.add(Box.createHorizontalStrut(10));

    redoButton = new NoteButton(
      "Redo", "RedoActiveSmall.gif", "RedoInactiveSmall.gif", "Redo");
    editToolBar.add(redoButton.getButton((MouseClicked) e ->
      tabbedPane.getSelectedComponent()
        .editorPane
        .undoManager
        .redo()));
    editToolBar.add(Box.createHorizontalStrut(10));

    // initiating styleToolBar
    try {
      SwingUtilities.invokeAndWait(() -> {
        try {
          styleToolBar = new JToolBar();
          styleToolBar.setLayout(new BoxLayout(styleToolBar, BoxLayout.X_AXIS));
          styleToolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
          styleToolBar.setFloatable(false);
          styleToolBar.setVisible(true);
          styleToolBar.setBorderPainted(false);
          styleToolBar.setFocusable(false);

          Log.LOGGER.trace("styleToolBar is declared");
        } catch (Exception e) {
          Log.LOGGER.fatal("Cannot initiate styleToolBar: " + e.toString());
          for (StackTraceElement stackLine : e.getStackTrace())
            Log.LOGGER.fatal(stackLine);
        }
      });
    } catch (Exception e) {
      Log.LOGGER.fatal("Cannot initiate styleToolBar: " + e.toString());
      for (StackTraceElement stackLine : e.getStackTrace())
        Log.LOGGER.fatal(stackLine);
    }

    try {
      styleToolBar.add(FontChooser.getFontChooser());
      styleToolBar.add(Box.createHorizontalStrut(10));
    } catch (NullPointerException npe) {
      Log.LOGGER.error("FontChooser is not displayed: no fonts available");
      Log.LOGGER.trace("Notepad is performing with one local font");
    }
    styleToolBar.add(FontSizeChooser.getFontChooser());
    styleToolBar.add(Box.createHorizontalStrut(10));

    boldButton = new NoteButton(
      "Bold", "BoldActiveSmall.gif", "BoldInactiveSmall.gif",
      "Set Bold font")
      .getButton(TextArea::setBold);
    styleToolBar.add(boldButton);

    italicButton = new NoteButton(
      "Italic", "ItalicActiveSmall.gif", "ItalicInactiveSmall.gif",
      "Set Italic font")
      .getButton(TextArea::setItalic);
    styleToolBar.add(italicButton);

    underlineButton = new NoteButton(
      "Underlined", "UnderlineActiveSmall.gif", "UnderlineInactiveSmall.gif",
      "Set Underlined font")
      .getButton(TextArea::setUnderline);
    styleToolBar.add(underlineButton);

    styleToolBar.add(Box.createHorizontalStrut(10));

    styleToolBar.add(FontColorChooser.getFontChooser());
    styleToolBar.add(Box.createHorizontalStrut(10));

    menuToolPanel.setLayout(new GridLayout(3, 1, 0, 0));
    menuToolPanel.add(FileMenu.getMenuBar());
    menuToolPanel.add(editToolBar);
    menuToolPanel.add(styleToolBar);

    mainPanel.add(menuToolPanel, BorderLayout.PAGE_START);

    // initiating JTabbedPane
    tabbedPane = new TabbedPane();
    mainPanel.add(tabbedPane, BorderLayout.CENTER);

    // initiating JFrame
    try {
      SwingUtilities.invokeAndWait(() -> {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mainFrame = new JFrame("My Notepad");

        //overriding default close operation
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
          @Override
          public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            NoteFrame.exit();
          }
        });

        mainFrame.setResizable(true);
        mainFrame.getContentPane().add(mainPanel);

        // add app icon
        String iconPath = System.getProperty("user.dir") + File.separator + "images" + File.separator + "IconSmall.gif";
        try {
          mainFrame.setIconImage(ImageIO.read(new File(iconPath)));
          Log.LOGGER.trace("App Icon is set");
        } catch (IOException ioe) {
          Log.LOGGER.error("Cannot set app Icon: " + ioe.toString());
          for (StackTraceElement stackLine : ioe.getStackTrace())
            Log.LOGGER.error(stackLine);
        }

        mainFrame.pack();
        mainFrame.setVisible(true);

        Log.LOGGER.trace("JFrame is set");
      });
    } catch (Exception e) {
      Log.LOGGER.fatal("Cannot initiate JFrame: " + e.toString());
      for (StackTraceElement stackLine : e.getStackTrace())
        Log.LOGGER.fatal(stackLine);
      System.exit(1);
    }
  }

  /** overrides default windows.close action to be sure all docs are saves */
  protected static void exit() {
    NoteFrame.closingState = true;

    noteFrameCloseThread = new Thread(() -> {
      // prepare JFrame for saving
      SaveQuestionFrame saveQFrame;

      // check whether there are tabs waiting to save
      for (int tabNum = 0; tabNum < TabbedPane.openedTabs.size(); tabNum++) {
        if (TabbedPane.openedTabs.get(tabNum).saveState) {
          NoteFrame.mainFrame.setVisible(true);
          tabbedPane.setSelectedIndex(tabNum);
          saveQFrame = new SaveQuestionFrame(true);

          // wait till current tab closes
          try {
            saveQFrame.closeOnDispose.join();
          } catch (InterruptedException ie) {
            Log.LOGGER.error("InterruptedException: NoteFrame.exit() may perform strange");
            for (StackTraceElement stackLine : ie.getStackTrace())
              Log.LOGGER.error(stackLine);
          }

          // if cancel button pressed - interrupt noteFrameCloseThread
          if (saveQFrame.cancelButtonPressed) {
            noteFrameCloseThread.interrupt();
            Log.LOGGER.trace("Frame disposal was interrupted: cancel button pressed");
          } else {
            NoteFrame.mainFrame.setVisible(false);
          }
        }
      }

      // delete empty new documents
      String dirPath = System.getProperty("user.dir") + File.separator + "docs";
      try {
        Files.walk(Paths.get(dirPath)).forEach(filePath -> {
          if (Files.isRegularFile(filePath))
            try {
              if (Files.size(filePath) == 0)
                Files.delete(filePath);
            } catch (IOException e) {
              Log.LOGGER.error("Unable to delete empty new documents after shutdown", e);
            }
        });
        Log.LOGGER.trace("Empty new docs has been deleted");
      } catch (IOException e) {
        Log.LOGGER.error("Unable to delete empty new documents after shutdown", e);
      }

      // save info about current tabs
      try {
        UserData.create();
        UserData.save();
        Log.LOGGER.trace("preferences.xml saved successfully");
      } catch (Exception e) {
        Log.LOGGER.error("Unable to save preferences.xml", e);
        for (StackTraceElement stackLine : e.getStackTrace())
          Log.LOGGER.error(stackLine);
      }

      if (!NoteFrame.mainFrame.isVisible()) {
        NoteFrame.mainFrame.dispose();
        System.exit(0);
      }
    });

    noteFrameCloseThread.setName("noteFrameCloseThread");
    noteFrameCloseThread.start();
  }
}
