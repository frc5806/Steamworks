package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Ultrasonic;

public class DistanceSensor {
	Ultrasonic sensor;
	public DistanceSensor() {
		sensor = new Ultrasonic(9, 8);
		sensor.setAutomaticMode(true);
	}
	
	public double getRangeInches() {
		return sensor.getRangeInches();
	}
	
	public boolean isThereYet() {
		return sensor.getRangeInches() < 20;
	}
}