package org.usfirst.frc.team5806.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
	static final double MAX_SPEED = 0.5;
	static final double MIN_SPEED = 0;
	static final double ENCODER_TO_DIST = 1;
	static final double TURN_CORRECTION_DAMPENING = 1/10.0;
	
	Encoder lEncoder;
	Encoder rEncoder;
	Victor lMotor = new Victor(9);
	Victor rMotor = new Victor(8);

	AHRS ahrs = new AHRS(SPI.Port.kMXP);

	public DriveTrain() {
		lEncoder = new Encoder(4, 5);
		rEncoder = new Encoder(2, 3);

		lMotor.setInverted(true);
	}
	
	public void driveFoward(double speed, double distance) {
		lEncoder.reset();
		rEncoder.reset();
		lMotor.set(speed);
		rMotor.set(speed);
		
		double startingAngle = ahrs.getAngle();
		double distanceTraveled;
		do {
			distanceTraveled = ENCODER_TO_DIST * ((lEncoder.get()+rEncoder.get()) / 2.0);
			
			//TODO: correct turning; see if we r using right dirs
			double speedCorrection = TURN_CORRECTION_DAMPENING * (ahrs.getAngle() - startingAngle);
			lMotor.set(speed-speedCorrection);
			rMotor.set(speed+speedCorrection);
		} while(distanceTraveled < distance);
	}
	
	/**
	 * Set speed to negative to turn the opposite direction.
	 */
	public void turn(double speed, double degrees) {
		double startingAngle = ahrs.getAngle();
		double degreesLeft;
		do { 
			degreesLeft = Math.abs(startingAngle - ahrs.getAngle());
			
			lMotor.set(speed);
			rMotor.set(-speed);
		} while(degreesLeft < degrees);
		lMotor.set(0);
		rMotor.set(0);
	}
	
	public void setSpeeds(double lSpeed, double rSpeed) {
		lSpeed = Math.signum(lSpeed)*Math.max(MIN_SPEED, Math.min(MAX_SPEED, Math.abs(lSpeed)));
		rSpeed = Math.signum(rSpeed)*Math.max(MIN_SPEED, Math.min(MAX_SPEED, Math.abs(rSpeed)));
		
		lMotor.set(lSpeed);
		rMotor.set(rSpeed);
	}
	
	public void updateDashboard() {
		SmartDashboard.putNumber("angle", ahrs.getAngle());
		SmartDashboard.putNumber("lEncoder", lEncoder.get());
		SmartDashboard.putNumber("rEncoder", rEncoder.get());
		SmartDashboard.putBoolean("lEncoderStopped", lEncoder.getStopped());
		SmartDashboard.putBoolean("rEncoderStopped", rEncoder.getStopped());

	}
		
}
