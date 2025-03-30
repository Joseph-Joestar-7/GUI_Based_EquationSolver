import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

public class GraphPanel extends JPanel {
    private Equation equation;
    
    // Zoom factor: pixels per unit
    private double scale = 40;
    // Offsets for panning (in pixels)
    private double offsetX = 0;
    private double offsetY = 0;
    
    // Variables for panning
    private int prevMouseX, prevMouseY;
    
    public GraphPanel(Equation equation) {
        this.equation = equation;
        initMouseListeners();
    }
    
    private void initMouseListeners() {
        // Panning: track mouse press and drag events.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevMouseX = e.getX();
                prevMouseY = e.getY();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX += e.getX() - prevMouseX;
                offsetY += e.getY() - prevMouseY;
                prevMouseX = e.getX();
                prevMouseY = e.getY();
                repaint();
            }
        });
        
        addMouseWheelListener(e -> {
            double zoomFactor = (e.getPreciseWheelRotation() > 0) ? 0.9 : 1.1;
            scale *= zoomFactor;
            repaint();
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        int centerX = width / 2 + (int) offsetX;
        int centerY = height / 2 + (int) offsetY;
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, centerY, width, centerY);  
        g2.drawLine(centerX, 0, centerX, height);   
        
        drawAxisTicks(g2, centerX, centerY, width, height);
        
        g2.setColor(Color.RED);
        int prevScreenX = 0, prevScreenY = 0;
        boolean firstPoint = true;
        
        for (int screenX = 0; screenX < width; screenX++) {
            double mathX = (screenX - centerX) / scale;
            double mathY = equation.evaluate(mathX);
            
            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) {
                firstPoint = true;
                continue;
            }
            
            int screenY = centerY - (int)(mathY * scale);
            
            if (firstPoint) {
                prevScreenX = screenX;
                prevScreenY = screenY;
                firstPoint = false;
            } else {
                g2.draw(new Line2D.Double(prevScreenX, prevScreenY, screenX, screenY));
                prevScreenX = screenX;
                prevScreenY = screenY;
            }
        }
    }
    
    private void drawAxisTicks(Graphics2D g2, int centerX, int centerY, int width, int height) {
        g2.setColor(Color.BLACK);
        int tickSize = 5;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        
        
        double xMin = -(centerX) / scale;
        double xMax = (width - centerX) / scale;
        for (double x = Math.ceil(xMin); x <= xMax; x++) {
            int screenX = centerX + (int)(x * scale);
            g2.drawLine(screenX, centerY - tickSize, screenX, centerY + tickSize);
            if (Math.abs(x) > 1e-6) {
                g2.drawString(String.format("%.1f", x), screenX - 10, centerY + 15);
            } else {
                g2.drawString("0", screenX - 10, centerY + 15);
            }
        }
        
        double yMin = -(height - centerY) / scale;
        double yMax = centerY / scale;
        for (double y = Math.ceil(yMin); y <= yMax; y++) {
            int screenY = centerY - (int)(y * scale);
            g2.drawLine(centerX - tickSize, screenY, centerX + tickSize, screenY);
            if (Math.abs(y) > 1e-6) {
                g2.drawString(String.format("%.1f", y), centerX + 5, screenY + 5);
            } else {
                g2.drawString("0", centerX + 5, screenY + 5);
            }
        }
    }
}
