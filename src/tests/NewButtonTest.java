package tests;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.Assert;

import src.NewButton;

import java.io.File;
import java.lang.reflect.Method;

public class NewButtonTest {

	@AfterClass
	public static void afterClass(){
		testMutualFields.deleteEmptyDocs();
	}

	@Test
	public void testGetDocName(){
		// bound to check: NoteFrame.getDocName()
		boolean checkGetDocName = true;
		String result = "";
		try {
			Method m = NewButton.class.getDeclaredMethod("getDocName");
			m.setAccessible(true);
			result = (String) m.invoke(new NewButton());
		} catch (Exception e){
			checkGetDocName = false;
		}
		System.out.println(result);
		if (checkGetDocName)
			checkGetDocName = !new File(System.getProperty("user.dir") + File.separator + "docs" + File.separator + result + ".txt")
				.exists();
		Assert.assertTrue("New doc name generated incorrectly", checkGetDocName);
	}
	
	@Test
	public void testDocCreated(){
		// bound to check: NewButton.createNewDoc()
		boolean checkDocCreated = true;
		String result = "";
		try {
			Method m = NewButton.class.getDeclaredMethod("createNewDoc", String.class);
			m.setAccessible(true);
			result = (String) m.invoke(new NewButton(), "NewButton12357");
		} catch (Exception e){
			checkDocCreated = false;
		}
		if (checkDocCreated)
			checkDocCreated = new File(result).exists();
		Assert.assertTrue("New Document was not created!", checkDocCreated);
	}
}
