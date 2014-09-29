package org.mwnorman.helpers;

import org.mwnorman.models.Account;

public class TestHelper {

    public TestHelper() {
    }
    
    public Account getAccountById(String id)  {
        if (id == null || id.isEmpty()) {
            throw new RuntimeException("missing id");
        }
        //pretend we got account from database
        return new Account(id, "this is an account");
    }
    
}