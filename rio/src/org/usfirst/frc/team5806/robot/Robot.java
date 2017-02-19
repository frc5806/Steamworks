package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.AnalogInput;
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
	Joystick stick;
	Shooter shooter;
	
	NeoMagic neoMagic;
	DistanceSensor sonar;
	Servo lin;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//train = new DriveTrain();
		stick = new Joystick(0);
		shooter = new Shooter();
		lin = new Servo(1);
		
		//neoMagic = new NeoMagic();
		SmartDashboard.putNumber("shooterSpeed", 0.0);
	}
	
	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
	}

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
    }

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		/*train.lEncoder.reset();
		train.rEncoder.reset();
		train.ahrs.reset();		*/
				        
        //leftHalf.calibrate();
        //leftHalf.setPosition(50);
        /*train.driveFoward(0.8, 0.1, 0.1, 0.2, 1*12*6.5);
        train.turn(0.8, 0.1, 0.1, 0.2, 90);
        train.turn(0.8, 0.1, 0.1, 0.2, 90);
        train.driveFoward(0.8, 0.1, 0.1, 0.2, 2*12*6.5);*/

    }
	
	@Override
	public void teleopPeriodic() {				
		if(stick.getRawButton(2)) {
			shooter.rampUp();
        }
        if(stick.getRawButton(3)) {
        	shooter.rampDown();
        }
        if(stick.getRawButton(1)) {
        	shooter.off();
        }
        
        lin.set(1);
        
        shooter.updateSubsystem();
        shooter.updateDashboard();
	}

    /**
     * This function is called periodically during test mode
     */
    @Override
    public void testPeriodic() {
        LiveWindow.run();
    }
}
