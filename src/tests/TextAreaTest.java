package tests;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.junit.Assert;

import src.NoteFrame;
import src.TabbedPane;
import src.TextArea;
import java.io.File;
import java.lang.reflect.Method;

public class TextAreaTest {

	private static File testFile = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator + "logFile.log");

	@BeforeClass
	public static void beforeClass(){
		NoteFrame.tabbedPane = new TabbedPane();
	}
	
	@AfterClass
	public static void afterClass(){
		testFile = null;
		testMutualFields.deleteEmptyDocs();
	}
	
	@Test
	public void testConstructor(){
		boolean checkCons = true;
		try {
			new TextArea(testFile);
		} catch (Exception e){
			checkCons = false;
		}
		Assert.assertTrue("Error calling constructor", checkCons);
	}

	@Test
	public void testFontChange(){
		boolean checkFont = true;
		try {
			Method m = TextArea.class.getDeclaredMethod("setItalic");
			m.setAccessible(true);
			m.invoke(new TextArea(testFile));
		} catch (Exception e){
			e.printStackTrace();
			checkFont = false;
		}
		Assert.assertTrue("Error changing font style", checkFont);
	}
}
