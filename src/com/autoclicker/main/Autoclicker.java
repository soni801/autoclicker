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
    JComboBox<String> timeIntervalUnit;
    
    // Event type
    JPanel eventTypePanel;
    JLabel eventTypeLabel;
    JCheckBox eventTypeMouseBox;
    JCheckBox eventTypeKeyboardBox;
    
    // Mouse event
    JPanel mouseEventPanel;
    JLabel mouseEventLabel;
    JComboBox<String> mouseEventAction;
    
    // Keyboard event
    JPanel keyboardEventPanel;
    JLabel keyboardEventLabel;
    JButton keyboardEventRecorder;
    
    public Autoclicker()
    {
        // Initialise objects
        frame = new JFrame();
        
        timeIntervalPanel = new JPanel();
        timeIntervalLabel = new JLabel("Time interval:");
        timeIntervalField = new JTextField(4);
        timeIntervalUnit = new JComboBox<>();
        
        eventTypePanel = new JPanel();
        eventTypeLabel = new JLabel("Event type:");
        eventTypeMouseBox = new JCheckBox("Mouse");
        eventTypeKeyboardBox = new JCheckBox("Keyboard");
        
        mouseEventPanel = new JPanel();
        mouseEventLabel = new JLabel("Mouse event:");
        mouseEventAction = new JComboBox<>();
    
        keyboardEventPanel = new JPanel();
        keyboardEventLabel = new JLabel("Keyboard event:");
        keyboardEventRecorder = new JButton("Record key");
        
        // Add items to ComboBox
        timeIntervalUnit.addItem("Hours");
        timeIntervalUnit.addItem("Minutes");
        timeIntervalUnit.addItem("Seconds");
        timeIntervalUnit.addItem("Milliseconds");
        timeIntervalUnit.addItem("Microseconds");
        timeIntervalUnit.addItem("Nanoseconds");
        
        mouseEventAction.addItem("Left Click");
        mouseEventAction.addItem("Middle Click");
        mouseEventAction.addItem("Right Click");
        
        // Add objects to panels
        timeIntervalPanel.add(timeIntervalLabel);
        timeIntervalPanel.add(timeIntervalField);
        timeIntervalPanel.add(timeIntervalUnit);
        
        eventTypePanel.add(eventTypeLabel);
        eventTypePanel.add(eventTypeMouseBox);
        eventTypePanel.add(eventTypeKeyboardBox);
        
        mouseEventPanel.add(mouseEventLabel);
        mouseEventPanel.add(mouseEventAction);
        
        keyboardEventPanel.add(keyboardEventLabel);
        keyboardEventPanel.add(keyboardEventRecorder);
        
        // Add panels to frame
        frame.add(timeIntervalPanel);
        frame.add(eventTypePanel);
        frame.add(mouseEventPanel);
        frame.add(keyboardEventPanel);
        
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