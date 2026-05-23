package com.myworkshopoutput.ui;

import com.myworkshopoutput.model.User;
import com.myworkshopoutput.service.AuthService;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Login view — email + password form, links to sign-up.
 */
public class LoginPanel extends JPanel {

    private final AppFrame app;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private JButton        loginButton;

    public LoginPanel(AppFrame app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(AppTheme.BG_PAGE);
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(AppTheme.PAD_XL, AppTheme.PAD_XL, AppTheme.PAD_LG, AppTheme.PAD_XL)
        ));
        card.setMaximumSize(new Dimension(360, 400));

        // ── Heading ──────────────────────────────────────────────────────
        JLabel heading = new JLabel("Sign in to My Workshop App");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Welcome back!");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(AppTheme.PAD_LG));

        // ── Email ────────────────────────────────────────────────────────
        card.add(fieldLabel("Email address"));
        card.add(Box.createVerticalStrut(4));
        emailField = AppTheme.styledField(0);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(emailField);
        card.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        // ── Password ─────────────────────────────────────────────────────
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        passwordField = AppTheme.styledPasswordField(0);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        // Press Enter to log in
        passwordField.addActionListener(e -> attemptLogin());
        card.add(passwordField);
        card.add(Box.createVerticalStrut(AppTheme.PAD_LG));

        // ── Status ───────────────────────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.FONT_SMALL);
        statusLabel.setForeground(AppTheme.BG_DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(AppTheme.PAD_SM));

        // ── Login button ─────────────────────────────────────────────────
        loginButton = AppTheme.primaryButton("Sign In");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> attemptLogin());
        card.add(loginButton);
        card.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        // ── Switch to sign-up ─────────────────────────────────────────────
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchRow.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(AppTheme.FONT_SMALL);
        noAccount.setForeground(AppTheme.TEXT_SECONDARY);
        JButton toSignup = new JButton("Create one");
        toSignup.setFont(AppTheme.FONT_SMALL);
        toSignup.setForeground(AppTheme.BG_PRIMARY);
        toSignup.setBorderPainted(false);
        toSignup.setContentAreaFilled(false);
        toSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toSignup.addActionListener(e -> app.showView(AppFrame.VIEW_SIGNUP));
        switchRow.add(noAccount);
        switchRow.add(toSignup);
        card.add(switchRow);

        add(card);
    }

    private void attemptLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            setStatus("Please enter your email and password.", false);
            return;
        }

        loginButton.setEnabled(false);
        setStatus("Signing in…", true);

        new Thread(() -> {
            try {
                User user = AuthService.signIn(email, password);
                SwingUtilities.invokeLater(() -> app.onLoginSuccess(user));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    setStatus(ex.getMessage(), false);
                    loginButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void setStatus(String message, boolean info) {
        statusLabel.setText(message);
        statusLabel.setForeground(info ? AppTheme.TEXT_SECONDARY : AppTheme.BG_DANGER);
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        return lbl;
    }
}