import java.awt.Color;

public class Particle {
    private static final double INFINITY = Double.POSITIVE_INFINITY;

    private double x, y;    
    private double vx, vy; 
    private double r;    
    private double m;   
    private Color c;     
    private int count;      

    
    public Particle(double rx, double ry, double vx, double vy, double radius, double mass, Color color) {
        this.vx = vx;
        this.vy = vy;
        this.x = rx;
        this.y = ry;
        this.r = radius;
        this.m   = mass;
        this.c  = color;
    }
    
    public Particle(double r, double m,  Color c) {
    	 x     = Math.random();
         y     = Math.random();
         vx     = 0.01 * (Math.random() - 0.5);
         vy     = 0.01 * (Math.random() - 0.5);
         this.r = r;
         this.m   = m;
         this.c  = c;
  	}

  
    public void move(double dt) {
        x += vx * dt;
        y += vy * dt;
    }

    public void draw() {
        DrawingFrame.filledCircle(x, y, r,c);
    }

    public int count() { return count; }
        
    public double timeToHit(Particle b) {
        Particle a = this;
        if (a == b) return INFINITY;
        double dx  = b.x - a.x;
        double dy  = b.y - a.y;
        double dvx = b.vx - a.vx;
        double dvy = b.vy - a.vy;
        double dvdr = dx*dvx + dy*dvy;
        if (dvdr > 0) return INFINITY;
        double dvdv = dvx*dvx + dvy*dvy;
        double drdr = dx*dx + dy*dy;
        double sigma = a.r + b.r;
        double d = (dvdr*dvdr) - dvdv * (drdr - sigma*sigma);
        if (d < 0) return INFINITY;
        return -(dvdr + Math.sqrt(d)) / dvdv;
    }

    public double timeToHitVerticalWall() {
        if      (vx > 0) return (1.0 - x - r) / vx;
        else if (vx < 0) return (r - x) / vx;  
        else             return INFINITY;
    }

    public double timeToHitHorizontalWall() {
        if      (vy > 0) return (1.0 - y - r) / vy;
        else if (vy < 0) return (r - y) / vy;
        else             return INFINITY;
    }

    public void bounceOff(Particle that) {
        double dx  = that.x - this.x;
        double dy  = that.y - this.y;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double dvdr = dx*dvx + dy*dvy;            
        double dist = this.r + that.r; 
        double F = 2 * this.m * that.m * dvdr / ((this.m + that.m) * dist);
        double fx = F * dx / dist;
        double fy = F * dy / dist;
        this.vx += fx / this.m;
        this.vy += fy / this.m;
        that.vx -= fx / that.m;
        that.vy -= fy / that.m;
        this.count++;
        that.count++;
    }

    public void bounceOffVerticalWall() {
        vx = -vx;
        count++;
    }

    public void bounceOffHorizontalWall() {
        vy = -vy;
        count++;
    }

    public double kineticEnergy() { return 0.5 * m * (vx*vx + vy*vy); }
    
}

