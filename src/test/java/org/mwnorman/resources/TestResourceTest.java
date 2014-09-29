package org.mwnorman.resources;

//javase imports
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

//java eXtension imports
import javax.servlet.ServletRegistration;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

//3rdparty imports
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationError;
import org.glassfish.jersey.servlet.ServletContainer;
import static org.glassfish.jersey.servlet.ServletProperties.JAXRS_APPLICATION_CLASS;

//testing imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//domain imports
import org.mwnorman.helpers.TestHelper;
import org.mwnorman.jersey.JacksonObjectMapperResolver;
import org.mwnorman.models.Account;
import static org.mwnorman.jersey.JacksonObjectMapperResolver.getObjectMapper;
import static org.mwnorman.resources.helpers.ResourceHelper.ID_PATHPARAM;
import static org.mwnorman.resources.helpers.ResourceHelper.ROOT_RESOURCE;
import static org.mwnorman.resources.helpers.ResourceHelper.TEST_RESOURCE;
import static org.mwnorman.resources.helpers.ResourceHelper.TEST_RESOURCE_PATH;

public class TestResourceTest  {
    
    static final String TEST_SIMPLE_NAME = TestResourceTest.class.getSimpleName();
    static Logger logger = Logger.getLogger(TestResourceTest.class.getName());
    
    public static final int PORT = 23456;
    public static final String REST_CONTEXT = "REST";
    public static final String VERSION = ROOT_RESOURCE;
    public static final String BASE_URI = "http://localhost:" + PORT + "/";
    public static final String INVALID_ID = "this is an invalid id";
    public static final String MOCK_ID = "28cdcb4d-b067-4298-a668-77b9d1060700";
    
    //JUnit fixtures
    static HttpServer server = null;
    static Object serverEntity = null;
    static Object clientEntity = null;
    static Client client = null;
    
    @BeforeClass
    public static void setUpGrizzlyServer() throws IOException {
        // build Grizzly server step-by-step instead of using GrizzlyWebContainerFactory -
        // allows us to create the server without auto-starting it, configure things, THEN start it
        WebappContext webappContext = new WebappContext("GrizzlyContext", "");
        ServletRegistration servletRegistration = webappContext.addServlet(
            "Jersey", ServletContainer.class);
        servletRegistration.addMapping("/" + REST_CONTEXT + "/*");
        Map<String, String> initParams = new HashMap<>();
        initParams.put(CommonProperties.MOXY_JSON_FEATURE_DISABLE, "true");
        initParams.put(ServerProperties.WADL_FEATURE_DISABLE, "true");
        initParams.put(JAXRS_APPLICATION_CLASS, TestResourceApplication.class.getName());
        initParams.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, "true");
        servletRegistration.setInitParameters(initParams);
        URI serverURI = UriBuilder.fromPath("/")
                .scheme("http")
                .host("0.0.0.0")
                .path("REST")
                .port(PORT).build();
        server = GrizzlyHttpServerFactory.createHttpServer(serverURI, false);
        server.getServerConfiguration().setHttpServerName(TEST_SIMPLE_NAME);
        webappContext.deploy(server);
        server.start();
        
        JacksonJaxbJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider();
        jacksonJsonProvider.setMapper(getObjectMapper());
        client = ClientBuilder
            .newClient()
            .register(new ClientResponseFilter() {
                @Override
                public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
                    try (
                        InputStream entityStream = responseContext.getEntityStream();
                        Scanner scanner = new Scanner(entityStream).useDelimiter("\\A"); //end-of-line
                    )
                    {
                        String entityString = scanner.hasNext() ? scanner.next() : "";
                        if (responseContext.getStatus() == 200) {
                            clientEntity = getObjectMapper().readValue(entityString, Account.class);
                        }
                        else {
                            clientEntity = entityString;
                        }
                    }
                    catch (IOException e) {
                        // ignore
                    }
                }
            })
            .register(jacksonJsonProvider);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }
    
    protected String buildPathToTestSource() {
        return ROOT_RESOURCE + "/" + TEST_RESOURCE + "/" + TEST_RESOURCE_PATH;
    }
    
    /*
     * @GET
     * @Path("root/test/{id}"
     */

    @Test
    public void test_getId_validId() {
        WebTarget webResource = client
            .target(BASE_URI)
            .path(buildPathToTestSource())
            .resolveTemplate(ID_PATHPARAM, MOCK_ID)
        ;
        Response clientResponse = null;
        try {
            clientResponse = webResource
                .request(MediaType.APPLICATION_JSON)
                .get();
            int status = clientResponse.getStatus();
            assertTrue("GET on {version}/user/{userId}/password did not return Ok(" +
                Response.Status.OK.getStatusCode() +")",
                status == Response.Status.OK.getStatusCode());
            assertEquals(serverEntity, clientEntity);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_getId_invalidId() {
        WebTarget webResource = client
            .target(BASE_URI)
            .path(buildPathToTestSource())
            .resolveTemplate(ID_PATHPARAM, INVALID_ID)
        ;
        Response clientResponse = null;
        try {
            clientResponse = webResource
                .request(MediaType.APPLICATION_JSON)
                .get();
            int status = clientResponse.getStatus();
            assertTrue("GET on {version}/user/{userId}/password did not return BadRequest(" +
                Response.Status.BAD_REQUEST.getStatusCode() +")",
                status == Response.Status.BAD_REQUEST.getStatusCode());
            List<ValidationError> listOfErrors = (List<ValidationError>)serverEntity;
            ValidationError validationError = listOfErrors.get(0);
            assertEquals("'" + INVALID_ID + 
                "' is not a valid id (xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx where x is any hexadecimal digit and y is one of 8, 9, a (A), or b (B)",
                validationError.getMessage());
            //clientEntity should contain validationError's, but doesn't
            logger.info(clientEntity.toString());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    static class TestResourceApplication extends ResourceConfig {
        public TestResourceApplication() {
            register(JacksonObjectMapperResolver.class);
            register(JacksonJaxbJsonProvider.class);
            register(JsonParseExceptionMapper.class);
            register(JsonMappingExceptionMapper.class);
            register(TestResource.class);
            packages("org.mwnorman.models;org.mwnorman.resources");
            register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(TestResource.class).to(TestResource.class);
                    bind(TestHelper.class).to(TestHelper.class);
                }
            });
            register(new ContainerResponseFilter() {
                @Override
                public void filter(ContainerRequestContext requestContext,
                    ContainerResponseContext responseContext) throws IOException {
                    serverEntity = responseContext.getEntity();
                }
            });
            registerInstances(new LoggingFilter(logger, true));
        }
    }

}