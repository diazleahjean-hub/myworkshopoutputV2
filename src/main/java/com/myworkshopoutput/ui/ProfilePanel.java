package com.myworkshopoutput.ui;

import com.myworkshopoutput.model.User;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Profile view — displays account details and the current JWT session.
 */
public class ProfilePanel extends JPanel {

    private final AppFrame app;

    // Dynamic labels
    private JLabel emailValue;
    private JLabel idValue;
    private JLabel expiresValue;
    private JLabel tokenSnippet;

    public ProfilePanel(AppFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_PAGE);
        buildUI();
    }

    private void buildUI() {
        add(new NavBar(app, AppFrame.VIEW_PROFILE), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_PAGE);
        content.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PAD_XL, AppTheme.PAD_XL, AppTheme.PAD_XL, AppTheme.PAD_XL));

        // ── Page title ────────────────────────────────────────────────────
        JLabel pageTitle = new JLabel("My Profile");
        pageTitle.setFont(AppTheme.FONT_HEADING);
        pageTitle.setForeground(AppTheme.TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(pageTitle);
        content.add(Box.createVerticalStrut(AppTheme.PAD_LG));

        // ── Account card ──────────────────────────────────────────────────
        content.add(sectionCard("Account", new String[][]{
                {"Email",      null},
                {"User ID",    null},
        }, new JLabel[]{
                emailValue   = infoValue("—"),
                idValue      = infoValue("—"),
        }));
        content.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        // ── Session card ──────────────────────────────────────────────────
        content.add(sectionCard("Session", new String[][]{
                {"Expires",        null},
                {"Access token",   null},
        }, new JLabel[]{
                expiresValue  = infoValue("—"),
                tokenSnippet  = infoValue("—"),
        }));
        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);
    }

    /** Populate dynamic values from the current user. */
    public void refresh(User user) {
        if (user == null) return;

        emailValue.setText(user.getEmail());
        idValue.setText(user.getId());

        String expiry = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm z")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(user.getExpiresAt()));
        expiresValue.setText(expiry);

        // Show only the first ~40 chars of the token for display
        String token = user.getAccessToken();
        tokenSnippet.setText(token.length() > 40 ? token.substring(0, 40) + "…" : token);
        tokenSnippet.setFont(AppTheme.FONT_MONO);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private JPanel sectionCard(String sectionTitle, String[][] rows, JLabel[] values) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(AppTheme.PAD_MD, AppTheme.PAD_LG, AppTheme.PAD_MD, AppTheme.PAD_LG)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel sTitle = new JLabel(sectionTitle);
        sTitle.setFont(AppTheme.FONT_SUBHEAD);
        sTitle.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(sTitle);
        card.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        for (int i = 0; i < rows.length; i++) {
            JPanel row = new JPanel(new BorderLayout(AppTheme.PAD_MD, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

            JLabel key = new JLabel(rows[i][0]);
            key.setFont(AppTheme.FONT_LABEL);
            key.setForeground(AppTheme.TEXT_SECONDARY);
            key.setPreferredSize(new Dimension(100, 24));
            row.add(key, BorderLayout.WEST);
            row.add(values[i], BorderLayout.CENTER);

            card.add(row);
            if (i < rows.length - 1) card.add(Box.createVerticalStrut(AppTheme.PAD_SM));
        }

        return card;
    }

    private JLabel infoValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_BODY);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        return lbl;
    }
}