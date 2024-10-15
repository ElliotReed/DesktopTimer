import javax.swing.*;
import java.awt.*;

public class Spinner extends JSpinner {
    public Spinner(SpinnerNumberModel spinnerNumberModel) {
        CustomStyles styles = new CustomStyles();
        Color clrSpinnerBorder =   styles.createColors(styles.clrPrimary);
        Dimension spinnerButtonSize = new Dimension(60, 50);

        setAlignmentX(Component.LEFT_ALIGNMENT);
        setFont(styles.baseFont.deriveFont(Font.BOLD, 18));
        setPreferredSize(spinnerButtonSize);
        setBorder(BorderFactory.createLineBorder(clrSpinnerBorder, 2));

        JComponent spinnerComponent = (JComponent) getEditor().getComponent(0);
        spinnerComponent.setBackground(styles.clrDark);
        spinnerComponent.setForeground(styles.clrLight);

    }
}
