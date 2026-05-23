package com.myworkshopoutput.ui;

import com.myworkshopoutput.model.User;
import com.myworkshopoutput.service.AuthService;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Sign-up view — create a new account.
 */
public class SignupPanel extends JPanel {

    private final AppFrame app;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel         statusLabel;
    private JButton        signupButton;

    public SignupPanel(AppFrame app) {
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
        card.setMaximumSize(new Dimension(360, 460));

        JLabel heading = new JLabel("Create an account");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("It's free and takes 30 seconds.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(AppTheme.PAD_LG));

        card.add(fieldLabel("Email address"));
        card.add(Box.createVerticalStrut(4));
        emailField = AppTheme.styledField(0);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(emailField);
        card.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        passwordField = AppTheme.styledPasswordField(0);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        card.add(fieldLabel("Confirm password"));
        card.add(Box.createVerticalStrut(4));
        confirmField = AppTheme.styledPasswordField(0);
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        confirmField.addActionListener(e -> attemptSignup());
        card.add(confirmField);
        card.add(Box.createVerticalStrut(AppTheme.PAD_LG));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.FONT_SMALL);
        statusLabel.setForeground(AppTheme.BG_DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(AppTheme.PAD_SM));

        signupButton = AppTheme.primaryButton("Create Account");
        signupButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.addActionListener(e -> attemptSignup());
        card.add(signupButton);
        card.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchRow.setOpaque(false);
        JLabel hasAccount = new JLabel("Already have an account?");
        hasAccount.setFont(AppTheme.FONT_SMALL);
        hasAccount.setForeground(AppTheme.TEXT_SECONDARY);
        JButton toLogin = new JButton("Sign in");
        toLogin.setFont(AppTheme.FONT_SMALL);
        toLogin.setForeground(AppTheme.BG_PRIMARY);
        toLogin.setBorderPainted(false);
        toLogin.setContentAreaFilled(false);
        toLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toLogin.addActionListener(e -> app.showView(AppFrame.VIEW_LOGIN));
        switchRow.add(hasAccount);
        switchRow.add(toLogin);
        card.add(switchRow);

        add(card);
    }

    private void attemptSignup() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            setStatus("Please fill in all fields.", false);
            return;
        }
        if (!password.equals(confirm)) {
            setStatus("Passwords do not match.", false);
            return;
        }

        signupButton.setEnabled(false);
        setStatus("Creating account…", true);

        new Thread(() -> {
            try {
                User user = AuthService.signUp(email, password);
                SwingUtilities.invokeLater(() -> app.onLoginSuccess(user));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    setStatus(ex.getMessage(), false);
                    signupButton.setEnabled(true);
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