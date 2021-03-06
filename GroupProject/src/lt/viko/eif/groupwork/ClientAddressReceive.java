package lt.viko.eif.groupwork;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

/**
 * *
 * @author Gytis Bartkus, Dominykas jurkus
 * Class used to receive clients IP address
 *
 */

@Path("/user")
public class ClientAddressReceive {

    @Context private javax.servlet.http.HttpServletRequest hsr;
    @GET
    @Path("get_ip")
    @Produces("text/plain")
    public String get_ip()
    {
            return hsr.getRemoteAddr();
    }
}