import java.awt.Color;

public class BrownianMotionSimulation {
	
    private PriorityQueue<Collision> pq;   
    private double t  = 0.0;      
    private double hz = 0.5;      
    private Particle[] particles;  

    private BrownianMotionSimulation(Particle[] particles) {
        this.particles = particles;
    }
    
    public static BrownianMotionSimulation makeSimulation(int N) {
    	if ( N < 10 && N > 2000 ) {
    		System.err.println("The number of particles must be between 10 and 2000.");
    		return null;
    	}
		Particle[] particles= new Particle[N];
		particles[0] = new Particle(0.56,0.56,0,0,0.05,10.0,Color.RED);
	    for (int i = 1; i < N; i++) {
	    	double mass = .1;
	        particles[i] = new Particle(mass*0.02,mass,Color.green);
	    }
	    return new BrownianMotionSimulation(particles);
    }

    private void predict(Particle a, double limit) {
        if (a == null) return;
        for (int i = 0; i < particles.length; i++) {
            double dt = a.timeToHit(particles[i]);
            if (t + dt <= limit)
                pq.insert(new Collision(t + dt, a, particles[i]));
        }
        double dtX = a.timeToHitVerticalWall();
        double dtY = a.timeToHitHorizontalWall();
        if (t + dtX <= limit) pq.insert(new Collision(t + dtX, a, null));
        if (t + dtY <= limit) pq.insert(new Collision(t + dtY, null, a));
    }

    private void redraw(double limit) {
        DrawingFrame.clear();
        for (int i = 0; i < particles.length; i++) {
            particles[i].draw();
        }
        DrawingFrame.show(20);
        if (t < limit) {
            pq.insert(new Collision(t + 1.0 / hz, null, null));
        }
    }

      
    public void simulate(double limit) {
        
        pq = new PriorityQueue<Collision>();
        for (int i = 0; i < particles.length; i++) {
            predict(particles[i], limit);
        }
        pq.insert(new Collision(0, null, null));       


        while (!pq.isEmpty()) { 
            Collision e = pq.delMin();
            if (!e.isValid()) continue;
            Particle a = e.a;
            Particle b = e.b;
            for (int i = 0; i < particles.length; i++)
                particles[i].move(e.time - t);
            t = e.time;
            if (a != null && b != null) a.bounceOff(b);            
            else if (a != null && b == null) a.bounceOffVerticalWall();  
            else if (a == null && b != null) b.bounceOffHorizontalWall(); 
            else if (a == null && b == null) redraw(limit);     
            predict(a, limit);
            predict(b, limit);
        }
    }


    private static class Collision implements Comparable<Collision> {
        private final double time;         
        private final Particle a, b;       
        private final int countA, countB;  
                
        public Collision(double t, Particle a, Particle b) {
            this.time = t;
            this.a    = a;
            this.b    = b;
            if (a != null) countA = a.count();
            else           countA = -1;
            if (b != null) countB = b.count();
            else           countB = -1;
        }

        public int compareTo(Collision that) {
            if      (this.time < that.time) return -1;
            else if (this.time > that.time) return +1;
            else                            return  0;
        }
        
        public boolean isValid() {
            if (a != null && a.count() != countA) return false;
            if (b != null && b.count() != countB) return false;
            return true;
        }
   
    }
    
    public static void main(String[] args) {
        	DrawingFrame.show(0);
        	//number of particles
	        int N = 500;
        	makeSimulation(N).simulate(100000);
    }
      
}