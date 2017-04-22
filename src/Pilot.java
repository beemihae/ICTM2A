


import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.MirrorMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;
import lejos.robotics.pathfinding.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ArrayList.*;
import java.awt.Point;
import java.io.Serializable;
import java.lang.Cloneable;
import java.lang.Iterable;
import java.util.Collection.*;
import java.util.List.*;
import java.util.ListIterator;
import java.util.function.Consumer;

import com.sun.glass.ui.Robot;

import java.util.List.*;

 
public class Pilot {
	static RobotPilot pilot;
	static WheeledChassis chassis;
	static Path currentPath;
	static Navigator kapitein;
	static PoseProvider ppv;
	static LineMap currentMap;
	static FourWayGridMesh currentMesh;
	static float gridSpace;
	static float clearance;
	static SearchAlgorithm alg = new AstarSearchAlgorithm();
	static Pose currentPose;
	static Waypoint goal;
	static IRSensor sensor;
	
	static ArrayList<float[]> getWaypoints () {  /** geeft Arraylist van [x,y] coordinaten van waypoints van current path**/
		ArrayList<float[]> waypoints = new ArrayList<float[]>();
		for (Waypoint waypoint : currentPath) {
			waypoints.add(new float[]{waypoint.x, waypoint.y});
		}
		/*for (int i = 0; i<waypoints.size(); i++) {
			System.out.println("x"+i+" = "+waypoints.get(i)[0]);
			System.out.println("y"+i+" = "+waypoints.get(i)[1]+"\n");
		}*/
		return waypoints;
	}
	
	static void createPilot () {
		Wheel leftwheel = WheeledChassis.modelWheel(Motor.C, 56.0).offset(74.0);
		Wheel rightwheel = WheeledChassis.modelWheel(Motor.D, 56.0).offset(-74.0);
		chassis = new WheeledChassis(new Wheel[] {leftwheel,  rightwheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new RobotPilot(chassis, Motor.A);
	}
	
	static void createNagivator () {
		ppv = new OdometryPoseProvider(pilot);
		kapitein = new Navigator(pilot,ppv);
	}
	
	static void updateMap(float[][] boundingPoints, ArrayList<float[][]> contouren) {
		float height = Math.abs(boundingPoints[0][1]-boundingPoints[3][1]);
		float width = Math.abs(boundingPoints[1][0]-boundingPoints[0][0]);
		float x = boundingPoints[0][0];
		float y = boundingPoints[0][1];
		Rectangle boundingRect = new Rectangle(x,y,width,height);
		ArrayList<Line> lines = new ArrayList<Line>();
		for (Iterator<float[][]> iterator = ( contouren.iterator()); iterator.hasNext();) {  // voor elke contour
			ArrayList<float[]> points = new ArrayList<float[]>();
			float[][] contour = iterator.next();
			for (int i = 0; i < contour.length; i++) {
				points.add(new float[]{contour[i][0], contour[i][1]});
			}	
			for (int i = 0; i < points.size()-1; i++) {
				lines.add(new Line(points.get(i)[0], points.get(i)[0], points.get(i+1)[0], points.get(i+1)[1]));
			}
			lines.add(new Line(points.get(0)[0], points.get(0)[1], points.get(points.size()-1)[0], points.get(points.size()-1)[1]));
		}
		/*for (int i = 0; i < lines.size(); i++) {
			System.out.println(lines.get(i).x1);
			System.out.println(lines.get(i).y1);
			System.out.println(lines.get(i).x2);
			System.out.println(lines.get(i).y2);
			System.out.println("\n"); */
		currentMap = new LineMap(lines.toArray(new Line[lines.size()]), boundingRect);
	}
	
	static void updateMesh () {
		currentMesh = new FourWayGridMesh(currentMap, gridSpace, clearance);
	}
	
	static void updatePath () throws DestinationUnreachableException {
		NodePathFinder padvinder = new NodePathFinder(alg, currentMesh);
		currentPath = padvinder.findRoute(currentPose, goal);
	}
	
	static void updatePath (float[][] boundingPoints, ArrayList<float[][]> contouren) throws DestinationUnreachableException {
		updateMap(boundingPoints, contouren);
		updateMesh();
		updatePath();
	}
	
	
	public static void main(String[] args)
	  {
		 createPilot();
		 pilot.setLinearSpeed(100);
		 pilot.setAngularSpeed(100);
		 //pilot.arc(-200, 90);
		 Behavior b1 = new DoPath();
		 Behavior b2 = new DetectObstacle();
		 Arbitrator arbitrator = new Arbitrator (new Behavior[] {b1,b2});
		 arbitrator.go();
		 
		 
		 
	  }
}

class RobotPilot extends MovePilot {
	RegulatedMotor sensorMotor;
	RotationListener listener = new RotationListener();
	public RobotPilot(Chassis chassis, RegulatedMotor sensorMotor) {
		super(chassis);
		this.sensorMotor=sensorMotor;
		super.addMoveListener(listener);
	}
	public void rotateSensor(int angle) {
		sensorMotor.rotate(angle);
	}
	
	class RotationListener implements MoveListener {
		private int angle=45;    // sensor zal over 45 graden draaien
		float sign;
		public void moveStarted(Move event, MoveProvider mp) {
			if (event.getMoveType().equals(Move.MoveType.ROTATE)){
				sign = Math.signum(event.getAngleTurned());
				rotateSensor((int)(sign*angle));
			}
			else if (event.getMoveType().equals(Move.MoveType.ARC)){
				sign = Math.signum(event.getArcRadius());
				rotateSensor((int)(sign*angle));}			
			;
		}
		public void moveStopped(Move event, MoveProvider mp) {
			rotateSensor(-(int)(sign*angle));;			
		}
	}
	
	
	
	
	
	
	
}

class DoPath implements Behavior {
	private boolean _surpressed = false;
	
	public void action() {
		_surpressed = false;
		Pilot.kapitein.setPath(Pilot.currentPath);
		Pilot.kapitein.followPath();
		while (!_surpressed) {
			Thread.yield();
		}
	}
	
	public void suppress() {
		this._surpressed=true;
		
	}
	
	public boolean takeControl() {
		return !Pilot.kapitein.pathCompleted();
	}

}

class DetectObstacle implements Behavior {
	
	public void action() {
		//take new picture and get new bounding points en contouren
		Pilot.updatePath(boundingPoints, contouren);
	}
	
	public void suppress() { //will never be called
	}
	
	public boolean takeControl() {
		return Pilot.sensor.distance<50; //minder dan 50mm van object
	}
	
}

class IRSensor extends Thread
{
    EV3IRSensor ir = new EV3IRSensor(SensorPort.S4);
    SampleProvider sp = ir.getDistanceMode();
    public int distance;

    IRSensor()
    {

    }
    
    public void run()
    {
        while (true)
        {
            float [] sample = new float[sp.sampleSize()];
            sp.fetchSample(sample, 0);
            distance = (int)sample[0];
            //System.out.println(" Distance: " + distance);
            
        }
        
    }
    
    
}



