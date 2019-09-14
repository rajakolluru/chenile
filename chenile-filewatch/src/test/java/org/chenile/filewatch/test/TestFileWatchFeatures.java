package org.chenile.filewatch.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.chenile.filewatch.handler.FileProcessor;
import org.chenile.filewatch.test.service.FooModel;
import org.chenile.filewatch.test.service.FooService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestChenileFileWatch.class)
@AutoConfigureMockMvc
@ActiveProfiles("unittest")
public class TestFileWatchFeatures {

    @Autowired FooService fooService;
    @Autowired FileSystem fileSystem;
    public static CountDownLatch latch = new CountDownLatch(3);
    public static List<FooModel> actualList = new ArrayList<>();
    public static List<FooModel> expectedList = new ArrayList<>();
    private Path csvtest;
    private Path csvtestactual;
    private Path jsontest;
    private Path jsontestactual;
    private Path processedCsvtest;
    private Path processedCsvtestactual;
    private Path processedJsontest;
    private Path processedJsontestactual;
    
    private ObjectMapper om;
    
    @Before public void setUp() throws IOException {
    	om = new ObjectMapper();
    	csvtest = fileSystem.getPath(TestChenileFileWatch.SRC_DIR + "/csvtest.header");
    	jsontest = fileSystem.getPath(TestChenileFileWatch.SRC_DIR + "/jsontest.header");
    	csvtestactual = fileSystem.getPath(TestChenileFileWatch.SRC_DIR + "/csvtest.csv");
    	jsontestactual = fileSystem.getPath(TestChenileFileWatch.SRC_DIR + "/jsontest.json");
    	processedCsvtest = fileSystem.getPath(TestChenileFileWatch.DEST_DIR + "/csvtest.header");
    	processedJsontest = fileSystem.getPath(TestChenileFileWatch.DEST_DIR + "/jsontest.header");
    	processedCsvtestactual = fileSystem.getPath(TestChenileFileWatch.DEST_DIR + "/csvtest.csv");
    	processedJsontestactual = fileSystem.getPath(TestChenileFileWatch.DEST_DIR + "/jsontest.json");
    }
    
    private void writeFiles() throws Exception {
    	// create two header files - csvtest.header and jsontest.header
    	
    	List<String> csvHeaderFileContents = headers("csvtest.csv","csv");
    	List<String> jsonHeaderFileContents = headers("jsontest.json","json");
    	
    	Files.write(csvtest, csvHeaderFileContents, StandardCharsets.UTF_8);
    	Files.write(jsontest, jsonHeaderFileContents, StandardCharsets.UTF_8);
    	Files.write(csvtestactual, csvFileContents(),StandardCharsets.UTF_8 );

    	Files.write(jsontestactual, jsonFileContents().getBytes());
    }
    
    private List<String> headers(String filename,String encoding){
    	List<String> list = new ArrayList<>();
    	list.add("x-tenant=tenant1");
    	list.add("x-user=user1");
    	list.add(FileProcessor.ACTUAL_FILE_NAME + "=" + filename);
    	list.add(FileProcessor.ACTUAL_FILE_ENCODING + "=" +  encoding);
    	list.add(FileProcessor.LAST_PROPERTY + "=" + "someValue");
    	
    	return list;
    }
    
    private List<String> csvFileContents(){
    	List<String> list = new ArrayList<>();
    	list.add("bar,baz");
    	FooModel foo = new FooModel("csvBar1","csvBaz1");
    	expectedList.add(foo);
    	list.add(foo.toCsvString());
    	return list;
    }
    
    private String jsonFileContents() throws Exception{
    	List<FooModel> list = new ArrayList<>();
    	FooModel foo = new FooModel("jsonBar1","jsonBaz1");
    	list.add(foo);expectedList.add(foo);
    	foo = new FooModel("jsonBar2","jsonBaz2");
    	list.add(foo);expectedList.add(foo);
    	return om.writeValueAsString(list);  	
    }
    
    @Test public void testIt() {
    	try {
    		writeFiles();
			assertTrue(latch.await(30, TimeUnit.SECONDS));
			checkIfAllInputProcessed();
			checkIfFilesMovedToProcessedFolder();
		} catch (Exception e) {			
			assertFalse(true);
		}
    }
    // check if the expected list matches the actual list
    private void checkIfAllInputProcessed() {
    	assertTrue(expectedList.stream().map(expectedItem -> {
    		return actualList.contains(expectedItem);
    	}).allMatch(bool -> bool == Boolean.TRUE));
    }
    
    private void checkIfFilesMovedToProcessedFolder() {
    	assertFalse(Files.exists(csvtest));
    	assertFalse(Files.exists(csvtestactual));
    	assertFalse(Files.exists(jsontest));
    	assertFalse(Files.exists(jsontestactual));
    	
    	assertTrue(Files.exists(processedCsvtest));
    	assertTrue(Files.exists(processedCsvtestactual));
    	assertTrue(Files.exists(processedJsontest));
    	assertTrue(Files.exists(processedJsontestactual));
    }

}
