package org.gtri.niem.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by brad on 8/31/16.
 */
public class DebugExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace(System.err);
        return Response.serverError().entity(exception.getMessage()).build();
    }

}
