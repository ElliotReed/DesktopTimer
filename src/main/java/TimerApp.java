import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

public class TimerApp {
    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private Timer timer;
    private final JButton startPauseResumeButton;
    private final JButton stopButton;
    private final JFrame frame;
    private final JLabel timeLabel;
    private final JSpinner hoursSpinner;
    private final JSpinner minutesSpinner;
    private final JSpinner secondsSpinner;
    private boolean isPaused = false;
    private boolean isPlaying;
    private int remainingTime;

    public TimerApp() {
        Font baseFont = new Font("System", Font.PLAIN, 16);
        Color clrDark = Color.DARK_GRAY;
        Color clrLight = Color.LIGHT_GRAY;
        Color clrPrimary = Color.getHSBColor(0.12f,0.5f,0.75f);
        Color clrButtonDefault = Color.getHSBColor(0.2f,0.5f,0.5f);
        Color clrButtonStart = Color.getHSBColor(0.3f,0.5f,0.5f);
        Color clrButtonStop = Color.getHSBColor(0.0f,0.5f,0.5f);
        CustomColors customColors = new CustomColors();
        Color clrSpinnerBorder =   customColors.createColors(clrPrimary);

        frame = new JFrame("Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 630);
        int x = prefs.getInt("windowX", 100);
        int y = prefs.getInt("windowY", 100);
        int width = prefs.getInt("width", 400);
        int height = prefs.getInt("height", 600);
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(clrDark);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Dimension spinnerButtonSize = new Dimension(60, 50);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        hoursSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        hoursSpinner.setFont(baseFont.deriveFont(Font.BOLD, 18));
        hoursSpinner.setPreferredSize(spinnerButtonSize);
        hoursSpinner.setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));
        JComponent hoursComponent = (JComponent) hoursSpinner.getEditor().getComponent(0);
        hoursComponent.setBackground(clrDark);
        hoursComponent.setForeground(clrLight);
//        hoursComponent.setBorder(BorderFactory.createCompoundBorder(
//            hoursComponent.getBorder(),
//            BorderFactory.createEmptyBorder(5, 15, 5, 15)
//        ));

        minutesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        minutesSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        minutesSpinner.setFont(baseFont.deriveFont(Font.BOLD, 18));
        minutesSpinner.setPreferredSize(spinnerButtonSize);
        minutesSpinner.setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));
        JComponent minutesComponent = (JComponent) minutesSpinner.getEditor().getComponent(0);
        minutesComponent.setBackground(clrDark);
        minutesComponent.setForeground(Color.lightGray);
//        minutesComponent.setBorder(BorderFactory.createCompoundBorder(
//                minutesComponent.getBorder(),
//                BorderFactory.createEmptyBorder(5, 15, 5, 15)
//        ));

        secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        secondsSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondsSpinner.setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));
        secondsSpinner.setFont(baseFont.deriveFont(Font.BOLD, 18));
        secondsSpinner.setPreferredSize(spinnerButtonSize);
        JComponent secondsComponent = (JComponent) secondsSpinner.getEditor().getComponent(0);
        secondsComponent.setBackground(clrDark);
        secondsComponent.setForeground(clrLight);
//        secondsComponent.setBorder(BorderFactory.createCompoundBorder(
//                secondsComponent.getBorder(),
//                BorderFactory.createEmptyBorder(5, 15, 5, 15)
//        ));

        JLabel hoursLabel = new JLabel("Hours:");
        hoursLabel.setForeground(clrLight);
        hoursLabel.setFont(baseFont);

        JLabel minutesLabel = new JLabel("Minutes:");
        minutesLabel.setForeground(clrLight);
        minutesLabel.setFont(baseFont);

        JLabel secondsLabel = new JLabel("Seconds:");
        secondsLabel.setForeground(clrLight);
        secondsLabel.setFont(baseFont);
        secondsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel hoursPanel = new JPanel();
        hoursPanel.setLayout(new BoxLayout(hoursPanel,BoxLayout.Y_AXIS));
        hoursPanel.setBackground(clrDark);
        hoursPanel.add(hoursLabel);
        hoursPanel.add(hoursSpinner);

        JPanel minutesPanel = new JPanel();
        minutesPanel.setLayout(new BoxLayout(minutesPanel,BoxLayout.PAGE_AXIS));
        minutesPanel.setBackground(clrDark);
        minutesPanel.add(minutesLabel);
        minutesPanel.add(minutesSpinner);

        JPanel secondsPanel = new JPanel();
        secondsPanel.setLayout(new BoxLayout(secondsPanel,BoxLayout.Y_AXIS));
        secondsPanel.setBackground(clrDark);
        secondsPanel.add(secondsLabel);
        secondsPanel.add(secondsSpinner);

        JPanel timeSetPanel = new JPanel();
        timeSetPanel.setBackground(clrDark);

        // Set GridBagConstraints for centering
        gbc.fill = GridBagConstraints.NONE; // No automatic stretching
        gbc.anchor = GridBagConstraints.CENTER; // Center components in grid cells
        gbc.weightx = 0; // Don't expand components horizontally
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.ipadx = 10;
        timeSetPanel.add(hoursPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        timeSetPanel.add(minutesPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        timeSetPanel.add(secondsPanel, gbc);
        frame.add(timeSetPanel, gbc);

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setForeground(clrPrimary);
        timeLabel.setFont(baseFont.deriveFont(Font.BOLD, 124));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(timeLabel, gbc);

        startPauseResumeButton = new JButton("Start");
        startPauseResumeButton.setBackground(clrButtonStart);
        startPauseResumeButton.setForeground(clrLight);
        startPauseResumeButton.setFont(baseFont.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
//        gbc.fill = GridBagConstraints.NONE;
        frame.add(startPauseResumeButton, gbc);

        stopButton = new JButton("Stop");
        stopButton.setBackground(clrButtonStop);
        stopButton.setForeground(clrLight);
        stopButton.setFont(baseFont.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(stopButton, gbc);
        stopButton.setEnabled(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                prefs.putInt("windowX", frame.getX());
                prefs.putInt("windowY", frame.getY());
                prefs.putInt("width", frame.getWidth());
                prefs.putInt("height", frame.getHeight());
                System.exit(0);
            }
        });

        startPauseResumeButton.addActionListener((ActionEvent e) -> {
            if (!isPlaying) {
                int hours = (int) hoursSpinner.getValue();
                int minutes = (int) minutesSpinner.getValue();
                int seconds = (int) secondsSpinner.getValue();

                remainingTime = hours * 3600 + minutes * 60 + seconds;
                startTimer();
                isPlaying = true;
                startPauseResumeButton.setText("Pause");
                stopButton.setEnabled(true);
            } else {
                if (!isPaused) {
                System.out.println("isPlaying not paused");
                    startPauseResumeButton.setText("Resume");
                    timer.stop();
                    isPaused = true;
                } else  {
                    startTimer();
                    isPaused = false;
                    startPauseResumeButton.setText("Pause");
                }
            }
        });

        startPauseResumeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                startPauseResumeButton.setBackground(clrButtonDefault);
                startPauseResumeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                startPauseResumeButton.setBackground(clrButtonStart);
            }
        });

        hoursSpinner.addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               updateTimerFromSpinners();
           }
       });

        minutesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTimerFromSpinners();
            }
        });

        secondsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTimerFromSpinners();
            }
        });

        stopButton.addActionListener(e -> {
                timer.stop();
                isPlaying = false;
                isPaused = false;
                stopButton.setEnabled(false);
                startPauseResumeButton.setText("Play");
                updateTimerFromSpinners();
        });

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                stopButton.setBackground(clrButtonDefault);
                stopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                stopButton.setBackground(clrButtonStop);
            }
        });

        frame.setVisible(true);
    }

    private Timer setUpTimer() {
        if (timer != null) {
            timer.stop();
        }

       return new Timer(1000, e -> {
            if (remainingTime > 0) {
                remainingTime--;
                updateTimeLabel();

                if (remainingTime == 0) {
                    stopButton.setEnabled(false);
                    timer.stop();
                    isPlaying = false;
                    startPauseResumeButton.setText("Play");
                    playSound();  // Play the sound when the timer ends
                }
            } else {
                timer.stop();
            }
        });
    }

    private void startTimer() {
        timer = setUpTimer();
        timer.start();
    }

    private void updateTimerFromSpinners() {
        int hours = (int) hoursSpinner.getValue();
        int minutes = (int) minutesSpinner.getValue();
        int seconds = (int) secondsSpinner.getValue();

        remainingTime = hours * 3600 + minutes * 60 + seconds;
            updateTimeLabel();
    }

    private void updateTimeLabel() {
        int hours = remainingTime / 3600;
        int minutes =( remainingTime % 3600) / 60;
        int seconds = remainingTime % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d",hours, minutes, seconds));
    }

    private void playSound() {
        try {
            // Load the WAV file from resources
            URL soundURL = getClass().getResource("/alarm.wav");
            if (soundURL == null) {
                throw new IOException("Audio file not found");
            }

            // Get AudioInputStream for the WAV file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);

            // Get a clip and play the audio
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimerApp::new);
    }
}
