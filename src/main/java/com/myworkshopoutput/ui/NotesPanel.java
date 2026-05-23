package com.myworkshopoutput.ui;

import com.myworkshopoutput.model.Note;
import com.myworkshopoutput.model.User;
import com.myworkshopoutput.service.NoteService;
import com.myworkshopoutput.util.AppTheme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Notes view — list all notes, add new ones, delete selected.
 * Fetches from Supabase on each refresh.
 */
public class NotesPanel extends JPanel {

    private final AppFrame      app;
    private User                currentUser;

    // Table
    private DefaultTableModel   tableModel;
    private JTable              table;
    private List<Note>          notes;

    // Form
    private JTextField          titleField;
    private JTextArea           contentArea;
    private JButton             saveButton;
    private JLabel              statusLabel;

    public NotesPanel(AppFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_PAGE);
        buildUI();
    }

    private void buildUI() {
        add(new NavBar(app, AppFrame.VIEW_NOTES), BorderLayout.NORTH);

        // ── Split: left = list, right = form ─────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setDividerSize(1);
        split.setBorder(null);

        split.setLeftComponent(buildNotesList());
        split.setRightComponent(buildNoteForm());

        add(split, BorderLayout.CENTER);
    }

    // ── Notes list ────────────────────────────────────────────────────────

    private JPanel buildNotesList() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_CARD);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(AppTheme.PAD_MD, AppTheme.PAD_MD, AppTheme.PAD_MD, AppTheme.PAD_MD)
        ));

        JLabel title = new JLabel("My Notes");
        title.setFont(AppTheme.FONT_SUBHEAD);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton deleteBtn = AppTheme.secondaryButton("Delete");
        deleteBtn.setFont(AppTheme.FONT_SMALL);
        deleteBtn.setForeground(AppTheme.BG_DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());
        header.add(deleteBtn, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Title", "Created"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(AppTheme.BG_CARD);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setFont(AppTheme.FONT_LABEL);
        table.getTableHeader().setBackground(AppTheme.BG_MUTED);
        table.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        // Constrain column widths
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setMinWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        // Empty state label
        JLabel emptyLabel = new JLabel("No notes yet. Add one →", SwingConstants.CENTER);
        emptyLabel.setFont(AppTheme.FONT_BODY);
        emptyLabel.setForeground(AppTheme.TEXT_SECONDARY);
        panel.add(emptyLabel, BorderLayout.SOUTH);

        return panel;
    }

    // ── Note form ─────────────────────────────────────────────────────────

    private JPanel buildNoteForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.BG_PAGE);
        panel.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PAD_LG, AppTheme.PAD_LG, AppTheme.PAD_LG, AppTheme.PAD_LG));

        JLabel formTitle = new JLabel("Add a Note");
        formTitle.setFont(AppTheme.FONT_SUBHEAD);
        formTitle.setForeground(AppTheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        // Title field
        JLabel titleLbl = new JLabel("Title");
        titleLbl.setFont(AppTheme.FONT_LABEL);
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLbl);
        panel.add(Box.createVerticalStrut(4));

        titleField = AppTheme.styledField(0);
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        // Content area
        JLabel contentLbl = new JLabel("Content");
        contentLbl.setFont(AppTheme.FONT_LABEL);
        contentLbl.setForeground(AppTheme.TEXT_PRIMARY);
        contentLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(contentLbl);
        panel.add(Box.createVerticalStrut(4));

        contentArea = new JTextArea(6, 20);
        contentArea.setFont(AppTheme.FONT_BODY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setBorder(null);
        contentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        panel.add(contentScroll);
        panel.add(Box.createVerticalStrut(AppTheme.PAD_MD));

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.FONT_SMALL);
        statusLabel.setForeground(AppTheme.BG_DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(AppTheme.PAD_SM));

        // Save button
        saveButton = AppTheme.primaryButton("Save Note");
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> saveNote());
        panel.add(saveButton);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // ── Actions ───────────────────────────────────────────────────────────

    public void refresh(User user) {
        this.currentUser = user;
        loadNotes();
    }

    private void loadNotes() {
        tableModel.setRowCount(0);
        statusLabel.setText("Loading…");
        statusLabel.setForeground(AppTheme.TEXT_SECONDARY);

        new Thread(() -> {
            try {
                List<Note> loaded = NoteService.getNotes(currentUser.getAccessToken());
                SwingUtilities.invokeLater(() -> {
                    notes = loaded;
                    for (Note n : notes) {
                        String date = n.getCreatedAt().length() >= 10
                                ? n.getCreatedAt().substring(0, 10)
                                : n.getCreatedAt();
                        tableModel.addRow(new Object[]{n.getTitle(), date});
                    }
                    statusLabel.setText(" ");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Could not load notes: " + ex.getMessage()));
            }
        }).start();
    }

    private void saveNote() {
        String title   = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            statusLabel.setText("Title is required.");
            statusLabel.setForeground(AppTheme.BG_DANGER);
            return;
        }

        saveButton.setEnabled(false);
        statusLabel.setText("Saving…");
        statusLabel.setForeground(AppTheme.TEXT_SECONDARY);

        new Thread(() -> {
            try {
                Note newNote = NoteService.insertNote(title, content, currentUser.getAccessToken());
                SwingUtilities.invokeLater(() -> {
                    tableModel.insertRow(0, new Object[]{newNote.getTitle(), "today"});
                    if (notes != null) notes.add(0, newNote);
                    titleField.setText("");
                    contentArea.setText("");
                    statusLabel.setText("Note saved!");
                    statusLabel.setForeground(AppTheme.BG_SUCCESS);
                    saveButton.setEnabled(true);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(ex.getMessage());
                    statusLabel.setForeground(AppTheme.BG_DANGER);
                    saveButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || notes == null || row >= notes.size()) {
            JOptionPane.showMessageDialog(this, "Select a note to delete.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Note note = notes.get(row);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + note.getTitle() + "\"?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try {
                NoteService.deleteNote(note.getId(), currentUser.getAccessToken());
                SwingUtilities.invokeLater(() -> {
                    tableModel.removeRow(row);
                    notes.remove(row);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}