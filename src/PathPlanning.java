package GeneralPackage;

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
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.NodePathFinder;
import lejos.robotics.pathfinding.Path;
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
//import com.sun.javafx.geom.Edge;
import java.lang.Object;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.awt.Point;
import java.awt.Rectangle;
//import lejos.robotics.pathfinding;
//import javafx.scene.Node;
import java.lang.Throwable;
import java.lang.Error;

public class Pathplanning {
   
   //GridMesh extends FourWayGridMesh en nieuwe constructor met ingebouwde functie om de nodes te deleten die niet mogen
    //Daarna nodepathfinder met dat mesh om te berekenen
     // wat ik krijg van elias: array[x][y] als volgt:
   
   // startpoint     
   // bovenhoeklinkso bovenhoekrechtso   onderhoekrechtso   onderhoeklinkso   tussenhoekeventueel
   // bovenhoeklinks1 bovenhoekrechts1   onderhoekrechts1   onderhoeklinks1
   // ...               ...               ...               ...
	
	class GridMesh extends FourWayGridMesh{						// nieuwe constructor die punten van obstakels weglaat
		public GridMesh(LineMap map, float gridSpace, float clearance, ArrayList arr)  {
				super(map,gridSpace,clearance);
			String foutboodschap = new String("Punt niet gevonden.");
		   for(int j=0;j<arr.length;j++){
		            switch(arr[j].length){
				case 4:{
					for(int k=0;k<(arr[j][0].y-arr[j][2].y);k++){      
						for(int l=0;l<(arr[j][1].x-arr[j][0].x);l++){
							Node temp1 = new Node(l,k);
							boolean right = super.removeNode(temp1);
							if(right==false){
								Error notfound = new Error(foutboodschap);
								}
							System.out.println( right );
						}
					}
					break;
				}
				case 6:
					if(arr[j][5].y>arr[j][2].y){ //ligt punt 6 boven punt 3?
							if(arr[j][5].x>arr[j][4].x){ //ligt punt 6 rechts van punt 5? 
									for(int k=0;k<(arr[j][0].y-arr[j][5].y-1);k++){     //bovenste deel van de fig 
										for(int l=0;l<(arr[j][1].x-arr[j][0].x);l++){
											Node temp = new Node(arr[j][5].x+l,arr[j][5].y+k+1);
											boolean right = super.removeNode(temp);
											if(right==false){
												Error notfound = new Error(foutboodschap);
											}
											System.out.println(right);
										}
									}
									for(int m=0;m<(arr[j][5].y-arr[j][2].y);m++){      //onderste deel van de figuur
										for(int n=0;n<(arr[j][2].x-arr[j][4].x);n++){
											Node temp = new Node(arr[j][4].x+n,arr[j][2].y+m);
											boolean right = super.removeNode(temp);
											if(right==false){
												Error notfound = new Error(foutboodschap);
											}
											System.out.println(right);
										}
									}
							}else{
								for(int k=0;k<(arr[j][0].y-arr[j][5].y);k++){     //bovenste deel van de fig 
									for(int l=0;l<(arr[j][1].x-arr[j][0].x);l++){
										Node temp = new Node(arr[j][5].x+l,arr[j][5].y+k);
										boolean right = super.removeNode(temp);
										if(right==false){
											Error notfound = new Error(foutboodschap);
											}
										System.out.println(right);
									}
								}
								for(int m=0;m<(arr[j][5].y-arr[j][2].y-1);m++){      //onderste deel van de figuur
									for(int n=0;n<(arr[j][2].x-arr[j][4].x);n++){
										Node temp = new Node(arr[j][4].x+n,arr[j][2].y+m);
										boolean right = super.removeNode(temp);
										if(right==false){
											Error notfound = new Error(foutboodschap);
											}
										System.out.println(right);
									}
								}
							}
					}else{
						if(arr[j][3].x>arr[j][2].x){ //ligt punt 4 rechts van punt 3? 
								for(int k=0;k<(arr[j][0].y-arr[j][2].y-1);k++){     //bovenste deel van de fig 
									for(int l=0;l<(arr[j][1].x-arr[j][0].x);l++){
										Node temp = new Node(arr[j][0].x+l,arr[j][2].y+k+1);
										boolean right = super.removeNode(temp);
										if(right==false){
											Error notfound = new Error(foutboodschap);
											}
										System.out.println(right);
									}
								}
								for(int m=0;m<(arr[j][2].y-arr[j][5].y);m++){      //onderste deel van de figuur
									for(int n=0;n<(arr[j][4].x-arr[j][5].x);n++){
										Node temp = new Node(arr[j][5].x+n,arr[j][5].y+m);
										boolean right = super.removeNode(temp);
										if(right==false){
											Error notfound = new Error(foutboodschap);
											}
										System.out.println(right);
									}
								}
						}else{
							for(int k=0;k<(arr[j][0].y-arr[j][2].y);k++){     //bovenste deel van de fig 
								for(int l=0;l<(arr[j][1].x-arr[j][0].x);l++){
									Node temp = new Node(arr[j][0].x+l,arr[j][2].y+k);
									boolean right = super.removeNode(temp);
									if(right==false){
											Error notfound = new Error(foutboodschap);
											}
									System.out.println(right);
								}
							}
							for(int m=0;m<(arr[j][2].y-arr[j][5].y-1);m++){      //onderste deel van de figuur
								for(int n=0;n<(arr[j][3].x-arr[j][5].x);n++){
									Node temp = new Node(arr[j][5].x+n,arr[j][5].y+m);
									boolean right = super.removeNode(temp);
									if(right==false){
											Error notfound = new Error(foutboodschap);
											}
									System.out.println(right);
								}
							}	
						}}
				break;
				default:
				break;
			    	}}
		}
		}
   
   public static void main(String[] args) throws IOException, DestinationUnreachableException{
 AstarSearchAlgorithm alg = new AstarSearchAlgorithm(); // nieuw algoritme maken om later te gebruiken
 int dimx = 1030;					// x dimensie van de foto
 int dimy = 2048;					// y dimensie van de foto
 float heading = 45;					// hoek met de x as van de startrichting van de robot

ArrayList<Point[]> arr = new ArrayList<Point>();
	    Point  arr1[] = new Point[1];
	   arr[0]=new Point(10 , 10); 
	   stuff.add(arr);
	   Point  arr2[] = new Point[4];
	   arr[0]=new Point(5 , 95);
	   arr[1]=new Point(95 , 95);
	   arr[2]=new Point(95 , 95);
	   arr[3]=new Point(95 , 95);
	   stuff.add(arr);
	   Point  arr3[] = new Point[1];
	   arr[0]=new Point(10 , 10);
	   arr[1]=new Point(95 , 95);
	   arr[2]=new Point(95 , 95);
	   arr[3]=new Point(95 , 95);
	   stuff.add(arr);
	   Point  arr4[] = new Point[1];
	   arr[0]=new Point(10 , 10); 
	   stuff.add(arr);
 Point  arr[][] = new Point[4][6];				// input die normaal van elias komt
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
lejos.robotics.geometry.Point end = new lejos.robotics.geometry.Point(85,85);				// zelfgekozen eindpunt
Waypoint endpoint = new Waypoint(end);			// formaat van eindpunt voor in de methode van pathplanning
Pose startpoint = new Pose(arr[0][0].x,arr[0][0].y,heading);	// startpunt uit elias zijn input
  lejos.robotics.geometry.Rectangle boundingRect = new lejos.robotics.geometry.Rectangle(0, dimy, dimx, dimy);		// rechthoek rond foto door dimensies geconstrueerd
 Line lines[] = new Line[dimy+ dimx];						// lijnen construeren voor in de rechthoek
      for(int i=0;i<dimx;i++){
      lines[i] = new Line(i+1, 0, i+1, dimy);
      }
      for(int j=0;j<dimy;j++){
      lines[j+dimx] = new Line(0, j+1, dimx , j+1);
      }   
 LineMap map = new LineMap(lines, boundingRect);	// een map maken van de lijnenrij hierboven
 String linemap = new String("linemap");
	   map.createSVGFile(linemap);
float kotjesbreedte = (float)1.2060546875;
float fout = (float)0.25;
GridMesh mesh = new Pathplanning().new GridMesh(map, kotjesbreedte , fout, arr);			// mesh maken van de map voor in de methode
NodePathFinder Pathfinder = new NodePathFinder(alg, mesh); 		// nieuwe nodepath voor in de methode
Path shortestpath = Pathfinder.findRoute(startpoint,endpoint);		// het momenteel kortste pad
 System.out.println(shortestpath.toString());
   }
   

}
