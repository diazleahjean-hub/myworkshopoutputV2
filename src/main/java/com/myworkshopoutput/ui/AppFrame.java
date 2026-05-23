package com.myworkshopoutput.ui;

import com.myworkshopoutput.model.User;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * The single JFrame for the whole app.
 * Uses CardLayout to switch between views without spawning new windows.
 *
 * Views:
 *   "login"     → LoginPanel
 *   "signup"    → SignupPanel
 *   "dashboard" → DashboardPanel
 *   "notes"     → NotesPanel
 *   "profile"   → ProfilePanel
 */
public class AppFrame extends JFrame {

    public static final String VIEW_LOGIN     = "login";
    public static final String VIEW_SIGNUP    = "signup";
    public static final String VIEW_DASHBOARD = "dashboard";
    public static final String VIEW_NOTES     = "notes";
    public static final String VIEW_PROFILE   = "profile";

    private final CardLayout   cardLayout = new CardLayout();
    private final JPanel       cardPanel  = new JPanel(cardLayout);

    // Current session — null when logged out
    private User currentUser;

    // Panels (lazily replaced when user logs in/out)
    private LoginPanel     loginPanel;
    private SignupPanel    signupPanel;
    private DashboardPanel dashboardPanel;
    private NotesPanel     notesPanel;
    private ProfilePanel   profilePanel;

    public AppFrame() {
        setTitle("MyApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(480, 520));
        setSize(520, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_PAGE);

        // Build auth panels (always available)
        loginPanel  = new LoginPanel(this);
        signupPanel = new SignupPanel(this);

        cardPanel.setBackground(AppTheme.BG_PAGE);
        cardPanel.add(loginPanel,  VIEW_LOGIN);
        cardPanel.add(signupPanel, VIEW_SIGNUP);

        add(cardPanel, BorderLayout.CENTER);
        showView(VIEW_LOGIN);
    }

    /** Navigate to a named view. */
    public void showView(String viewName) {
        // Lazily build authenticated panels the first time we need them
        if ((VIEW_DASHBOARD.equals(viewName) || VIEW_NOTES.equals(viewName) || VIEW_PROFILE.equals(viewName))
                && currentUser == null) {
            showView(VIEW_LOGIN);
            return;
        }

        switch (viewName) {
            case VIEW_DASHBOARD -> {
                if (dashboardPanel == null) {
                    dashboardPanel = new DashboardPanel(this);
                    cardPanel.add(dashboardPanel, VIEW_DASHBOARD);
                }
                dashboardPanel.refresh(currentUser);
            }
            case VIEW_NOTES -> {
                if (notesPanel == null) {
                    notesPanel = new NotesPanel(this);
                    cardPanel.add(notesPanel, VIEW_NOTES);
                }
                notesPanel.refresh(currentUser);
            }
            case VIEW_PROFILE -> {
                if (profilePanel == null) {
                    profilePanel = new ProfilePanel(this);
                    cardPanel.add(profilePanel, VIEW_PROFILE);
                }
                profilePanel.refresh(currentUser);
            }
        }

        cardLayout.show(cardPanel, viewName);
        setTitle("MyApp" + switch (viewName) {
            case VIEW_DASHBOARD -> " — Dashboard";
            case VIEW_NOTES     -> " — My Notes";
            case VIEW_PROFILE   -> " — Profile";
            default             -> "";
        });
    }

    /** Called by LoginPanel / SignupPanel after a successful auth. */
    public void onLoginSuccess(User user) {
        this.currentUser = user;

        // Reset authenticated panels so they rebuild fresh for this user
        dashboardPanel = null;
        notesPanel     = null;
        profilePanel   = null;

        showView(VIEW_DASHBOARD);
    }

    /** Called by any authenticated panel's logout button. */
    public void onLogout() {
        currentUser    = null;
        dashboardPanel = null;
        notesPanel     = null;
        profilePanel   = null;
        showView(VIEW_LOGIN);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}