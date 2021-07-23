package com.autoclicker.main;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;

/**
 * @author Soni
 */
public class Autoclicker implements NativeKeyListener
{
    // Frame
    JFrame frame;
    
    // Time interval
    JPanel timeIntervalPanel;
    JLabel timeIntervalLabel;
    JTextField timeIntervalField;
    JComboBox<String> timeIntervalUnitSelector;
    
    public Autoclicker()
    {
        // Initialise objects
        frame = new JFrame();
        
        timeIntervalPanel = new JPanel();
        timeIntervalLabel = new JLabel("Time interval:");
        timeIntervalField = new JTextField(4);
        timeIntervalUnitSelector = new JComboBox<>();
        
        // Add items to ComboBox
        timeIntervalUnitSelector.addItem("Hours");
        timeIntervalUnitSelector.addItem("Minutes");
        timeIntervalUnitSelector.addItem("Seconds");
        timeIntervalUnitSelector.addItem("Milliseconds");
        timeIntervalUnitSelector.addItem("Microseconds");
        timeIntervalUnitSelector.addItem("Nanoseconds");
        
        // Add objects to panels
        timeIntervalPanel.add(timeIntervalLabel);
        timeIntervalPanel.add(timeIntervalField);
        timeIntervalPanel.add(timeIntervalUnitSelector);
        
        // Add panels to frame
        frame.add(timeIntervalPanel);
        
        // Initialise frame
        frame.setTitle("Soni's Autoclicker");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setVisible(true);
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    public static void main(String[] args)
    {
        new Autoclicker();
    }
}