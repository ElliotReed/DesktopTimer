import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SetTimePanel extends JPanel {
    private final TimerApp timerApp;
    private final JSpinner hoursSpinner;
    private final JSpinner minutesSpinner;
    private final JSpinner secondsSpinner;

    public SetTimePanel(TimerApp timerApp) {
        this.timerApp = timerApp;
        PresetsManager presetsManager = new PresetsManager();
        CustomStyles styles = new CustomStyles();
        Color clrSpinnerBorder =   styles.createColors(styles.clrPrimary);
        GridBagConstraints gbc = new GridBagConstraints();
        Dimension spinnerButtonSize = new Dimension(60, 50);

        this.setBackground(styles.clrDark);

        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new GridBagLayout());
        spinnerPanel.setBackground(styles.clrDark);

        hoursSpinner = new Spinner(new SpinnerNumberModel(0, 0, 23, 1));
        minutesSpinner = new Spinner(new SpinnerNumberModel(0, 0, 59, 1));
        secondsSpinner = new Spinner(new SpinnerNumberModel(0, 0, 59, 1));

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

        // Set GridBagConstraints for centering
        gbc.fill = GridBagConstraints.NONE; // No automatic stretching
        gbc.anchor = GridBagConstraints.CENTER; // Center components in grid cells
        gbc.weightx = 0; // Don't expand components horizontally
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        spinnerPanel.add(hoursPanel, gbc);

        gbc.gridx = 1;
        spinnerPanel.add(minutesPanel, gbc);

        gbc.gridx = 2;
        spinnerPanel.add(secondsPanel, gbc);

        JButton savePresetButton = new JButton("Save as Preset");
        gbc.gridx = 3;
        spinnerPanel.add(savePresetButton, gbc);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(spinnerPanel);
        File xmlFile = new File("presets.xml");
        PresetsPanel presetsPanel = new PresetsPanel(this);
        this.add(presetsPanel);

        hoursSpinner.addChangeListener(e -> onSetTimeChange());

        minutesSpinner.addChangeListener(e -> onSetTimeChange());

        secondsSpinner.addChangeListener(e -> onSetTimeChange());

        savePresetButton.addActionListener(e -> {
        });
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

