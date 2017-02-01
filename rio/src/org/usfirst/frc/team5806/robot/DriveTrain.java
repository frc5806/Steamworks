package org.usfirst.frc.team5806.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
	static final double MAX_SPEED = 0.5;
	static final double MIN_SPEED = 0.1;
	static final double FORWARD_DAMPENING_THRESHOLD = 6*Math.PI/2.0;
	static final double TURN_DAMPENING_THRESHOLD = 30.0;
	static final double LEFT_ENCODER_TO_DIST = 6*Math.PI / 355.0;
	static final double RIGHT_ENCODER_TO_DIST = 6*Math.PI / 359.0;
	static final double FORWARD_CORRECTION_FACTOR = 0.1;
	static final double TURN_CORRECTION_FACTOR = 0.1;
	
	Encoder lEncoder;
	Encoder rEncoder;
	Victor lMotor;
	Victor rMotor;

	AHRS ahrs;

	public DriveTrain() {
		lEncoder = new Encoder(2, 3);
		rEncoder = new Encoder(4, 5);
		lMotor = new Victor(9);
		rMotor = new Victor(8);
		ahrs = new AHRS(SPI.Port.kMXP);

		rMotor.setInverted(true);
		ahrs.reset();
		lEncoder.reset();
		rEncoder.reset();
	}
	
	// Don't try negative speed for now
	public void driveFoward(double speed, double distance) {
		lEncoder.reset();
		rEncoder.reset();
		lMotor.set(speed);
		rMotor.set(speed);
		
		double startingAngle = ahrs.getAngle();
		double startingSpeed = speed;
		double distanceTraveled;
		do {
			distanceTraveled = ((Math.abs(lEncoder.get()*LEFT_ENCODER_TO_DIST)+Math.abs(rEncoder.get()*RIGHT_ENCODER_TO_DIST)) / 2.0);
			
			double speedCorrection = FORWARD_CORRECTION_FACTOR * (Math.abs(lEncoder.get()*LEFT_ENCODER_TO_DIST)-Math.abs(rEncoder.get()*RIGHT_ENCODER_TO_DIST));
			speedCorrection = Math.min(Math.max(speedCorrection, -startingSpeed), startingSpeed);
			lMotor.set(speed-speedCorrection);
			rMotor.set(speed+speedCorrection);
			
			SmartDashboard.putNumber("speedCorrection", speedCorrection);
			SmartDashboard.putNumber("distanceTraveled", distanceTraveled);
			if(distance-distanceTraveled < FORWARD_DAMPENING_THRESHOLD) speed = Math.max(Math.min(0.10, startingSpeed), ((distance-distanceTraveled)/FORWARD_DAMPENING_THRESHOLD)*startingSpeed);
			updateDashboard();
		} while(distanceTraveled < distance);
		lMotor.set(0);
		rMotor.set(0);
	}
	
	/**
	 * Set speed to negative to turn the opposite direction.
	 */
	// Don't try negative speed for now
	public void turn(double speed, double degrees) {
		lEncoder.reset();
		rEncoder.reset();
		
		double startingAngle = ahrs.getAngle();
		double startingSpeed = speed;
		double degreesTurned;
		do { 
			degreesTurned = Math.abs(ahrs.getAngle() - startingAngle);
			
			double speedCorrection = TURN_CORRECTION_FACTOR * (Math.abs(lEncoder.get()*LEFT_ENCODER_TO_DIST)-Math.abs(rEncoder.get()*RIGHT_ENCODER_TO_DIST));
			speedCorrection = Math.min(Math.max(speedCorrection, -startingSpeed), startingSpeed);
			speedCorrection = 0;
			lMotor.set(Math.max(speed-speedCorrection, 0));
			rMotor.set(Math.min(-(speed+speedCorrection), 0));
			
			if(degrees-degreesTurned < TURN_DAMPENING_THRESHOLD) speed = Math.max(Math.min(0.1, startingSpeed), ((degrees-degreesTurned)/TURN_DAMPENING_THRESHOLD)*startingSpeed);
			
			SmartDashboard.putNumber("speedCorrection", speedCorrection);
			SmartDashboard.putNumber("degreesTurned", degreesTurned);
			updateDashboard();
		} while(degreesTurned < degrees);
		lMotor.set(0);
		rMotor.set(0);
	}
	
	public void setSpeeds(double lSpeed, double rSpeed) {
		if(Math.abs(lSpeed) < MIN_SPEED) lSpeed = 0;
		if(Math.abs(rSpeed) < MIN_SPEED) rSpeed = 0;
		lSpeed = Math.signum(lSpeed)*Math.min(MAX_SPEED, Math.abs(lSpeed));
		rSpeed = Math.signum(rSpeed)*Math.min(MAX_SPEED, Math.abs(rSpeed));
		
		lMotor.set(lSpeed);
		rMotor.set(rSpeed);
	}
	
	public void updateDashboard() {
		SmartDashboard.putNumber("angle", ahrs.getAngle());
		SmartDashboard.putNumber("lEncoder", lEncoder.get());
		SmartDashboard.putNumber("rEncoder", rEncoder.get());
		SmartDashboard.putNumber("lEncoderDist", lEncoder.get()*LEFT_ENCODER_TO_DIST);
		SmartDashboard.putNumber("rEncoderDist", rEncoder.get()*RIGHT_ENCODER_TO_DIST);
		SmartDashboard.putBoolean("lEncoderStopped", lEncoder.getStopped());
		SmartDashboard.putBoolean("rEncoderStopped", rEncoder.getStopped());
	}
		
}
