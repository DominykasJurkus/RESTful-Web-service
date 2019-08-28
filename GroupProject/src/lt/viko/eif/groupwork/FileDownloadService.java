package lt.viko.eif.groupwork;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/files")
public class FileDownloadService {

	//http://localhost:8080/GroupProject/rest/files/download
 
    @GET
    @Path("/download")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getTextFile(String id) throws Exception  
    {
		return FileManager.downloadFile(id);
    }
}