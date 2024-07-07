import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageSelectWindow selectWindow = new ImageSelectWindow();
            selectWindow.setVisible(true);
        });
    }
}