import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;


public final class DrawingFrame  {

    private static final int DEFAULT_SIZE = 512;
    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;
    private static boolean defer = false;
    private static double xmin, ymin, xmax, ymax;
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;
    private static JFrame frame;
    private DrawingFrame() { }
    static { init(); }
    
    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        xmin = 0;
        xmax = 1;
        ymin = 0;
        ymax = 1;
        clear();
        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                  RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        frame.setContentPane(draw);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Brownian Motion");
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    private static double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
    private static double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }
    private static double factorX(double w) { return w * width  / Math.abs(xmax - xmin);  }
    private static double factorY(double h) { return h * height / Math.abs(ymax - ymin);  }
    
    public static void clear() { 
    	offscreen.setColor(Color.WHITE);
    	offscreen.fillRect(0, 0, width, height);
    	draw(); 
    }

    private static void pixel(double x, double y) {
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }

    public static void filledCircle(double x, double y, double r,Color color) {
    	offscreen.setColor(color);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    @SuppressWarnings("static-access")
	public static void show(int t) {
        defer = false;
        draw();
        try { Thread.currentThread().sleep(t); }
        catch (InterruptedException e) { System.out.println("Error sleeping"); }
        defer = true;
    }


    private static void draw() {
        if (defer) return;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }

}

