package tests;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import src.TabbedPane;

public class TabbedPaneTest {

	@AfterClass
	public static void afterClass(){
		testMutualFields.deleteEmptyDocs();
	}

	@Test
	public void testAddNewTab(){
		/* bounds to check: new TabbedPane() including
		 * new TextArea()
		 * new ClosePanel()
		 * UndoRedo.refreshControls
		 */
		boolean checkNewTab = true;
		try {
			new TabbedPane();
		} catch (Exception e){
			e.printStackTrace();
			checkNewTab = false;
		}
		Assert.assertTrue("TabbedPane.addNewTab() error", checkNewTab);
	}
}
