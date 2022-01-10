package com.autoclicker.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
    // Metadata
    public static final String VERSION = "0.6";

    // Status texts
    public String status;
    public final String activeText = "Running. Press %s to stop";
    public final String inactiveText = "Not running. Press %s to start";

    // Image paths
    public final String activeImagePath = String.valueOf(getClass().getClassLoader().getResource("active.png")).substring(6);
    public final String inactiveImagePath = String.valueOf(getClass().getClassLoader().getResource("inactive.png")).substring(6);

    // -------
    boolean running;
    int keyboardEvent;
    long waitTime;

    int toggleButton = 64;

    boolean recordingMouse;
    int recordingKeyboard = 0;

    JTextField timeIntervalField;
    JComboBox<String> timeIntervalUnit;

    JCheckBox jitterAmountCheckBox;
    JTextField jitterAmountField;
    JComboBox<String> jitterAmountUnit;

    JCheckBox eventTypeMouseBox;
    JCheckBox eventTypeKeyboardBox;

    JComboBox<String> mouseEventAction;

    JCheckBox mousePositionCheckBox;

    JTextField mousePositionWriteX;
    JTextField mousePositionWriteY;

    JButton mousePositionRecorderRecorder;

    JButton keyboardEventRecorder;

    ImageIcon active;
    ImageIcon inactive;
    JLabel statusImageLabel;

    JButton optionsRebindRecorder;

    public Autoclicker()
    {
        // Create the window
        Display display = new Display();
        Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText("Soni's Autoclicker");
        shell.setLayout(new FillLayout());

        // Load images
        Image activeImage = new Image(display, activeImagePath);
        Image inactiveImage = new Image(display, inactiveImagePath);

        // Create layout presets
        RowLayout emptyRowLayout = new RowLayout();
        emptyRowLayout.marginLeft = 0;
        emptyRowLayout.center = true;

        RowLayout rowLayout = new RowLayout();
        rowLayout.center = true;

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
        timeIntervalGroup.setLayout(rowLayout);
        timeIntervalGroup.setText("Time interval");

        // Create time interval spinner
        Spinner timeIntervalSpinner = new Spinner(timeIntervalGroup, SWT.NONE);

        // Create time interval combo
        Combo timeIntervalCombo = new Combo(timeIntervalGroup, SWT.READ_ONLY);
        timeIntervalCombo.add("Days");
        timeIntervalCombo.add("Hours");
        timeIntervalCombo.add("Minutes");
        timeIntervalCombo.add("Seconds");
        timeIntervalCombo.add("Milliseconds");
        timeIntervalCombo.add("Microseconds");
        timeIntervalCombo.add("Nanoseconds");
        timeIntervalCombo.select(4);

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
        Combo jitterIntervalCombo = new Combo(jitterIntervalComposite, SWT.READ_ONLY);
        jitterIntervalCombo.add("Days");
        jitterIntervalCombo.add("Hours");
        jitterIntervalCombo.add("Minutes");
        jitterIntervalCombo.add("Seconds");
        jitterIntervalCombo.add("Milliseconds");
        jitterIntervalCombo.add("Microseconds");
        jitterIntervalCombo.add("Nanoseconds");
        jitterIntervalCombo.select(4);

        // Create status group
        Group statusGroup = new Group(timingComposite, SWT.NONE);
        statusGroup.setLayoutData(fillData());
        statusGroup.setLayout(new GridLayout());
        statusGroup.setText("Status");

        // Create status image
        Label statusImage = new Label(statusGroup, SWT.NONE);
        statusImage.setImage(inactiveImage);

        // Create status label
        Label statusLabel = new Label(statusGroup, SWT.NONE);
        statusLabel.setText(inactiveText.formatted(NativeKeyEvent.getKeyText(toggleButton)));

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
        Combo mouseButtonCombo = new Combo(mouseButtonComposite, SWT.READ_ONLY);
        mouseButtonCombo.add("Left Click");
        mouseButtonCombo.add("Middle Click");
        mouseButtonCombo.add("Right Click");
        mouseButtonCombo.select(0);

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
        bindingTabGroup.setLayout(rowLayout);
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

        // Add native listeners
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);

        // Launch the application
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) if (!display.readAndDispatch()) display.sleep();

        // Safely quit
        display.dispose();
        System.exit(0);

        /*
        active = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("active.png")));
        inactive = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("inactive.png")));

        mousePositionRecorderRecorder.addActionListener(e -> recordingMouse = true);

        keyboardEventRecorder.addActionListener(e ->recordingKeyboard = 1;

        optionsRebindRecorder = new JButton(NativeKeyEvent.getKeyText(toggleButton));
        optionsRebindRecorder.addActionListener(ev ->recordingKeyboard = 2;

        // Disable space activating key recording
        keyboardEventRecorder.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");

        frame.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());
        */
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) { }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        // Check whether the application is recording for a new key action
        switch (recordingKeyboard)
        {
            // Not recording
            case 0 -> {
                // Check for correct key press to enable autoclicker
                if (nativeKeyEvent.getKeyCode() == toggleButton)
                {
                    if (running)
                    {
                        // Disable autoclicker
                        running = false;
                        statusImageLabel.setIcon(inactive);
                        status = inactiveText.formatted(NativeKeyEvent.getKeyText(toggleButton));
                    }
                    else
                    {
                        // Enable autoclicker
                        running = true;
                        statusImageLabel.setIcon(active);
                        status = activeText.formatted(NativeKeyEvent.getKeyText(toggleButton));

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
            // Recording keyboard action
            case 1 -> {
                recordingKeyboard = 0;
                keyboardEventRecorder.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
                keyboardEvent = getJavaKeyEvent(nativeKeyEvent).getKeyCode();
            }
            // Recording binding
            case 2 -> {
                recordingKeyboard = 0;
                optionsRebindRecorder.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
                toggleButton = nativeKeyEvent.getKeyCode();

                if (running) status = activeText.formatted(NativeKeyEvent.getKeyText(toggleButton));
                else status = inactiveText.formatted(NativeKeyEvent.getKeyText(toggleButton));
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
        return new GridData(SWT.FILL, SWT.FILL, false, true);
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