package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearMech {
	GearHalf left, right;
	public GearMech() {
		left = new GearHalf(2, 5, 4, -1);
	}
	
	public void calibrate() {
		left.calibrate();
		right.calibrate();
	}
	
	public void open() {
		left.setPosition(0.3, 100);
		right.setPosition(0.3, 100);
	}
	public void close() {
		left.setPosition(0.3, 20);
		right.setPosition(0.3, 20);
	}
	
	// Positive position is left
	public void moveDirection(double speed, double deltaPosition) {
		int leftStartPos = left.getPosition(), rightStartPos = right.getPosition();
		double startingSpeed = speed;
		long startMillis = System.currentTimeMillis();
		
		left.setSpeed(speed*(int)Math.signum(deltaPosition));
		right.setSpeed(speed*(int)Math.signum(deltaPosition));

		do {
			SmartDashboard.putNumber("deltaPosition", Math.abs(left.getPosition()-leftStartPos));
			double error = (Math.abs(deltaPosition) - Math.abs(left.getPosition()-leftStartPos)) / Math.abs(deltaPosition);
			if(error < GearHalf.ERROR_DAMPENING_THRESHOLD) {
				speed = GearHalf.MIN_SPEED + ((startingSpeed - GearHalf.MIN_SPEED) * error / GearHalf.ERROR_DAMPENING_THRESHOLD);
				
				left.setSpeed(speed*(int)Math.signum(deltaPosition));
				right.setSpeed(speed*(int)Math.signum(deltaPosition));
			}
		} while(Math.abs(left.getPosition()-leftStartPos) < Math.abs(deltaPosition));
		
		left.setSpeed(0);
		right.setSpeed(0);
	}
}
