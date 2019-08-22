package lt.viko.eif.groupwork;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class FileManager {

	//Gets file from uploadFile function and saves it to a folder
	public static String saveFile(InputStream uploadedInputStream, FormDataContentDisposition fileDetail)
	{
		//Where to save
		String uploadedFileLocation = "C:/Users/Dominykas Jurkus/Desktop/Upload/"  + fileDetail.getFileName();
		
        //Saving file
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
		
		return "File successfully uploaded to: " + uploadedFileLocation;
	}
}
