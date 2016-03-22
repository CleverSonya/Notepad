package tests;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.junit.Assert;
import javax.swing.JLabel;
import src.ClosePanel;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import src.TabbedPane;

public class ClosePanelTest {

	private static JLabel testButton;
	private static File logFile = new File(System.getProperty("user.dir") + File.separator + "logFile.log");
	private static ArrayList<String> logData = new ArrayList<> ();
	@SuppressWarnings("unused")
	private static TabbedPane tabbedPane;
	
	@BeforeClass
	public static void beforeClass(){
		// acquiring semaphore to prevent tread races with other tests
		try {
			testMutualFields.sem.acquire();
		} catch (InterruptedException e){
			System.out.println("InterruptedException while acquiring semaphore");
		}

		// creating test objects
		testButton = new ClosePanel("testClosePanel").close;
		tabbedPane = new TabbedPane();
		
		// performing mouse enter and click
		MouseEvent eMouseEntered= new MouseEvent(testButton, MouseEvent.MOUSE_ENTERED, 0, 0, 10, 10, 1, false);
		testButton.dispatchEvent(eMouseEntered);

		// gathering log data
		try {
			BufferedReader in = new BufferedReader(new FileReader(logFile));
			String line = in.readLine();
			while (line != null) {
				logData.add(line); 
				line = in.readLine();
			}
			in.close();
		} catch (IOException e){
			System.out.println("IO Exception while gathering logData, tests cannot be performed");
		}
		
		// don't forget to release semaphore
		testMutualFields.sem.release();
	}
	
	@AfterClass
	public static void afterClass(){
		testButton = null;
		logData = null;
		tabbedPane = null;
		testMutualFields.deleteEmptyDocs();
	}
	
	@Test
	public void testImageSet(){
		/* bounds to check:
		 * inactive image file
		 */
		Assert.assertNotNull("Closing icon is null", testButton.getIcon());
	}

	@Test
	public void testMouseEnteredIconChanged(){
		/* bounds to check:
		 * active image file
		 */
		boolean checkIconChanged = false;
		for (String line : logData)
			if (line.contains("mouse entered")){
				checkIconChanged = true; break;
			}
		Assert.assertTrue("Close image doesn't change after mouse entered or log is incorrect", checkIconChanged);
	}
}
