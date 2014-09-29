package org.mwnorman.resources.helpers;

public interface ResourceHelper {

    public static final String ROOT_RESOURCE = "root";
    public static final String ROOT_RESOURCE_NAME = "RootV1";
    public static final String TEST_RESOURCE = "test";
    public static final String ID_PATHPARAM = "id";
    public static final String TEST_RESOURCE_PATH = "{" + ID_PATHPARAM + "}";
    
    //Version 4 UUIDs have the form xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx where 
    // x is any hexadecimal digit and y is one of 8, 9, a (A), or b (B)
    // http://tools.ietf.org/rfc/rfc4122.txt
    
    public static final String VALID_GUID = 
        "^\\p{XDigit}{8}-\\p{XDigit}{4}-4\\p{XDigit}{3}-[89abAB]\\p{XDigit}{3}-\\p{XDigit}{12}$";
    
}