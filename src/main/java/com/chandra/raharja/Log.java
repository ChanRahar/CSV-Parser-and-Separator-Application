package com.chandra.raharja;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    public Logger logger;
    FileHandler fh;

    public Log(String file_name) throws SecurityException, IOException {

        fh = new FileHandler(file_name);
        logger = Logger.getLogger("Statistic");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
