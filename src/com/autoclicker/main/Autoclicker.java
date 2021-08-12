package com.autoclicker.main;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.keyboard.SwingKeyAdapter;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Soni
 */
public class Autoclicker extends SwingKeyAdapter implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener
{
    boolean running;
    int keyboardEvent;
    long waitTime;
    
    boolean recordingMouse;
    boolean recordingKeyboard;
    
    // Keystrokes
    KeyStroke optionsKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_DOWN_MASK);
    KeyStroke exitKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);
    
    // Frame & menu bar
    JFrame frame;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem optionsMenuItem;
    JMenuItem exitMenuItem;
    
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
    
    // Mouse position
    JPanel mousePositionPanel;
    JCheckBox mousePositionCheckBox;
    
    // Mouse position write
    JPanel mousePositionWritePanel;
    JLabel mousePositionWriteXLabel;
    JTextField mousePositionWriteX;
    JLabel mousePositionWriteYLabel;
    JTextField mousePositionWriteY;
    
    // Mouse position recorder
    JPanel mousePositionRecorderPanel;
    JLabel mousePositionRecorderLabel;
    JButton mousePositionRecorderRecorder;
    
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
        menuBar = new JMenuBar();
        menu = new JMenu("Autoclicker");
        
        timeIntervalPanel = new JPanel();
        timeIntervalLabel = new JLabel("Time interval:");
        timeIntervalField = new JTextField(3);
        timeIntervalUnit = new JComboBox<>();
    
        jitterAmountPanel = new JPanel();
        jitterAmountCheckBox = new JCheckBox("Jitter");
        jitterAmountField = new JTextField(3);
        jitterAmountUnit = new JComboBox<>();
        
        eventTypePanel = new JPanel();
        eventTypeLabel = new JLabel("Event type:");
        eventTypeMouseBox = new JCheckBox("Mouse");
        eventTypeKeyboardBox = new JCheckBox("Keyboard");
        
        mouseEventPanel = new JPanel();
        mouseEventLabel = new JLabel("Mouse event:");
        mouseEventAction = new JComboBox<>();
    
        mousePositionPanel = new JPanel();
        mousePositionCheckBox = new JCheckBox("Specific mouse position");
        
        mousePositionWritePanel = new JPanel();
        mousePositionWriteXLabel = new JLabel("Write a mouse position: X:");
        mousePositionWriteX = new JTextField(3);
        mousePositionWriteYLabel = new JLabel("Y:");
        mousePositionWriteY = new JTextField(3);
        
        mousePositionRecorderPanel = new JPanel();
        mousePositionRecorderLabel = new JLabel("Or record a mouse position:");
        mousePositionRecorderRecorder = new JButton("Record");
        
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
            if(eventTypeMouseBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40 + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40 - 40);
            
            mouseEventPanel.setVisible(eventTypeMouseBox.isSelected());
            mousePositionPanel.setVisible(eventTypeMouseBox.isSelected());
        });
        
        eventTypeKeyboardBox.addItemListener(e ->
        {
            if(eventTypeKeyboardBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40);
            
            keyboardEventPanel.setVisible(eventTypeKeyboardBox.isSelected());
        });
        
        mousePositionCheckBox.addItemListener(e ->
        {
            if(mousePositionCheckBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40 + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40 - 40);
            
            mousePositionWritePanel.setVisible(mousePositionCheckBox.isSelected());
            mousePositionRecorderPanel.setVisible(mousePositionCheckBox.isSelected());
        });
        
        mousePositionRecorderRecorder.addActionListener(e -> recordingMouse = true);
        
        keyboardEventRecorder.addActionListener(e ->
        {
            recordingKeyboard = true;
            keyboardEventRecorder.setText("Recording...");
        });
        
        // Initialise menu items
        optionsMenuItem = new JMenuItem(new AbstractAction("Options")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFrame optionsFrame = new JFrame("Options");
                optionsFrame.setSize(320, 300);
                optionsFrame.setResizable(false);
                optionsFrame.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());
                optionsFrame.setLocationRelativeTo(null);
                optionsFrame.setLayout(new BoxLayout(optionsFrame.getContentPane(), BoxLayout.Y_AXIS));
                optionsFrame.setVisible(true);
            }
        });
        
        exitMenuItem = new JMenuItem(new AbstractAction("Exit")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        
        // Set keystrokes
        optionsMenuItem.setAccelerator(optionsKeyStroke);
        exitMenuItem.setAccelerator(exitKeyStroke);
        
        // Disable space activating key recording
        keyboardEventRecorder.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        
        // Set tooltips
        timeIntervalPanel.setToolTipText("Amount of time to wait between each action");
        jitterAmountCheckBox.setToolTipText("<html>Timing inconsistency (jitter)<br>A random number between 0 and the specified jitter will be chosen<br>and added to the time interval at each action.");
        eventTypePanel.setToolTipText("Which events to execute at the chosen time interval");
        mouseEventPanel.setToolTipText("Which mouse event to execute per action");
        mousePositionCheckBox.setToolTipText("Whether the autoclicker should click a specific location on the screen");
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
        
        mousePositionPanel.add(mousePositionCheckBox);
        
        mousePositionWritePanel.add(mousePositionWriteXLabel);
        mousePositionWritePanel.add(mousePositionWriteX);
        mousePositionWritePanel.add(mousePositionWriteYLabel);
        mousePositionWritePanel.add(mousePositionWriteY);
        
        mousePositionRecorderPanel.add(mousePositionRecorderLabel);
        mousePositionRecorderPanel.add(mousePositionRecorderRecorder);
        
        keyboardEventPanel.add(keyboardEventLabel);
        keyboardEventPanel.add(keyboardEventRecorder);
        
        statusPanel.add(statusImageLabel);
        statusPanel.add(statusTextLabel);
        
        // Add panels to frame
        frame.add(timeIntervalPanel);
        frame.add(jitterAmountPanel);
        frame.add(eventTypePanel);
        frame.add(mouseEventPanel);
        frame.add(mousePositionPanel);
        frame.add(mousePositionWritePanel);
        frame.add(mousePositionRecorderPanel);
        frame.add(keyboardEventPanel);
        frame.add(statusPanel);
        
        // Add menu bar to frame
        menu.add(optionsMenuItem);
        menu.add(exitMenuItem);
        
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        
        // Set default combo box values
        timeIntervalUnit.setSelectedItem("Milliseconds");
        jitterAmountUnit.setSelectedItem("Milliseconds");
        
        // Set enabled & visibility
        jitterAmountField.setEnabled(false);
        jitterAmountUnit.setEnabled(false);
        mouseEventPanel.setVisible(false);
        mousePositionPanel.setVisible(false);
        mousePositionWritePanel.setVisible(false);
        mousePositionRecorderPanel.setVisible(false);
        keyboardEventPanel.setVisible(false);
    
        // Set JNativeHook logging
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    
        // Add native listeners
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        
        // Initialise frame
        frame.setTitle("Soni's Autoclicker");
        frame.setSize(320, 220);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setVisible(true);
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) { }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        // Check if the application is recording for a new key action
        if (recordingKeyboard)
        {
            recordingKeyboard = false;
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
                                // Print wait amount
                                System.out.printf("Waited %dms\n", System.currentTimeMillis() - waitTime);
                                
                                // Check to do mouse action
                                if (eventTypeMouseBox.isSelected())
                                {
                                    // Check to position mouse
                                    if (mousePositionCheckBox.isSelected())
                                    {
                                        robot.mouseMove(Integer.parseInt(mousePositionWriteX.getText()), Integer.parseInt(mousePositionWriteY.getText()));
                                    }
                                    
                                    // Initialise button to 0
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
                        
                                waitTime = System.currentTimeMillis();
                                
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
    
                                // Sleep for jitter amount
                                if (jitterAmountCheckBox.isSelected())
                                {
                                    long amount = new Random().nextInt(Integer.parseInt(jitterAmountField.getText()));
                                    switch (Objects.requireNonNull(jitterAmountUnit.getSelectedItem()).toString())
                                    {
                                        case "Days" -> TimeUnit.DAYS.sleep(amount);
                                        case "Hours" -> TimeUnit.HOURS.sleep(amount);
                                        case "Minutes" -> TimeUnit.MINUTES.sleep(amount);
                                        case "Seconds" -> TimeUnit.SECONDS.sleep(amount);
                                        case "Milliseconds" -> TimeUnit.MILLISECONDS.sleep(amount);
                                        case "Microseconds" -> TimeUnit.MICROSECONDS.sleep(amount);
                                        case "Nanoseconds" -> TimeUnit.NANOSECONDS.sleep(amount);
                                    }
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
    
    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) { }
    
    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent)
    {
        // Check if the application is recording mouse position
        if (recordingMouse)
        {
            recordingMouse = false;
            mousePositionRecorderRecorder.setText("Record");
            
            mousePositionWriteX.setText(String.valueOf(nativeMouseEvent.getX()));
            mousePositionWriteY.setText(String.valueOf(nativeMouseEvent.getY()));
        }
    }
    
    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) { }
    
    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent)
    {
        // Check if the application is recording mouse position
        if (recordingMouse)
        {
            mousePositionRecorderRecorder.setText(String.format("(%d, %d)", nativeMouseEvent.getX(), nativeMouseEvent.getY()));
        }
    }
    
    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) { }
    
    public static void main(String[] args)
    {
        // Register native hook
        try { GlobalScreen.registerNativeHook(); }
        catch (NativeHookException e) { e.printStackTrace(); }
        
        new Autoclicker();
    }
}