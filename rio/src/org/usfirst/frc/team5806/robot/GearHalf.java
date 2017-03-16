package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearHalf extends Subsystem {
	public enum GearState {
		MOVING, OFF, FAST_CALIBRATION;
	}
	int lastPosition = 0;
	int direction;
	int lastEncoder = 0;
	int lastDirection = 1;
	int startingPosition;
	double speed;
	double maxSpeed, minSpeed, accelLength, deaccelLength, deltaPosition;
	long startMillis, startFastCalibration;
	
	GearState state = GearState.OFF;
	Victor motor;
	Counter encoder;
	DigitalInput limitSwitch;
	String name;
	long lastUpdate = -1;
	
	public GearHalf(int motorPort, int encoderPort, int limitSwitchPort, int direction, String name) {
		motor = new Victor(motorPort);
		encoder = new Counter(new DigitalInput(encoderPort));
		limitSwitch = new DigitalInput(limitSwitchPort);
		this.direction = Integer.signum(direction);
		this.name = name;
		
		encoder.reset();
		motor.set(0);
	}
	
	public void calibrate() {
		motor.set(direction*-0.4);
		while(limitSwitch.get()) {
			updateDashboard();
		}
		motor.set(0);
		lastPosition = 0;
		lastDirection = 1;
		encoder.reset();
	}

	public void fastCalibrate() {
		startFastCalibration = System.currentTimeMillis();
		state = GearState.FAST_CALIBRATION;
		motor.set(direction*-0.9);
	}
	
	public void movePosition(double maxSpeed, double minSpeed, double accelLength, double deaccelLength, double deltaPosition) {
		this.maxSpeed = maxSpeed;
		this.minSpeed = minSpeed;
		this.accelLength = accelLength;
		this.deaccelLength = deaccelLength;
		this.deltaPosition = deltaPosition;
		
		startingPosition = getPosition();
        	speed = minSpeed;
		startMillis = System.currentTimeMillis();
		state = GearState.MOVING;
		setSpeed(speed*(int)Math.signum(deltaPosition));
	}
	
	public void setPosition(double maxSpeed, double minSpeed, double accelLength, double deaccelLength, double position) {
		movePosition(maxSpeed, minSpeed, accelLength, deaccelLength, position - getPosition());
	}
	
	public int getPosition() {
		return lastPosition + lastDirection*Math.abs(encoder.get() - lastEncoder);
	}
	
	public void setSpeed(double speed) {
		lastPosition += lastDirection*Math.abs(encoder.get()-lastEncoder);
		lastEncoder = encoder.get();
		
		if(Math.abs(speed - 0) > 0.001) lastDirection = (int)Math.signum(speed);
		motor.set(direction*speed);
	}

	@Override
	public void stop() {
		motor.stopMotor();
	}

	@Override
	public void updateSubsystem() {
		if(lastUpdate == -1) lastUpdate = System.currentTimeMillis();
		if(!limitSwitch.get()) {
			lastPosition = 0;
			lastDirection = 1;
			encoder.reset();
			motor.set(0);
		}
		switch(state) {
		case MOVING:
			if(Math.abs(getPosition()-startingPosition) < Math.abs(deltaPosition) && (Math.signum(deltaPosition) > 0 || limitSwitch.get())) {
				double error = (Math.abs(deltaPosition) - Math.abs(getPosition()-startingPosition)) / Math.abs(deltaPosition);
				if(1-error < accelLength) {
					speed = minSpeed + ((maxSpeed - minSpeed) * (1-error) / accelLength);
				} else if (error < deaccelLength) {
					speed = minSpeed + ((maxSpeed - minSpeed) * error / deaccelLength);
				} else {
					speed = maxSpeed;
				}
				setSpeed(speed*(int)Math.signum(deltaPosition));
			} else {
				state = GearState.OFF;
				setSpeed(0);
			}
			break;
		case FAST_CALIBRATION:
			if(!limitSwitch.get() || System.currentTimeMillis() - startFastCalibration > 500) {
				setPosition(0.4, 0.25, 0.1, 0.2, -2000);
			}
		case OFF:
			break;
		}


		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber(name+"GearMotor", motor.get());
		SmartDashboard.putNumber(name+"GearEncoder", encoder.get());
		SmartDashboard.putBoolean(name+"GearLimit", limitSwitch.get());
		SmartDashboard.putNumber(name+"GearPosition", getPosition());
		SmartDashboard.putNumber(name+"GearDeltaPosition", Math.abs(getPosition()-startingPosition));
	}
}
