package com.whj;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.whj.Main.*;

class Function {

    private static ArrayList<Caret> nodeMapPosition = new ArrayList<>();
    private static ArrayList<Integer> nodeLevel = new ArrayList<>();
    private static ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<>();

    static void editOnLine() {
        NetworkFunction.editOL();
    }
    static void tryControl() throws IOException {
        NetworkFunction.tryControl(socket);
    }
    static void editOffLine() throws IOException {
        NetworkFunction.shutdownSocket();
    }

    static void toRender(String text) {
        RendererFunction.toRender(text);
    }

    static void saveHTML(String text) throws IOException {
        HTMLFunction.saveHTML(text);
    }

    static void setCatalog(String text) {
        CatalogFunction.setCatalog(text);
    }
    static void relocate(DefaultMutableTreeNode node) {
        CatalogFunction.relocate(node);
    }

    static boolean saveFile() {
        return FileFunction.saveFile();
    }
    static void openFile() {
        FileFunction.openFile();
    }
    static int confirmToSave() {
        return FileFunction.confirmToSave();
    }
}

class NetworkFunction {

    static void editOL() {
        try {
            socket = new Socket(Main.IP_ADDR, Main.PORT);
            Thread thread = new Thread(new SendAndRecvThread());
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class SendAndRecvThread implements Runnable {
        @Override
        public void run() {
            try {
                while (!socket.isClosed()) {
                    tryControl(socket);
                    if (menu.controlFlag.isSelected()) {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        while (menu.controlFlag.isSelected()) {
                            out.writeUTF("Submit New Document");
                            out.writeUTF(editor.getText());
                            Thread.sleep(5000);
                        }
                        out.writeUTF("Release Control");
                    }
                    while (!menu.controlFlag.isSelected()) {
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        String text = in.readUTF();
                        if (!text.equals("Free Now"))
                            editor.setText(text);
                        //System.out.println(text);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void tryControl(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF("Request Control");
        String result = in.readUTF();
        //System.out.println("result1 : " + result);

        if (result.equals("Enabled")) {
            menu.controlFlag.setSelected(true);
            JOptionPane.showMessageDialog(frame,
                    "Control success, remember to RELEASE!",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            menu.controlFlag.setSelected(false);
            JOptionPane.showMessageDialog(Main.frame,
                    "Sorry, the document is being edited. Try later.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static void shutdownSocket() throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("Disconnect");
        socket.close();
    }
}

class RendererFunction {
    static void toRender(String text) {
        renderer.setText(HTMLFunction.toHTML(text));
    }
}

class HTMLFunction {
    static String toHTML(String text) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    static void saveHTML(String text) throws IOException {
        String fname = "a";
        if (Main.fileName != null) {
            fname = Main.fileName;
            fname = fname.substring(0, fname.length() - 3);
        }
        fname = fname + ".html";
        FileWriter fw = new FileWriter(fname);
        fw.write(toHTML(text));
        fw.close();
    }
}

class CatalogFunction {

    private static ArrayList<Caret> nodeMapPosition = new ArrayList<>();
    private static ArrayList<Integer> nodeLevel = new ArrayList<>();
    private static ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<>();

    static void setCatalog(String text) {
        root.removeAllChildren();

        nodeLevel.clear();
        nodeList.clear();
        nodeMapPosition.clear();

        nodeLevel.add(0);
        nodeList.add(root);
        nodeMapPosition.add(editor.getCaret());

        Pattern p = Pattern.compile("#+\\s.+\\r\\n");
        Matcher m = p.matcher(text);
        while (m.find()) {
            String[] cata = text.substring(m.start(), m.end()).split(" ", 2);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(cata[1]);
            findFather(count(cata[0])).add(node);
            nodeList.add(node);
            nodeLevel.add(count(cata[0]));
        }

        Main.model.reload();
        expandAll(catalog, new TreePath(root), true);
    }

    static private int count(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '#')
                count ++;
        }
        return count;
    }

    static private DefaultMutableTreeNode findFather(int level) {
        int index = nodeList.size() - 1;
        while (nodeLevel.get(index) >= level)
            index --;
        return nodeList.get(index);
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand)
    {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    static void relocate(DefaultMutableTreeNode node) {

//        System.out.println("nodeName:" + node.toString());
//        editor.scrollToReference(node.toString());

//        JViewport viewport = editSp.getViewport();
//        viewport.setViewPosition(nodeMapPosition.get(getOrder(node)));

        editor.requestFocus();
        editor.setCaret(nodeMapPosition.get(getOrder(node)));

//        int index = getOrder(node);
//        editor.setSelectionStart(index);
//        editor.setSelectionEnd(index+1);
    }

    static private int getOrder(DefaultMutableTreeNode node) {
        int i = 0;
        while (i < nodeList.size()) {
            if (nodeList.get(i).equals(node))
                return i;
            ++i;
        }
        return -1;
    }
}

class FileFunction {
    static boolean saveFile() {
        if (!Main.hasPath) {
            FileDialog fd = new FileDialog(new Frame(), "Save File", FileDialog.SAVE);
            fd.setVisible(true);
            Main.fileName = fd.getFile();
            Main.filePath = fd.getDirectory();
        }
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(Main.filePath + Main.fileName + ".md ");
            bw = new BufferedWriter(fw);
            String textContent = editor.getText();
            bw.write(textContent);
            bw.flush();
            Main.hasPath = true;
            Main.isModified = false;
            Main.frame.setTitle("Markdown Editor OL" + " - " + Main.fileName);
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return false;
    }

    static void openFile() {
        FileDialog fd = new FileDialog(new Frame(), "Open File", FileDialog.LOAD);
        fd.setVisible(true);
        try {
            FileReader fr = new FileReader(fd.getDirectory() + fd.getFile());
            BufferedReader br = new BufferedReader(fr);
            editor.setText("");
            Main.fileName = fd.getFile();
            Main.filePath= fd.getDirectory();
            Main.frame.setTitle("Markdown Editor OL" + " - " + Main.fileName);
            String line;
            Document text = editor.getDocument();
            while ((line = br.readLine()) != null)
                text.insertString(text.getLength(), line + "\n", new SimpleAttributeSet());
            Main.isInitFile = false;
            Main.hasPath = true;
            Main.isModified = false;
        } catch (IOException | BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    static int confirmToSave() {
        //System.out.println("confirm : " +Main.isInitFile+Main.hasPath+Main.isModified);
        if (!Main.isInitFile && Main.isModified) {
            return 1 - JOptionPane.showConfirmDialog(Main.frame,
                    "Current file has not been saved, SAVE it or not?", "Warning",
                    JOptionPane.YES_NO_OPTION);
        }
        return 0;
    }
}