package org.usfirst.frc.team5806.robot;

public class GearMech extends Subsystem {
	GearHalf left, right;
	public GearMech() {
		left = new GearHalf(2, 5, 4, -1, "left");
		//right = new GearHalf(2, 5, 4, -1, "right");
	}
	
	public void calibrate() {
		left.calibrate();
		right.calibrate();
	}
	
	public void open() {
		left.setPosition(0.3, 0.15, 0.1, 0.1, 100);
		right.setPosition(0.3, 0.15, 0.1, 0.1, 100);
	}
	public void close() {
		left.setPosition(0.3, 0.15, 0.1, 0.1, 20);
		right.setPosition(0.3, 0.15, 0.1, 0.1, 20);	
	}
	
	// Positive position is left
	//TODO: right now this just uses the left position for stuff, gotta be fixed
	public void moveDirection(double maxSpeed, double minSpeed, double accelLength, double deaccelLength, double deltaPosition) {
		left.movePosition(maxSpeed, minSpeed, accelLength, deaccelLength, deltaPosition);
		left.movePosition(maxSpeed, minSpeed, accelLength, deaccelLength, deltaPosition);
	}

	@Override
	public void stop() {
		left.stop();
		right.stop();
	}

	@Override
	public void updateSubsystem() {
		left.updateSubsystem();
		right.updateSubsystem();
	}

	@Override
	public void updateDashboard() {
		left.updateDashboard();
		right.updateDashboard();
	}
}
