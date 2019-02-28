package com.whj;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.net.Socket;

public class Main {

    static JFrame frame = new JFrame();

    static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Catalog");
    static DefaultTreeModel model = new DefaultTreeModel(root);
    static String fileName = "Untitled";
    static String filePath = null;
    static boolean isInitFile = true;
    static boolean hasPath = false;
    static boolean isModified = false;

    static Menu menu = new Menu();
    static Catalog catalog = new Catalog(model);
    static Editor editor = new Editor();
    static JScrollPane editSp = new JScrollPane();
    static Renderer renderer = new Renderer();

    static Socket socket;
    static final int PORT = 7777;
    static String IP_ADDR = "127.0.0.1";

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        new GUI();
    }

}