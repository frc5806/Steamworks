package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Subsystem {
	public enum ShooterState{
		RAMP_UP, RAMP_DOWN, CRUISE, OFF
	}
	public enum FeederState{
		ON, OFF
	}
	
	static final double CRUISE_SPEED = 0.8;
	static final double FEEDER_ON_SPEED = 0.3;
	static final double ACCEL_TIME_MILLIS = 1000;
	static final double DEACCEL_TIME_MILLIS = 1000;
	
	Victor shooterMotor;
	Victor[] feederMotors;
	
	private ShooterState shooterState = ShooterState.OFF;
	private FeederState feederState = FeederState.OFF;
	private long lastUpdate = -1;
	
	public Shooter() {
		shooterMotor = new Victor(8);
		shooterMotor.setInverted(true);
		feederMotors = new Victor[]{new Victor(6), new Victor(7)};
		feederMotors[0].setInverted(true);
		feederMotors[1].setInverted(true);
		off();
	}
	
	public void rampUp() {
		shooterState = ShooterState.RAMP_UP;
	}
	
	public void rampDown() {
		feederState = FeederState.OFF;
		shooterState = ShooterState.RAMP_DOWN;
	}
	public void off() {
		feederState = FeederState.OFF;
		shooterState = ShooterState.OFF;
		shooterMotor.set(0);
	}
	
	@Override
	public void stop() {
		shooterMotor.stopMotor();
		for(Victor motor : feederMotors) motor.stopMotor();
	}

	@Override
	public void updateSubsystem() {
		if(lastUpdate == -1) lastUpdate = System.currentTimeMillis();
		switch(shooterState) {
		case OFF:
			shooterMotor.set(0);
			break;
		case RAMP_UP:
			if(Math.abs(shooterMotor.get()) >= CRUISE_SPEED) {
				shooterMotor.set(CRUISE_SPEED);
				shooterState = ShooterState.CRUISE;
				feederState = FeederState.ON;
			}
			shooterMotor.set(((System.currentTimeMillis()-lastUpdate) / ACCEL_TIME_MILLIS)*CRUISE_SPEED + Math.abs(shooterMotor.get()));
			break;
		case RAMP_DOWN:
			if(Math.abs(shooterMotor.get()) <= 0) {
				shooterMotor.set(0);
				shooterState = ShooterState.OFF;
			}
			shooterMotor.set(((System.currentTimeMillis()-lastUpdate) / DEACCEL_TIME_MILLIS)*-CRUISE_SPEED + Math.abs(shooterMotor.get()));
			break;
		case CRUISE:
			shooterMotor.set(CRUISE_SPEED);
			break;
		}
		
		switch(feederState) {
		case ON:
			for(Victor motor : feederMotors) motor.set(FEEDER_ON_SPEED);
			break;
		case OFF:
			for(Victor motor : feederMotors) motor.set(0);
			break;
		}
		
		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber("state", shooterState.ordinal());
		SmartDashboard.putNumber("shootSpeed", shooterMotor.get());
		SmartDashboard.putNumber("firstFeederSpeed", feederMotors[0].get());
		SmartDashboard.putNumber("secondFeederSpeed", feederMotors[0].get());
	}

}
