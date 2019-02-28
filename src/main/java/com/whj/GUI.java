package com.whj;

import javax.swing.*;
import java.awt.*;

import static com.whj.Main.*;

class GUI {

    GUI() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        GridBagLayout gridBagLayout = new GridBagLayout();
        frame.setTitle("Markdown Editor OL" + " - " + Main.fileName);
        frame.setName("mdEditorOL");
        frame.setLayout(gridBagLayout);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200,800);

        frame.setJMenuBar(menu);

        JPanel sideArea = new JPanel();
        JScrollPane catalogSp = new JScrollPane();
        JPanel mainArea = new JPanel();
        JScrollPane renderSp = new JScrollPane();

        catalogSp.getViewport().add(catalog);
        editSp.getViewport().add(editor);
        renderSp.getViewport().add(renderer);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3,10,3,0);
        c.weighty = 1;

        c.gridx = 0;
        c.weightx = 0.1;
        gridBagLayout.setConstraints(sideArea, c);
        sideArea.setLayout(new GridLayout(1,1));
        //sideArea.setLayout(new BorderLayout());
        sideArea.add(catalogSp);

        c.gridx = 1;
        c.weightx = 1;
        gridBagLayout.setConstraints(mainArea, c);
        mainArea.setLayout(new GridLayout(1,2,5,5));
        mainArea.add(editSp);
        mainArea.add(renderSp);

        frame.getContentPane().add(sideArea);
        frame.getContentPane().add(mainArea);

        Toolkit tk=Toolkit.getDefaultToolkit();
        Image img = tk.getImage(".\\src\\main\\resources\\铅笔.png");
        frame.setIconImage(img);
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);

//        ImageIcon icon1 = new ImageIcon(".\\src\\main\\resources\\icon_arrow_right.png");
//        ImageIcon icon2 = new ImageIcon(".\\src\\main\\resources\\expanded.gif");
//        Icon icon3 = new ImageIcon("open.gif");
//        Icon icon4 = new ImageIcon("close.gif");
//        Icon icon5 = new ImageIcon("E:\\桌面\\Java\\lab4\\src\\main\\resources\\icon_fenlei.png");
//        UIManager.put("Tree.collapsedIcon", icon1);
//        UIManager.put("Tree.expandedIcon", icon2);
//        UIManager.put("Tree.openIcon", icon3);
//        UIManager.put("Tree.closedIcon", icon4);
//        UIManager.put("Tree.leafIcon", icon5);

        frame.setVisible(true);
    }

}
