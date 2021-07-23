package com.autoclicker.main;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Soni
 */
public class Autoclicker implements NativeKeyListener
{
    boolean running;
    
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
    
    // Status
    JPanel statusPanel;
    ImageIcon active;
    ImageIcon inactive;
    JLabel statusImageLabel;
    JLabel statusTextLabel;
    
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
        
        statusPanel = new JPanel();
        active = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("active.png")));
        inactive = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("inactive.png")));
        statusImageLabel = new JLabel(inactive);
        statusTextLabel = new JLabel("Press F6 to start");
        
        // Add items to ComboBox
        timeIntervalUnit.addItem("Days");
        timeIntervalUnit.addItem("Hours");
        timeIntervalUnit.addItem("Minutes");
        timeIntervalUnit.addItem("Seconds");
        timeIntervalUnit.addItem("Milliseconds");
        timeIntervalUnit.addItem("Microseconds");
        timeIntervalUnit.addItem("Nanoseconds");
        
        mouseEventAction.addItem("Left Click");
        mouseEventAction.addItem("Middle Click");
        mouseEventAction.addItem("Right Click");
        
        // Add item listeners
        eventTypeMouseBox.addItemListener(e -> mouseEventPanel.setVisible(eventTypeMouseBox.isSelected()));
        eventTypeKeyboardBox.addItemListener(e -> keyboardEventPanel.setVisible(eventTypeKeyboardBox.isSelected()));
        
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
        
        statusPanel.add(statusImageLabel);
        statusPanel.add(statusTextLabel);
        
        // Add panels to frame
        frame.add(timeIntervalPanel);
        frame.add(eventTypePanel);
        frame.add(mouseEventPanel);
        frame.add(keyboardEventPanel);
        frame.add(statusPanel);
        
        // Set visibility
        mouseEventPanel.setVisible(false);
        keyboardEventPanel.setVisible(false);
        
        // Initialise frame
        frame.setTitle("Soni's Autoclicker");
        frame.setSize(320, 230);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setVisible(true);
        
        // Set JNativeHook logging
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        // Check for correct key press
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_F6)
        {
            if (running)
            {
                // Disable autoclicker
                running = false;
                statusImageLabel.setIcon(inactive);
            }
            else
            {
                // Enable autoclicker
                running = true;
                statusImageLabel.setIcon(active);
                
                // Create new thread to handle autoclicking
                new Thread(() ->
                {
                    try
                    {
                        // Create robot immediately when thread starts
                        Robot robot = new Robot();
                        
                        // Loop while autoclicker is running
                        while (running)
                        {
                            // Check to do mouse action
                            if (eventTypeMouseBox.isSelected())
                            {
                                // Initialise button to 0 to avoid having default branch in switch statement
                                int button = 0;
        
                                // Check which mouse action to do
                                switch (Objects.requireNonNull(mouseEventAction.getSelectedItem()).toString())
                                {
                                    case "Left Click" -> button = InputEvent.BUTTON1_DOWN_MASK;
                                    case "Middle Click" -> button = InputEvent.BUTTON2_DOWN_MASK;
                                    case "Right Click" -> button = InputEvent.BUTTON3_DOWN_MASK;
                                }
        
                                // Execute mouse action
                                robot.mousePress(button);
                                robot.mouseRelease(button);
                            }
    
                            // Check to do keyboard action
                            if (eventTypeKeyboardBox.isSelected())
                            {
        
                            }
                            
                            // Sleep for the desired amount of time
                            switch (Objects.requireNonNull(timeIntervalUnit.getSelectedItem()).toString())
                            {
                                case "Days" -> TimeUnit.DAYS.sleep(Long.parseLong(timeIntervalField.getText()));
                                case "Hours" -> TimeUnit.HOURS.sleep(Long.parseLong(timeIntervalField.getText()));
                                case "Minutes" -> TimeUnit.MINUTES.sleep(Long.parseLong(timeIntervalField.getText()));
                                case "Seconds" -> TimeUnit.SECONDS.sleep(Long.parseLong(timeIntervalField.getText()));
                                case "Milliseconds" -> TimeUnit.MILLISECONDS.sleep(Long.parseLong(timeIntervalField.getText()));
                                case "Microseconds" -> TimeUnit.MICROSECONDS.sleep(Long.parseLong(timeIntervalField.getText()));
                                case "Nanoseconds" -> TimeUnit.NANOSECONDS.sleep(Long.parseLong(timeIntervalField.getText()));
                            }
                        }
                    }
                    catch (AWTException | InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    public static void main(String[] args)
    {
        try { GlobalScreen.registerNativeHook(); }
        catch (NativeHookException e) { e.printStackTrace(); }
        
        GlobalScreen.addNativeKeyListener(new Autoclicker());
    }
}