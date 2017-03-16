package org.usfirst.frc.team5806.robot;

import org.usfirst.frc.team5806.robot.Shooter.FeederState;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable; 
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	static final int trigPin = 9, echoPin = 8;
	static double GEAR_BOTTOM_CAM_POS = 0;
	static double DRIVE_CAM_POS = 0.3;
	static final int AUTO_TYPE = 1;
	
	static final int OVERRIDE_CAM = 18, CAM_SLIDER = 1, GATE_BALL = 2, GATE_GEAR = 1;  
	static final int FAST_ZERO_GEAR = 7, ZERO_SLOWLY_GEAR = 9, CLOSE_GEAR = 8, RIGHT_TO_RIGHT = 6, LEFT_TO_RIGHT = 13, LEFT_TO_LEFT = 12, RIGHT_TO_LEFT = 11;  
	static final int FEEDER_FORWARD = 5, FEEDER_REVERSE = 17, SHOOTER_ON = 4, SHOOTER_OFF = 3, FEEDER_POWER = 3, SHOOTER_POWER = 2;
	static final int LIFTER_CLOCK = 16, LIFTER_COUNTERCLOCK = 14, LIFTER_POWER = 0;

	
	DriveTrain train;
	Joystick stickLeft, stickRight, stickMech;
	Shooter shooter;
	GearMech gearMech;
	UsbCamera camera;
	Servo camServo, gateServo;
	Victor lifterMotor;
	
	boolean safeToLift = false;

	double camPos = DRIVE_CAM_POS;
	
	
	public class MyThread extends Thread
	{
	   private Shooter shooter;
	   public boolean isGood = true;

	   public MyThread(Shooter shooter)
	   {
	      this.shooter = shooter;
	   }

	   @Override
	   public void run()
	   {
	      shooter.on();
	      while(isGood != false) {
	    	  shooter.updateSubsystem();
	    	  //shooter.updateDashboard();
	      }
	   }
	}

	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		train = new DriveTrain();
		stickMech = new Joystick(0);
		stickLeft = new Joystick(2);
		stickRight = new Joystick(1);
		shooter = new Shooter();
		lifterMotor = new Victor(3);
		lifterMotor.setInverted(true);
		gateServo = new Servo(7);
		gearMech = new GearMech();
		camServo = new Servo(0);
	}
	
	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
		if(AUTO_TYPE == 0) { // forward
			train.driveFoward(0.5, 0.2, 0.2, 0.1, 5*12, 1);
		} if(AUTO_TYPE == 1) { // left hard
			train.driveFoward(0.4, 0.2, 0.2, 0.2, 9.1*12, 1);
			train.turn(0.4, 0.15, 0.3, 0.3, 60, 1);
			train.driveFoward(0.4, 0.2, 0.2, 0.2, 12, 1);
		} else if(AUTO_TYPE == 2) { // right hard
			train.driveFoward(0.4, 0.2, 0.2, 0.2, 8.8*12, 1);
			train.turn(0.4, 0.15, 0.3, 0.3, 60, -1);
			train.driveFoward(0.4, 0.2, 0.2, 0.2, 12, 1);
		} else if(AUTO_TYPE == 3) {
			MyThread thread = new MyThread(shooter);
			thread.start();
			gateServo.set(0.9);
			train.driveFoward(0.8, 0.2, 0.2, 0.3, 9.5*12, 1);
			train.turn(0.7, 0.15, 0.2, 0.5, 85, -1);
			train.driveFoward(0.8, 0.1, 0.2, 0.3, 2.75*12, 1);
			Timer.delay(3);
			train.driveFoward(0.6, 0.2, 0.2, 0.3, 4*12, -1);
			train.turn(0.7, 0.25, 0.2, 0.5, 85, -1);
			train.driveFoward(0.8, 0.2, 0.2, 0.3, 6*12, 1);
			train.turn(0.3, 0.15, 0.2, 0.5, 25, 1);
			thread.isGood = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shooter.feederState = FeederState.ON;
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		if(AUTO_TYPE == 3) {
			shooter.updateSubsystem();
			lifterMotor.set(0.3);
		}
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480);

		gateServo.set(0.0);
		
		train.lEncoder.reset();
		train.rEncoder.reset();
		train.ahrs.reset();
		//gearMech.calibrate();
	}

	@Override
	public void teleopPeriodic() {	
		// Feeder
		shooter.feederSpeed = Math.abs(stickMech.getRawAxis(FEEDER_POWER)+1)/2.0;
		if(stickMech.getRawButton(FEEDER_FORWARD)) {
			shooter.feederState = FeederState.ON;
		} else if (stickMech.getRawButton(FEEDER_REVERSE)) {
			shooter.feederState = FeederState.REVERSE;
		} else {
			shooter.feederState = FeederState.OFF;
		}

		// Shooter
		if(stickMech.getRawButton(SHOOTER_ON)) {
			shooter.on();
		}
		if(stickMech.getRawButton(SHOOTER_OFF)) {
			shooter.off();
		}

		// Lifter (off if no button pushed)
		if(safeToLift && stickMech.getRawButton(LIFTER_CLOCK)) {
			lifterMotor.set(Math.abs(stickMech.getRawAxis(LIFTER_POWER)+1)/2.0);
		} else if(safeToLift && stickMech.getRawButton(LIFTER_COUNTERCLOCK)) {
			lifterMotor.set(-Math.abs(stickMech.getRawAxis(LIFTER_POWER)+1)/2.0);
		} else {
			lifterMotor.set(0);
		}

		// Open slowly
		if(stickMech.getRawButton(ZERO_SLOWLY_GEAR)) {
			gearMech.open();
		}
		// Open fast
		if(stickMech.getRawButton(FAST_ZERO_GEAR)) {
			gearMech.fastCalibrate();
		}
		// Close 
		if(stickMech.getRawButton(CLOSE_GEAR)) {
			gearMech.close();
		}
		
		
		if(stickMech.getRawButton(RIGHT_TO_RIGHT)) {
			gearMech.right.setSpeed(-0.6);
		} else if(stickMech.getRawButton(RIGHT_TO_LEFT)) {
			gearMech.right.setSpeed(0.6);
		} else if(stickMech.getRawButton(LEFT_TO_RIGHT)) {
			gearMech.left.setSpeed(0.6);
		} else if(stickMech.getRawButton(LEFT_TO_LEFT)) {
			gearMech.left.setSpeed(-0.6);
		} else if (!stickMech.getRawButton(ZERO_SLOWLY_GEAR) && !stickMech.getRawButton(FAST_ZERO_GEAR) && !stickMech.getRawButton(CLOSE_GEAR)) {
			gearMech.left.setSpeed(0);
			gearMech.right.setSpeed(0);
		}


		// Set the gate position
		if(stickMech.getRawButton(GATE_BALL)) {
			gateServo.set(0.9);
			safeToLift = true;
		}
		if(stickMech.getRawButton(GATE_GEAR)) {
			safeToLift = false;
			gateServo.set(0.0);
		}

		// Camera on beckman's side
		if (stickLeft.getRawButton(5)) {
			camPos = GEAR_BOTTOM_CAM_POS;
		}
		if (stickLeft.getRawButton(3)) {
			camPos = DRIVE_CAM_POS;
		}
		if(stickMech.getRawButton(OVERRIDE_CAM)) {
			camPos = (stickMech.getRawAxis(CAM_SLIDER)+1.0)/2.0;
		}

		camServo.set(camPos);


		
		train.setDistanceSpeeds(-Math.signum(stickLeft.getRawAxis(1))*Math.floor(10*Math.abs(stickLeft.getRawAxis(1)))/10.0, -Math.signum(stickRight.getRawAxis(1))*Math.floor(10*Math.abs(stickRight.getRawAxis(1)))/10.0);

		shooter.updateSubsystem();
		shooter.updateDashboard();
		train.updateDashboard();
		train.updateSubsystem();
		gearMech.updateSubsystem();
		gearMech.updateDashboard();

		Timer.delay(0.01);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
