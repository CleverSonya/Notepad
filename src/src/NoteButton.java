
package src;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
/** Default button for notepad menu
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.2
 */
public class NoteButton extends JLabel{

  private JLabel button;

  /**  Default and only constructor
  *    @param name Name of button
  *    @param activeImageName short name of file with enabled button icon in /images/
  *    @param inactiveImageName short name of file with disabled button icon in /images/
  *    @param tooltipText tooltipText to be shown at mouse enter
   */
  NoteButton(String name, String activeImageName,
         String inactiveImageName, String tooltipText){
    try {
      button = new JLabel();
      button.setName(name);
      // set enabled button image
      try {
        button.setIcon(
          new ImageIcon(
            ImageIO.read(
              new File(
                System.getProperty("user.dir") + File.separator + "images" + File.separator + activeImageName
              )
            )
          ));
      } catch (IOException ioe){
        Log.LOGGER.error("IOException in setting button icon: icon file is unreachable");
        for (StackTraceElement stackLine : ioe.getStackTrace())
          Log.LOGGER.fatal(stackLine);
      }
      // set disabled button image
      try {
        button.setDisabledIcon(
          new ImageIcon(
            ImageIO.read(
              new File(
                System.getProperty("user.dir") + File.separator + "images" + File.separator + inactiveImageName
              )
            )
          ));
      } catch (IOException ioe){
        Log.LOGGER.error("IOException in setting button icon: icon file is unreachable");
        for (StackTraceElement stackLine : ioe.getStackTrace())
          Log.LOGGER.fatal(stackLine);
      }

      button.setVisible(true);
      button.setToolTipText(tooltipText);
      button.setEnabled(false);
      button.setAlignmentX(Component.RIGHT_ALIGNMENT);
      Log.LOGGER.trace(button.getName() + " is set");
    } catch (Exception ex){
      Log.getLog().error("Error initiating button " + button.getName() + " : " + ex.toString());
      for (StackTraceElement stackLine : ex.getStackTrace())
        Log.LOGGER.error(stackLine);
    }
  }

  /** Returns button with given mouseListener
  * @param mouseListener MouseListener to set
  * @return Jlabel button with new mouseListener */
  protected JLabel getButton(MouseListener mouseListener) {
    button.addMouseListener(mouseListener);
    return button;
  }

  /** Returns buttons with mouseListener set according to given state
  * @param func A function wich takes selected TextArea as params and returns boolean
  * @return Jlabel button with new mouseListener */
  protected JLabel getButton(Function<TextArea, Boolean> func){
    button.addMouseListener(
      (MouseClicked) e -> button.setEnabled(
        func.apply(NoteFrame.tabbedPane.getSelectedComponent())
      )
    );
    return button;
  }

  /** Overrides JLabel.setEnabled()
   * @param enabled A boolean of whether or no a button should be enabled
   */
  @Override
  public void setEnabled(boolean enabled){
    button.setEnabled(enabled);
  }

  protected boolean checkActive(){ return button.isEnabled();}

}
