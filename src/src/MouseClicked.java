
package src;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
/** Interface extending MouseListener with four default empty methods to use it as ActionListener
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public interface MouseClicked extends MouseListener{
  default void mousePressed(MouseEvent e){}
  default void mouseReleased(MouseEvent e){}
  default void mouseExited(MouseEvent e){}
  default void mouseEntered(MouseEvent e){}
}
