package lt.viko.eif.groupwork;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
*
* Class used for file uploading
*/


@Path("/files")
public class FileUploadService {
	
	//http://localhost:8080/GroupProject/rest/files/upload
	
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile (
/**
    		   * Method that allows us to upload file to database by ID
    		   *
    		   * @param id  allows us to find files by ID
    		   * @param uploadedInputStream 
    		   * @param fileDetails
    		   * @param password string, which we use for file encryption
    		   * @return Saves file to database
    		   */

            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("id") String id,
            @FormDataParam("password") String password) throws Exception
    {    	
		return FileManager.saveFile(uploadedInputStream, fileDetails, id, password);
    }
}