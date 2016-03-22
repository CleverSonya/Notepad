package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import src.UserData;

public class UserDataTest {
	
	private static File logFile = new File(System.getProperty("user.dir") + File.separator + "logFile.log");
	private static ArrayList<String> logData = new ArrayList<> ();
	
	@BeforeClass
	public static void beforeClass(){
		
		// acquiring semaphore to prevent tread races with other tests
		try {
			testMutualFields.sem.acquire();
		} catch (InterruptedException e){
			System.out.println("InterruptedException while acquiring semaphore");
		}
		
		// accessing protected method read()
		try {
			Method m = UserData.class.getDeclaredMethod("read");
			m.setAccessible(true);
			m.invoke(new UserData());
		}  catch (Exception e){
			System.out.println(e.toString() + " while accessing methods, test cannot be performed");
		}
	
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
		for (Method m : UserData.class.getDeclaredMethods())
			m.setAccessible(true);
		testMutualFields.deleteEmptyDocs();
	}
	
	@Test
	public void testLogWriting(){
		/* bounds to check:
		 * Log.getLog()
		 */
		Assert.assertFalse("LogFile doesn't exists or incorrect", logData.isEmpty());
	}
	
	@Test
	public void testRead(){
		/* bounds to check:
		 * NoteFrame.openedTabs
		 */
		boolean checkRead = false;
		for (String line : logData)
			if (line.contains("edTabs information rea")){
				checkRead = true; break;
			}
		Assert.assertTrue("Cannot read preferences.xml", checkRead);
	}
}
