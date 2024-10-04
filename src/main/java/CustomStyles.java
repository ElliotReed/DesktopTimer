import java.awt.*;

public class CustomStyles {
    public Font baseFont = new Font("System", Font.PLAIN, 16);
    public Color clrDark = Color.DARK_GRAY;
    public Color clrLight = Color.LIGHT_GRAY;
    public Color clrPrimary = Color.getHSBColor(0.12f,0.5f,0.75f);
    public Color clrButtonDefault = Color.getHSBColor(0.2f,0.5f,0.5f);
    public Color clrButtonStart = Color.getHSBColor(0.3f,0.5f,0.5f);
    public Color clrButtonStop = Color.getHSBColor(0.0f,0.5f,0.5f);

    public Color createColors(Color baseColor) {
    return baseColor;
    }

    private Color modifyHSBColor() {
//        public static void main(String[] args) {
            // Step 1: Get the original color using getHSBColor
            Color originalColor = Color.getHSBColor(0.5f, 0.8f, 0.7f); // Example HSB values

            // Print the original color
            System.out.println("Original Color: " + originalColor);

            // Step 2: Convert the original color to HSB components
            float[] hsbValues = Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), null);

            // Step 3: Modify the brightness (b) component
            float newBrightness = hsbValues[2] * 0.9f;  // Reduce brightness by 10%

            // Ensure brightness stays within the range [0, 1]
            newBrightness = Math.min(1.0f, Math.max(0.0f, newBrightness));

            // Step 4: Create a new color with the modified HSB values
            Color modifiedColor = Color.getHSBColor(hsbValues[0], hsbValues[1], newBrightness);

            // Print the modified color
            System.out.println("Modified Color: " + modifiedColor);
//        }
        return modifiedColor;
    }
}


