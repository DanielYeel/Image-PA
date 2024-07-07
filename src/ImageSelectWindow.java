import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSelectWindow extends JFrame {

    public ImageSelectWindow(){
        setTitle("Select an Image");
        setSize(300,100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setFocusable(true);
        setLocationRelativeTo(null);

        JButton openButton = new JButton("Open Image");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        add(openButton,BorderLayout.CENTER);
    }

    private void openImage(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try{
                BufferedImage image = ImageIO.read(selectedFile);
                ImageProcessingWindow processingWindow = new ImageProcessingWindow(image);
                processingWindow.setVisible(true);
                dispose();
            } catch (IOException ex){
                JOptionPane.showMessageDialog(this, "Failed to load image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
