
package src;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/** A class with main info about font */
class FontInfo {
  Font font;
  String fontName;

  FontInfo(Font f) {
    font = f;
    fontName = f.getFontName();
  }

  @Override
  public String toString() { return fontName;  }

}
/** ComboBox with list of accessible font familys.
 *  TTF fonts are stored in /user_data/fonts so the list may be updated any time.
 * @author PopovaSS
 * @version 1.2
 * @since 1.0
 */
public class FontChooser {

  private static JComboBox<FontInfo> fontCombo;
  protected static DefaultComboBoxModel<FontInfo> fontData;
  private static LinkedList<FontInfo> fontList;
  // flag = true if we have to set selected item without changing text properties
  private static boolean flag = false;

  /**  Gets font out of fontList if there is a font with given name or returns the default font
    *  @param fontName (String) name of font to return
    *  @param currentFont font that is currently set to the selected text
    *  @return Font Either a font of given fontName or a currentFont if no font is available
   */
  private static Font getFontByName(String fontName, Font currentFont) {
    for (FontInfo fi : fontList) {
      if (fi.fontName.equals(fontName)) {
        return fi.font;
      }
    }
    Log.LOGGER.error("No font available by this name");
    return currentFont;
  }

  /** Reads /user_data/fonts to reach accessible fonts
   * @return boolean Whether or not font library readed successfully*/
  protected static boolean fillFontData() {
    // check if font dir exists
    String docPath = System.getProperty("user.dir") + File.separator + "user_data" + File.separator + "fonts";
    if (!new File(docPath).exists()) {
      Log.LOGGER.error("Font library is missing");
      Log.LOGGER.trace("Using local font without formatting");
      return false;
    }

    // fill fontList
    fontList = new LinkedList<>();
    try {
      Files.walk(Paths.get(docPath)).forEach(filePath -> {
        if (filePath.toFile().isFile())
          try {
            fontList.add(
              new FontInfo(
                Font.createFont(Font.TRUETYPE_FONT, filePath.toFile())
              ));
          } catch (IOException ioe) {
            Log.LOGGER.error("Font file is unreadable: " + filePath);
            filePath.toFile().delete();
            Log.LOGGER.trace("Unreadable font deleted successfully");
          } catch (FontFormatException ffe) {
            Log.LOGGER.error("Not ttf file in font directory: " + filePath);
            filePath.toFile().delete();
            Log.LOGGER.trace("Not ttf file in font directory deleted successfully");
          }
      });
    } catch (IOException ie) {
      Log.LOGGER.error("IOException during reading font files directory");
      for (StackTraceElement stackLine : ie.getStackTrace())
        Log.LOGGER.error(stackLine);
      if (fontList.isEmpty()) {
        Log.LOGGER.trace("Using local font without formatting");
        return false;
      }
    }

    // fill fontData
    fontData = new DefaultComboBoxModel<>();
    for (FontInfo f : fontList)
      fontData.addElement(f);

    Log.LOGGER.trace("Font data readed successfully");
    return true;

  }

  /** Initiates FontChooser */
  private static void initiate() {
    try {
      // first of all - read font from user_data/fonts
      // if unsuccessful - use local fonts instead
      if (!fillFontData()) return;

      fontCombo = new JComboBox<>(fontData);

      fontCombo.addActionListener(actionEvent -> {
        if (!flag)
          try {
            NoteFrame.tabbedPane.getSelectedComponent().updateFont(getFontByName(
              fontCombo.getSelectedItem().toString(),
              fontData.getElementAt(10).font));
          } catch (NullPointerException pe) {
            // do nothing: this happens during initialization
          } catch (Exception ex) {
            Log.getLog().error("FontChooser initiation block: " + ex.toString());
            for (StackTraceElement stackLine : ex.getStackTrace())
              Log.LOGGER.error(stackLine);
          }
        else {
          // do not change text in this case
          flag = false;
        }
      });

      fontCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

      fontCombo.setMaximumSize(new Dimension(175, 25));

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

      // read current Font
      FontChooser.updateFont();

      Log.LOGGER.trace("FontChooser is set");

    } catch (Exception ex) {
      Log.getLog().error("FontChooser initiation block: " + ex.toString());
      for (StackTraceElement stackLine : ex.getStackTrace())
        Log.LOGGER.error(stackLine);
    }

  }

  /** Returns intiated and setted JComboBox
   * @return JComboBox A ComboBox ready to be added on JPanel
   */
  public static JComboBox<FontInfo> getFontChooser() {
    if (fontCombo == null) initiate();
    // it may still return null - if no fonts are available
    return fontCombo;
  }

  /** Update JComboBox to show font that is currently set on selected text */
  public static void updateFont() {
    if (fontCombo == null) initiate();
    try {
      Font currFont = NoteFrame.tabbedPane.getSelectedComponent().currentFont;
      for (int i = 0; i < fontData.getSize(); i++) {
        FontInfo fi = fontData.getElementAt(i);
        if (fi.font == currFont) {
          fontCombo.setSelectedItem(fontData.getElementAt(i));
          break;
        }
      }
    } catch (NullPointerException npe) {
      // that means no font has been chosen yet
      fontCombo.setSelectedItem(fontData.getElementAt(10));
    }
    Log.LOGGER.trace("Font updated");
  }

  /** Set selected font by given font name as String
   *  @param font (String) name of font to set
   */
  protected static void setSelected(String font) {
    boolean chosen = false;
    flag = true;
    for (int i = 0; i < fontData.getSize(); i++)
      if (fontData.getElementAt(i).font.getFamily().equals(font)) {
        fontCombo.setSelectedItem(fontData.getElementAt(i));
        chosen = true;
        break;
      }
    if (!chosen) {
      fontCombo.setSelectedItem(fontData.getElementAt(10));
    }
  }
}
