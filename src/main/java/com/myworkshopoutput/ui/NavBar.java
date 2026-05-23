package com.myworkshopoutput.ui;

import com.myworkshopoutput.service.AuthService;
import com.myworkshopoutput.model.User;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Top navigation bar shown on all authenticated views.
 * Contains view tabs and a logout button.
 */
public class NavBar extends JPanel {

    public NavBar(AppFrame app, String activeView) {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(0, AppTheme.PAD_LG, 0, AppTheme.PAD_LG));
        setPreferredSize(new Dimension(0, 52));

        // ── Left: app name ──────────────────────────────────────────────
        JLabel brand = new JLabel("MyApp");
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        brand.setForeground(AppTheme.TEXT_ON_DARK);
        add(brand, BorderLayout.WEST);

        // ── Center: nav tabs ────────────────────────────────────────────
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        tabs.setOpaque(false);
        tabs.add(navTab("Dashboard", AppFrame.VIEW_DASHBOARD, activeView, app));
        tabs.add(navTab("Notes",     AppFrame.VIEW_NOTES,     activeView, app));
        tabs.add(navTab("Profile",   AppFrame.VIEW_PROFILE,   activeView, app));
        add(tabs, BorderLayout.CENTER);

        // ── Right: logout ───────────────────────────────────────────────
        JButton logout = new JButton("Logout");
        logout.setFont(AppTheme.FONT_SMALL);
        logout.setForeground(AppTheme.TEXT_ON_DARK);
        logout.setBackground(new Color(255, 255, 255, 30));
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setOpaque(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            User user = app.getCurrentUser();
            if (user != null) {
                new Thread(() -> AuthService.signOut(user.getAccessToken())).start();
            }
            app.onLogout();
        });
        add(logout, BorderLayout.EAST);
    }

    private JButton navTab(String label, String viewName, String activeView, AppFrame app) {
        JButton btn = new JButton(label);
        btn.setFont(AppTheme.FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean active = viewName.equals(activeView);
        btn.setBackground(active
                ? new Color(255, 255, 255, 40)
                : new Color(0, 0, 0, 0));
        btn.setForeground(active
                ? Color.WHITE
                : new Color(199, 210, 254)); // indigo-200

        btn.addActionListener(e -> app.showView(viewName));
        return btn;
    }
}