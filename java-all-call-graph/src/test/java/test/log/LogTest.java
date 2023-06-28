package test.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Test
    public void colorTest(){

        logger.debug("debug");
        logger.info("info");
        logger.warn("warning");
        logger.error("error");
    }
}
