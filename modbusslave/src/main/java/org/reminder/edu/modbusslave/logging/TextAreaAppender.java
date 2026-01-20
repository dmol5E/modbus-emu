package org.reminder.edu.modbusslave.logging;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaAppender extends AbstractAppender {

    private volatile TextArea textArea;

    public TextAreaAppender(String name, Filter filter, TextArea textArea) {
        super(name, filter, PatternLayout.createDefaultLayout(), false, Property.EMPTY_ARRAY);
        this.textArea = textArea;
    }

    @Override
    public void append(LogEvent event) {
        String logMessage = new String(getLayout().toByteArray(event));
        
        // Update the TextArea on the JavaFX Application Thread
        TextArea currentTextArea = this.textArea;
        if (currentTextArea != null) {
            if (Platform.isFxApplicationThread()) {
                currentTextArea.appendText(logMessage);
            } else {
                Platform.runLater(() -> {
                    TextArea ta = this.textArea; // Capture current reference
                    if (ta != null) {
                        ta.appendText(logMessage);
                    }
                });
            }
        }
    }
}