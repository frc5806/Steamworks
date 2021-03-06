package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Subsystem {
	public enum ShooterState{
		RAMP_UP, RAMP_DOWN, ON, OFF
	}
	public enum FeederState{
		ON, OFF, REVERSE
	}
	
	public double feederSpeed = 0.5;
	static final double ACCEL_TIME = 5;
	static final double DEACCEL_TIME = 5;
	static final int TOP_TICKS_PER_SECOND = 1200;
	static final long BACKUP_TIME = 350, FORWARD_TIME = 1200;
	static final double SAMPLING_PERIOD = 200/1000.0;
	
	Victor[] shooterMotors;
	Victor[] feederMotors;
	Encoder encoder;
	
	private ShooterState shooterState = ShooterState.OFF;
	public FeederState feederState = FeederState.OFF;
	private double lastUpdate = -1, rampUpStart, rampDownStart;
	private long[] lastBackups;
	private int lastTicks = 0;
	private double lastSpeed = 0, lastTicksPerSecond = 0, lastError = 0, lastDeltaSpeed = 0;
	public double cruiseSpeed = 0.3;
	
	public Shooter() {
		shooterMotors = new Victor[]{new Victor(Ports.SHOOTER)};
		shooterMotors[0].setInverted(true);
		feederMotors = new Victor[]{new Victor(Ports.FEEDERS)};
		feederMotors[0].setInverted(true);
		//encoder = new Encoder(Ports.SHOOTER_ENCODER[0], Ports.SHOOTER_ENCODER[1]);
		SmartDashboard.putNumber("topShooterSpeed", 0.75);
		off();
		stop();
	}
	
	public void on() {
		lastUpdate = -1;
		//lastTicks = encoder.get();
		rampUpStart = Timer.getFPGATimestamp();
		shooterState = ShooterState.ON;
		//feederState = FeederState.ON;
	}
	
	public void off() {
		feederState = FeederState.OFF;
		rampDownStart = Timer.getFPGATimestamp();
		shooterState = ShooterState.RAMP_DOWN;
	}
	
	@Override
	public void stop() {
		for(Victor motor : shooterMotors) motor.stopMotor();
		for(Victor motor : feederMotors) motor.stopMotor();
	}

	@Override
	public void updateSubsystem() {
		if(lastUpdate == -1) {
			//lastTicks = encoder.get();
			lastUpdate = Timer.getFPGATimestamp();
			lastBackups = new long[]{System.currentTimeMillis(), System.currentTimeMillis()-((BACKUP_TIME+FORWARD_TIME)/2)};
			return;
		}
		if(Timer.getFPGATimestamp() - lastUpdate < SAMPLING_PERIOD) return;
		switch(shooterState) {
		case RAMP_UP:
			if(Math.abs(shooterMotors[0].get()) >= cruiseSpeed) {
				for(Victor motor : shooterMotors) motor.set(cruiseSpeed);
				shooterState = ShooterState.ON;
			}
			for(Victor motor : shooterMotors) motor.set(((Timer.getFPGATimestamp()-rampUpStart) / ACCEL_TIME)*cruiseSpeed);
			break;
		case RAMP_DOWN:
			if(Math.abs(shooterMotors[0].get()) <= 0.05) {
				for(Victor motor : shooterMotors) motor.set(0);
				shooterState = ShooterState.OFF;
			}
			for(Victor motor : shooterMotors) motor.set(((Timer.getFPGATimestamp()-rampDownStart) / DEACCEL_TIME)*-cruiseSpeed);
			break;
		case OFF:
			for(Victor motor : shooterMotors) motor.set(0);
			break;
		case ON:
			/*double ticksPerSecond = (Math.abs(lastTicks)-Math.abs(encoder.get())) / (Timer.getFPGATimestamp() - lastUpdate);
			double error = CRUISE_SPEED*TOP_TICKS_PER_SECOND - Math.abs(ticksPerSecond);
			lastDeltaSpeed = 0.045 * error / (CRUISE_SPEED*TOP_TICKS_PER_SECOND);
			lastSpeed += lastDeltaSpeed;
			for(Victor motor : shooterMotors) motor.set(lastSpeed);//motor.set(SmartDashboard.getNumber("topShooterSpeed", 0.75));
			lastTicks = Math.abs(encoder.get());
			lastTicksPerSecond = ticksPerSecond;
			lastError = error;*/
			for(Victor motor : shooterMotors) motor.set(cruiseSpeed);
			break;
		}
		
		switch(feederState) {
		case ON:
			for(int i  = 0; i < feederMotors.length; i++) {
				feederMotors[i].set(feederSpeed);
			}
			break;
		case REVERSE:
			for(int i  = 0; i < feederMotors.length; i++) {
				feederMotors[i].set(-feederSpeed);
			}
			break;
		case OFF:
			for(Victor motor : feederMotors) motor.set(0);
			break;
		}
		
		lastUpdate = Timer.getFPGATimestamp();
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber("state", shooterState.ordinal());
		SmartDashboard.putNumber("shootSpeed0", shooterMotors[0].get());
		SmartDashboard.putNumber("firstFeederSpeed", feederMotors[0].get());
		//SmartDashboard.putNumber("secondFeederSpeed", feederMotors[0].get());
		SmartDashboard.putNumber("timeSinceBackup", System.currentTimeMillis() - lastBackups[0]);
		SmartDashboard.putNumber("lastSpeed", lastSpeed);
		SmartDashboard.putNumber("ticks", Math.abs(lastTicks));
		SmartDashboard.putNumber("lastError", lastError);
		SmartDashboard.putNumber("lastTicksPerSecond", lastTicksPerSecond);
		SmartDashboard.putNumber("lastDeltaSpeed", lastDeltaSpeed);
	}

}
