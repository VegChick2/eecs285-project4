package eecs285.proj4.qiaotian;

import javax.swing.JFrame;

public class SimpleBudgetClient
{
    public static void main(String[] args)
    {
        SimpleBudgetFrame sbFrame;

        sbFrame = new SimpleBudgetFrame("Simple Budget", "budgetData.txt");
        sbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}