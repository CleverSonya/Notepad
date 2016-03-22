
package src;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Notepad logger.
 *  Output file - logFile.&nbsp;txt in the main app folder.
 *  Format - /src/log4j.xml
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */

public class Log {

  final static Logger LOGGER = LogManager.getLogger();

  /** Returns initiated Logger
   * @return Logger A ready to use Logger
   */
	public static Logger getLog() {	return LOGGER;}

}