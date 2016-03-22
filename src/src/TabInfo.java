
package src;
/** Contains the main info of a notepad tab to be sure in can be reopened
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class TabInfo {
	String tabName;
	String tabPath;
	boolean saveState = false;

	/**	Default constructor.
	*	@param name Short file name used as tab title
	*	@param path A path to file for it to be reopened
	*	@param state Shows whether there are unsaved changes
	 */
	TabInfo(String name, String path, boolean state){
		tabName = name; tabPath = path; saveState = state;
	}

	/**	Constructor called when opening a new tab (so there are no unsaved changes yet)
		*	@param name Short file name used as tab title
		*	@param path A path to file for it to be reopened
	 */
	TabInfo(String name, String path){
		tabName = name; tabPath = path;
	}

	/**	Returns a TabInfo with updated saveState
  *  @param tabInfo A TabInfo to be changed
	* @param newState Shows whether there are unsaved changes
	* @return TabIndo A changed TabInfo object
 */
	protected static TabInfo saveState(TabInfo tabInfo, boolean newState){
		return new TabInfo(tabInfo.tabName, tabInfo.tabPath, newState);
	}
}
