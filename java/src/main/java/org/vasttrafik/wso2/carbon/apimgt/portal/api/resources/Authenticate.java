package org.vasttrafik.wso2.carbon.apimgt.portal.api.resources;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.vasttrafik.wso2.carbon.apimgt.portal.api.security.Security;
import org.vasttrafik.wso2.carbon.common.api.utils.ResponseUtils;
import org.vasttrafik.wso2.carbon.identity.api.utils.UserAdminUtils;
import org.vasttrafik.wso2.carbon.identity.oauth.authcontext.JWTToken;
import org.vasttrafik.wso2.carbon.identity.oauth.authcontext.JWTTokenGenerator;
import org.vasttrafik.wso2.carbon.common.api.beans.AuthenticatedUser;
import org.vasttrafik.wso2.carbon.common.api.beans.Credentials;


@Path("/authenticate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class Authenticate extends PortalResource {
	
	private static JWTTokenGenerator tokenGenerator = new JWTTokenGenerator();
	
	@POST
    public Response postAuthenticate(@Valid final Credentials credentials) 
    	throws ServerErrorException 
    {
		try {
			// Get username
			String userName = credentials.getUserName();
			
			if ("admin".equalsIgnoreCase(userName)) {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
				
			// Authenticate the user
		    UserAdminUtils.authenticateCredentials(userName, credentials.getCredential());
		    // Login the user
		    Security.login(credentials);
			
			// Generate a token
			JWTToken jwtToken = tokenGenerator.generateToken(userName);
			// Create the response object
			AuthenticatedUser user = new AuthenticatedUser(jwtToken);
			// Create the response object
			return Response.status(201).entity(user).build();
		}
		catch(NotAuthorizedException | InternalServerErrorException ie) {
			return Response.status(Response.Status.UNAUTHORIZED)
				.entity(ie.getCause())
				.build();
		}
		catch (Exception e) {
			Response response = ResponseUtils.serverError(e);
			throw new ServerErrorException(response);
		}
    }
}
