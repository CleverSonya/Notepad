
package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;

/** A button to be shown in SaveQuestionFrame
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
class SaveQuestionButton extends JButton{
	/**	Default and only constructor for Yes/No/Cancel buttons
	*		@param text Yes/No/Cancel text
	*		@param actionListener An action to be performed
	 */
	SaveQuestionButton(
		String text, ActionListener actionListener
	){
		super();
		setText(text);
		setName(text + "SaveQuestionButton");
		setVisible(true);
		setOpaque(false);
		addActionListener(actionListener);
	}
}

/** A frame is shown when closing an unsaved document
 *  @author PopovaSS
 *  @version 1.2
 *  @since 1.0
 */
public class SaveQuestionFrame extends JFrame{

	protected Thread closeOnDispose;
	protected boolean cancelButtonPressed = false;

	/**	Default and only constructor. Creates a new frame any time the frame is called
		*	@param onDispose True when not the only tab, but the main Notepad frame is disposing. Prevents thread locks.
	*/
	SaveQuestionFrame(boolean onDispose){
		super("My Notepad - Save");

		if (onDispose){
			closeOnDispose = new Thread(() -> {
				//setVisible(true);
				synchronized(closeOnDispose){
					try {
						closeOnDispose.wait();
					} catch (InterruptedException ie){
						// that's ok
						Log.LOGGER.debug("SaveQuestionFrame.closeOnDispose has been interrupted");
					}
				}
			});
			closeOnDispose.setName("closeOnDisposeThread");
			closeOnDispose.start();
		}

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(300, 120));
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(7, 7, 0, 7);
		c.fill = GridBagConstraints.CENTER;

		JLabel textLabel = new JLabel("Save changes?");
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		c.gridwidth = 3;
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(textLabel, c);

		c.gridwidth = 1;
		c.ipady = 10;
		c.ipadx = 40;
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(new SaveQuestionButton(
			"Yes", e -> {
			SaveAction.performSaveAction();
			setVisible(false);
			CloseAction.performCloseAction();
			if (onDispose) closeOnDispose.interrupt();
		}), c);

		c.gridx = 1;
		c.gridy = 1;
		mainPanel.add(new SaveQuestionButton(
			"No", e -> {
			setVisible(false);
			CloseAction.performCloseAction();
			if (onDispose) closeOnDispose.interrupt();
		}), c);

		c.gridx = 2;
		c.gridy = 1;
		mainPanel.add(new SaveQuestionButton(
			"Cancel", e -> {
			setVisible(false);
			cancelButtonPressed = true;
			if (onDispose) closeOnDispose.interrupt();
		}), c);

		JFrame.setDefaultLookAndFeelDecorated(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().add(mainPanel);
		setMinimumSize(new Dimension(350, 80));
		pack();
		setVisible(true);

		Log.LOGGER.trace("SaveQuestionFrame is initiated");
	}

}
