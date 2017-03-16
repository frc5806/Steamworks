package org.usfirst.frc.team5806.robot;

public class GearMech extends Subsystem {
	GearHalf left, right;
	public GearMech() {
		right = new GearHalf(1, 7, 9, 1, "right");
		left = new GearHalf(2, 6, 8, 1, "left");
	}
	
	public void calibrate() {
		left.calibrate();
		right.calibrate();
	}
	
	public void fastCalibrate() {
		left.fastCalibrate();
		right.fastCalibrate();
	}

	public void close() {
		left.setPosition(0.25, 0.25, 0.1, 0.1, 110);
		right.setPosition(0.25, 0.25, 0.1, 0.1, 110);
	}
	public void open() {
		left.setPosition(0.4, 0.25, 0.1, 0.2, -200);
		right.setPosition(0.4, 0.25, 0.1, 0.2, -200);	
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
