package lt.viko.eif.groupwork;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/files")
public class FileDownloadService {

	//http://localhost:8080/GroupProject/rest/files/download
	
	// The folder path of the file location
    private static String fileLocation = "C:/Users/Dominykas Jurkus/Desktop/Upload/";
 
    @GET
    @Path("/download")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getTextFile(String fileName) {
 
        File file = new File(fileLocation + fileName);
 
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=\"test_text_file.txt\"");
        return response.build();
    }
}
