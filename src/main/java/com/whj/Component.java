package com.whj;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static com.whj.Main.*;

class Menu extends JMenuBar {

    private editOLItem editFlag = new editOLItem("Edit OnLine");
    controlItem controlFlag = new controlItem("Control");

    Menu() {
        super();
        JMenu file = new JMenu("File");
        JMenuItem fileItem[] = {
                new newItem("New"),
                new openItem("Open"),
                new saveItem("Save"),
                new saveAsItem("Save as"),
                new exitItem("Exit"),
        };
        this.add(file);
        for (JMenuItem aFileItem : fileItem)
            file.add(aFileItem);

        JMenu html = new JMenu("HTML");
        html.add(new htmlItem("Output HTML"));
        this.add(html);

        JMenu editOL = new JMenu("Advanced");
        editOL.add(new editOLItem("Edit Online"));
        editOL.add(controlFlag);
        this.add(editOL);
    }

    class newItem extends JMenuItem implements ActionListener {
        newItem(String s) {
            super(s);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Function.confirmToSave() == 1)
                Function.saveFile();

            editor.setText("");
            Main.fileName = "Untitled";
            Main.filePath = null;
            isInitFile = true;
            isModified = false;
            hasPath = false;
            Main.frame.setTitle("Markdown Editor OL" + " - " + Main.fileName);
        }
    }

    class openItem extends JMenuItem implements ActionListener {
        openItem(String s) {
            super(s);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Function.confirmToSave() == 1)
                hasPath = Function.saveFile();
            Function.openFile();
        }
    }

    class saveItem extends JMenuItem implements ActionListener {
        saveItem(String s) {
            super(s);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            hasPath = Function.saveFile();
        }
    }

    class saveAsItem extends JMenuItem implements ActionListener {
        saveAsItem(String s) {
            super(s);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            hasPath = false;
            hasPath = Function.saveFile();
        }
    }

    class exitItem extends JMenuItem implements ActionListener {
        exitItem(String s) {
            super(s);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Function.confirmToSave() == 1)
                hasPath = Function.saveFile();
            System.exit(0);
        }
    }

    class htmlItem extends JMenuItem implements ActionListener {
        htmlItem(String s) {
            super(s);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Function.saveHTML(editor.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    static class editOLItem extends JCheckBoxMenuItem implements ActionListener {
        editOLItem(String s) {
            super(s, false);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!menu.editFlag.isSelected()) {
                menu.editFlag.setSelected(true);
                Function.editOnLine();
                //System.out.println(menu.editFlag.isSelected());
            }
            else {
                try {
                    Function.editOffLine();
                    menu.controlFlag.setSelected(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static class controlItem  extends JCheckBoxMenuItem implements ActionListener {
        controlItem(String s) {
            super(s,false);
            addActionListener(this);
        }
        @Override
        public void actionPerformed(ActionEvent e) { }
    }
}

class Catalog extends JTree {

    Catalog(DefaultTreeModel model) {
        super(model);
        this.setShowsRootHandles(true);
        this.setEditable(false);
        this.setCellRenderer(new MyDefaultTreeCellRenderer());

        this.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) { }

        });
        this.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) { }
            @Override
            public void treeCollapsed(TreeExpansionEvent event) { }
        });

//        addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//                DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
//                if(node == null)
//                    return;
//                //changeSelectedTabbedPane(tabbedPane, paneNameIndexMap.get(node.toString()));
//                //System.out.println(node.toString());
//                Function.relocate(node);
//            }
//        });

    }

    class MyDefaultTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value,
                boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            setText(value.toString());

            if (sel) {
                setForeground(getTextSelectionColor());
            } else {
                setForeground(getTextNonSelectionColor());
            }

            ImageIcon rootIcon = new ImageIcon(".\\src\\main\\resources\\icon_xpp.png");
            ImageIcon expandedIcon = new ImageIcon(".\\src\\main\\resources\\icon_arrow_down.png");
            ImageIcon collapsedIcon = new ImageIcon(".\\src\\main\\resources\\icon_arrow_RIGHT.png");
            ImageIcon childIcon = new ImageIcon(".\\src\\main\\resources\\icon_fenlei.png");

            DefaultMutableTreeNode nodeType = (DefaultMutableTreeNode) value;
            if (tree.getModel().getRoot().equals(nodeType)) {
                setIcon(rootIcon);
            } else if (nodeType.getChildCount() > 0) {
                if (expanded)
                    setIcon(expandedIcon);
                else
                    setIcon(collapsedIcon);
            } else {
                setIcon(childIcon);
            }

            return this;
        }

    }

}

class Editor extends JEditorPane implements DocumentListener {

    Editor() {
        super();
        this.setFont(new Font("console", Font.PLAIN,14));
        this.getDocument().addDocumentListener(this);
    }

    private void handle() {
        isInitFile = false;
        isModified = true;
        Function.toRender(this.getText());   //text end with \r\n !!!!
        Function.setCatalog(this.getText());
//        try {
//            Rectangle r = modelToView(getCaretPosition());
//            Point position = new Point(r.x, r.y);
//            System.out.println(" " + r.x + " " + r.y);
//            Function.catalogue(this.getText(), position);
//        } catch (BadLocationException e1) {
//            e1.printStackTrace();
//        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        handle();
    }
    @Override
    public void removeUpdate(DocumentEvent e) {
        handle();
    }
    @Override
    public void changedUpdate(DocumentEvent e) {
        handle();
    }
}

class Renderer extends JEditorPane {

    Renderer() {
        super();
        setEditorKit(new HTMLEditorKit());
        setContentType("text/html; charset=utf-8");
        setEditable(false);
    }
}