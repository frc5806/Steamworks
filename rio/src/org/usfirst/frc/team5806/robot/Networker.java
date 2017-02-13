package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Networker {
	NetworkTable table;
	public Networker() {
		table = NetworkTable.getTable("vision");
	}
	
	public int[] getGearCoords() {
		return null;
	}
	public int[] getBoilerCoords() {
		return null;
	}
}
