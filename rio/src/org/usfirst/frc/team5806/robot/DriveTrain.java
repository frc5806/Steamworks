package org.usfirst.frc.team5806.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
	static final double MAX_SPEED = 0.5;
	static final double MIN_SPEED = 0.1;
	static final double LEFT_ENCODER_TO_DIST = 6*Math.PI / 356.0;
	static final double RIGHT_ENCODER_TO_DIST = 6*Math.PI / 236.0;
	static final double TURN_CORRECTION_DAMPENING = 1/10.0;
	static final double SPEED_CORRECTION_DAMPENING = 0.5;
	
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
			distanceTraveled = ((Math.abs(lEncoder.get()*LEFT_ENCODER_TO_DIST)+Math.abs(rEncoder.get()*RIGHT_ENCODER_TO_DIST)) / 2.0);
			
			double speedCorrection = TURN_CORRECTION_DAMPENING * (ahrs.getAngle() - startingAngle);
			lMotor.set(speed-speedCorrection);
			rMotor.set(speed+speedCorrection);
			
			SmartDashboard.putNumber("angleDif", ahrs.getAngle() - startingAngle);
			SmartDashboard.putNumber("speedCorrection", speedCorrection);
			SmartDashboard.putNumber("distanceTraveled", distanceTraveled);
			updateDashboard();
		} while(distanceTraveled < distance);
	}
	
	/**
	 * Set speed to negative to turn the opposite direction.
	 */
	public void turn(double speed, double degrees) {
		lEncoder.reset();
		rEncoder.reset();
		
		double startingAngle = ahrs.getAngle();
		double degreesTurned;
		do { 
			degreesTurned = Math.abs(ahrs.getAngle() - startingAngle);
			
			// Pos if left dist greater than right
			double speedCorrection = SPEED_CORRECTION_DAMPENING * Math.abs(lEncoder.get()*LEFT_ENCODER_TO_DIST)-Math.abs(rEncoder.get()*RIGHT_ENCODER_TO_DIST);
			// If left dist greater than right, slow down left and speed up right
			lMotor.set(Math.max(speed-speedCorrection, 0));
			rMotor.set(Math.min(-(speed+speedCorrection), 0));
			
			SmartDashboard.putNumber("speedCorrection", speedCorrection);
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
