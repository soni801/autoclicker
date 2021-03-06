package com.autoclicker.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.*;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.keyboard.SwingKeyAdapter;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.util.Properties;
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
    public static final String APPLICATION_VENDOR = "Soni";
    public static final String APPLICATION_NAME = "Autoclicker";
    public static final String APPLICATION_VERSION = "0.7";

    // Runtime data
    private static String CONFIG_DIRECTORY;
    private static final String CONFIG_FILE_NAME = "settings.cfg";
    private static final String[] TIME_UNITS = new String[]{"Days", "Hours", "Minutes", "Seconds", "Milliseconds", "Microseconds", "Nanoseconds"};
    Properties settings = new Properties();

    // Settings
    InputType toggleType = InputType.Keyboard;
    int toggleKey = 67; // Default to F9
    boolean alwaysOnTop; // Default to false

    // Execution data
    boolean active;
    int recording;

    public int timeInterval = 1, jitterAmount;
    public int timeUnit = 4, jitterUnit = 4; // Default to milliseconds
    public boolean jitter;
    public int x, y;

    public int mouse = -1, keyboard = -1; // Disable by default
    public boolean mouseMove;

    // Status texts
    public String status, activeText, inactiveText;

    // Class objects
    Shell shell;

    // GUI elements
    Label statusImage, statusLabel;
    Image activeImage, inactiveImage, iconImage;
    Button mousePositionButton, keyboardButton, toggleButton, startButton, stopButton;
    Spinner mousePositionXSpinner, mousePositionYSpinner;

    public Autoclicker()
    {
        // Define config directory
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) CONFIG_DIRECTORY = System.getenv("AppData");
        else if (os.contains("mac")) CONFIG_DIRECTORY = System.getProperty("user.home") + "/Library/Application Support";
        else CONFIG_DIRECTORY = System.getProperty("user.home") + "/.local/share";
        CONFIG_DIRECTORY += "/%s/%s/".formatted(APPLICATION_VENDOR, APPLICATION_NAME);

        // Load settings
        loadSettings();

        // -------------------------------------------------------------------------------------------------------------

        // Create the window
        Display display = new Display();
        if (alwaysOnTop) shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.ON_TOP);
        else shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);

        // Load images
        activeImage = new Image(display, this.getClass().getClassLoader().getResourceAsStream("active.png"));
        inactiveImage = new Image(display, this.getClass().getClassLoader().getResourceAsStream("inactive.png"));
        iconImage = new Image(display, this.getClass().getClassLoader().getResourceAsStream("icon.png"));

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
        Spinner timeIntervalSpinner = new Spinner(timeIntervalGroup, SWT.BORDER);
        timeIntervalSpinner.setMaximum(10000);
        timeIntervalSpinner.setMinimum(1);
        timeIntervalSpinner.setSelection(1);
        timeIntervalSpinner.setToolTipText("The amount of time to wait between each action");

        // Create time interval combo
        Combo timeIntervalCombo = new Combo(timeIntervalGroup, SWT.READ_ONLY);
        for (String unit : TIME_UNITS) timeIntervalCombo.add(unit);
        timeIntervalCombo.select(4);

        // Create jitter group
        Group jitterGroup = new Group(timingComposite, SWT.NONE);
        jitterGroup.setLayout(new GridLayout());
        jitterGroup.setText("Jitter");

        // Create jitter checkbox
        Button jitterCheckbox = new Button(jitterGroup, SWT.CHECK);
        jitterCheckbox.setText("Enable jitter (inconsistency)");
        jitterCheckbox.setToolTipText("""
            Timing inconsistency.
            If enabled, a random number will be chosen between 0 and the value
            you specify here, and will then be added onto the time interval.
            This makes the autoclicker have inconsistency in its timing.""");

        // Create jitter amount composite
        Composite jitterAmountComposite = new Composite(jitterGroup, SWT.CHECK);
        jitterAmountComposite.setLayout(emptyRowLayout);

        // Create jitter amount spinner
        Spinner jitterAmountSpinner = new Spinner(jitterAmountComposite, SWT.BORDER);
        jitterAmountSpinner.setMaximum(1000);
        jitterAmountSpinner.setEnabled(false);

        // Create jitter amount combo
        Combo jitterAmountCombo = new Combo(jitterAmountComposite, SWT.READ_ONLY);
        for (String unit : TIME_UNITS) jitterAmountCombo.add(unit);
        jitterAmountCombo.select(4);
        jitterAmountCombo.setEnabled(false);

        // Create status group
        Group statusGroup = new Group(timingComposite, SWT.NONE);
        statusGroup.setLayoutData(fillData());
        statusGroup.setLayout(new GridLayout());
        statusGroup.setText("Status");

        // Create status image
        statusImage = new Label(statusGroup, SWT.NONE);
        statusImage.setImage(inactiveImage);

        // Create status label
        statusLabel = new Label(statusGroup, SWT.NONE);
        statusLabel.setText("\n\n");
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Create toggleButtons composite
        Composite toggleButtonsComposite = new Composite(statusGroup, SWT.NONE);
        toggleButtonsComposite.setLayout(emptyRowLayout);

        // Create start button
        startButton = new Button(toggleButtonsComposite, SWT.PUSH);
        startButton.setText("Start");

        // Create stop button
        stopButton = new Button(toggleButtonsComposite, SWT.PUSH);
        stopButton.setText("Stop");
        stopButton.setEnabled(false);

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

        // Create mouse checkbox
        Button mouseCheckbox = new Button(mouseGroup, SWT.CHECK);
        mouseCheckbox.setText("Enable mouse event");
        mouseCheckbox.setToolTipText("Execute a mouse action every interval");

        // Create mouse button composite
        Composite mouseButtonComposite = new Composite(mouseGroup, SWT.NONE);
        mouseButtonComposite.setLayout(emptyRowLayout);

        // Create mouse button label
        Label mouseButtonLabel = new Label(mouseButtonComposite, SWT.NONE);
        mouseButtonLabel.setText("Button:");
        mouseButtonLabel.setEnabled(false);

        // Create mouse button combo
        Combo mouseButtonCombo = new Combo(mouseButtonComposite, SWT.READ_ONLY);
        mouseButtonCombo.add("Left Click");
        mouseButtonCombo.add("Middle Click");
        mouseButtonCombo.add("Right Click");
        mouseButtonCombo.select(0);
        mouseButtonCombo.setEnabled(false);

        // Create mouse position checkbox
        Button mousePositionCheckbox = new Button(mouseGroup, SWT.CHECK);
        mousePositionCheckbox.setText("Specific mouse position");
        mousePositionCheckbox.setEnabled(false);
        mousePositionCheckbox.setToolTipText("Click in a specific spot on the screen every time");

        // Create mouse position composite
        Composite mousePositionComposite = new Composite(mouseGroup, SWT.NONE);
        mousePositionComposite.setLayout(emptyRowLayout);

        // Create mouse position x label
        Label mousePositionXLabel = new Label(mousePositionComposite, SWT.NONE);
        mousePositionXLabel.setText("X:");
        mousePositionXLabel.setEnabled(false);

        // Create mouse position x spinner
        mousePositionXSpinner = new Spinner(mousePositionComposite, SWT.BORDER);
        mousePositionXSpinner.setMinimum(-10000);
        mousePositionXSpinner.setMaximum(10000);
        mousePositionXSpinner.setEnabled(false);

        // Create mouse position y label
        Label mousePositionYLabel = new Label(mousePositionComposite, SWT.NONE);
        mousePositionYLabel.setText("Y:");
        mousePositionYLabel.setEnabled(false);

        // Create mouse position y spinner
        mousePositionYSpinner = new Spinner(mousePositionComposite, SWT.BORDER);
        mousePositionYSpinner.setMinimum(-10000);
        mousePositionYSpinner.setMaximum(10000);
        mousePositionYSpinner.setEnabled(false);

        // Create mouse position button
        mousePositionButton = new Button(mouseGroup, SWT.PUSH);
        mousePositionButton.setText("Or record a position");
        mousePositionButton.setEnabled(false);

        // Create keyboard group
        Group keyboardGroup = new Group(eventGroup, SWT.NONE);
        keyboardGroup.setLayoutData(fillData());
        keyboardGroup.setLayout(new GridLayout());
        keyboardGroup.setText("Keyboard");

        // Create keyboard checkbox
        Button keyboardCheckbox = new Button(keyboardGroup, SWT.CHECK);
        keyboardCheckbox.setText("Enable keyboard event");
        keyboardCheckbox.setToolTipText("Execute a keyboard action every interval");

        // Create keyboard button
        keyboardButton = new Button(keyboardGroup, SWT.PUSH);
        keyboardButton.setText("Record a key");
        keyboardButton.setEnabled(false);

        // -------------------------------------------------------------------------------------------------------------

        // Create settings composite
        Composite settingsComposite = new Composite(tabFolder, SWT.NONE);
        settingsComposite.setLayout(new GridLayout());
        settingsTab.setControl(settingsComposite);

        // Create binding tab group
        Group bindingTabGroup = new Group(settingsComposite, SWT.NONE);
        bindingTabGroup.setLayoutData(fillData());
        bindingTabGroup.setLayout(new GridLayout());
        bindingTabGroup.setText("Hotkeys");

        // Create binding label
        Label bindingLabel = new Label(bindingTabGroup, SWT.NONE);
        bindingLabel.setText("""
            Which buttons to use for performing actions in the application.
            They can be either mouse or keyboard buttons.""");

        // Create toggle composite
        Composite toggleComposite = new Composite(bindingTabGroup, SWT.NONE);
        toggleComposite.setLayout(emptyRowLayout);

        // Create toggle label
        Label toggleLabel = new Label(toggleComposite, SWT.NONE);
        toggleLabel.setText("Toggle autoclicker");

        // Create toggle button
        toggleButton = new Button(toggleComposite, SWT.PUSH);
        toggleButton.setText(NativeKeyEvent.getKeyText(toggleKey));
        toggleButton.setLayoutData(new RowData(80, SWT.DEFAULT));

        // Create behaviour group
        Group behaviourGroup = new Group(settingsComposite, SWT.NONE);
        behaviourGroup.setLayoutData(fillData());
        behaviourGroup.setLayout(rowLayout);
        behaviourGroup.setText("Behaviour");

        // Create always on top checkbox
        Button alwaysOnTopCheckbox = new Button(behaviourGroup, SWT.CHECK);
        alwaysOnTopCheckbox.setText("Always on top (requires restart)");
        alwaysOnTopCheckbox.setSelection(alwaysOnTop);

        // Create empty composite
        Composite emptyComposite = new Composite(settingsComposite, SWT.NONE);
        emptyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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
            
            It is built in Java using <a href="https://github.com/kwhat/jnativehook">JNativeHook</a> and <a href="https://www.eclipse.org/swt/">SWT</a>
            and is fully open source - you can check out the repository on <a href="https://github.com/soni801/autoclicker">GitHub</a>.""".formatted(APPLICATION_VERSION));

        // -------------------------------------------------------------------------------------------------------------

        // Logic for setting time interval amount
        timeIntervalSpinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                timeInterval = timeIntervalSpinner.getSelection();
            }
        });

        // Logic for setting time interval unit
        timeIntervalCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                timeUnit = timeIntervalCombo.getSelectionIndex();
            }
        });

        // Logic for toggling jitter
        jitterCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // Store value
                jitter = jitterCheckbox.getSelection();

                // Toggle UI
                jitterAmountSpinner.setEnabled(jitter);
                jitterAmountCombo.setEnabled(jitter);
            }
        });

        // Logic for setting jitter amount
        jitterAmountSpinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                jitterAmount = jitterAmountSpinner.getSelection();
            }
        });

        // Logic for setting jitter unit
        jitterAmountCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                jitterUnit = jitterAmountCombo.getSelectionIndex();
            }
        });

        // Logic for starting the autoclicker
        startButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                start();
            }
        });

        // Logic for stopping the autoclicker
        stopButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                active = false;
                refreshStatus();
            }
        });

        // Logic for toggling mouse event
        mouseCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // Store values
                if (!mouseCheckbox.getSelection()) mouse = -1;
                else mouse = mouseButtonCombo.getSelectionIndex();

                // Toggle UI
                mouseButtonLabel.setEnabled(mouse != -1);
                mouseButtonCombo.setEnabled(mouse != -1);
                mousePositionCheckbox.setEnabled(mouse != -1);

                mousePositionXLabel.setEnabled(mouse != -1 && mouseMove);
                mousePositionXSpinner.setEnabled(mouse != -1 && mouseMove);
                mousePositionYLabel.setEnabled(mouse != -1 && mouseMove);
                mousePositionYSpinner.setEnabled(mouse != -1 && mouseMove);
                mousePositionButton.setEnabled(mouse != -1 && mouseMove);
            }
        });

        // Logic for selecting mouse event type
        mouseButtonCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                mouse = mouseButtonCombo.getSelectionIndex();
            }
        });

        // Logic for toggling specific mouse position
        mousePositionCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // Store value
                mouseMove = mousePositionCheckbox.getSelection();

                // Toggle UI
                mousePositionXLabel.setEnabled(mouseMove);
                mousePositionXSpinner.setEnabled(mouseMove);
                mousePositionYLabel.setEnabled(mouseMove);
                mousePositionYSpinner.setEnabled(mouseMove);
                mousePositionButton.setEnabled(mouseMove);
            }
        });

        // Logic for setting mouse X position
        mousePositionXSpinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                x = mousePositionXSpinner.getSelection();
            }
        });

        // Logic for setting mouse Y position
        mousePositionYSpinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                y = mousePositionYSpinner.getSelection();
            }
        });

        // Logic for recording mouse position
        mousePositionButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                recording = 3;
                mousePositionButton.setText("Recording...");
            }
        });

        // Logic for toggling keyboard event
        keyboardCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // Store values
                if (!keyboardCheckbox.getSelection())
                {
                    keyboard = -1;
                    keyboardButton.setText("Record a key");
                }

                // Toggle UI
                keyboardButton.setEnabled(keyboardCheckbox.getSelection());
            }
        });

        // Logic for keyboard event key recording
        keyboardButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                recording = 1;
                keyboardButton.setText("Recording...");
            }
        });

        // Logic for toggle button rebinding
        toggleButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                recording = 2;
                toggleButton.setText("Recording...");
                toggleButton.setEnabled(false);
            }
        });

        alwaysOnTopCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                alwaysOnTop = alwaysOnTopCheckbox.getSelection();
            }
        });

        // Logic for launching browser when pressing links on about page
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

        // Refresh UI
        refreshBinds();
        refreshStatus();

        // Set window details & launch
        shell.setLayout(new FillLayout());
        shell.setText("Soni's Autoclicker");
        shell.setImage(iconImage);
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) if (!display.readAndDispatch()) display.sleep();

        // Safely quit
        saveSettings();
        System.exit(0);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {}

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        // Check whether the application is recording for a new key action
        // Check the recording state of the application
        switch (recording)
        {
            // Not recording, toggle clicker
            case 0 -> {
                // Check for correct key press to enable autoclicker
                if (toggleType == InputType.Keyboard && nativeKeyEvent.getKeyCode() == toggleKey)
                {
                    active = !active; // Invert active state
                    if (active) start(); // Enable autoclicker
                    else refreshStatus();
                }
            }
            // Recording keyboard action
            case 1 -> {
                // Update key event button
                // This has to be done before it is stored as a Java Key Event
                shell.getDisplay().asyncExec(() -> keyboardButton.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode())));

                recording = 0;
                keyboard = getJavaKeyEvent(nativeKeyEvent).getKeyCode();
                refreshBinds();
            }
            // Recording toggle bind
            case 2 -> {
                recording = 0;
                toggleType = InputType.Keyboard;
                toggleKey = nativeKeyEvent.getKeyCode();
                refreshBinds();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {}

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {}

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent)
    {
        // Surround with try/catch to avoid errors if the application is exited while recording
        try
        {
            // Check the recording state of the application
            switch (recording)
            {
                case 0 -> {
                    // Check for correct key press to enable autoclicker
                    if (toggleType == InputType.Mouse && nativeMouseEvent.getButton() == toggleKey)
                    {
                        active = !active; // Invert active state
                        if (active) start(); // Enable autoclicker
                        else refreshStatus();
                    }
                }
                // Recording toggle bind
                case 2 -> {
                    recording = 0;
                    toggleType = InputType.Mouse;
                    toggleKey = nativeMouseEvent.getButton();
                    refreshBinds();
                }
                // Recording position for mouse event
                case 3 -> {
                    recording = 0;
                    x = nativeMouseEvent.getX();
                    y = nativeMouseEvent.getY();
                    shell.getDisplay().asyncExec(() ->
                    {
                        mousePositionButton.setText("Or record a position");
                        mousePositionXSpinner.setSelection(x);
                        mousePositionYSpinner.setSelection(y);
                    });
                }
            }
        }
        catch (Exception ignored) {}
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {}

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent)
    {
        // Avoid errors on application exit
        try
        {
            // Set mouse position text
            shell.getDisplay().asyncExec(() ->
            {
                if (recording == 3) mousePositionButton.setText("(%d, %d)".formatted(nativeMouseEvent.getX(), nativeMouseEvent.getY()));
            });
        }
        catch (Exception ignored) {}
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {}

    // Method to start the autoclicker
    private synchronized void start()
    {
        // Create new thread to handle events
        new Thread(() ->
        {
            try
            {
                // Update status
                active = true;
                refreshStatus();

                // Create objects immediately when thread starts
                Robot robot = new Robot();
                Random r = new Random();

                // Loop while autoclicker is running
                while (active)
                {
                    // Check to position mouse
                    if (mouse != -1 && mouseMove) robot.mouseMove(x, y);

                    // Initialise button to 0
                    int button = 0;

                    // Set mouse button
                    switch (mouse)
                    {
                        case 0 -> button = InputEvent.BUTTON1_DOWN_MASK;
                        case 1 -> button = InputEvent.BUTTON2_DOWN_MASK;
                        case 2 -> button = InputEvent.BUTTON3_DOWN_MASK;
                    }

                    // Execute mouse action
                    if (mouse != -1)
                    {
                        robot.mousePress(button);
                        robot.mouseRelease(button);
                    }

                    // Execute keyboard action
                    if (keyboard != -1)
                    {
                        robot.keyPress(keyboard);
                        robot.keyRelease(keyboard);
                    }

                    // Sleep for the desired amount of time
                    sleep(timeUnit, timeInterval); // Time interval
                    if (jitter) sleep(jitterUnit, r.nextInt(jitterAmount)); // Jitter amount
                }
            }
            catch (AWTException | InterruptedException ignored) {}
        }).start();
    }

    // Method to get fill data
    public GridData fillData()
    {
        return new GridData(SWT.FILL, SWT.FILL, false, false);
    }

    // Methods to redraw
    public void refreshStatus()
    {
        shell.getDisplay().asyncExec(() ->
        {
            // Refresh status text
            status = active ? activeText : inactiveText;
            statusImage.setImage(active ? activeImage : inactiveImage);
            statusLabel.setText(status);

            // Refresh buttons
            startButton.setEnabled(!active);
            stopButton.setEnabled(active);
        });
    }

    public void refreshBinds()
    {
        shell.getDisplay().asyncExec(() ->
        {
            // Define bind text
            String bindText = toggleType == InputType.Keyboard ? NativeKeyEvent.getKeyText(toggleKey) : "Button %d".formatted(toggleKey);

            // Update status texts
            activeText = "Running.\nPress %s to stop".formatted(bindText);
            inactiveText = "Not running.\nPress %s to start".formatted(bindText);
            refreshStatus();

            // Update settings button
            toggleButton.setText(bindText);

            // Re-enable settings button
            toggleButton.setEnabled(true);
        });
    }

    private void sleep(int timeUnit, long timeInterval) throws InterruptedException
    {
        switch (timeUnit)
        {
            case 0 -> TimeUnit.DAYS.sleep(timeInterval);
            case 1 -> TimeUnit.HOURS.sleep(timeInterval);
            case 2 -> TimeUnit.MINUTES.sleep(timeInterval);
            case 3 -> TimeUnit.SECONDS.sleep(timeInterval);
            case 4 -> TimeUnit.MILLISECONDS.sleep(timeInterval);
            case 5 -> TimeUnit.MICROSECONDS.sleep(timeInterval);
            case 6 -> TimeUnit.NANOSECONDS.sleep(timeInterval);
        }
    }

    private void loadSettings()
    {
        try
        {
            // Load settings file
            FileInputStream fileInputStream = new FileInputStream(CONFIG_DIRECTORY + CONFIG_FILE_NAME);
            settings.load(fileInputStream);

            // Load settings
            toggleKey = Integer.parseInt(settings.getProperty("toggle-key"));
            toggleType = InputType.valueOf(settings.getProperty("toggle-type"));
            alwaysOnTop = Boolean.parseBoolean(settings.getProperty("always-on-top"));
        }
        catch (FileNotFoundException e)
        {
            // Create settings directory
            System.out.println("Settings file does not exist. Attempting to create...");
            System.out.println(new File(CONFIG_DIRECTORY + CONFIG_FILE_NAME).getParentFile().mkdirs() ? "Created subdirectories" : "Could not create subdirectories. (Do they already exist?)");

            // Create settings file
            try
            {
                System.out.println(new File(CONFIG_DIRECTORY + CONFIG_FILE_NAME).createNewFile() ? "Created settings file" : "Failed to create settings file");
            }
            catch (IOException ignored) {}
        }
        catch (NullPointerException | NumberFormatException e)
        {
            System.out.println("One or more settings keys were missing or corrupt. All user settings have been reset.");
            System.out.println("If this keeps occurring, please create an issue on GitHub.");
        }
        catch (IOException ignored) {}
    }

    private void saveSettings()
    {
        try
        {
            // Update settings
            settings.setProperty("toggle-key", String.valueOf(toggleKey));
            settings.setProperty("toggle-type", String.valueOf(toggleType));
            settings.setProperty("always-on-top", String.valueOf(alwaysOnTop));

            // Store settings
            settings.store(new FileOutputStream(CONFIG_DIRECTORY + CONFIG_FILE_NAME), null);
        }
        catch (IOException e)
        {
            System.out.println("Could not save settings");
        }
    }

    public static void main(String[] args)
    {
        // Disable JNativeHook logging
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        // Register native hook
        try
        {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ignored) {}

        // Launch application
        new Autoclicker();
    }
}