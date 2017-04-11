



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



 
public class Pilot {
	static RobotPilot pilot;
	static WheeledChassis chassis;
	static void createPilot () {
		Wheel leftwheel = WheeledChassis.modelWheel(Motor.C, 56.0).offset(74.0);
		Wheel rightwheel = WheeledChassis.modelWheel(Motor.D, 56.0).offset(-74.0);
		chassis = new WheeledChassis(new Wheel[] {leftwheel,  rightwheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new RobotPilot(chassis, Motor.A);
	}
	public static void main(String[] args)
	  {
		 createPilot();
		 pilot.setLinearSpeed(100);
		 pilot.setAngularSpeed(100);
		 pilot.arc(-200, 90);
		 //Behavior b1 = new Drive();
		 //Arbitrator arbitrator = new Arbitrator (new Behavior[] {b1});
		 //arbitrator.go();
	    
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
		private int angle=45;    // sensor zal over 45� draaien
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

class Drive implements Behavior {
	private boolean _surpressed = false;
	
	public void action() {
		_surpressed = false;
		while (!_surpressed) {
			Pilot.pilot.arcForward(100);
		}
	}
	
	public void suppress() {
		this._surpressed=true;
		
	}
	
	public boolean takeControl() {
		return true;
	}

}

class IRSensor extends Thread
{
    EV3IRSensor ir = new EV3IRSensor(SensorPort.S4);
    SampleProvider sp = ir.getDistanceMode();
    public int distance = 255;

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
            System.out.println(" Distance: " + distance);
            
        }
        
    }
    
    
}



