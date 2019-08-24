package lt.viko.eif.groupwork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class FileManager {

	public static String saveFile(InputStream uploadedInputStream, FormDataContentDisposition fileDetails, String id)
	{	
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			
			DB mongoDB = mongoClient.getDB("groupproject");
		     
			//Let's store the standard data in regular collection
			DBCollection collection = mongoDB.getCollection("filestorage");
			
			// Let's query to ensure ID does not already exist in Mongo
		    // if it does, we will alert the user 
		    BasicDBObject query = new BasicDBObject();
		    query.put("_id", id);
		    DBCursor cursor = collection.find(query);
		    
		    if (!cursor.hasNext()) 
		    {
				// Build our document and add all the fields
				BasicDBObject document = new BasicDBObject();
				document.append("_id", id);
				document.append("filename", fileDetails.getFileName());
				
				//insert the document into the collection 
				collection.insert(document);
				
				// Now let's store the binary file data using filestore GridFS  
				GridFS fileStore = new GridFS(mongoDB, "filestorage");
				
				GridFSInputFile inputFile = fileStore.createFile(uploadedInputStream);
				
				inputFile.setId(id);
				inputFile.setFilename(fileDetails.getFileName());
				inputFile.save();
				
				return "Upload has been successful";
			} 
		    else 
		    {
				String status = "Unable to insert record with ID: " + id +" as record already exists!!!";
				return status;
			}
		    
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "Failed to connect to database";
		}
	}
	
	public static Response downloadFilebyID(String id)
	{
		try {
			Response response = null;
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB mongoDB = mongoClient.getDB("groupproject");
			    
			//Let's store the standard data in regular collection
			DBCollection collection = mongoDB.getCollection("filestorage");
		
			BasicDBObject query = new BasicDBObject();
			query.put("_id", id);
			DBObject doc = collection.findOne(query);
			DBCursor cursor = collection.find(query);
			        
			if (cursor.hasNext()) 
			{
				Set<String> allKeys = doc.keySet();
				HashMap<String, String> fields = new HashMap<String,String>();
				
				for (String key: allKeys) 
				{
					fields.put(key, doc.get(key).toString());
				}
					         
				GridFS fileStore = new GridFS(mongoDB, "filestorage");
				GridFSDBFile gridFile = fileStore.findOne(query);
					  
				InputStream in = gridFile.getInputStream();
					          
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int data = in.read();
				
				while (data >= 0) 
				{
					out.write((char) data);
					data = in.read();
				}
				out.flush();
					  
				ResponseBuilder builder = Response.ok(out.toByteArray());
				builder.header("Content-Disposition", "attachment; filename=" + fields.get("filename"));
				response = builder.build();
			} 
			else 
			{
				response = Response.status(404).
				entity(" Unable to get file with ID: " + id).
				type("text/plain").
				build();
			}
			
			return response;
			
		} catch(IOException e)
		{
			Response response = Response.status(404).
					entity("Failed to connect to database").
					type("text/plain").
					build();
			
			return response;
		}
	}
}
