import java.awt.*;

public class CustomColors {
//    public final Color[] colors;
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


