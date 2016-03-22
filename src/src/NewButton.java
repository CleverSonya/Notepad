
package src;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
/** Button to create a new empty document.
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class NewButton extends NoteButton{

	public NewButton(){
		super("New document", "NewSmall.gif", "NewSmall.gif",
			"New Text Document");
	}

	/** generating a name of document that is available to create
	 * @return String generated document name */
	protected static String getDocName(){
		int num = IntStream.range(1,Integer.MAX_VALUE)
			.filter(i -> !new File(
					System.getProperty("user.dir") + File.separator + "docs" + File.separator + "New Document" + i + ".txt")
				.exists())
			.findAny()
			.getAsInt();
		Log.getLog().trace("Prepare to open new document: name generated");
		return "New Document" + num;
	}

	/** Creates new empty document */
	protected static void performNewAction(){
		try {
			String fileName = getDocName();

			// create new document
			String filePath = createNewDoc(fileName);
			
			// adding new tab to tabbedPane
			NoteFrame.tabbedPane.addNewTab(fileName, filePath);

			NoteFrame.tabbedPane.setSelectedIndex(
				NoteFrame.tabbedPane.indexOfTab(fileName)
			);

			NoteFrame.mainFrame.repaint();
			Log.getLog().trace("New document opened");
			
		} catch (Exception ex) {
			Log.getLog().error("NewButton ActionListener: " + ex.toString());
			for (StackTraceElement stackLine : ex.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
	}

	/** creates new File by given fileName
	* @param fileName String name of file (format not included) to create
	* @return String filePath */
	protected static String createNewDoc(String fileName){
		String filePath = System.getProperty("user.dir") + File.separator + "docs" + File.separator + fileName + ".txt";
		try {
			boolean created = new File(filePath).createNewFile();
			if (created)
				Log.getLog().trace("Prepare to open new document: file created");
			else
				Log.getLog().error("Error creating new file!");
		} catch (IOException e) {
			Log.getLog().error("Error creating new file!");
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
		return filePath;
	}
	
}
