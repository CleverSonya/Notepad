
package src;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/** Notepad menu.
 *  Contains File and Style submenus.
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */

public class FileMenu {

  private static JMenuBar menuBar;
  private static JMenuItem saveMenuItem;

  /**  JMenuItem factory
  *    @param name   name to be shown in menu*   @param description    accessible description shown on mouse enter
  *    @param description a description to be shown on mouse enter
  *    @param keyStroke    hot keys to call item
  *    @param enabled    JMenuItem.setEnabled
  *    @param actionListener     the action to perform
  *    @return     JMenuItem ready to be added to JMenuBar
   */
  private static JMenuItem getItem(
    String name, String description, KeyStroke keyStroke, boolean enabled, ActionListener actionListener
  ) {
    JMenuItem item = new JMenuItem(name);
    item.setAccelerator(keyStroke);
    item.getAccessibleContext().setAccessibleDescription(description);
    item.setEnabled(enabled);
    item.addActionListener(actionListener);
    return item;
  }

  /**  JMenuItem factory return enabled by default buttons
  *    @param name   name to be shown in menu
  *    @param description    accessible description shown on mouse enter
  *    @param keyStroke    hot keys to call item
  *    @param actionListener     the action to perform
  *    @return     JMenuItem ready to be added to JMenuBar
   */
  private static JMenuItem getItem(
    String name, String description, KeyStroke keyStroke, ActionListener actionListener
  ) {
    JMenuItem item = new JMenuItem(name);
    item.setAccelerator(keyStroke);
    item.getAccessibleContext().setAccessibleDescription(description);
    item.setEnabled(true);
    item.addActionListener(actionListener);
    return item;
  }

  /**  JMenuItem factory return enabled by default buttons without hot keys
      @param name   name to be shown in menu
      @param description    accessible description shown on mouse enter
      @param actionListener     the action to perform
      @return JMenuItem ready to be added to JMenuBar
   */
  private static JMenuItem getItem(
    String name, String description, ActionListener actionListener
  ) {
    JMenuItem item = new JMenuItem(name);
    item.getAccessibleContext().setAccessibleDescription(description);
    item.addActionListener(actionListener);
    return item;
  }

  /** used instead of default constructor */
  private static void initiate() {

    JMenu fileMenu, styleMenu;

    menuBar = new JMenuBar();

    fileMenu = new JMenu("File");
    fileMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
    menuBar.add(fileMenu);

    // File - New
    fileMenu.add(getItem(
      "New", "Create New Document",
      KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK),
      actionEvent -> NewButton.performNewAction()
    ));

    // File - Save
    saveMenuItem = getItem(
      "Save", "Save",
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
      false,
      actionEvent -> SaveAction.performSaveAction()
    );
    fileMenu.add(saveMenuItem);

    // File - Save as
    fileMenu.add(getItem(
      "Save as...", "Save as...",
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
      actionEvent -> SaveAsAction.performSaveAction()
    ));

    // File - Open
    fileMenu.add(getItem(
      "Open", "Open new document",
      KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK),
      actionEvent -> OpenAction.performOpenAction()
    ));

    fileMenu.addSeparator();

    // File - Copy
    fileMenu.add(getItem(
      "Copy", "Copy",
      KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
      actionEvent -> {
        TextArea textArea = NoteFrame.tabbedPane.getSelectedComponent();
        StringSelection stringSelection = new StringSelection(textArea.getSelectedText());
        Toolkit.getDefaultToolkit()
          .getSystemClipboard()
          .setContents(stringSelection, null);
      }));

    // File - Cut
    fileMenu.add(getItem(
      "Cut", "Cut",
      KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
      actionEvent -> {
        TextArea textArea = NoteFrame.tabbedPane.getSelectedComponent();
        StringSelection stringSelection = new StringSelection(textArea.getSelectedText());
        Toolkit.getDefaultToolkit()
          .getSystemClipboard()
          .setContents(stringSelection, null);
        textArea.removeSelected();
      }));

    // File - Paste
    fileMenu.add(getItem(
      "Paste", "Paste",
      KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
      actionEvent -> {
        String result = "";
        try {
          result = Toolkit.getDefaultToolkit()
            .getSystemClipboard()
            .getContents(null)
            .getTransferData(DataFlavor.stringFlavor)
            .toString();
        } catch (Exception ex) {
          Log.LOGGER.debug("Ctrl-V error: " + ex.getMessage());
        }
        NoteFrame.tabbedPane.getSelectedComponent()
          .insertText(result);
      }));

    fileMenu.addSeparator();

    // File - Undo
    fileMenu.add(getItem(
      "Undo", "Undo",
      KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK),
      actionEvent ->
        NoteFrame.tabbedPane.getSelectedComponent()
          .editorPane
          .undoManager
          .undo()
    ));

    // File - Redo
    fileMenu.add(getItem(
      "Redo", "Redo",
      KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK),
      actionEvent ->
        NoteFrame.tabbedPane.getSelectedComponent()
          .editorPane
          .undoManager
          .redo()
    ));

    // Menu - Style
    styleMenu = new JMenu("Style");
    styleMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
    menuBar.add(styleMenu);

    // Style - Bold
    fileMenu.add(getItem(
      "Bold", "Set bold font",
      KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK),
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setBold())
    );

    // Style - Italic
    fileMenu.add(getItem(
      "Italic", "Set italic font",
      KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK),
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setItalic())
    );

    // Style - Underline
    fileMenu.add(getItem(
      "Underline", "Set Underlined font",
      KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK),
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setUnderline())
    );

    styleMenu.addSeparator();

    // Style - Red
    fileMenu.add(getItem(
      "Red", "Set Red font",
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setColor(Color.RED))
    );

    // Style - Blue
    fileMenu.add(getItem(
      "Blue", "Set Blue font",
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setColor(Color.BLUE))
    );

    // Style - Green
    fileMenu.add(getItem(
      "Green", "Set Green font",
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setColor(Color.GREEN))
    );

    // Style - Black
    fileMenu.add(getItem(
      "Black", "Set Black font",
      actionEvent -> NoteFrame.tabbedPane.getSelectedComponent().setColor(Color.BLACK))
    );
  }

  /** Returns JMenuBar ready to be added to JPanel,initiating one if nessecary
     @return JMenuBar with all the buttons declared
  */
  public static JMenuBar getMenuBar() {
    if (menuBar == null) initiate();
    return menuBar;
  }

  /**  Set File - Save menuIted to a given active state
  *    @param state    true if JMenuItem needs to be enabled, false otherwise
  */
  protected static void activateSaveOption(boolean state) {
    if (menuBar == null) initiate();
    saveMenuItem.setEnabled(state);
  }

}
