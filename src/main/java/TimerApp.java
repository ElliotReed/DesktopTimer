import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.prefs.Preferences;

public class TimerApp {
    public enum TimerState {
        PLAYING,
        PAUSED,
        FINISHED
    }

    private final Preferences preferences = Preferences.userRoot().node(this.getClass().getName());
    private Timer timer;

    private  TimerState state;
    private final JButton startPauseResumeButton;
    private final JButton stopButton;
    private final JFrame frame;
    private final JLabel timeLabel;
    SetTimePanel setTimePanel;

    private int remainingTime;

    public TimerApp() {
        this.state = null;
        CustomStyles styles = new CustomStyles();

        int x = preferences.getInt("windowX", 100);
        int y = preferences.getInt("windowY", 100);
        int width = preferences.getInt("width", 800);
        int height = preferences.getInt("height", 600);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/appIcon.png")));

        frame = new JFrame("Timer");
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(styles.clrDark);

        frame.add(Box.createHorizontalStrut(25), BorderLayout.WEST);
        frame.add(Box.createHorizontalStrut(25), BorderLayout.EAST);

     ;   timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setForeground(styles.clrPrimary);
        timeLabel.setFont(styles.baseFont.deriveFont(Font.BOLD, 0));
        frame.add(timeLabel, BorderLayout.CENTER);
        resizeFont(timeLabel);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        lowerPanel.setBackground(styles.clrDark);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        controls.setBackground(styles.clrDark);

        startPauseResumeButton = new JButton("Start");
        startPauseResumeButton.setBackground(styles.clrButtonStart);
        startPauseResumeButton.setForeground(styles.clrLight);
        startPauseResumeButton.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        controls.add(startPauseResumeButton);

        stopButton = new JButton("Stop");
        stopButton.setBackground(styles.clrButtonStop);
        stopButton.setForeground(styles.clrLight);
        stopButton.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        stopButton.setEnabled(false);
        controls.add(stopButton);

        lowerPanel.add(controls);

        setTimePanel = new SetTimePanel(this);
        lowerPanel.add(Box.createVerticalStrut(10));
        lowerPanel.add(setTimePanel);

        frame.add(lowerPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                preferences.putInt("windowX", frame.getX());
                preferences.putInt("windowY", frame.getY());
                preferences.putInt("width", frame.getWidth());
                preferences.putInt("height", frame.getHeight());
                System.exit(0);
            }
        });

        timeLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFont(timeLabel);
            }
        });

        startPauseResumeButton.addActionListener((ActionEvent e) -> {
            if (state == null || state == TimerState.FINISHED) {
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

        stopButton.addActionListener(e -> {
            timer.stop();
            state = null;
            stopButton.setEnabled(false);
            startPauseResumeButton.setText("Start");
            remainingTime = setTimePanel.getSetTime();
            updateTimeLabel();
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

    public void setRemainingTime(int seconds) {
        remainingTime = seconds;
        updateTimeLabel();
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
                    remainingTime = setTimePanel.getSetTime();
                    updateTimeLabel();
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

    private void updateTimeLabel() {
        ClockTime clockTime = TimeConverter.getHourMinutesSecondsFromSeconds(remainingTime);
        timeLabel.setText(TimeConverter.getTimeString(clockTime.hours, clockTime.minutes, clockTime.seconds));
    }

    private void resizeFont(JLabel label) {
        Font labelFont = label.getFont();
        String labelText = label.getText();

        int labelWidth = label.getWidth();
        int labelHeight = label.getHeight();

        if (labelWidth <= 0 || labelHeight <= 0) {
            return;
        }

        int newFontSize = Math.min((labelWidth / labelText.length()) * 2, labelHeight) - 1;
        label.setFont(new Font(labelFont.getName(), Font.BOLD, newFontSize));
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
