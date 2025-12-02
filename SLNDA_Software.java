/*======================== 
Library GUI Interface for SLNDA Software
    1.javax.swing
    2.java.awt 
    3.awt.event
/*======================== */

import javax.swing.*; // * = Anything from javax.swing
import java.awt.*; // * = Anything from java.awt
import java.awt.event.*; // * = Anything from java.awt.event

/*======================== 
Library Sound Interface, Equalizer Recording for SLNDA Software
    1.javax.sound.sampled  
    2.java.util.Timer
    3.java.util.TimerTask
    4.Tarsos DSP
/*======================== */

import javax.sound.sampled.*;
import java.util.Timer;
import java.util.TimerTask;

/*======================== 
Library Files Management for SLNDA Software
    1.javax.io
/*======================== */

import java.io.*;



public class SLNDA_Software {
    public static void main(String[] args) {
        // Set Frame 
        JFrame SLNDA_Frame = new JFrame("SLNDA Software");
        SLNDA_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SLNDA_Frame.setSize(1920, 1080);
        SLNDA_Frame.setVisible(true);

        // Set Title Bar for import icon
        SLNDA_Frame.setTitle ("SLNDA Software");
        SLNDA_Frame.setIconImage (Toolkit.getDefaultToolkit().getImage(""));


        //Panel for Controls
        JPanel SLNDA_Control_Panel =  new JPanel();
        SLNDA_Control_Panel.setLayout(new GridLayout(2, 1));

        //Creating an instance SLNDA Live form the main method
        SLNDA_Software outlerClassInstance = new SLNDA_Software(); 
        SLNDA_Live_Performance livePerformance = new SLNDA_Live_Performance();
        livePerformance.startLivePerformace();

    }

        public static class SLNDA_Live_Performance {
        // Live Performance Function 
        public void startLivePerformace() {
            // Instantint New
            SLNDA_Live_Performance performance = new SLNDA_Live_Performance();
            // Using
            performance.performAction();
        }

        public void performAction() {
            // Code for Live Performance Action
            System.out.println("Live Performance is Running...");
        }
    }


}