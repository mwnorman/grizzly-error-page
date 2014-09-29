package org.mwnorman.resources;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

import static org.mwnorman.resources.helpers.ResourceHelper.ROOT_RESOURCE;
import static org.mwnorman.resources.helpers.ResourceHelper.ROOT_RESOURCE_NAME;
import static org.mwnorman.resources.helpers.ResourceHelper.TEST_RESOURCE;

@Path(ROOT_RESOURCE)
@Named(ROOT_RESOURCE_NAME)
public class Resource {
    
	protected final TestResource testResource;

    @Inject
    public Resource(TestResource testResource) {
    	this.testResource = testResource;
	}
    
    @Path(TEST_RESOURCE)
    public TestResource getTestResource() {
        return testResource;
    }
    
}