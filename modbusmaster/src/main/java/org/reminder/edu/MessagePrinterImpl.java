package org.reminder.edu;

import javafx.scene.control.TextArea;

public class MessagePrinterImpl implements MessagePrinter {

    private TextArea textArea;

    public MessagePrinterImpl(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void info(String message) {
        // textArea.setText(message);
        textArea.appendText(message + "\n");
    }

    @Override
    public void error(String message) {
        // textArea.setText(message);
        textArea.appendText(message + "\n");
    }

}
