
package src;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *  Notepad tab component.
 *  Contains tab title and x-button for closing a tab.
 *
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */

public class ClosePanel extends JPanel {

  static final long serialVersionUID = 42L;

  public JLabel close;
  private JLabel title;
  private BufferedImage myPicture;
  private String basePath = System.getProperty("user.dir") + File.separator + "images" + File.separator;
  protected static String currentTitle;

  /** Default constructor
   *  @param text   file name to show on tab title
  */
  public ClosePanel(String text) {
    try {
      new JPanel();
      setLayout(new FlowLayout());

      title = new JLabel(text);

      close = new JLabel();
      try {
        myPicture = ImageIO.read(new File(basePath + "closeInactive.png"));
      } catch (IOException e) {
        Log.LOGGER.error("Tab close icon not found");
        for (StackTraceElement stackLine : e.getStackTrace())
          Log.LOGGER.error(stackLine);
      }
      close.setIcon(new ImageIcon(myPicture));
      close.setToolTipText("Close the tab");
      try {
        close.addMouseListener(new MouseListener() {
          public void mouseEntered(MouseEvent e) {
            try {
              myPicture = ImageIO.read(new File(basePath + "closeActive.gif"));
              Log.LOGGER.trace("Tab close icon has changed (mouse entered)");
            } catch (IOException ex) {
              Log.LOGGER.error("Tab close icon not found");
              for (StackTraceElement stackLine : ex.getStackTrace())
                Log.LOGGER.error(stackLine);
            }
            close.setIcon(new ImageIcon(myPicture));
            close.repaint();
          }

          public void mouseExited(MouseEvent e) {
            try {
              myPicture = ImageIO.read(new File(basePath + "closeInactive.png"));
              Log.LOGGER.trace("Tab close icon has changed (mouse exited)");
            } catch (IOException ex) {
              Log.LOGGER.error("Tab close icon not found");
              for (StackTraceElement stackLine : ex.getStackTrace())
                Log.LOGGER.error(stackLine);
            }
            close.setIcon(new ImageIcon(myPicture));
            close.repaint();
          }

          public void mouseClicked(MouseEvent e) {
            // activate closing tab
            currentTitle = text;
            NoteFrame.tabbedPane.setSelectedIndex(
              NoteFrame.tabbedPane.indexOfTab(ClosePanel.currentTitle));
            // delete an empty new document
            Log.LOGGER.trace("Closing a tab...");

            // check whether there is anything to save
            // if there's nothing to save - just close
            if (!NoteFrame.saveButton.checkActive())
              CloseAction.performCloseAction();
            else {
              new SaveQuestionFrame(false);
            }
            Log.LOGGER.trace("Closing tab: empty new document deleted");
          }

          public void mousePressed(MouseEvent e) {
            NoteFrame.tabbedPane.setSelectedIndex(
              NoteFrame.tabbedPane.indexOfTab(text));
          }

          public void mouseReleased(MouseEvent e) {}
        });
      } catch (Exception e) {
        Log.LOGGER.error("Error during creating tab close icon");
        for (StackTraceElement stackLine : e.getStackTrace())
          Log.LOGGER.error(stackLine);
      }

      add(title);
      add(close);
      Log.LOGGER.trace("ClosePanel initiated");
    } catch (Exception e) {
      Log.LOGGER.error("Cannot initiate TabPanel");
      for (StackTraceElement stackLine : e.getStackTrace())
        Log.LOGGER.error(stackLine);
    }
  }

  /** Update tab title and returns private field
   *  @param newTitle   new file name to show on tab title
   *  @return ClosePanel object;
  */
  protected ClosePanel getUpdatedPanel(String newTitle) {
    this.title.setText(newTitle);
    return this;
  }

}
