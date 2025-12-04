/*======================== 
Library GUI Interface for SLNDA Software
    1.javax.swing
    2.java.awt 
    3.awt.event
======================== */

import javax.swing.*; // * = Anything from javax.swing
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*; // * = Anything from java.awt
import java.awt.event.*; // * = Anything from java.awt.event

/*======================== 
Library Sound Interface, Equalizer Recording for SLNDA Software
    1.javax.sound.sampled  
    2.java.util.Timer
    3.java.util.TimerTask
    4.Tarsos DSP
======================== */

import javax.sound.sampled.*;
import java.util.Timer;
import java.util.TimerTask;

/*======================== 
Library Files Management for SLNDA Software
    1.javax.io
======================== */

import java.io.*;



public class SLNDA_Software {
    // Global variables for audio recording
    private static TargetDataLine targetLine;
    private static AudioInputStream audioStream;
    private static boolean isRecording = false;
    private static boolean isPlaying = false;
    private static JLabel statusLabel;
    private static JPanel pluginBar;
    private static JTextArea waveformDisplay;
    private static Timer levelMeterTimer;
    
    public static void main(String[] args) {
        // Set Frame 
        JFrame SLNDA_Frame = new JFrame("SLNDA Software");
        SLNDA_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SLNDA_Frame.setSize(1920, 1080);
        SLNDA_Frame.setLayout(new BorderLayout());

        // Set Title Bar for import icon
        SLNDA_Frame.setTitle("SLNDA Software");
        // SLNDA_Frame.setIconImage(Toolkit.getDefaultToolkit().getImage("path/to/icon.png"));

        // ============ Navbar (Top Menu Bar) ============
        JMenuBar navbar = new JMenuBar();
        navbar.setPreferredSize(new Dimension(1920, 40));
        navbar.setBackground(new Color(45, 45, 45));
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        
        JMenuItem newProject = new JMenuItem("New Project");
        newProject.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(SLNDA_Frame, 
                "Create a new project? Unsaved changes will be lost.", 
                "New Project", 
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                waveformDisplay.setText("=== NEW PROJECT CREATED ===\nReady to record...");
                updateStatus("New project created");
            }
        });
        
        JMenuItem openProject = new JMenuItem("Open Project");
        openProject.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("SLNDA Project (*.slnda)", "slnda"));
            int result = fileChooser.showOpenDialog(SLNDA_Frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                waveformDisplay.setText("=== PROJECT OPENED ===\n" + selectedFile.getName());
                updateStatus("Opened: " + selectedFile.getName());
            }
        });
        
        JMenuItem saveProject = new JMenuItem("Save Project");
        saveProject.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("SLNDA Project (*.slnda)", "slnda"));
            int result = fileChooser.showSaveDialog(SLNDA_Frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getAbsolutePath().endsWith(".slnda")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".slnda");
                }
                updateStatus("Saved: " + fileToSave.getName());
                JOptionPane.showMessageDialog(SLNDA_Frame, "Project saved successfully!");
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(SLNDA_Frame, 
                "Are you sure you want to exit?", 
                "Exit", 
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        fileMenu.add(newProject);
        fileMenu.add(openProject);
        fileMenu.add(saveProject);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setForeground(Color.WHITE);
        
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> {
            updateStatus("Undo performed");
            JOptionPane.showMessageDialog(SLNDA_Frame, "Undo action performed");
        });
        
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(e -> {
            updateStatus("Redo performed");
            JOptionPane.showMessageDialog(SLNDA_Frame, "Redo action performed");
        });
        
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(e -> {
            JDialog prefDialog = new JDialog(SLNDA_Frame, "Preferences", true);
            prefDialog.setSize(400, 300);
            prefDialog.setLayout(new GridLayout(4, 2, 10, 10));
            
            prefDialog.add(new JLabel("Sample Rate:"));
            JComboBox<String> sampleRateBox = new JComboBox<>(new String[]{"44100 Hz", "48000 Hz", "96000 Hz"});
            prefDialog.add(sampleRateBox);
            
            prefDialog.add(new JLabel("Buffer Size:"));
            JComboBox<String> bufferBox = new JComboBox<>(new String[]{"256", "512", "1024", "2048"});
            prefDialog.add(bufferBox);
            
            prefDialog.add(new JLabel("Audio Driver:"));
            JComboBox<String> driverBox = new JComboBox<>(new String[]{"ASIO", "DirectSound", "WASAPI"});
            prefDialog.add(driverBox);
            
            JButton saveBtn = new JButton("Save");
            saveBtn.addActionListener(ev -> {
                updateStatus("Preferences saved");
                prefDialog.dispose();
            });
            prefDialog.add(saveBtn);
            
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(ev -> prefDialog.dispose());
            prefDialog.add(cancelBtn);
            
            prefDialog.setLocationRelativeTo(SLNDA_Frame);
            prefDialog.setVisible(true);
        });
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(preferencesItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);
        
        JMenuItem pluginBarItem = new JMenuItem("Toggle Plugin Bar");
        pluginBarItem.addActionListener(e -> {
            pluginBar.setVisible(!pluginBar.isVisible());
            updateStatus("Plugin bar " + (pluginBar.isVisible() ? "shown" : "hidden"));
        });
        
        JMenuItem fullscreenItem = new JMenuItem("Fullscreen");
        fullscreenItem.addActionListener(e -> {
            SLNDA_Frame.dispose();
            if (SLNDA_Frame.isUndecorated()) {
                SLNDA_Frame.setUndecorated(false);
                SLNDA_Frame.setExtendedState(JFrame.NORMAL);
                updateStatus("Exited fullscreen");
            } else {
                SLNDA_Frame.setUndecorated(true);
                SLNDA_Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                updateStatus("Fullscreen mode");
            }
            SLNDA_Frame.setVisible(true);
        });
        
        viewMenu.add(pluginBarItem);
        viewMenu.add(fullscreenItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        
        JMenuItem aboutItem = new JMenuItem("About SLNDA");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(SLNDA_Frame, 
                "SLNDA Software v1.0\n\n" +
                "Professional Audio Recording & Live Performance Software\n\n" +
                "© 2024 SLNDA Team\n" +
                "All rights reserved.", 
                "About SLNDA", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JMenuItem documentationItem = new JMenuItem("Documentation");
        documentationItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(SLNDA_Frame, 
                "Documentation:\n\n" +
                "1. Click 'Start' to begin recording\n" +
                "2. Click 'Stop' to stop recording\n" +
                "3. Use volume slider to adjust levels\n" +
                "4. Select plugins from the left panel\n" +
                "5. Save your project via File > Save Project", 
                "Documentation", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        helpMenu.add(aboutItem);
        helpMenu.add(documentationItem);
        
        navbar.add(fileMenu);
        navbar.add(editMenu);
        navbar.add(viewMenu);
        navbar.add(helpMenu);
        
        SLNDA_Frame.setJMenuBar(navbar);

        // ============ Plugin Bar (Left Sidebar) ============
        pluginBar = new JPanel();
        pluginBar.setLayout(new BoxLayout(pluginBar, BoxLayout.Y_AXIS));
        pluginBar.setPreferredSize(new Dimension(250, 1080));
        pluginBar.setBackground(new Color(35, 35, 35));
        pluginBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Plugin Bar Title
        JLabel pluginTitle = new JLabel("Plugins");
        pluginTitle.setFont(new Font("Arial", Font.BOLD, 18));
        pluginTitle.setForeground(Color.WHITE);
        pluginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pluginBar.add(pluginTitle);
        pluginBar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Plugin Buttons
        String[] plugins = {"Equalizer", "Reverb", "Delay", "Compressor", "Limiter", "Distortion"};
        for (String pluginName : plugins) {
            JButton pluginButton = new JButton(pluginName);
            pluginButton.setMaximumSize(new Dimension(230, 40));
            pluginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            pluginButton.setBackground(new Color(60, 60, 60));
            pluginButton.setForeground(Color.WHITE);
            pluginButton.setFocusPainted(false);
            pluginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            pluginButton.addActionListener(e -> {
                updateStatus(pluginName + " plugin activated");
                JOptionPane.showMessageDialog(SLNDA_Frame, 
                    pluginName + " Plugin Activated!\n\nPlugin is now processing audio.", 
                    pluginName, 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            pluginBar.add(pluginButton);
            pluginBar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        SLNDA_Frame.add(pluginBar, BorderLayout.WEST);

        // ============ Main Center Panel ============
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(50, 50, 50));
        
        // Waveform Display Area
        waveformDisplay = new JTextArea();
        waveformDisplay.setEditable(false);
        waveformDisplay.setBackground(new Color(25, 25, 25));
        waveformDisplay.setForeground(new Color(0, 255, 0));
        waveformDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        waveformDisplay.setText("=== SLNDA AUDIO DISPLAY ===\n\nWaiting for input...\n\nPress START to begin recording.");
        
        JScrollPane scrollPane = new JScrollPane(waveformDisplay);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Control Panel (Bottom of Center)
        JPanel SLNDA_Control_Panel = new JPanel();
        SLNDA_Control_Panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        SLNDA_Control_Panel.setBackground(new Color(50, 50, 50));
        SLNDA_Control_Panel.setPreferredSize(new Dimension(800, 150));

        // Creating components for control panel
        JButton startButton = new JButton("⏺ START");
        JButton stopButton = new JButton("⏹ STOP");
        JButton recordButton = new JButton("🎙 RECORD");
        JSlider volumeSlider = new JSlider(0, 100, 50);

        // Adjusting the size and style of buttons
        Dimension buttonSize = new Dimension(150, 50);
        startButton.setPreferredSize(buttonSize);
        stopButton.setPreferredSize(buttonSize);
        recordButton.setPreferredSize(buttonSize);
        
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        startButton.setFont(buttonFont);
        stopButton.setFont(buttonFont);
        recordButton.setFont(buttonFont);
        
        startButton.setBackground(new Color(0, 150, 0));
        startButton.setForeground(Color.WHITE);
        stopButton.setBackground(new Color(200, 0, 0));
        stopButton.setForeground(Color.WHITE);
        recordButton.setBackground(new Color(200, 50, 50));
        recordButton.setForeground(Color.WHITE);
        
        startButton.setFocusPainted(false);
        stopButton.setFocusPainted(false);
        recordButton.setFocusPainted(false);

        // Volume Slider styling
        volumeSlider.setPreferredSize(new Dimension(300, 50));
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setBackground(new Color(50, 50, 50));
        volumeSlider.setForeground(Color.WHITE);
        
        // Adding Listeners to Buttons and Slider
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    isPlaying = true;
                    SLNDA_Live_Performance livePerformance = new SLNDA_Live_Performance();
                    livePerformance.startLivePerformance();
                    waveformDisplay.append("\n\n▶ PLAYBACK STARTED at " + java.time.LocalTime.now());
                    updateStatus("Playing...");
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    
                    // Simulate level meter
                    startLevelMeter();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPlaying = false;
                isRecording = false;
                waveformDisplay.append("\n■ STOPPED at " + java.time.LocalTime.now());
                updateStatus("Stopped");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                recordButton.setEnabled(true);
                
                // Stop level meter
                if (levelMeterTimer != null) {
                    levelMeterTimer.cancel();
                    levelMeterTimer = null;
                }
                
                // Stop recording if active
                if (targetLine != null && targetLine.isOpen()) {
                    targetLine.stop();
                    targetLine.close();
                }
            }
        });
        
        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRecording) {
                    isRecording = true;
                    waveformDisplay.append("\n\n🎙 RECORDING STARTED at " + java.time.LocalTime.now());
                    updateStatus("Recording...");
                    recordButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    
                    // Start actual audio recording
                    new Thread(() -> startRecording()).start();
                    startLevelMeter();
                }
            }
        });

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int volume = volumeSlider.getValue();
                updateStatus("Volume: " + volume + "%");
                
                // Visual feedback in waveform display
                if (volumeSlider.getValueIsAdjusting()) {
                    String volumeBar = "Volume: [" + "█".repeat(volume / 5) + 
                                     "░".repeat(20 - volume / 5) + "] " + volume + "%";
                    waveformDisplay.append("\n" + volumeBar);
                }
            }
        });

        // Adding components to the control panel
        SLNDA_Control_Panel.add(startButton);
        SLNDA_Control_Panel.add(stopButton);
        SLNDA_Control_Panel.add(recordButton);
        SLNDA_Control_Panel.add(new JLabel("Volume:"));
        SLNDA_Control_Panel.add(volumeSlider);

        centerPanel.add(SLNDA_Control_Panel, BorderLayout.SOUTH);
        SLNDA_Frame.add(centerPanel, BorderLayout.CENTER);

        // ============ Status Bar (Bottom) ============
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(1920, 30));
        statusBar.setBackground(new Color(30, 30, 30));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel versionLabel = new JLabel("SLNDA v1.0");
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        SLNDA_Frame.add(statusBar, BorderLayout.SOUTH);
        
        // Make frame visible (only once at the end)
        SLNDA_Frame.setVisible(true);
    }

    // Helper method to update status bar
    private static void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message + " | " + java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    // Level meter simulation
    private static void startLevelMeter() {
        levelMeterTimer = new Timer();
        levelMeterTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying || isRecording) {
                    int level = (int)(Math.random() * 100);
                    String meter = "Level: [" + "█".repeat(level / 5) + 
                                 "░".repeat(20 - level / 5) + "] " + level + " dB";
                    SwingUtilities.invokeLater(() -> {
                        waveformDisplay.append("\n" + meter);
                        waveformDisplay.setCaretPosition(waveformDisplay.getDocument().getLength());
                    });
                }
            }
        }, 0, 1000);
    }
    
    // Actual audio recording method
    private static void startRecording() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                SwingUtilities.invokeLater(() -> {
                    waveformDisplay.append("\n❌ ERROR: Audio line not supported!");
                    updateStatus("Recording failed - device not supported");
                });
                return;
            }
            
            targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(format);
            targetLine.start();
            
            SwingUtilities.invokeLater(() -> {
                waveformDisplay.append("\n✓ Audio device initialized successfully");
                updateStatus("Recording in progress...");
            });
            
            // Recording loop
            byte[] buffer = new byte[4096];
            while (isRecording && targetLine != null) {
                int bytesRead = targetLine.read(buffer, 0, buffer.length);
                // Here you would normally write to a file or process the audio
                // For demo purposes, we just show that we're capturing data
            }
            
        } catch (LineUnavailableException e) {
            SwingUtilities.invokeLater(() -> {
                waveformDisplay.append("\n❌ ERROR: " + e.getMessage());
                updateStatus("Recording error");
            });
        }
    }

    public static class SLNDA_Live_Performance {
        // Live Performance Function 
        public void startLivePerformance() {
            performAction();
        }

        public void performAction() {
            System.out.println("Live Performance is Running...");
        }
    }
}