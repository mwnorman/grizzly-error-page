package org.mwnorman;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.mwnorman.resources.ResourcesTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    ResourcesTestSuite.class
})
public class AllTests {
}