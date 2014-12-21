package com.cagricelebi.coreutils;

import com.cagricelebi.coreutils.conn.HttpConnectionHandler;
import com.cagricelebi.coreutils.conn.Response;
import com.cagricelebi.coreutils.log.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        try {
            System.out.println("Hi stdout!");
            logger.log("Hi logback!");
            Response r = HttpConnectionHandler.getUrl("http://cagricelebi.com/ip/");
            logger.log(r.getOutput());
        } catch (Exception e) {
            try {
                logger.log(e);
            } catch (Exception e2) {
                e2.printStackTrace();
                e.printStackTrace();
            }
        }
    }
}
