package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearHalf extends Subsystem {
	public enum GearState {
		MOVING, OFF;
	}
	int lastPosition = 0;
	int direction;
	int lastEncoder = 0;
	int lastDirection = 1;
	int startingPosition;
    double speed;
    double maxSpeed, minSpeed, accelLength, deaccelLength, deltaPosition;
	long startMillis;
	
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
	}
	
	public void calibrate() {
		motor.set(direction*-0.25);
		while(limitSwitch.get()) {
			
		}
		motor.set(0);
		lastPosition = 0;
		lastDirection = 1;
		encoder.reset();
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
	
	private void setSpeed(double speed) {
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
		switch(state) {
		case MOVING:
			if(Math.abs(getPosition()-startingPosition) < Math.abs(deltaPosition) && (System.currentTimeMillis() - startMillis < 200 || limitSwitch.get())) {
				double error = (Math.abs(deltaPosition) - Math.abs(getPosition()-startingPosition)) / Math.abs(deltaPosition);
	            if(1-error < accelLength) {
					speed = minSpeed + ((maxSpeed - minSpeed) * (1-error) / accelLength);
	            }
	            if(error < deaccelLength) {
					speed = minSpeed + ((maxSpeed - minSpeed) * error / deaccelLength);
	            }
			} else {
				state = GearState.OFF;
			}
			break;
		case OFF:
			setSpeed(0);
			break;
		}
		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber(name+"GearMotor", motor.get());
		SmartDashboard.putNumber(name+"GearEncoder", encoder.get());
		SmartDashboard.putBoolean(name+"GearLimit", limitSwitch.get());
		SmartDashboard.putNumber("deltaPosition", Math.abs(getPosition()-startingPosition));
	}
}
