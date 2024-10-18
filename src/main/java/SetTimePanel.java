import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SetTimePanel extends JPanel {
    private final TimerApp timerApp;
    private final JSpinner hoursSpinner;
    private final JSpinner minutesSpinner;
    private final JSpinner secondsSpinner;
    private PresetsPanel presetsPanel;
    private CustomStyles styles;

    public SetTimePanel(TimerApp timerApp) {
        this.timerApp = timerApp;
        styles = new CustomStyles();

        PresetsManager presetsManager = new PresetsManager();
        Color clrSpinnerBorder =   styles.createColors(styles.clrPrimary);
        Dimension spinnerButtonSize = new Dimension(60, 50);

        this.setBackground(styles.clrDark);

        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setBackground(styles.clrDark);

        JPanel hoursPanel = new JPanel();
        JLabel hoursLabel = new JLabel("Hours:");
        styleSpinner(hoursPanel, hoursLabel);
        hoursSpinner = new Spinner(new SpinnerNumberModel(0, 0, 23, 1));
        hoursPanel.add(hoursLabel);
        hoursPanel.add(hoursSpinner);

        JPanel minutesPanel = new JPanel();
        JLabel minutesLabel = new JLabel("Minutes:");
        styleSpinner(minutesPanel, minutesLabel);
        minutesSpinner = new Spinner(new SpinnerNumberModel(0, 0, 59, 1));
        minutesPanel.add(minutesLabel);
        minutesPanel.add(minutesSpinner);

        JPanel secondsPanel = new JPanel();
        JLabel secondsLabel = new JLabel("Seconds:");
        styleSpinner(secondsPanel, secondsLabel);
        secondsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondsSpinner = new Spinner(new SpinnerNumberModel(0, 0, 59, 1));
        secondsPanel.add(secondsLabel);
        secondsPanel.add(secondsSpinner);

        spinnerPanel.add(hoursPanel);
        spinnerPanel.add(minutesPanel);
        spinnerPanel.add(secondsPanel);

        JButton savePresetButton = new JButton("Save as Preset");

        spinnerPanel.add(Box.createHorizontalStrut(25));
        spinnerPanel.add(savePresetButton);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(spinnerPanel);
        presetsPanel = new PresetsPanel(this);
        this.add(presetsPanel);

        hoursSpinner.addChangeListener(e -> onSetTimeChange());

        minutesSpinner.addChangeListener(e -> onSetTimeChange());

        secondsSpinner.addChangeListener(e -> onSetTimeChange());

        savePresetButton.addActionListener(e -> {
            presetsPanel.addPreset(getSetTime());
        });
    }

    private void styleSpinner(JPanel panel, JLabel label) {
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(styles.clrDark);

        label.setForeground(styles.clrLight);
        label.setFont(styles.baseFont);
    }

    private void onSetTimeChange() {
        timerApp.setRemainingTime(getSetTime());
    }

    public int getSetTime() {
        int hours = (int) hoursSpinner.getValue();
        int minutes = (int) minutesSpinner.getValue();
        int seconds = (int) secondsSpinner.getValue();
        return TimeConverter.getTotalSecondsFromHoursMinutesSeconds(hours,minutes,seconds);
    }

    public void handlePresetPanel(int seconds) {
        ClockTime clockTime = TimeConverter.getHourMinutesSecondsFromSeconds(seconds);
        hoursSpinner.setValue(clockTime.hours);
        minutesSpinner.setValue(clockTime.minutes);
        secondsSpinner.setValue(clockTime.seconds);
    }
}

