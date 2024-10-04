import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

public class TimerApp {
    public enum TimerState {
        PLAYING,
        PAUSED,
        FINISHED
    }

    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private Timer timer;

    private  TimerState state;
    private final JButton startPauseResumeButton;
    private final JButton stopButton;
    private final JFrame frame;
    private final JLabel timeLabel;
    private final JSpinner hoursSpinner;
    private final JSpinner minutesSpinner;
    private final JSpinner secondsSpinner;
    private int remainingTime;

    public TimerApp() {
        this.state = null;
        CustomStyles styles = new CustomStyles();
        Color clrSpinnerBorder =   styles.createColors(styles.clrPrimary);

        frame = new JFrame("Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 630);
        int x = prefs.getInt("windowX", 100);
        int y = prefs.getInt("windowY", 100);
        int width = prefs.getInt("width", 800);
        int height = prefs.getInt("height", 600);
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(styles.clrDark);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Dimension spinnerButtonSize = new Dimension(60, 50);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        hoursSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        hoursSpinner.setFont(styles.baseFont.deriveFont(Font.BOLD, 18));
        hoursSpinner.setPreferredSize(spinnerButtonSize);
        hoursSpinner.setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));
        JComponent hoursComponent = (JComponent) hoursSpinner.getEditor().getComponent(0);
        hoursComponent.setBackground(styles.clrDark);
        hoursComponent.setForeground(styles.clrLight);

        minutesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        minutesSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        minutesSpinner.setFont(styles.baseFont.deriveFont(Font.BOLD, 18));
        minutesSpinner.setPreferredSize(spinnerButtonSize);
        minutesSpinner.setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));
        JComponent minutesComponent = (JComponent) minutesSpinner.getEditor().getComponent(0);
        minutesComponent.setBackground(styles.clrDark);
        minutesComponent.setForeground(Color.lightGray);

        secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        secondsSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondsSpinner.setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));
        secondsSpinner.setFont(styles.baseFont.deriveFont(Font.BOLD, 18));
        secondsSpinner.setPreferredSize(spinnerButtonSize);
        JComponent secondsComponent = (JComponent) secondsSpinner.getEditor().getComponent(0);
        secondsComponent.setBackground(styles.clrDark);
        secondsComponent.setForeground(styles.clrLight);

        JLabel hoursLabel = new JLabel("Hours:");
        hoursLabel.setForeground(styles.clrLight);
        hoursLabel.setFont(styles.baseFont);

        JLabel minutesLabel = new JLabel("Minutes:");
        minutesLabel.setForeground(styles.clrLight);
        minutesLabel.setFont(styles.baseFont);

        JLabel secondsLabel = new JLabel("Seconds:");
        secondsLabel.setForeground(styles.clrLight);
        secondsLabel.setFont(styles.baseFont);
        secondsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel hoursPanel = new JPanel();
        hoursPanel.setLayout(new BoxLayout(hoursPanel,BoxLayout.Y_AXIS));
        hoursPanel.setBackground(styles.clrDark);
        hoursPanel.add(hoursLabel);
        hoursPanel.add(hoursSpinner);

        JPanel minutesPanel = new JPanel();
        minutesPanel.setLayout(new BoxLayout(minutesPanel,BoxLayout.PAGE_AXIS));
        minutesPanel.setBackground(styles.clrDark);
        minutesPanel.add(minutesLabel);
        minutesPanel.add(minutesSpinner);

        JPanel secondsPanel = new JPanel();
        secondsPanel.setLayout(new BoxLayout(secondsPanel,BoxLayout.Y_AXIS));
        secondsPanel.setBackground(styles.clrDark);
        secondsPanel.add(secondsLabel);
        secondsPanel.add(secondsSpinner);

        JPanel timeSetPanel = new JPanel();
        timeSetPanel.setBackground(styles.clrDark);

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
        timeLabel.setForeground(styles.clrPrimary);
        timeLabel.setFont(styles.baseFont.deriveFont(Font.BOLD, 124));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(timeLabel, gbc);

        startPauseResumeButton = new JButton("Start");
        startPauseResumeButton.setBackground(styles.clrButtonStart);
        startPauseResumeButton.setForeground(styles.clrLight);
        startPauseResumeButton.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(startPauseResumeButton, gbc);

        stopButton = new JButton("Stop");
        stopButton.setBackground(styles.clrButtonStop);
        stopButton.setForeground(styles.clrLight);
        stopButton.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(stopButton, gbc);
        stopButton.setEnabled(false);

        PresetsPanel presetsPanel = new PresetsPanel(styles.clrDark, styles.clrLight, styles.baseFont);
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(presetsPanel, gbc);

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
            if (state == null || state == TimerState.FINISHED) {
               if(state == null) {
                updateTimerFromSpinners();
               }

                startTimer();
                state = TimerState.PLAYING;
                startPauseResumeButton.setText("Pause");
                stopButton.setEnabled(true);
            } else {
                if (state == TimerState.PLAYING) {
                    timer.stop();
                    startPauseResumeButton.setText("Resume");
                    state = TimerState.PAUSED;
                } else if (state == TimerState.PAUSED) {
                    startTimer();
                    state = TimerState.PLAYING;
                    startPauseResumeButton.setText("Pause");
                }
            }
        });

        startPauseResumeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                startPauseResumeButton.setBackground(styles.clrButtonDefault);
                startPauseResumeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                startPauseResumeButton.setBackground(styles.clrButtonStart);
            }
        });

        hoursSpinner.addChangeListener(e -> updateTimerFromSpinners());

        minutesSpinner.addChangeListener(e -> updateTimerFromSpinners());

        secondsSpinner.addChangeListener(e -> updateTimerFromSpinners());

        stopButton.addActionListener(e -> {
            timer.stop();
            state = null;
            stopButton.setEnabled(false);
            updateTimerFromSpinners();
            startPauseResumeButton.setText("Start");
        });

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                stopButton.setBackground(styles.clrButtonDefault);
                stopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                stopButton.setBackground(styles.clrButtonStop);
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
                    state = TimerState.FINISHED;
                    playSound();
                    startPauseResumeButton.setText("Start");
                    updateTimerFromSpinners();
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
        ClockTime time = TimeConverter.getHourMinutesSecondsFromSeconds(remainingTime);
        timeLabel.setText(TimeConverter.getTimeString(time.hours, time.minutes, time.seconds));
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
