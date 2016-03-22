
package src;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JTabbedPane;
/** TabbedPane for a Notepad.
 *  Each tab contains a TextArea with document text.
 * @author PopovaSS
 * @version 1.2
 * @since 1.0
 */
public class TabbedPane extends JTabbedPane{

	static final long serialVersionUID = 42L;

	protected static LinkedList<TabInfo> openedTabs = new LinkedList<>();

	public TabbedPane(){
		super();
		try {
			setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			if (openedTabs.isEmpty()) {
				// if docs dir doesn't exists - create
				String filePath = System.getProperty("user.dir") + File.separator + "docs";
				if (!new File(filePath).exists()) {
					if (!new File(filePath).mkdir())
						throw new IOException("Unable to make docs dir");
				}

				// generating new document name
				String fileName = NewButton.getDocName();
				filePath = filePath + File.separator + fileName + ".txt";

				addNewTab(fileName, filePath);

				Log.LOGGER.trace("New empty doc is prepared for tabbedPane");
			} else {
				for (TabInfo entry : openedTabs) {
					// 1 - add a tab with null component to prevent IndexOutOfBoundsException during setting textArea
					addTab(entry.tabName, null);
					// 2 - set tab component and tab closePanel
					setComponentAt(getTabCount() - 1, new TextArea(new File(entry.tabPath)));
					setTabComponentAt(getTabCount() - 1, new ClosePanel(entry.tabName));
				}
				Log.LOGGER.trace("Previously opened tabs are opened");
			}
			Log.LOGGER.trace("JTabbedPane initiated");
		} catch (Exception e){
			Log.LOGGER.fatal("Errors initiating tabbedPane: " + e.getMessage());
			for (StackTraceElement stackLine : e.getStackTrace())
			Log.LOGGER.error(stackLine);
			NoteFrame.exit();
		}
	}

	/**	Extends JTabbedPane.addTab method to save new tab information
		*	@param fileName Short file name to be shown on tab header
	*		@param filePath Path to the file that has to be shown
	 */
	public void addNewTab(String fileName, String filePath){
		// 1 - add a tab with null component to prevent IndexOutOfBoundsException during setting textArea
		addTab(fileName, null);
		// 2 - place the info about new tab to openedTabs ans existingFiles
		openedTabs.add(indexOfTab(fileName), new TabInfo(fileName, filePath));
		// 3 - set tab component and tab closePanel
		setComponentAt(getTabCount() - 1, new TextArea(new File(filePath)));
		setTabComponentAt(getTabCount() - 1, new ClosePanel(fileName));
	}

	/** 	Overrides JTabbedPane.setSelectedIndex to update save buttons enabled status
		*	@param index Index of tab to be selected
	 */
	@Override
	public void setSelectedIndex(int index){
		super.setSelectedIndex(index);
		// update save state
		if ((openedTabs.size() > index)&&(index >= 0)) {
			// if this is not called during adding a new tab
			Log.LOGGER.trace("ActiveTab: " + openedTabs.get(index).tabName);
			setSaveState(openedTabs.get(index).saveState);
		}
	}

	/**	Updates tab title after the file saved with another name
		*	@param newTitle new file name to display
	 */
	protected void updateSelectedTabComponent(String newTitle){
		ClosePanel newPanel = (ClosePanel)getTabComponentAt(getSelectedIndex());
		newPanel = newPanel.getUpdatedPanel(newTitle);
		setTabComponentAt(getSelectedIndex(), newPanel);
	}

	/** Extends JTabbedPane.remove with updating openedTabs and buttons state */
	protected void removeSelectedTab(){
		openedTabs.remove(getSelectedIndex());
		remove(getSelectedIndex());
		// update save button
		NoteFrame.saveButton
			.setEnabled(
				openedTabs.get(getSelectedIndex()
				).saveState
			);
	}

	/** 	Overrides JTabbedPane.getSelectedComponent to return a TextArea instead of Component
			@return TextArea selected area
	 */
	@Override
	public TextArea getSelectedComponent(){
		return (TextArea)super.getSelectedComponent();
	}

	/**	Updates buttons enabled state after new tab is selected
			@param newState A boolean of whether there are changes or not
	 */
	protected void setSaveState(boolean newState){
		int tabIn = getSelectedIndex();
		boolean oldState = openedTabs.get(tabIn).saveState;
		
		// if state changed - update TabInfo
		if (oldState != newState)
			openedTabs.set(tabIn, TabInfo.saveState(openedTabs.get(tabIn), newState));
		
		// update save button
		NoteFrame.saveButton.setEnabled(newState);
		FileMenu.activateSaveOption(newState);
	}
}
