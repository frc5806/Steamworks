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
import edu.wpi.first.wpilibj.networktables.NetworkTable; import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	static final int trigPin = 9, echoPin = 8;
	static double shooterSpeed = 0.75;
	
	DriveTrain train;
	Joystick stickLeft;
	Joystick stickRight;
	Shooter shooter;
	GearMech gearMech;
	UsbCamera camera;
	
	NeoMagic neoMagic;
	DistanceSensor sonar;
	Servo camServo, gateServo;
	Victor lifterMotor;
	boolean gateOpen = true;
	
	public enum CamState {
		BOTTOM_GEAR, DRIVE_VIEW
	}
	CamState camState = CamState.DRIVE_VIEW;
	
	static final int AUTO_TYPE = 1;
	
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
		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		//shooter = new Shooter();
		//lifterMotor = new Victor(3);
		//lifterMotor.setInverted(true);
		//gateServo = new Servo(7);
		//gearMech = new GearMech();
		camServo = new Servo(0);
		//sonar = new DistanceSensor();
		
		//neoMagic = new NeoMagic();
		SmartDashboard.putNumber("shooterSpeed", 0.0);
	}
	
	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
		if(AUTO_TYPE == 0) {
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
    	//shooter.updateSubsystem();
    	//lifterMotor.set(0.3);
    }

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(640, 480);
        
		train.lEncoder.reset();
		train.rEncoder.reset();
		train.ahrs.reset();
    	//lifterMotor.set(0);
    	//gearMech.calibrate();

				        
        //leftHalf.calibrate();
        //leftHalf.setPosition(50);
        //train.turn(0.8, 0.1, 0.1, 0.2, 90);
        //train.driveFoward(0.8, 0.1, 0.1, 0.2, 2*12*6.5);

    }
	
	@Override
	public void teleopPeriodic() {	
		/*
		if(stick.getRawButton(1)) {
			shooter.feederState = FeederState.ON;
        }
		if(stick.getRawAxis(2) > 0.7) {
			gateOpen = false;
        } 
		if(stick.getRawAxis(3) > 0.7) {
			gateOpen = true;	
        }
		if(stick.getRawButton(2)) {
			shooter.on();
        }
        if(stick.getRawButton(3)) {
        	shooter.off();
        }
        if(stick.getRawButton(4)) {
        	lifterMotor.set(1);
        }
        if(stick.getRawButton(5)) {
        	lifterMotor.set(0);
        }
        if(stick.getRawButton(6)) {
        	shooter.feederState = FeederState.OFF;
        }
        if(stick.getRawButton(7)) {
        	gearMech.open();
        }
        if(stick.getRawButton(8)) {
        	gearMech.close();
        }
        
        if(gateOpen) {
			//gateServo.set(0.9);
		} else {
			//gateServo.set(0.0);
		}*/
		
		if (stickLeft.getRawButton(5)) {
			camState = CamState.BOTTOM_GEAR;
		}
		if (stickLeft.getRawButton(3)) {
			camState = CamState.DRIVE_VIEW;
		}
		
		if (camState == CamState.BOTTOM_GEAR) {
			camServo.set(0);
		} 
		if (camState == CamState.DRIVE_VIEW) {
			camServo.set(0.3);
		}
		
        
        train.setDistanceSpeeds(-Math.signum(stickLeft.getRawAxis(1))*Math.floor(10*Math.abs(stickLeft.getRawAxis(1)))/10.0, -Math.signum(stickRight.getRawAxis(1))*Math.floor(10*Math.abs(stickRight.getRawAxis(1)))/10.0);
        //train.setSpeeds(-Math.signum(stickLeft.getRawAxis(1))*Math.floor(10*Math.abs(stickLeft.getRawAxis(1)))/10.0, -Math.signum(stickLeft.getRawAxis(1))*Math.floor(10*Math.abs(stickLeft.getRawAxis(1)))/10.0);
        //gearMech.left.motor.set(stick.getRawAxis(1));
        //gearMech.right.motor.set(stick.getRawAxis(5));
                
        //shooter.updateSubsystem();
        //shooter.updateDashboard();
        train.updateDashboard();
        train.updateSubsystem();
        Timer.delay(0.01);
        //gearMech.updateSubsystem();
        //gearMech.updateDashboard();
        //SmartDashboard.putNumber("ultrasonic", sonar.getRangeInches());
	}

    /**
     * This function is called periodically during test mode
     */
    @Override
    public void testPeriodic() {
        LiveWindow.run();
    }
}
