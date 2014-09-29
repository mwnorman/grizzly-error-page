package org.mwnorman.resources;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;

import org.mwnorman.helpers.TestHelper;
import org.mwnorman.models.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mwnorman.resources.helpers.ResourceHelper.ID_PATHPARAM;
import static org.mwnorman.resources.helpers.ResourceHelper.TEST_RESOURCE_PATH;
import static org.mwnorman.resources.helpers.ResourceHelper.VALID_GUID;

public class TestResource {
	
	static Logger logger = LoggerFactory.getLogger(TestResource.class.getName());

    protected final TestHelper testHelper;

    @Inject
    public TestResource(TestHelper testHelper) {
        this.testHelper = testHelper;
    }
	
    @GET
    @Path(TEST_RESOURCE_PATH)
    @Produces(APPLICATION_JSON)
    public Response getAccount(
        @Pattern(regexp=VALID_GUID, message="{id.invalid}") 
        @PathParam(ID_PATHPARAM) String id) {
       
    	Account accountEntity = testHelper.getAccountById(id);
    	 
    	return Response
    	    .status(OK)
    	    .type(APPLICATION_JSON)
    	    .entity(accountEntity)
    	    .build()
    	;
    }

}