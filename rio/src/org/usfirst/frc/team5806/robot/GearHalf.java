package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

public class GearHalf {
	int lastPosition = 0;
	double lastSpeed = 0;
	
	Victor motor;
	Counter encoder;
	
	public GearHalf() {
		motor = new Victor(2);
		encoder = new Counter(new DigitalInput(7));
	}
	
	public void movePosition(double speed, double position) {
		int startingPosition = getPosition();
		setSpeed(speed);
		while(startingPosition+position != getPosition()) {
			
		}
		setSpeed(0);
	}
	
	public void setPosition(double speed, double position) {
		setSpeed(speed);
		while(position != getPosition()) {
			
		}
		setSpeed(0);
	}
	
	public int getPosition() {
		return lastPosition + (int)Math.signum(lastSpeed)*encoder.get();
	}
	
	public void setSpeed(double speed) {
		lastPosition += Math.signum(lastSpeed)*encoder.get();
		encoder.reset();
		
		lastSpeed = speed;
		motor.set(speed);
	}
}
