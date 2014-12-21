package com.cagricelebi.coreutils.log;

import org.slf4j.LoggerFactory;

public class Logger {

    private final org.slf4j.Logger logger;

    private Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public static Logger getLogger(String className) {
        return new Logger(LoggerFactory.getLogger(className));
    }

    public static Logger getLogger(Class clazz) {
        return new Logger(LoggerFactory.getLogger(clazz));
    }

    public void log(Object obj) {
        if (obj == null) {
            logger.info("null");
        } else {
            logger.info(obj.toString());
        }
    }

    public void log(String message) {
        logger.info(message);
    }

    public void log(String message, Object o1, Object o2) {
        logger.info(message, o1, o2);
    }

    public void log(String message, Object... os) {
        logger.info(message, os);
    }

    public void log(String message, Throwable t) {
        logger.error(message, t);
    }

    public void log(Throwable t) {
        logger.error(t.getMessage(), t);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String message, Object o1, Object o2) {
        logger.debug(message, o1, o2);
    }

    public void debug(String message, Object... os) {
        logger.debug(message, os);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, Object o1, Object o2) {
        logger.info(message, o1, o2);
    }

    public void info(String message, Object... os) {
        logger.info(message, os);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Object o1, Object o2) {
        logger.error(message, o1, o2);
    }

    public void error(String message, Object... os) {
        logger.error(message, os);
    }

    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public void error(Throwable t) {
        logger.error(t.getMessage(), t);
    }

}
