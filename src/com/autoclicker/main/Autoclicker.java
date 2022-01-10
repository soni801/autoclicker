package com.autoclicker.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
    public static final String VERSION = "0.6";

    boolean running;
    int keyboardEvent;
    long waitTime;

    int toggleButton = 64;

    boolean recordingMouse;
    int recordingKeyboard = 0;

    // Keystrokes
    KeyStroke optionsKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_DOWN_MASK);
    KeyStroke exitKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);

    // Frames & menu bar
    JFrame frame;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem optionsMenuItem;
    JMenuItem exitMenuItem;
    JFrame optionsFrame;

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

    // Rebind
    JPanel optionsRebindPanel;
    JLabel optionsRebindLabel;
    JButton optionsRebindRecorder;
    JLabel optionsRebindNotice;

    public Autoclicker()
    {
        // Create the window
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Soni's Autoclicker");
        shell.setLayout(new FillLayout());

        // Create empty layout presets
        RowLayout emptyRowLayout = new RowLayout();
        emptyRowLayout.marginLeft = 0;

        GridLayout emptyGridLayout = new GridLayout();
        emptyGridLayout.marginWidth = 0;
        emptyGridLayout.marginHeight = 0;

        // Initialise tabs
        TabFolder tabFolder = new TabFolder(shell, SWT.NONE);

        TabItem autoclickerTab = new TabItem(tabFolder, SWT.NONE);
        autoclickerTab.setText("Autoclicker");

        TabItem settingsTab = new TabItem(tabFolder, SWT.NONE);
        settingsTab.setText("Settings");

        TabItem aboutTab = new TabItem(tabFolder, SWT.NONE);
        aboutTab.setText("About");

        // Create autoclicker composite
        Composite autoclickerComposite = new Composite(tabFolder, SWT.NONE);
        autoclickerComposite.setLayout(new GridLayout(2, false));
        autoclickerTab.setControl(autoclickerComposite);

        // Create timing composite
        Composite timingComposite = new Composite(autoclickerComposite, SWT.NONE);
        timingComposite.setLayoutData(fillData());
        timingComposite.setLayout(emptyGridLayout);

        // Create time interval group
        Group timeIntervalGroup = new Group(timingComposite, SWT.NONE);
        timeIntervalGroup.setLayoutData(fillData());
        timeIntervalGroup.setLayout(new RowLayout());
        timeIntervalGroup.setText("Time interval");

        // Create time interval spinner
        Spinner timeIntervalSpinner = new Spinner(timeIntervalGroup, SWT.NONE);

        // Create time interval combo
        Combo timeIntervalCombo = new Combo(timeIntervalGroup, SWT.NONE);

        // Create jitter group
        Group jitterGroup = new Group(timingComposite, SWT.NONE);
        jitterGroup.setLayoutData(fillData());
        jitterGroup.setLayout(new GridLayout());
        jitterGroup.setText("Jitter");

        // Create jitter checkbox
        Button jitterCheckbox = new Button(jitterGroup, SWT.CHECK);
        jitterCheckbox.setText("Enable jitter (inconsistency)");

        // Create jitter interval composite
        Composite jitterIntervalComposite = new Composite(jitterGroup, SWT.CHECK);
        jitterIntervalComposite.setLayout(emptyRowLayout);

        // Create jitter interval spinner
        Spinner jitterIntervalSpinner = new Spinner(jitterIntervalComposite, SWT.NONE);

        // Create jitter interval combo
        Combo jitterIntervalCombo = new Combo(jitterIntervalComposite, SWT.NONE);

        // Create event group
        Group eventGroup = new Group(autoclickerComposite, SWT.NONE);
        eventGroup.setLayoutData(fillData());
        eventGroup.setLayout(new GridLayout());
        eventGroup.setText("Events");

        // Create mouse group
        Group mouseGroup = new Group(eventGroup, SWT.NONE);
        mouseGroup.setLayoutData(fillData());
        mouseGroup.setLayout(new GridLayout());
        mouseGroup.setText("Mouse");

        // Create mouse button composite
        Composite mouseButtonComposite = new Composite(mouseGroup, SWT.NONE);
        mouseButtonComposite.setLayout(emptyRowLayout);

        // Create mouse button label
        Label mouseButtonLabel = new Label(mouseButtonComposite, SWT.NONE);
        mouseButtonLabel.setText("Button:");

        // Create mouse button combo
        Combo mouseButtonCombo = new Combo(mouseButtonComposite, SWT.NONE);

        // Create mouse position checkbox
        Button mousePositionCheckbox = new Button(mouseGroup, SWT.CHECK);
        mousePositionCheckbox.setText("Specific mouse position");

        // Create mouse position composite
        Composite mousePositionComposite = new Composite(mouseGroup, SWT.NONE);
        mousePositionComposite.setLayout(emptyRowLayout);

        // Create mouse position x label
        Label mousePositionXLabel = new Label(mousePositionComposite, SWT.NONE);
        mousePositionXLabel.setText("X:");

        // Create mouse position x spinner
        Spinner mousePositionXSpinner = new Spinner(mousePositionComposite, SWT.NONE);

        // Create mouse position y label
        Label mousePositionYLabel = new Label(mousePositionComposite, SWT.NONE);
        mousePositionYLabel.setText("Y:");

        // Create mouse position y spinner
        Spinner mousePositionYSpinner = new Spinner(mousePositionComposite, SWT.NONE);

        // Create mouse position button
        Button mousePositionButton = new Button(mouseGroup, SWT.PUSH);
        mousePositionButton.setText("Or record a position");

        // Create keyboard group
        Group keyboardGroup = new Group(eventGroup, SWT.NONE);
        keyboardGroup.setLayoutData(fillData());
        keyboardGroup.setLayout(new GridLayout());
        keyboardGroup.setText("Keyboard");

        // Create keyboard button
        Button keyboardButton = new Button(keyboardGroup, SWT.PUSH);
        keyboardButton.setText("Record a key");

        // -------------------------------------------------------------------------------------------------------------

        // Create settings composite
        Composite settingsComposite = new Composite(tabFolder, SWT.NONE);
        settingsComposite.setLayout(new FillLayout());
        settingsTab.setControl(settingsComposite);

        // Create binding tab group
        Group bindingTabGroup = new Group(settingsComposite, SWT.NONE);
        bindingTabGroup.setLayoutData(fillData());
        bindingTabGroup.setLayout(new RowLayout());
        bindingTabGroup.setText("Key binds");

        // Create toggle composite
        Composite toggleComposite = new Composite(bindingTabGroup, SWT.NONE);
        toggleComposite.setLayout(emptyRowLayout);

        // Create toggle label
        Label toggleLabel = new Label(toggleComposite, SWT.NONE);
        toggleLabel.setText("Toggle autoclicker");

        // Create toggle button
        Button toggleButton = new Button(toggleComposite, SWT.PUSH);
        toggleButton.setText("F6");
        toggleButton.setLayoutData(new RowData(80, SWT.DEFAULT));

        // -------------------------------------------------------------------------------------------------------------

        // Create about composite
        Composite aboutComposite = new Composite(tabFolder, SWT.NONE);
        aboutComposite.setLayout(new GridLayout());
        aboutTab.setControl(aboutComposite);

        // Create about label
        Link aboutLabel = new Link(aboutComposite, SWT.NONE);
        aboutLabel.setText("""
                Soni's Autoclicker v%s
                By <a href="https://github.com/soni801">Soni</a>
                
                Soni's Autoclicker is an attempt at making automation
                powerful and accessible to everyone.
                
                It is built in Java and is fully open source - you can check
                out the repository on <a href="https://github.com/soni801/autoclicker">GitHub</a>.
                
                It uses <a href="https://github.com/kwhat/jnativehook">JNativeHook</a> and <a href="https://www.eclipse.org/swt/">SWT</a> for its features.
                
                Thanks to <a href="https://github.com/LilleAndersen">Little</a> for testing.""".formatted(VERSION));
        aboutLabel.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Program.launch(e.text);
            }
        });

        // -------------------------------------------------------------------------------------------------------------

        // Launch the application
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) if (!display.readAndDispatch()) display.sleep();

        // Safely quit
        display.dispose();
        System.exit(0);

        /*// Initialise objects
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
        statusTextLabel = new JLabel(String.format("Not running. Press %s to start", NativeKeyEvent.getKeyText(toggleButton)));

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
            if (eventTypeMouseBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40 + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40 - 40);

            mouseEventPanel.setVisible(eventTypeMouseBox.isSelected());
            mousePositionPanel.setVisible(eventTypeMouseBox.isSelected());
        });

        eventTypeKeyboardBox.addItemListener(e ->
        {
            if (eventTypeKeyboardBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40);

            keyboardEventPanel.setVisible(eventTypeKeyboardBox.isSelected());
        });

        mousePositionCheckBox.addItemListener(e ->
        {
            if (mousePositionCheckBox.isSelected()) frame.setSize(frame.getWidth(), frame.getHeight() + 40 + 40);
            else frame.setSize(frame.getWidth(), frame.getHeight() - 40 - 40);

            mousePositionWritePanel.setVisible(mousePositionCheckBox.isSelected());
            mousePositionRecorderPanel.setVisible(mousePositionCheckBox.isSelected());
        });

        mousePositionRecorderRecorder.addActionListener(e -> recordingMouse = true);

        keyboardEventRecorder.addActionListener(e ->
        {
            recordingKeyboard = 1;
            keyboardEventRecorder.setText("Recording...");
        });

        // Initialise menu items
        optionsMenuItem = new JMenuItem(new AbstractAction("Options")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Initialise objects
                optionsFrame = new JFrame("Options");

                optionsRebindPanel = new JPanel();
                optionsRebindLabel = new JLabel("Toggle button:");
                optionsRebindRecorder = new JButton(NativeKeyEvent.getKeyText(toggleButton));
                optionsRebindNotice = new JLabel("(resets on close)");

                // Add listeners
                optionsRebindRecorder.addActionListener(ev ->
                {
                    recordingKeyboard = 2;
                    optionsRebindRecorder.setText("Recording...");
                });

                //Add objects to panels
                optionsRebindPanel.add(optionsRebindLabel);
                optionsRebindPanel.add(optionsRebindRecorder);
                optionsRebindPanel.add(optionsRebindNotice);

                // Add panels to frame
                optionsFrame.add(optionsRebindPanel);

                optionsFrame.setSize(300, 80);
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
        frame.setVisible(true);*/
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) { }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        // Check whether the application is recording for a new key action
        switch (recordingKeyboard)
        {
            case 0 -> {
                // Check for correct key press to enable autoclicker
                if (nativeKeyEvent.getKeyCode() == toggleButton)
                {
                    if (running)
                    {
                        // Disable autoclicker
                        running = false;
                        statusImageLabel.setIcon(inactive);
                        statusTextLabel.setText(String.format("Not running. Press %s to start", NativeKeyEvent.getKeyText(toggleButton)));
                    }
                    else
                    {
                        // Enable autoclicker
                        running = true;
                        statusImageLabel.setIcon(active);
                        statusTextLabel.setText(String.format("Running. Press %s to stop", NativeKeyEvent.getKeyText(toggleButton)));

                        // Create new thread to handle events
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
                            } catch (AWTException | InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            }
            case 1 -> {
                recordingKeyboard = 0;
                keyboardEventRecorder.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
                keyboardEvent = getJavaKeyEvent(nativeKeyEvent).getKeyCode();
            }
            case 2 -> {
                recordingKeyboard = 0;
                optionsRebindRecorder.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
                toggleButton = nativeKeyEvent.getKeyCode();

                if (running)
                    statusTextLabel.setText(String.format("Running. Press %s to stop", NativeKeyEvent.getKeyText(toggleButton)));
                else
                    statusTextLabel.setText(String.format("Not running. Press %s to start", NativeKeyEvent.getKeyText(toggleButton)));
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
        // Set mouse position text
        if (recordingMouse) mousePositionRecorderRecorder.setText(String.format("(%d, %d)", nativeMouseEvent.getX(), nativeMouseEvent.getY()));
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) { }

    // Method to get fill data
    public GridData fillData()
    {
        return new GridData(SWT.FILL, SWT.TOP, false, false);
    }

    public static void main(String[] args)
    {
        // Disable JNativeHook logging
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        // Register native hook
        try { GlobalScreen.registerNativeHook(); }
        catch (NativeHookException e) { e.printStackTrace(); }

        // Launch application
        new Autoclicker();
    }
}