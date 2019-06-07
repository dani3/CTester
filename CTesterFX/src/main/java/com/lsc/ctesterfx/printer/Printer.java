package com.lsc.ctesterfx.printer;

import com.lsc.ctesterfx.constants.Colors;
import com.lsc.ctesterfx.interfaces.ILogger;
import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

/**
 *
 * @author dma@logossmartcard.com
 */
public class Printer implements ILogger
{
    // RichTextArea that will contain the output of the test.
    private final InlineCssTextArea mOutputTextArea = new InlineCssTextArea();
    private static Printer mPrinter;

    private Printer() {}

    public static synchronized Printer newInstance()
    {
        if (mPrinter == null)
        {
            mPrinter = new Printer();
        }

        return mPrinter;
    }

    /**
     * Set up the output area.
     *
     * @param container: layout containing the output pane.
     */
    public void setup(BorderPane container)
    {
        // Set the common style for output. Monospace and font size.
        mOutputTextArea.setStyle("-fx-font-family: monospace; -fx-font-size: 10pt;");
        // Not editable.
        mOutputTextArea.setEditable(false);
        // Transparent background
        mOutputTextArea.setBackground(Background.EMPTY);
        // No wrapping.
        mOutputTextArea.setWrapText(false);

        // Container of the output text area. The virtualized container will only render the text visible.
        VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(mOutputTextArea);
        VBox.setVgrow(vsPane, Priority.ALWAYS);

        // Force to fill the parent size.
        vsPane.prefWidthProperty().bind(container.prefWidthProperty());
        vsPane.prefHeightProperty().bind(container.prefHeightProperty());

        container.setCenter(vsPane);
    }

    @Override
    public void log(String text)
    {
        logWithFormat(text + "\n", Colors.createAsString(Colors.Color.GRAY));
    }

    @Override
    public void logComment(String text)
    {
        logWithFormat(COMMENT_HEADER + text + "\n", Colors.createAsString(Colors.Color.GRAY));
    }

    @Override
    public void logError(String text)
    {
        logWithFormat(ERROR_HEADER + text + "\n", Colors.createAsString(Colors.Color.RED));
    }

    @Override
    public void logWarning(String text)
    {
        logWithFormat(WARNING_HEADER + text + "\n", Colors.createAsString(Colors.Color.YELLOW));
    }

    @Override
    public void logDebug(String text)
    {
        logWithFormat(DEBUG_HEADER + text + "\n", Colors.createAsString(Colors.Color.BLUE));
    }

    /**
     * Logs in the output panel a text with a specific color.
     *
     * @param text: text to be printed.
     * @param color: color to be used.
     */
    private void logWithFormat(String text, String color)
    {
        LogRunnable logRunnable = new LogRunnable(text, color);

        Platform.runLater(logRunnable);
    }

    /**
     * Runnable needed to print something from a background process.
     */
    private class LogRunnable implements Runnable
    {
        private final String mText;
        private final String mColor;
        private int mIndex;

        public LogRunnable(String text, String color)
        {
            mText = text;
            mColor = color;
        }

        @Override
        public void run()
        {
            // Save the last paragraph's index
            mIndex = mOutputTextArea.getDocument().getParagraphs().size() - 1;

            mOutputTextArea.appendText(mText);
            mOutputTextArea.setStyle(mIndex, "-fx-fill: " + mColor);
        }
    }
}
