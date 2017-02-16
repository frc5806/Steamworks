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
	
	Counter gearEncoder;
	Victor spinnyMotor;
	NeoMagic neoMagic;
	DistanceSensor sonar;
	Victor shooter;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		train = new DriveTrain();
		stick = new Joystick(0);
		//neoMagic = new NeoMagic();
		shooter = new Victor(5);
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
		train.lEncoder.reset();
		train.rEncoder.reset();
		train.ahrs.reset();
		shooter.set(0); //make sure shooter off to start
				        
        //leftHalf.calibrate();
        //leftHalf.setPosition(50);
        /*train.driveFoward(0.8, 0.1, 0.1, 0.2, 1*12*6.5);
        train.turn(0.8, 0.1, 0.1, 0.2, 90);
        train.turn(0.8, 0.1, 0.1, 0.2, 90);
        train.driveFoward(0.8, 0.1, 0.1, 0.2, 2*12*6.5);*/

    }
	
	@Override
	public void teleopPeriodic() {
		shooterSpeed = SmartDashboard.getNumber("shooterSpeed", 0.0);
		shooter.set(shooterSpeed);
		
		if(stick.getRawButton(2)) {
            //leftHalf.setPosition(0.3, 0.15, 0.1, 0.1, 100);
        }
        if(stick.getRawButton(3)) {
            //leftHalf.setPosition(0.3, 0.15, 0.1, 0.1, 20);
        }

		/*SmartDashboard.putNumber("encoder", leftHalf.encoder.get());
		SmartDashboard.putNumber("pos", leftHalf.getPosition());
		SmartDashboard.putNumber("lastEnc", leftHalf.lastEncoder);*/
		//SmartDashboard.putBoolean("limit", leftHalf.limitSwitch.get());
		//leftHalf.setSpeed(stick.getRawAxis(1)*0.5);
	}

    /**
     * This function is called periodically during test mode
     */
    @Override
    public void testPeriodic() {
        LiveWindow.run();
    }
}
