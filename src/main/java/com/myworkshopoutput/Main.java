package com.myworkshopoutput;

import com.myworkshopoutput.ui.AppFrame;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;

/**
 * Application entry point.
 */
public class Main {

    public static void main(String[] args) {
        // Apply theme tweaks before any UI is constructed
        AppTheme.apply();

        // All Swing work must happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                AppFrame frame = new AppFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to start the app:\n\n" + e.getMessage() +
                                "\n\nMake sure your .env file exists and contains SUPABASE_URL and SUPABASE_ANON_KEY.",
                        "Startup error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}