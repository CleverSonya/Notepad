
package src;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
/** ComboBox with font size to set
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class FontSizeChooser {
	private static JComboBox<Integer> fontCombo;
	protected static DefaultComboBoxModel<Integer> sizeData;
	// flag = true if we have to set selected item without changing text properties
	private static boolean flag = false;

	/** Initiates FontSizeChooser on first call */
	private static void initiate(){
		Integer[] fontList;
		try {
			//fill sizeData
			fontList = new Integer[]{8,10,11,12,14,16,18,20,22,24,26,32,48,72};
			sizeData = new DefaultComboBoxModel<>();
      for (Integer aFontList : fontList) sizeData.addElement(aFontList);

			fontCombo = new JComboBox<>(sizeData);
				
			fontCombo.addActionListener(actionEvent -> {
					if (!flag)
						try {
              NoteFrame.tabbedPane.getSelectedComponent()
                .setSize((Integer)fontCombo.getSelectedItem());
						} catch (NullPointerException pe){
							Log.LOGGER.debug("NPE in FontSizeChooser block during initialization");
						} catch (Exception ex) {
							Log.getLog().error("FontSizeChooser action block: " + ex.toString());
							for (StackTraceElement stackLine : ex.getStackTrace())
								Log.LOGGER.error(stackLine);
						}
					else {
						// do not change text in this case
						flag = false;
					}
			});
			
			fontCombo.setSelectedItem(sizeData.getElementAt(4));
			fontCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
			fontCombo.setMaximumSize(new Dimension(50, 25));
			fontCombo.setBackground(Color.WHITE);
			fontCombo.setRenderer(new DefaultListCellRenderer() {
				final static long serialVersionUID = 25122015L;
			    @Override
			    public void paint(Graphics g) {
			        setBackground(Color.WHITE);
			        setForeground(Color.BLACK);
			        super.paint(g);
			    }
			});
		
			Log.LOGGER.trace("FontSizeChooser is set");
		} catch (Exception ex) {
			Log.getLog().error("FontSizeChooser initiation block: " + ex.toString());
			for (StackTraceElement stackLine : ex.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
	}

	/** Returns a FontSizeChooser, initiating one if neccessary
	 * @return JComboBox A FontSizeChooser ready to be added on JPanel
	 */
	public static JComboBox<Integer> getFontChooser(){
		if (fontCombo == null) initiate();
		return fontCombo;
	}

	/**	Sets selected element of ComboBox to the given value
	*		@param size Size to set
	*/
	protected static void setSelected(int size){
		boolean chosen = false;
		flag = true;
		for (int i = 0; i < sizeData.getSize(); i++)
			if (sizeData.getElementAt(i) == size) {
				fontCombo.setSelectedItem(sizeData.getElementAt(i));
				chosen = true;
				break;
			}
		if (!chosen) fontCombo.setSelectedItem(sizeData.getElementAt(4));
	}
}
