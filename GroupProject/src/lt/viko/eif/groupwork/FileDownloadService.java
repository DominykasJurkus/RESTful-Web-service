package lt.viko.eif.groupwork;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
*
* Class used for file downloading
*/

@Path("/files")
public class FileDownloadService {

	//http://localhost:8080/GroupProject/rest/files/download
	
	@Context private javax.servlet.http.HttpServletRequest hsr;
    @GET
    @Path("/download/{id}/{password}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getTextFile(@PathParam("id") String id,
    							@PathParam("password") String password) throws Exception  
    {
		return FileManager.downloadFile(id, password, hsr.getRemoteAddr());
    }
}