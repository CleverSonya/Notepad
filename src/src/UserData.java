
package src;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.util.stream.IntStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/** A manager for reading and saving user info about opened tabs after starting ang exiting a Notepad
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class UserData {
	
	protected static String dirPath = System.getProperty("user.dir") + File.separator + "user_data";
	protected static String filePath = dirPath + File.separator + "preferences.xml";
	protected static String encoding = System.getProperty("file.encoding");
	
	/** creates new /user_data/preferences.xml */
	protected static void create(){	
		try {
			File directory = new File(dirPath);
			if (!directory.exists())
				if (!directory.mkdir())
					throw new IOException("Unable to create user_data dir");
			File dataFile = new File(dirPath);
			
			// delete previously saved data
			if (dataFile.exists())
				dataFile.delete();
			dataFile.createNewFile();
		} catch (IOException ioe){
			Log.LOGGER.error("Cannot save openedTabs info: IOException : " + ioe.getMessage());
			for (StackTraceElement stackLine : ioe.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
		Log.LOGGER.trace("New file preferences.xml is created");
	}

	/** save info about currently opened tabs */
	protected static void save(){
		try {
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = df.newDocumentBuilder();
			Document doc = db.newDocument();
			
			Element root = doc.createElement("root");
			Element openedTabs = doc.createElement("openedTabs");
			root.appendChild(openedTabs);
			
			for (TabInfo entry : TabbedPane.openedTabs){
				Element tab = doc.createElement("tab");
				tab.setTextContent("tab");
				openedTabs.appendChild(tab);
				
				Element tabName= doc.createElement("tabName");
				tabName.setTextContent(entry.tabName);
				tab.appendChild(tabName);
				
				Element tabPath = doc.createElement("tabPath");
				tabPath.setTextContent(entry.tabPath);
				tab.appendChild(tabPath);
				
				Log.LOGGER.trace("Saving opened tab information: " + entry.tabName);
			}
			
			FileOutputStream fo = new FileOutputStream(filePath);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, encoding);
				
			t.transform(new DOMSource(root), new StreamResult(fo));	
				
			fo.close();
			Log.LOGGER.trace("All the opened tabs info saved to preferences.xml");
		} catch (Exception e){
			Log.LOGGER.error("Exception while writing preferences.xml: no information about previously opened tabs would be saved");
			Log.LOGGER.error(e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
		}
	}

	/** read /user_data/preferences.xml when launching a Notepad */
	protected static void read(){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			File f = new File(filePath);
			if (f.exists() && f.length() != 0) {
				NodeList nList = db.parse(f).getElementsByTagName("tab");
				IntStream.range(0,nList.getLength())
					.forEach(i -> {
						NodeList childList = nList.item(i).getChildNodes();
						if (new File(childList.item(2).getTextContent()).exists()) {
							TabbedPane.openedTabs.add(
								TabbedPane.openedTabs.size(),
								new TabInfo(childList.item(1).getTextContent(), childList.item(2).getTextContent()));
						} else {
							Log.LOGGER.trace("Previously saved file is missing, opening other files or a new doc");
						}
					});
				// if there are no doc to open - try default
				if (TabbedPane.openedTabs.size() == 0) {
					String defaultPath = System.getProperty("user.dir") + File.separator + "docs" + File.separator + "Welcome.txt";
					if (!new File(filePath).exists()) {
						Log.LOGGER.trace("No default Welcome.txt file to open");
						return;
					}
					TabbedPane.openedTabs.add(
						TabbedPane.openedTabs.size(),
						new TabInfo("Welcome", defaultPath));
					Log.LOGGER.trace("Opening default file: Welcome.txt");
				} else {
					Log.LOGGER.trace("No previously saved tabs to open");
				}
			}
			Log.LOGGER.trace("OpenedTabs information readed succesfully");
			
		} catch (IOException e){
			Log.LOGGER.error("Exception while reading preferences.xml: file not found");
			Log.LOGGER.error(e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
			Log.LOGGER.trace("Opening a new empty document");
		} catch (NullPointerException e){
			Log.LOGGER.error("Exception while reading preferences.xml: wrong format");
			Log.LOGGER.error(e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
			Log.LOGGER.trace("Opening a new empty document");
		} catch (Exception e){
			Log.LOGGER.error("Exception while reading preferences.xml: " + e.toString());
			for (StackTraceElement stackLine : e.getStackTrace())
				Log.LOGGER.error(stackLine);
			Log.LOGGER.trace("Opening a new empty document");
		}
	}

}
