import java.util.*;
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
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
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

import java.util.ArrayList;
import com.sun.javafx.geom.Edge;
import java.lang.Object
import java.awt.geom.Point2D
import java.awt.Point

import javafx.scene.Node;

public class PathPlanning {
   
   \\GridMesh extends FourWayGridMesh en nieuwe constructor met ingebouwde functie om de nodes te deleten die niet mogen
    \\Daarna nodepathfinder met dat mesh om te berekenen
     \\ wat ik krijg van elias: array[x][y] als volgt:
   
   \\ startpoint     
   \\ bovenhoeklinkso bovenhoekrechtso   onderhoekrechtso   onderhoeklinkso   tussenhoekeventueel
   \\ bovenhoeklinks1 bovenhoekrechts1   onderhoekrechts1   onderhoeklinks1
   \\ ...               ...               ...               ...
   
   public static void main(String[] args){
 public AstarSearchAlgorithm alg = new AstarSearchAlgorithm();
 public int dimx = 1030;
 public int dimy = 2048;
 public float heading = 45;
 public Point  arr[][] = new Point[][];
arr[0][0]=new Point(10 , 10);  
arr[1][0]=new Point(5 , 95);  
arr[1][1]=new Point(95 , 95);  
arr[1][2]=new Point(95 , 5);  
arr[1][3]=new Point(5 , 5);  
arr[2][0]=new Point(45 , 55);
arr[2][1]=new Point(55 , 55);     
arr[2][2]=new Point(55 , 45);
arr[2][3]=new Point(45 , 45);
arr[3][0]=new Point(25 , 65);
arr[3][1]=new Point(65 , 65);
arr[3][2]=new Point(65 , 75);
arr[3][3]=new Point(70 , 75);
arr[3][4]=new Point(70 , 60);
arr[3][5]=new Point(25 , 60);
 public Point end = new Point(85,85);
public WayPoint endpoint = new WayPoint(end);
public Pose startpoint = new Pose(arr[0][0].getX,arr[0][0].getY,heading);
  public Rectangle boundingRect = new Rectangle(0, dimy, dimx, dimy);
 public line lines[] = new line[];
      for(int i=0;i<dimx;i++){
      lines[i] = Line(i+1, 0, i+1, dimy);
      }
      for(int j=0;j<dimy;j++){
      lines[j+dimx] = Line(0, j+1, dimx , j+1);
      }
      
 public LineMap map = new LineMap( lines, boundingRect);
	   map.createSVGFile(linemap);
 public GridMesh mesh = new GridMesh(map, 1.2060546875 , 0.25);
 public NodePathFinder Pathfinder = new NodePathFinder(alg, mesh); 
 public Path shortestpath = Pathfinder.findRoute(startpoint,endpoint);
 system.out.println(shortestpath.tostring());
   }
class GridMesh extends FourWayGridMesh{
public GridMesh(LineMap map, float gridSpace, float clearance, Point[][] arr)  {
		super(map,gridspace,clearance);
	
   for(int j=0;j<arr.length;j++){
            switch(arr[j].length)
		case 4:
		for(int k=0;k<(arr[j][0].getY-arr[j][2].getY);k++){      
   			for(int l=0;l<(arr[j][1].getX-arr[j][0].getX),l++){
				public Node temp = new Node(l,k);
				public boolean right = super.removeNode(temp);
				if(right==false){
						public Error notfound = new Error(Punt niet gevonden.);
						}
				system.out.println(right);
			}
		}
		break;
	   	   
		case 6:
		if(arr[j][5].getY>arr[j][2].getY){ %ligt punt 6 boven punt 3?
			if(arr[j][5].getX>arr[j][4].getX){ %ligt punt 6 rechts van punt 5? 
				for(int k=0;k<(arr[j][0].getY-arr[j][5].getY-1);k++){     //bovenste deel van de fig 
   					for(int l=0;l<(arr[j][1].getX-arr[j][0].getX),l++){
						public Node temp = new Node(arr[j][5].getX+l,arr[j][5].getY+k+1);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
				for(int m=0;m<(arr[j][5].getY-arr[j][2].getY);m++){      //onderste deel van de figuur
   					for(int n=0;n<(arr[j][2].getX-arr[j][4].getX),n++){
						public Node temp = new Node(arr[j][4].getX+n,arr[j][2].getY+m);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
			}else{
				for(int k=0;k<(arr[j][0].getY-arr[j][5].getY);k++){     //bovenste deel van de fig 
   					for(int l=0;l<(arr[j][1].getX-arr[j][0].getX),l++){
						public Node temp = new Node(arr[j][5].getX+l,arr[j][5].getY+k);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
				for(int m=0;m<(arr[j][5].getY-arr[j][2].getY-1);m++){      //onderste deel van de figuur
   					for(int n=0;n<(arr[j][2].getX-arr[j][4].getX),n++){
						public Node temp = new Node(arr[j][4].getX+n,arr[j][2].getY+m);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
		}else{
		if(arr[j][3].getX>arr[j][2].getX){ %ligt punt 4 rechts van punt 3? 
				for(int k=0;k<(arr[j][0].getY-arr[j][2].getY-1);k++){     //bovenste deel van de fig 
   					for(int l=0;l<(arr[j][1].getX-arr[j][0].getX),l++){
						public Node temp = new Node(arr[j][0].getX+l,arr[j][2].getY+k+1);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
				for(int m=0;m<(arr[j][2].getY-arr[j][5].getY);m++){      //onderste deel van de figuur
   					for(int n=0;n<(arr[j][4].getX-arr[j][5].getX),n++){
						public Node temp = new Node(arr[j][5].getX+n,arr[j][5].getY+m);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
			}else{
				for(int k=0;k<(arr[j][0].getY-arr[j][2].getY);k++){     //bovenste deel van de fig 
   					for(int l=0;l<(arr[j][1].getX-arr[j][0].getX),l++){
						public Node temp = new Node(arr[j][0].getX+l,arr[j][2].getY+k);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}
				for(int m=0;m<(arr[j][2].getY-arr[j][5].getY-1);m++){      //onderste deel van de figuur
   					for(int n=0;n<(arr[j][3].getX-arr[j][5].getX),n++){
						public Node temp = new Node(arr[j][5].getX+n,arr[j][5].getY+m);
						public boolean right = super.removeNode(temp);
							if(right==false){
									public Error notfound = new Error(Punt niet gevonden.);
									}
						system.out.println(right);
					}
				}	
		}
		break;
	    	}
}
}
}
