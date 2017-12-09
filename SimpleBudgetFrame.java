package eecs285.proj4.qiaotian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static java.lang.System.exit;

public class SimpleBudgetFrame extends JFrame {


    private  JMenuItem saveItem;
    private  JMenuItem exitItem;

    private JComboBox catCombo;


    private JTextField merchant;
    private JTextField amount;

    private JLabel balance;

    private JButton addtranscation;

    private  ClientServerSocket theClient;



    public SimpleBudgetFrame(String title, String filename) {
        super(title);
        JButton equalsButton;
        theClient = new ClientServerSocket("127.0.0.1", 45000);
        theClient.startClient();
        theClient.sendString("r"+"%"+filename+"#"); //read
        assert (theClient.recvString().equals("%read#"));
        theClient.sendString("l");
        String list=theClient.recvString();
        Vector<String> catlist=getargs(list);

        String[] calistarray = new String[catlist.size()];
        catlist.copyInto(calistarray);




        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        saveItem = new JMenuItem("Open Image");
        saveItem.addActionListener(new myActionListener());
        exitItem = new JMenuItem("Quit Program");
        exitItem.addActionListener(new myActionListener());
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);//menu



        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.PAGE_AXIS));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.LINE_AXIS));
        p1.add(new JLabel("Category:"));
        catCombo = new JComboBox(calistarray);
        catCombo.addActionListener(new myActionListener());
        p1.add(catCombo);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
        p2.add(new JLabel("Merchant:"));
        merchant=new JTextField();
        p2.add(merchant);

        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.LINE_AXIS));
        p3.add(new JLabel("Amount:$"));
        amount=new JTextField();
        p3.add(amount);

        JPanel p4 = new JPanel();
        p4.setLayout(new BoxLayout(p4, BoxLayout.LINE_AXIS));
        balance=new JLabel("Balance:$N/A");
        p4.add(balance);

        JPanel p5 = new JPanel();
        p5.setLayout(new BoxLayout(p5, BoxLayout.LINE_AXIS));
        addtranscation=new JButton("Add Transaction");
        addtranscation.addActionListener(new myActionListener());
        p5.add(addtranscation);

        framePanel.add(p1);
        framePanel.add(p2);
        framePanel.add(p3);
        framePanel.add(p4);
        framePanel.add(p5);


        setLayout(new GridLayout(1,1));

        add(framePanel);




        pack();
        setVisible(true);
    }

    public class myActionListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {

           Object source=ae.getSource();

            if(source==saveItem)
            {
                theClient.sendString("s");
                assert(theClient.recvString().equals("%save#"));

            }else if (source==exitItem)
            {
                theClient.sendString("e");
                assert(theClient.recvString().equals("%exit#"));
                exit(0);

            }
            else if(source==catCombo)
            {
                theClient.sendString("g%"+(String)catCombo.getSelectedItem()+"#");
                balance.setText("Balance:$"+getargs(theClient.recvString()).lastElement());

            }else if(source==addtranscation)
            {
                theClient.sendString(
                        "t%"+(String) catCombo.getSelectedItem()+"#"
                        +"%"+merchant.getText()+"#"
                        +"%"+amount.getText()+"#");
                balance.setText("Balance:$"+getargs(theClient.recvString()).lastElement());

            }




        }
    }

    private Vector<String> getargs(String in) {
        int argindex = 0;
        int i = 0;
        Vector<String> args=new Vector<String>();

        String now="";

        while (i<in.length()) {
            if (in.charAt(i) == '%') {
                args.add("");
                now=args.lastElement();

                i++;
            } else if (in.charAt(i) == '#') {
                i++;
            } else {
                i++;

                now+= now + in.charAt(i);
            }
        }//grt arguments

        return args;
    }
}