
package src;

import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.awt.GridBagLayout;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JPanel;
import javax.swing.undo.UndoableEdit;
import java.awt.GridBagConstraints;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.Font;
import javax.swing.text.AttributeSet;
import javax.swing.event.CaretEvent;
import javax.swing.text.DefaultCaret;
import javax.swing.text.BadLocationException;
/** An Enum used in TextArea.setFont() */
enum FontOptions{ BOLD, ITALIC, UNDERLINE, FONT, SIZE, COLOR}
/** A text element shown in every notepab tab.
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class TextArea extends JPanel{

	static final long serialVersionUID = 42L;
	protected UndoRedo editorPane;
	private JScrollPane scrollPane;
	private DefaultStyledDocument doc = new DefaultStyledDocument();
	private File sourceFile;
	protected Font currentFont;
	// selected text pointers
	private int start, end;
	private int size;
	private Color color;

	/**	Set selected text attributes
		*	@param fontOptions an Enum show a change that needs to be done
		*	@return boolean Returns true or the result of StyleConstans.isBold/isItalic/isUnderlined
	 */
	private boolean setFont(FontOptions fontOptions){
		// getting selected text borders
		if (editorPane.getSelectedText() != null) {
			// if there is some text chosen
			start = editorPane.getSelectionStart();
			end = editorPane.getSelectionEnd() - start;
		} else {  // else - update all text
			start = 0;
			end = doc.getLength() - 1;
		}

		// get attributes of the first symbol of selected fragment
		SimpleAttributeSet set = new SimpleAttributeSet(
			doc.getCharacterElement(start).getAttributes());

		boolean state = true;

		switch (fontOptions){
			case BOLD: {
				state = !StyleConstants.isBold(set);
				StyleConstants.setBold(set, state);
				break;
			}
			case ITALIC: {
				state = !StyleConstants.isItalic(set);
				StyleConstants.setItalic(set, state);
				break;
			}
			case UNDERLINE: {
				state = !StyleConstants.isUnderline(set);
				StyleConstants.setUnderline(set, state);
				break;
			}
			case FONT: {
				StyleConstants.setFontFamily(
					set, currentFont.getFamily());
				break;
			}
			case SIZE:{
				StyleConstants.setFontSize(
					set, size);
				break;
			}
			case COLOR: {
				StyleConstants.setForeground(
					set, color);
				break;
			}
		}

		doc.setCharacterAttributes(start, end, set, true);

		scrollPane.repaint();
		if (fontOptions.equals(FontOptions.FONT))
			Log.LOGGER.trace("Font state : " + currentFont);
		else
			Log.LOGGER.trace("Font state : " + fontOptions + " : " + state);
		return state;

	}

	/**	Default and only constructor with a file to be shown
		*	@param file A file, which contents has to be shown
	 */
	public TextArea(File file){
		// reading source
		sourceFile = file;
		readSource();

		// setting default attributes
		SimpleAttributeSet attr = new SimpleAttributeSet();
		// prevent NPE
		if (FontChooser.fontData == null) FontChooser.fillFontData();
		currentFont = ((FontInfo)FontChooser.getFontChooser().getSelectedItem()).font;
		StyleConstants.setFontFamily(attr, currentFont.getFamily());
		StyleConstants.setFontSize(attr,
			(Integer)FontSizeChooser.getFontChooser().getSelectedItem());
		doc.setCharacterAttributes(0, doc.getLength() - 1, attr, true);

		// creating editorPane
		editorPane = new UndoRedo();
		
		// setting text wrap to none
		try {
			editorPane.setEditorKit(new WrapEditorKit());
			Log.LOGGER.trace("WrapEditorKit is set properly");
		} catch (Exception e){
			Log.LOGGER.error("Cannot set WrapEditorKit: " + e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
		
		// showing new document properly
		try {
			addDocumentListener();
			addCaretListener();
			editorPane.setDocument(doc);
			editorPane.setEditable(true);
			
			// setting Margin with non-default borders
			Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
			Border empty = new EmptyBorder(7,7,7,7);
			CompoundBorder border = new CompoundBorder(line, empty);
			editorPane.setBorder(border);
			
			scrollPane = new JScrollPane(editorPane);
			scrollPane.setPreferredSize(new Dimension(500, 500));
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.PAGE_START;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;
			this.add(scrollPane, c);
			Log.LOGGER.trace("New document is shown properly in text area");
		} catch (Exception e){
			Log.LOGGER.error("Error showing new document in text area: " + e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
	}
	
	//** reading source file */
	private void readSource(){
		// read characters
		try {
			if (!sourceFile.exists())
				sourceFile.createNewFile();
			else {
				BufferedReader in = new BufferedReader(new FileReader(sourceFile));
				String line = in.readLine();
				while(line != null) {
					doc.insertString(doc.getLength(), line + "\n", SimpleAttributeSet.EMPTY);
					line = in.readLine();
				}
				in.close();
			}

			Log.LOGGER.trace("New document source is readed");
		} catch (Exception e){
			Log.LOGGER.error("Cannot read new document source: " + e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
	}
	
	private void addCaretListener(){
		editorPane.setCaret(new DefaultCaret(){
			static final long serialVersionUID = 42L;
			@Override
			   public void setSelectionVisible(boolean visible) {
			      super.setSelectionVisible(true);
			   }
		});
		
		editorPane.addCaretListener((CaretEvent e) -> {
				if (e.getDot() != e.getMark()){
					// = if there is some selected text
					if (e.getDot() < e.getMark()){
						start = e.getDot();
						end = e.getMark() - start;
					} else {
						start = e.getMark();
						end = e.getDot() - start;
					}
				} else {
					start = 0; end = doc.getLength() - 1;
				}
				AttributeSet set = doc.getCharacterElement(start).getAttributes();
				NoteFrame.boldButton.setEnabled(StyleConstants.isBold(set));
				NoteFrame.italicButton.setEnabled(StyleConstants.isItalic(set));
				NoteFrame.underlineButton.setEnabled(StyleConstants.isUnderline(set));
				FontSizeChooser.setSelected(StyleConstants.getFontSize(set));
				FontChooser.setSelected(StyleConstants.getFontFamily(set));
		});
	}

	/**	Updates documentListener
		*	@param e DocumentEven that just happened
	 */
	private void tryUndo(DocumentEvent e){
		try {
			editorPane.undoManager.undoableEditHappened(
				new UndoableEditEvent(
					editorPane, (UndoableEdit)e));
		} catch (ClassCastException cce){
			Log.LOGGER.debug("ClassCastException in DocumentListener : non-undoable event");
		}
		NoteFrame.tabbedPane.setSaveState(true);
	}

	private void addDocumentListener(){
		doc.addDocumentListener(new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent e){
				tryUndo(e);
			}
			@Override
			public void insertUpdate(DocumentEvent e){
				tryUndo(e);
			}
			@Override
			public void removeUpdate(DocumentEvent e){
				tryUndo(e);
			}
		});
	}

	/** Returns currently displayed document
	* @return DefaultStyledDocument currently displayed document */
	protected DefaultStyledDocument getDoc(){
		return doc;
	}

	/** Returns a source file path
	* @return String source file path */
	protected String getDocPath(){
		return sourceFile.getPath();
	}

	/** Updates selected text to a new font family
	* @param f Font to be set */
	protected void updateFont(Font f){
		currentFont = f;
		setFont(FontOptions.FONT);
	}

	/** Updates selected text to a new size
	 @param newSize size to be set */
	protected void setSize(Integer newSize){
		size = newSize;
		setFont(FontOptions.SIZE);
	}

	/**  Changes selected text bold status
	 * @return boolean whether seleted text is bold now*/
	protected boolean setBold(){
		return setFont(FontOptions.BOLD);
	}

	/** Changes selected text italic status
	 * @return boolean whether selected text is italic now*/
	protected boolean setItalic(){
		return setFont(FontOptions.ITALIC);
	}

	/** Changes selected text underline status
	 * @return boolean whether selected text is underlined now*/
	protected boolean setUnderline(){
		return setFont(FontOptions.UNDERLINE);
	}

	/** Updates selected text to a new color
	* @param newColor color to be set */
	protected void setColor(Color newColor){
		color = newColor;
		setFont(FontOptions.COLOR);
	}

	/** Returns selected piece of text
	* @return selected piece of text */
	protected String getSelectedText(){
		return editorPane.getSelectedText();
	}

	/** Attempts to perform past action
	* @param text A text to be pasted */
	protected void insertText(String text){
		int caretPosition = editorPane.getCaretPosition();
		SimpleAttributeSet set = new SimpleAttributeSet(doc.getCharacterElement(caretPosition).getAttributes());
		try {
			doc.insertString(caretPosition, text, set);
		} catch (BadLocationException bad){
			Log.LOGGER.error("Cannot insert String : " + bad.getMessage());
		}
		scrollPane.repaint();
	}

	/** remove selected piece of text */
	protected void removeSelected(){
		try {
			doc.remove(editorPane.getSelectionStart(), editorPane.getSelectionEnd() - editorPane.getSelectionStart());
		} catch (BadLocationException bad){
			Log.LOGGER.error("Cannot remove String : " + bad.getMessage());
		}
	}

}
