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
import lejos.robotics.mapping.*;
import lejos.robotics.geometry.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;

import java.util.ArrayList;
import java.util.ArrayList.*;
import java.io.Serializable;
import java.lang.Cloneable;
import java.lang.Iterable;
import java.util.Collection.*;
import java.util.Iterator;
import java.util.List.*;
import java.util.function.Consumer;

import com.sun.glass.ui.Robot;


public class PathPlanning2 {

	public static void main(String[] args) throws DestinationUnreachableException {
		float gridSpace = 0.1f;
		float clearance = 0.1f;   // dit moet de afstand van centrum van robot tot verste uiteinde ervan zijn (straal omgeschreven cirkel zogezegd)
		
		float[][] boundingPoints = new float[][]{{1.1f,0.5f},{10.5f,0.5f},{10.5f,11.5f},{1.1f,11.5f}};
		ArrayList<float[][]> contouren = new ArrayList<float[][]>();
		contouren.add(new float[][]{{3.5f, 4f},{6f, 4.5f},{5f, 6.5f},{2f,5f}});
		LineMap map = generateMap(boundingPoints, contouren);
		FourWayGridMesh gridMesh = new FourWayGridMesh(map, gridSpace, clearance);
		NodePathFinder padvinder = new NodePathFinder(new AstarSearchAlgorithm(), gridMesh);
		Path pad = padvinder.findRoute(new Pose(1.7f, 1.1f, 45), new Waypoint(new Point(2f,8f)));
		ArrayList<double[]> waypoints = new ArrayList<double[]>();
		for (Waypoint waypoint : pad) {
			waypoints.add(new double[]{waypoint.x,waypoint.y});
		}
		for (int i = 0; i<waypoints.size(); i++) {
			System.out.println("x"+i+" = "+waypoints.get(i)[0]);
			System.out.println("y"+i+" = "+waypoints.get(i)[1]+"\n");
		}
		PoseProvider ppv = new OdometryPoseProvider(Pilot.pilot);
		Navigator kapitein = new Navigator(Pilot.pilot, ppv);
		kapitein.followPath(pad);

	}
	
	public static LineMap generateMap(float[][] boundingPoints, ArrayList<float[][]> contouren) {
		float height = Math.abs(boundingPoints[0][1]-boundingPoints[3][1]);
		float width = Math.abs(boundingPoints[1][0]-boundingPoints[0][0]);
		float x = boundingPoints[0][0];
		float y = boundingPoints[0][1];
		Rectangle boundingRect = new Rectangle(x,y,width,height);
		ArrayList<Line> lines = new ArrayList<Line>();
		for (Iterator<float[][]> iterator = ( contouren.iterator()); iterator.hasNext();) {  // voor elke contour
			ArrayList<Point> points = new ArrayList<Point>();
			float[][] contour = iterator.next();
			for (int i = 0; i < contour.length; i++) {
				points.add(new Point(contour[i][0], contour[i][1]));
			}	
			for (int i = 0; i < points.size()-1; i++) {
				lines.add(new Line(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y));
			}
			lines.add(new Line(points.get(0).x, points.get(0).y, points.get(points.size()-1).x, points.get(points.size()-1).y));
		}
		for (int i = 0; i < lines.size(); i++) {
			System.out.println(lines.get(i).x1);
			System.out.println(lines.get(i).y1);
			System.out.println(lines.get(i).x2);
			System.out.println(lines.get(i).y2);
			System.out.println("\n");
		}
		LineMap map = new LineMap(lines.toArray(new Line[lines.size()]), boundingRect);
		return map;
		
	}
	

}
