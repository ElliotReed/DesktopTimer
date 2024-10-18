import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PresetsPanel extends JPanel {
    private final List<String[]> loadedPresets;
    private PresetsManager presetsManager;
    public  PresetsPanel(SetTimePanel setTimePanel) {
        CustomStyles styles = new CustomStyles();

        setLayout(new BorderLayout());
        setBackground(styles.clrDark);

        JLabel presetsPanelLabel = new JLabel("Presets");
        presetsPanelLabel.setForeground(styles.clrLight);
        presetsPanelLabel.setFont(styles.baseFont.deriveFont(Font.BOLD, 16));
        presetsPanelLabel.setBorder(new EmptyBorder(10,10,10,10));
        add(presetsPanelLabel, BorderLayout.PAGE_START);

        presetsManager = new PresetsManager();
        loadedPresets = presetsManager.loadPresetsFromFile();
        JPanel presetPresets = new JPanel();
        presetPresets.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        for (String[] preset : loadedPresets) {
            JButton button = new JButton();
            ClockTime time = TimeConverter.getHourMinutesSecondsFromSeconds(Integer.parseInt(preset[1]));
            button.setText(TimeConverter.getTimeString(time.hours, time.minutes, time.seconds));
            button.addActionListener(e -> {
                int presetTimeInSeconds = Integer.parseInt(preset[1]);
                setTimePanel.handlePresetPanel(presetTimeInSeconds);
            });
            presetPresets.add(button);
        }

        add(presetPresets,BorderLayout.CENTER);
    }

    public void addPreset(int seconds) {
        String nextId = presetsManager.getNextPresetId();
        String[] newPreset = {nextId, String.valueOf(seconds)};
        loadedPresets.add(newPreset);
        String[][] allPresets = new String[loadedPresets.size()][];
        loadedPresets.toArray(allPresets);
        presetsManager.savePresetsToFile(allPresets);
        JOptionPane.showMessageDialog(this, "Preset saved successfully!");
    }
}
