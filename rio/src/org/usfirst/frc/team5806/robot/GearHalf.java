package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearHalf {
	int lastPosition = 0;
	int direction;
	int lastEncoder = 0;
	int lastDirection = 1;
	
	final double MIN_SPEED = 0.1;
	final double ERROR_DAMPENING_THRESHOLD = 0.1;

	Victor motor;
	Counter encoder;
	DigitalInput limitSwitch;
	
	public GearHalf(int motorPort, int encoderPort, int limitSwitchPort, int direction) {
		motor = new Victor(motorPort);
		encoder = new Counter(new DigitalInput(encoderPort));
		limitSwitch = new DigitalInput(limitSwitchPort);
		this.direction = Integer.signum(direction);
		
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
	
	public void movePosition(double speed, double deltaPosition) {
		int startingPosition = getPosition();
		double startingSpeed = speed;
		long startMillis = System.currentTimeMillis();
		setSpeed(speed*(int)Math.signum(deltaPosition));
		while(Math.abs(getPosition()-startingPosition) < Math.abs(deltaPosition) && (System.currentTimeMillis() - startMillis < 200 || limitSwitch.get())) {
			SmartDashboard.putNumber("deltaPosition", Math.abs(getPosition()-startingPosition));
			double error = (Math.abs(deltaPosition) - Math.abs(getPosition()-startingPosition)) / Math.abs(deltaPosition);
			if(error < ERROR_DAMPENING_THRESHOLD) {
				speed = MIN_SPEED + ((startingSpeed - MIN_SPEED) * error / ERROR_DAMPENING_THRESHOLD);
				setSpeed(speed*(int)Math.signum(deltaPosition));
			}
		}
		setSpeed(0);
	}
	
	public void setPosition(double speed, double position) {
		movePosition(speed, position - getPosition());
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
}
