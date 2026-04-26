package org.reminder.edu.common.javafx.logging;

import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.Level;

public class LogAppenderManager {
    private static TextAreaAppender textAreaAppender;

    public static void registerTextArea(TextArea textArea) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        textAreaAppender = new TextAreaAppender("TextAreaAppender", null, textArea);
        textAreaAppender.start();

        config.addAppender(textAreaAppender);

        LoggerConfig rootLoggerConfig = config.getLoggerConfig("");
        rootLoggerConfig.addAppender(textAreaAppender, Level.INFO, null);

        context.updateLoggers();
    }

    public static void unregisterTextArea() {
        if (textAreaAppender != null) {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            LoggerConfig rootLoggerConfig = config.getLoggerConfig("");
            rootLoggerConfig.removeAppender(textAreaAppender.getName());

            textAreaAppender.stop();
            config.getAppenders().remove(textAreaAppender.getName());

            textAreaAppender = null;

            context.updateLoggers();
        }
    }
}
