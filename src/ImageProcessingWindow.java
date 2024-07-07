import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageProcessingWindow extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private JLabel imageLabel;
    private int divisionX = -1;
    private String selectedManipulation = "Grayscale";

    public ImageProcessingWindow(BufferedImage image){
        this.originalImage = image;
        this.currentImage = deepCopy(image);
        setTitle("Image PA");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        imageLabel = new JLabel(new ImageIcon(resizeImage(image, 780, 600))) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (divisionX != -1) {
                    g.setColor(Color.RED);
                    g.drawLine(divisionX, 0, divisionX, getHeight());
                }
            }
        };

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        add(buttonPanel, BorderLayout.SOUTH);

        addButton(buttonPanel, "Grayscale", e -> selectedManipulation = "Grayscale");
        addButton(buttonPanel, "Negative", e -> selectedManipulation = "Negative");
        addButton(buttonPanel, "No Red", e -> selectedManipulation = "Eliminate Red");
        addButton(buttonPanel, "No Green", e -> selectedManipulation = "Eliminate Green");
        addButton(buttonPanel, "No Blue", e -> selectedManipulation = "Eliminate Blue");
        addButton(buttonPanel, "Poster", e -> selectedManipulation = "Poster");
        addButton(buttonPanel, "Light", e -> selectedManipulation = "Lighter");
        addButton(buttonPanel, "Dark", e -> selectedManipulation = "Darker");
        addButton(buttonPanel, "Tint", e -> selectedManipulation = "Tint");
        addButton(buttonPanel, "Sepia", e -> selectedManipulation = "Sepia");

        imageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                divisionX = e.getX();
                applyMouseManipulation();
                imageLabel.repaint();
            }
        });

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                saveCurrentManipulation();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveImageOnExit();
            }
        });

    }

    private void addButton(JPanel panel, String label, ActionListener action) {
        JButton button = new JButton(label);
        button.addActionListener(action);
        panel.add(button);
    }

    private Image resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        float ratio = Math.min((float) targetWidth / width, (float) targetHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return scaledImage;
    }

    private void applyMouseManipulation() {
        if (divisionX == -1) return;
        BufferedImage manipulatedImage = applyManipulationToDivision(divisionX);
        updateImageLabel(manipulatedImage);
    }

    private BufferedImage applyManipulationToDivision(int divisionX) {
        int imageWidth = originalImage.getWidth();
        int labelWidth = imageLabel.getWidth();

        int divisionPoint = (int) (((double) divisionX / labelWidth) * imageWidth);

        BufferedImage manipulatedImage = deepCopy(originalImage);

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < divisionPoint; x++) {
                int rgba = originalImage.getRGB(x, y);
                Color col = new Color(rgba, true);
                Color newCol = applyManipulation(col);
                manipulatedImage.setRGB(x, y, newCol.getRGB());
            }
        }
        return manipulatedImage;
    }

    private Color applyManipulation(Color col){
        return switch (selectedManipulation) {
            case "Grayscale" -> applyGrayscale(col);
            case "Negative" -> applyNegative(col);
            case "Eliminate Red" -> applyEliminateRed(col);
            case "Eliminate Green" -> applyEliminateGreen(col);
            case "Eliminate Blue" -> applyEliminateBlue(col);
            case "Poster" -> applyPoster(col);
            case "Lighter" -> applyLighter(col);
            case "Darker" -> applyDarker(col);
            case "Tint" -> applyTint(col);
            case "Sepia" -> applySepia(col);
            default -> col;
        };
    }

    private void saveCurrentManipulation() {
        if (divisionX == -1) return;
        currentImage = applyManipulationToDivision(divisionX);
        originalImage = deepCopy(currentImage);
        updateImageLabel(currentImage);
    }

    private void updateImageLabel(BufferedImage newImage) {
        imageLabel.setIcon(new ImageIcon(resizeImage(newImage, 780, 600)));
        imageLabel.revalidate();
        imageLabel.repaint();
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private Color applyGrayscale(Color col) {
        int gray = (col.getRed() + col.getGreen() + col.getBlue()) / 3;
        return new Color(gray, gray, gray);
    }

    private Color applyNegative(Color col) {
        return new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
    }

    private Color applyEliminateRed(Color col) {
        return new Color(0, col.getGreen(), col.getBlue());
    }

    private Color applyEliminateGreen(Color col) {
        return new Color(col.getRed(), 0, col.getBlue());
    }

    private Color applyEliminateBlue(Color col) {
        return new Color(col.getRed(), col.getGreen(), 0);
    }

    private Color applyPoster(Color col){
        int r = (col.getRed() /64) *64;
        int g = (col.getGreen() /64) *64;
        int b = (col.getBlue() /64) *64;
        return new Color(r,g,b);
    }

    private Color applyLighter(Color col) {
        int r = Math.min(col.getRed() + 30, 255);
        int g = Math.min(col.getGreen() + 30, 255);
        int b = Math.min(col.getBlue() + 30, 255);
        return new Color(r, g, b);
    }

    private Color applyDarker(Color col) {
        int r = Math.max(col.getRed() - 30, 0);
        int g = Math.max(col.getGreen() - 30, 0);
        int b = Math.max(col.getBlue() - 30, 0);
        return new Color(r, g, b);
    }

    private Color applyTint(Color col) {
        int r = Math.min(col.getRed() + 30, 255);
        return new Color(r, col.getGreen(), col.getBlue());
    }

    private Color applySepia(Color col) {
        int r = col.getRed();
        int g = col.getGreen();
        int b = col.getBlue();

        int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
        int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
        int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

        tr = Math.min(tr, 255);
        tg = Math.min(tg, 255);
        tb = Math.min(tb, 255);

        return new Color(tr, tg, tb);
    }

    private void saveImageOnExit(){
        File downloadsDir = new File(System.getProperty("user.home"), "Downloads");

        String randomFileName = "image_" + new Random().nextInt(100000) + ".png";
        File outputFile = new File(downloadsDir, randomFileName);

        try {
            ImageIO.write(currentImage, "png", outputFile);
            System.out.println("Image saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
