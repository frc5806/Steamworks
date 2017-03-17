package org.usfirst.frc.team5806.robot;

public class GearMech extends Subsystem {
	GearHalf left, right;
	public GearMech() {
		right = new GearHalf(1, 6, 4, 1, "right");
		left = new GearHalf(2, 7, 5, 1, "left");
	}
	
	public void calibrate() {
		left.calibrate();
		right.calibrate();
	}
	
	public void calibrateClosed() {
		left.calibrateClosed();
		right.calibrateClosed();
	}

	public void fastCalibrate() {
		left.fastCalibrate();
		right.fastCalibrate();
	}

	public void close() {
		left.close();
		right.close();
	}
	public void open() {
		left.open();
		right.open();
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
