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

        frame = new JFrame("Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 630);
        int x = preferences.getInt("windowX", 100);
        int y = preferences.getInt("windowY", 100);
        int width = preferences.getInt("width", 800);
        int height = preferences.getInt("height", 600);
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(styles.clrDark);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setForeground(styles.clrPrimary);
        timeLabel.setFont(styles.baseFont.deriveFont(Font.BOLD, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(timeLabel, gbc);

        startPauseResumeButton = new JButton("Start");
        startPauseResumeButton.setBackground(styles.clrButtonStart);
        startPauseResumeButton.setForeground(styles.clrLight);
        startPauseResumeButton.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(startPauseResumeButton, gbc);

        stopButton = new JButton("Stop");
        stopButton.setBackground(styles.clrButtonStop);
        stopButton.setForeground(styles.clrLight);
        stopButton.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(stopButton, gbc);
        stopButton.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setTimePanel = new SetTimePanel(this);
        frame.add(setTimePanel, gbc);

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
