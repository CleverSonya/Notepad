
package src;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.io.File;
import java.awt.Dimension;

/** PopupMenu with font colors to set: red, blue, green, black.
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */

public class FontColorChooser {

	private static JLabel colorChooser;
	private static JPopupMenu popUp;

	/**	Returns ready-to-use JMenuItem with given params.
	*		@param colorName Name of color to set
	*		@return JMenuItem an item ready to be added to JPopupMenu
	 */
	private static JMenuItem getMenuItem(String colorName){
		colorName = Character.toUpperCase(colorName.charAt(0)) +
			colorName.substring(1).toLowerCase();
    JMenuItem item = new JMenuItem(colorName);
    item.setName(colorName);
    item.getAccessibleContext().setAccessibleDescription("Set " + colorName + " font color");
    item.setIcon(new ImageIcon(System.getProperty("user.dir") +
      File.separator + "images" + File.separator + "Color" + colorName + ".gif"));
    item.setEnabled(true);
		Color color;
		switch (colorName){
			case "Red": { color = Color.RED; break;	}
			case "Blue": { color = Color.BLUE; break; }
			case "Green": { color = Color.GREEN; break;}
			default: color = Color.BLACK;
		}
    item.addActionListener(actionEvent ->
			NoteFrame.tabbedPane.getSelectedComponent()
				.setColor(color));
    return item;
  }

	/** Initiates FontColorChooser */
	private static void initiate(){

		try {
			String basePath = System.getProperty("user.dir") + File.separator + "images" + File.separator;
			
			colorChooser = new JLabel();
			colorChooser.setIcon(new ImageIcon(basePath + "ColorAlLSmall.gif"));
			colorChooser.setVisible(true);
			colorChooser.setName("Font color");
			colorChooser.setToolTipText("Font color");
			colorChooser.setOpaque(false);
			colorChooser.setEnabled(true);

			// seetting popup
			popUp = new JPopupMenu();
      popUp.add(getMenuItem("Red"));
			popUp.add(getMenuItem("Blue"));
			popUp.add(getMenuItem("Green"));
			popUp.add(getMenuItem("Black"));
			popUp.pack();

      colorChooser.addMouseListener((MouseClicked) e -> {
        Dimension size = colorChooser.getSize();
        int xPos = ((size.width - popUp.getPreferredSize().width) / 4);
        int yPos = size.height;
        popUp.show(colorChooser, xPos, yPos);
      });
			
			Log.LOGGER.trace("FontColorChooser is set");
				
		} catch (Exception ex) {
			Log.getLog().error("FontColorChooser initiation block: " + ex.toString());
			for (StackTraceElement stackLine : ex.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
	}

	/** Returns colorChooser, initiating one in neccessary
	 * @return JLabel FontColorChooser ready to be added on JPanel
	 */
	public static JLabel getFontChooser(){
		if (colorChooser == null) initiate();
		return colorChooser;
	}
}
