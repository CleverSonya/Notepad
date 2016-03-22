package tests;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

public class testMutualFields {
	
	protected final static Semaphore sem = new Semaphore(1);
	
	protected static void deleteEmptyDocs(){
		String dirPath = System.getProperty("user.dir") + File.separator + "docs";
		try {
			Files.walk(Paths.get(dirPath)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath))
			    	try {
				    	if (Files.size(filePath) == 0)
				    		Files.delete(filePath);
			    	} catch (IOException e){
			    		//
					}
			});
		} catch (IOException e){
			//
		}
	}
}
