import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PresetsPanel extends JPanel {
    public  PresetsPanel(Color clrDark, Color clrLight, Font baseFont) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(clrDark);

        JLabel presetsPanelLabel = new JLabel("Presets");
        presetsPanelLabel.setForeground(clrLight);
        presetsPanelLabel.setFont(baseFont.deriveFont(Font.BOLD, 16));
        add(presetsPanelLabel);

        File xmlFile = new File("presets.xml");
        PresetsManager presetsManager = new PresetsManager();
        List<String[]> loadedPresets = presetsManager.loadPresetsFromFile(xmlFile);

        for (String[] preset : loadedPresets) {
            JButton button = new JButton();
            ClockTime time = TimeConverter.getHourMinutesSecondsFromSeconds(Integer.parseInt(preset[1]));
            button.setText(TimeConverter.getTimeString(time.hours, time.minutes, time.seconds));
            this.add(button);
        }
    }
}
