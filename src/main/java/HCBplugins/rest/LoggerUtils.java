package HCBplugins.rest; //

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtils {
    /**************************************************************************
     * method initialises new logger, adds new console and file handlers to it
     * sets the log level to all and changes the system property of log format
     * log string will look like
     * Apr 14, 2022 5:08:49 PM HCBplugins.rest.CfOptChange settingLogger
     * INFO: starting log....
     * @return logger instance with console and file handler added to it
     *************************************************************************/
    public static Logger createLogger(String className) {
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%4$s %2$s: %5$s%6$s%n");
        Logger newLogger = Logger.getLogger(className);
        newLogger.setUseParentHandlers(false);
        Handler conHandler = new ConsoleHandler();
        conHandler.setLevel(Level.ALL);
        newLogger.addHandler(conHandler);
        newLogger.info("created logger. initialized console handler");
        try {
            Handler fileHandler = new FileHandler("C:\\Users\\digit\\Documents" +
                    "\\JAVA\\Plugin\\REST4DevOps\\log.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            newLogger.addHandler(fileHandler);
            newLogger.info("logger fileHandler creation success");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            newLogger.warning("logger fileHandler creation failure");
        }

        newLogger.info("started log....");
        return newLogger;
    }
}
