package HCBplugins.rest; //

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtils {
    // private final String logFileName;
    private static Logger oneAndOnlyLogger;
    private final String logFilePath = "C:\\Users\\digit\\Documents\\JAVA\\Plugin\\REST4DevOps\\log.log";
    private final String loggerName = "REST4DevopsLogger";

    /**************************************************************************
     * constructor sets the system property defining how log messages will
     * look like and invokes logger creation method
     *************************************************************************/
    LoggerUtils() {
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%4$s %2$s: %5$s%6$s%n");
        oneAndOnlyLogger = createLogger();
    }

    /**************************************************************************
     * method initialises new logger, adds new console and file handlers to it
     * sets the log level to ALL. Log string will look like
     * Apr 29, 2022 5:39:14 PM HCBplugins.rest.LoggerUtils initializeLogger
     * INFO: created logger REST4DevopsLogger... initialized console handler
     *************************************************************************/
    private Logger createLogger() {
        Logger logger = Logger.getLogger(loggerName);
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.info("created logger " + loggerName + "... initialized console handler");
        try {
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.info("logger fileHandler creation success");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("failed to open log file for writing....");
        }
        logger.info("started log....");
        return logger;
    }

    public static Logger getLogger() {
        return oneAndOnlyLogger;
    }

    /**************************************************************************
     * method for closing the og file. should be called at the end of invoking
     * method
     *************************************************************************/
    public void closeLogFiles() {
        for (Handler handler : oneAndOnlyLogger.getHandlers()) {
            handler.close();
        }
    }
}
