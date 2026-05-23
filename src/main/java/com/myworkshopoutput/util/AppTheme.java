package com.myworkshopoutput.util;

import javax.swing.*;
import java.awt.*;

public class AppTheme {

    public static final Color BG_PAGE       = new Color(243, 179, 231);
    public static final Color BG_CARD       = Color.WHITE;
    public static final Color BG_PRIMARY    = new Color(79, 70, 229);
    public static final Color BG_PRIMARY_HV = new Color(67, 56, 202);
    public static final Color BG_DANGER     = new Color(220, 38,  38);
    public static final Color BG_SUCCESS    = new Color(22,  163, 74);
    public static final Color BG_MUTED      = new Color(241, 241, 245);

    public static final Color TEXT_PRIMARY   = new Color(17,  24,  39);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color TEXT_ON_DARK   = Color.WHITE;
    public static final Color BORDER         = new Color(229, 231, 235);

    public static final Font FONT_HEADING  = new Font("SansSerif", Font.BOLD,   22);
    public static final Font FONT_SUBHEAD  = new Font("SansSerif", Font.BOLD,   15);
    public static final Font FONT_BODY     = new Font("SansSerif", Font.PLAIN,  13);
    public static final Font FONT_SMALL    = new Font("SansSerif", Font.PLAIN,  11);
    public static final Font FONT_LABEL    = new Font("SansSerif", Font.BOLD,   12);
    public static final Font FONT_MONO     = new Font("Monospaced", Font.PLAIN, 12);

    public static final int PAD_SM = 8;
    public static final int PAD_MD = 16;
    public static final int PAD_LG = 24;
    public static final int PAD_XL = 40;

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background", BG_PAGE);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("PasswordField.font", FONT_BODY);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Button.font", FONT_BODY);
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG_PRIMARY);
        btn.setForeground(TEXT_ON_DARK);
        btn.setFont(FONT_SUBHEAD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_PRIMARY_HV);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_PRIMARY);
            }
        });
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG_MUTED);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField styledField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }
}