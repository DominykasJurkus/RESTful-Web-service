package lt.viko.eif.groupwork;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	//Gets file from uploadFile function and saves it to a folder
	public static String saveFile(InputStream uploadedInputStream, FormDataContentDisposition fileDetails, String id, String password) throws Exception
	{
		//Temp save directory
		String uploadedFileLocation = "C:/Users/Dominykas Jurkus/Desktop/temp/"  + "FileToEncrypt.txt";
		
        //Saving file to temp
        try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			out = new FileOutputStream(new File(uploadedFileLocation));
			
			while ((read = uploadedInputStream.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}
			
			out.flush();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
        
        // Encrypt File
        FileEncryption.encryptFile(password);
		
		return FileManager.saveFileToDatabase(fileDetails, id);
	}
	
	public static Response downloadFile(String id, String password, String IPaddress) throws Exception
	{	
		Response response = null;
		String fileName = FileEncryption.decryptFile(downloadFileFromDatabaseByIDToTemp(id), password);
		
		String IP = "0:0:0:0:0:0:0:1";
		
		if(fileName == null)
		{
			response = Response.status(404).
					entity("File Name can not be empty").
					type("text/plain").
					build();
		}
		else if(IPaddress.equals(IP))
		{
			File file = new File("C:/Users/Dominykas Jurkus/Desktop/temp/downloaded - " + fileName);
			 
	        ResponseBuilder builder = Response.ok((Object) file);
	        builder.header("Content-Disposition", "attachment; filename=\"test_text_file.txt\"");
	        response = builder.build();
		}
		else
		{
			response = Response.status(500).
					entity("Wrong IP address").
					type("text/plain").
					build();
        }
		
		return response;
	}

	public static String saveFileToDatabase(FormDataContentDisposition fileDetails, String id) throws Exception
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
				
				File encryptedFile = new File("C:/Users/Dominykas Jurkus/Desktop/temp/EncryptedFile.aes");
				
				GridFSInputFile inputFile = fileStore.createFile(encryptedFile);
				
				inputFile.setId(id);
				inputFile.setFilename(fileDetails.getFileName());
				inputFile.save();
				
				encryptedFile.delete();
				
				return "Upload has been successful";
			} 
		    else 
		    {
				String status = "Unable to insert record with ID: " + id +" as record already exists!";
				return status;
			}
		    
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "Failed to connect to database";
		}
	}
	
	public static String downloadFileFromDatabaseByIDToTemp(String id)
	{
		try {
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
				
				//Save to temp folder the encrypted file from DB
				ByteArrayOutputStream byteArrayOutputStream = out;
				try(OutputStream outputStream = new FileOutputStream("C:/Users/Dominykas Jurkus/Desktop/temp/" + fields.get("filename"))) {
				    byteArrayOutputStream.writeTo(outputStream);
				}
				
				out.close();
				
				return fields.get("filename");
			} 
			else
			{
				return null;
			}
		} catch(IOException e)
		{
			return null;
		}
	}
	
	void CleanTemp()
	{
		
	}
}
