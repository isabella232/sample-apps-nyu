import static com.mongodb.client.model.Filters.in;
import static org.junit.Assert.*;

import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.junit.Test;


public class InsertDataTest {

	String connectionString = "mongodb+srv://m001-student:student123#@sandbox-trhqa.mongodb.net/test";
	
	 
	//Check the connection
	@Test
	public void TestConnection() {		
		MongoClientURI clientUri = new MongoClientURI(connectionString);
		try(MongoClient client = new MongoClient(clientUri))
		{
			assertNotNull(client);
		}    
	}
	
	//Test for adding one data set
	@Test
	public void AddOneOrderWithDataTest() {
		
		String stockKeepingUnit = "MDBTS110";
		String item = "color pencil";
		Double unit_price = 9.50;
		int quantity = 32;
		
		InsertData insertData = new InsertData();		
		List<Document> filteredDocumentsBefore = insertData.CountOrderSize(connectionString);
		InsertData.AddOneOrderWithData(connectionString, stockKeepingUnit, item, unit_price, quantity);
		List<Document> filteredDocumentsAfter = insertData.CountOrderSize(connectionString);
			
		assertEquals(filteredDocumentsBefore.size()+1, filteredDocumentsAfter.size());	
		//List<Document> queryResult = collection.find().into(new ArrayList<Document>());
		//System.out.println(queryResult.size());
		
	
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void AddOneOrderWithDataTest_InvalidConnectionString_ThrowsException() 
	{
		InsertData insertData = new InsertData();	
		List<Document> AddDocuments = InsertData.AddOneOrderWithData("", "MDBTS120", "color pencil", 9.50, 32 );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void AddOneOrderWithDataTest_emptyValue_ThrowsException() 
	{
		InsertData insertData = new InsertData();	
		List<Document> AddDocuments = InsertData.AddOneOrderWithData(connectionString, "", "color pencil", 0.0, 32 );
	}
	
	
	@Test
	public void AddOneOrderWithFileTest() throws JSONException, IOException, ParseException {
		
		InsertData insertData = new InsertData();		
		List<Document> filteredDocumentsBefore = insertData.CountOrderSize(connectionString);
		List<Document> AddDocuments = insertData.AddOneOrderWithFile(connectionString,"simpledata.json");
		List<Document> filteredDocumentsAfter = insertData.CountOrderSize(connectionString);
		// check if the data is added and the overall size has incremented by 1
		
		assertEquals(filteredDocumentsBefore.size()+1, filteredDocumentsAfter.size());	
		Document result = AddDocuments.get(filteredDocumentsAfter.size()-1);
		String id = result.getString("_id");
		assertEquals("9a999e1a1a11cc111fb1f99c",id);	
	   
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void AddOneOrderWithFileTest_InvalidConnectionString_ThrowsException() throws IOException, ParseException   
	{
		InsertData insertData = new InsertData();	
		List<Document> AddDocuments = insertData.AddOneOrderWithFile("", "simpledata.json" );
	}
	
	@Test(expected=FileNotFoundException.class)
	public void AddOneOrderWithFileTest_InvalidFile_ThrowsException() throws IOException, ParseException   
	{
		InsertData insertData = new InsertData();	
		List<Document> AddDocuments = insertData.AddOneOrderWithFile(connectionString, "simple.json" );
		
	}
		
	
	@Test
	public void AddMultipleOrdersWithFilesTest() throws FileNotFoundException {
		
		InsertData insertData = new InsertData();		
		List<Document> multipleOrder = new ArrayList<>();
		  
		String[] orderfiles = new String[3];
		
		for (int i=0; i< orderfiles.length; i++)
		{
			orderfiles[i] = "order"+(i+1)+".json";
		}
		
		List<Document> filteredDocumentsBefore = insertData.CountOrderSize(connectionString);
		List<Document> AddDocuments = insertData.AddMultipleOrdersWithFiles(connectionString, orderfiles);
		List<Document> filteredDocumentsAfter = insertData.CountOrderSize(connectionString);
		
		// check if the data is added and the overall size has incremented by the size of the array
		assertEquals(filteredDocumentsBefore.size()+orderfiles.length, filteredDocumentsAfter.size());	
		Document result = AddDocuments.get(filteredDocumentsAfter.size()-1);
		String id = result.getString("_id");
		assertEquals("3a333e3a3a33cc333fb3f33c",id);	
	
		
	}
	
	@Test
	public void AddMultipleOrderWithDataTest() {
		
		String[] stockKeepingUnit = {"MAERW111","MBERQ112","MCERT113"};
		String[] item = {"color pencil","marker","compass"};
		Double[] unit = {5.0, 24.5, 35.20};
		int[] quant= {7,14,23};
		
		InsertData insertData = new InsertData();		
		List<Document> filteredDocumentsBefore = insertData.CountOrderSize(connectionString);
		InsertData.AddMultipleOrderWithData(connectionString, stockKeepingUnit, item, unit, quant);
		List<Document> filteredDocumentsAfter = insertData.CountOrderSize(connectionString);
			
		assertEquals(filteredDocumentsBefore.size()+3, filteredDocumentsAfter.size());	
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void AddMultipleOrderWithDataTest_InvalidConnectionString_ThrowsException() 
	{
		String[] stockKeepingUnit = {"MAERW111","MBERQ112","MCERT113"};
		String[] item = {"color pencil","marker","compass"};
		Double[] unit = {5.0, 24.5, 35.20};
		int[] quant= {7, 14,23};
		
		InsertData insertData = new InsertData();	
		List<Document> AddDocuments = insertData.AddMultipleOrderWithData("",  stockKeepingUnit, item, unit, quant );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void AddMultipleOrderWithDataTest_emptyValue_ThrowsException() 
	{
		String[] stockKeepingUnit = {};
		String[] item = {"color pencil","marker","compass"};
		Double[] unit = {};
		int[] quant= {7, 14,23};
		
		InsertData insertData = new InsertData();	
		List<Document> AddDocuments = insertData.AddMultipleOrderWithData("",  stockKeepingUnit, item, unit, quant );
	}

	
	@Test
	public void RemoveAddedOrders() 
	{
		
		MongoClientURI clientUri = new MongoClientURI(connectionString);
		MongoClient client = new MongoClient(clientUri);
		MongoDatabase database = client.getDatabase("stores");
		MongoCollection<Document> collection = database.getCollection("orders");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String today = formatter.format(date);
		String[] removeDates = {"2018-03-14","2018-03-11", "2018-04-11", today};
		DeleteResult deletedOrder = collection.deleteMany(in("orderPlaced",removeDates));
		List<Document> queryResult = collection.find().into(new ArrayList<Document>());
		System.out.println(queryResult.size());
		//assertEquals(102, queryResult.size());
		//assertEquals(102, queryResult.size());
	
	}
	
}	
