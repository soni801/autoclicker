package com.autoclicker.main;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.keyboard.SwingKeyAdapter;

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
public class Autoclicker extends SwingKeyAdapter implements NativeKeyListener
{
    boolean running;
    boolean recording;
    int keyboardEvent;
    
    // Frame
    JFrame frame;
    
    // Time interval
    JPanel timeIntervalPanel;
    JLabel timeIntervalLabel;
    JTextField timeIntervalField;
    JComboBox<String> timeIntervalUnit;
    
    // Jitter amount
    JPanel jitterAmountPanel;
    JCheckBox jitterAmountCheckBox;
    JTextField jitterAmountField;
    JComboBox<String> jitterAmountUnit;
    
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
    
        jitterAmountPanel = new JPanel();
        jitterAmountCheckBox = new JCheckBox("Jitter");
        jitterAmountField = new JTextField(4);
        jitterAmountUnit = new JComboBox<>();
        
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
        statusTextLabel = new JLabel("Not running. Press F6 to start");
        
        // Add items to ComboBox
        timeIntervalUnit.addItem("Days");
        timeIntervalUnit.addItem("Hours");
        timeIntervalUnit.addItem("Minutes");
        timeIntervalUnit.addItem("Seconds");
        timeIntervalUnit.addItem("Milliseconds");
        timeIntervalUnit.addItem("Microseconds");
        timeIntervalUnit.addItem("Nanoseconds");
    
        jitterAmountUnit.addItem("Days");
        jitterAmountUnit.addItem("Hours");
        jitterAmountUnit.addItem("Minutes");
        jitterAmountUnit.addItem("Seconds");
        jitterAmountUnit.addItem("Milliseconds");
        jitterAmountUnit.addItem("Microseconds");
        jitterAmountUnit.addItem("Nanoseconds");
        
        mouseEventAction.addItem("Left Click");
        mouseEventAction.addItem("Middle Click");
        mouseEventAction.addItem("Right Click");
        
        // Add listeners
        jitterAmountCheckBox.addItemListener(e ->
        {
            jitterAmountField.setEnabled(jitterAmountCheckBox.isSelected());
            jitterAmountUnit.setEnabled(jitterAmountCheckBox.isSelected());
        });
        
        eventTypeMouseBox.addItemListener(e ->
        {
            if(eventTypeMouseBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40);
            
            mouseEventPanel.setVisible(eventTypeMouseBox.isSelected());
        });
        
        eventTypeKeyboardBox.addItemListener(e ->
        {
            if(eventTypeKeyboardBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40);
            
            keyboardEventPanel.setVisible(eventTypeKeyboardBox.isSelected());
        });
        
        keyboardEventRecorder.addActionListener(e ->
        {
            recording = true;
            keyboardEventRecorder.setText("Recording...");
        });
        
        // Set abstract requirements
        keyboardEventRecorder.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        
        // Set tooltips
        timeIntervalPanel.setToolTipText("Amount of time to wait between each action");
        jitterAmountPanel.setToolTipText("Timing inconsistency (jitter)");
        eventTypePanel.setToolTipText("Which events to execute at the chosen time interval");
        mouseEventPanel.setToolTipText("Which mouse event to execute per action");
        keyboardEventPanel.setToolTipText("Which keyboard event to execute per action");
        keyboardEventRecorder.setToolTipText("<html>Press to start recording key inputs.<br>The next key input will be set as the key event.");
        
        // Add objects to panels
        timeIntervalPanel.add(timeIntervalLabel);
        timeIntervalPanel.add(timeIntervalField);
        timeIntervalPanel.add(timeIntervalUnit);
        
        jitterAmountPanel.add(jitterAmountCheckBox);
        jitterAmountPanel.add(jitterAmountField);
        jitterAmountPanel.add(jitterAmountUnit);
        
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
        frame.add(jitterAmountPanel);
        frame.add(eventTypePanel);
        frame.add(mouseEventPanel);
        frame.add(keyboardEventPanel);
        frame.add(statusPanel);
        
        // Set default combo box values
        timeIntervalUnit.setSelectedItem("Milliseconds");
        jitterAmountUnit.setSelectedItem("Milliseconds");
        
        // Set enabled & visibility
        jitterAmountField.setEnabled(false);
        jitterAmountUnit.setEnabled(false);
        mouseEventPanel.setVisible(false);
        keyboardEventPanel.setVisible(false);
        
        // Initialise frame
        frame.setTitle("Soni's Autoclicker");
        frame.setSize(320, 200);
        frame.setResizable(false);
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
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) { }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        // Check that the application is not recording for a new key action
        if (recording)
        {
            recording = false;
            keyboardEventRecorder.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
            keyboardEvent = getJavaKeyEvent(nativeKeyEvent).getKeyCode();
        }
        else
        {
            // Check for correct key press to enable autoclicker
            if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_F6)
            {
                if (running)
                {
                    // Disable autoclicker
                    running = false;
                    statusImageLabel.setIcon(inactive);
                    statusTextLabel.setText("Not running. Press F6 to start");
                }
                else
                {
                    // Enable autoclicker
                    running = true;
                    statusImageLabel.setIcon(active);
                    statusTextLabel.setText("Running. Press F6 to stop");
            
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
                                    robot.keyPress(keyboardEvent);
                                    robot.keyRelease(keyboardEvent);
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
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) { }
    
    public static void main(String[] args)
    {
        try { GlobalScreen.registerNativeHook(); }
        catch (NativeHookException e) { e.printStackTrace(); }
        
        GlobalScreen.addNativeKeyListener(new Autoclicker());
    }
}