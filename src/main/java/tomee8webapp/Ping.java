package tomee8webapp;


import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.ws.WebServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


@Path("ping")
@Startup
@Singleton
@TransactionAttribute(NOT_SUPPORTED)
@Lock(LockType.READ)
public class Ping {
    private static final Logger log = LogManager.getLogger(Ping.class);
    private static final Marker gMarker = MarkerManager.getMarker("ping");

    @Context SecurityContext securityContext;




    @PostConstruct
    private void init() {
        log.info(gMarker, "allocation of ping bean complete:");
    }



    @PreDestroy
    private void destroy() {
        log.info(gMarker, "shutdown of ping bean complete:");
    }
    
    
    private static XMLOutputter getXmlOutputterPretty() {
        Format f = Format.getPrettyFormat()
            .setEncoding("utf-8" )
            .setIndent("    ")
            .setLineSeparator("\n")
        ;
        return new XMLOutputter(f);
    }



    @GET
    @Lock(LockType.READ)
    public Response status()
    throws WebServiceException
    {
        final Date startDate = new Date();

        String callerNm="unknown";
        try {
            callerNm = securityContext.getUserPrincipal().getName();

            final Date endDate = new Date();

            final Element eRoot = new Element("ping");
            final String entity;


            eRoot.setAttribute("received-at", startDate.toString())
	            .setAttribute("replied-at", endDate.toString())
	        ;
           entity = getXmlOutputterPretty().outputString(new Document(eRoot));

            return Response.ok()
                .entity(entity)
                .build()
            ;
        }
        finally {
            log.info(gMarker, "ping: "+ "; caller-name=" + callerNm);
        }
    }

}

