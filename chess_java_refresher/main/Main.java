package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[]args){
        JFrame window = new JFrame("Simple Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes program when window closes
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        //add game panel to window
        gamePanel gp = new gamePanel();
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();
    }


}

