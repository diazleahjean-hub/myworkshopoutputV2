package com.myworkshopoutput.ui;

import com.myworkshopoutput.model.User;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Dashboard — first view after login.
 * Shows a welcome message and quick-action cards.
 */
public class DashboardPanel extends JPanel {

    private final AppFrame app;
    private JLabel         welcomeLabel;
    private JLabel         sessionLabel;

    public DashboardPanel(AppFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_PAGE);
        buildUI();
    }

    private void buildUI() {
        add(new NavBar(app, AppFrame.VIEW_DASHBOARD), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_PAGE);
        content.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PAD_XL, AppTheme.PAD_XL, AppTheme.PAD_XL, AppTheme.PAD_XL));

        // ── Greeting ─────────────────────────────────────────────────────
        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(AppTheme.FONT_HEADING);
        welcomeLabel.setForeground(AppTheme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcomeLabel);
        content.add(Box.createVerticalStrut(4));

        sessionLabel = new JLabel("Session active");
        sessionLabel.setFont(AppTheme.FONT_BODY);
        sessionLabel.setForeground(AppTheme.TEXT_SECONDARY);
        sessionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sessionLabel);
        content.add(Box.createVerticalStrut(AppTheme.PAD_XL));

        // ── Quick-action cards ────────────────────────────────────────────
        JPanel cards = new JPanel(new GridLayout(1, 2, AppTheme.PAD_MD, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        cards.add(quickCard(
                "My Notes",
                "Write and manage your personal notes.",
                AppTheme.BG_PRIMARY,
                () -> app.showView(AppFrame.VIEW_NOTES)
        ));
        cards.add(quickCard(
                "Profile",
                "View your account information.",
                new Color(15, 118, 110), // teal
                () -> app.showView(AppFrame.VIEW_PROFILE)
        ));

        content.add(cards);
        content.add(Box.createVerticalStrut(AppTheme.PAD_XL));

        // ── Info panel ───────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(AppTheme.BG_CARD);
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(AppTheme.PAD_MD, AppTheme.PAD_LG, AppTheme.PAD_MD, AppTheme.PAD_LG)
        ));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel infoTitle = new JLabel("Getting started");
        infoTitle.setFont(AppTheme.FONT_SUBHEAD);
        infoTitle.setForeground(AppTheme.TEXT_PRIMARY);
        info.add(infoTitle);
        info.add(Box.createVerticalStrut(4));

        JLabel infoBody = new JLabel("<html>Head to <b>Notes</b> to add your first note, or visit <b>Profile</b> to see your account details.</html>");
        infoBody.setFont(AppTheme.FONT_BODY);
        infoBody.setForeground(AppTheme.TEXT_SECONDARY);
        info.add(infoBody);

        content.add(info);
        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);
    }

    /** Rebuild dynamic text for the current user. */
    public void refresh(User user) {
        if (user == null) return;

        String email = user.getEmail();
        String name  = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        welcomeLabel.setText("Welcome back, " + name + "!");

        String expiry = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(user.getExpiresAt()));
        sessionLabel.setText("Signed in as " + email + "  ·  Session expires " + expiry);
    }

    private JPanel quickCard(String title, String body, Color bg, Runnable onClick) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PAD_LG, AppTheme.PAD_LG, AppTheme.PAD_LG, AppTheme.PAD_LG));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SUBHEAD);
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));

        JLabel bodyLabel = new JLabel("<html><div style='width:140px'>" + body + "</div></html>");
        bodyLabel.setFont(AppTheme.FONT_SMALL);
        bodyLabel.setForeground(new Color(255, 255, 255, 200));
        card.add(bodyLabel);
        card.add(Box.createVerticalGlue());

        JLabel arrow = new JLabel("→");
        arrow.setFont(AppTheme.FONT_SUBHEAD);
        arrow.setForeground(Color.WHITE);
        card.add(arrow);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(bg);
            }
        });

        return card;
    }
}