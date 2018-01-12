package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO imports
public class GearMech extends Subsystem{
    static int UP_POSITION = 2100; // TODO real nums
    static int DOWN_POSITION = 1300;

    enum AxelState {
        UP, DOWN, STILL
    };

    Victor axel;
    Victor roller;
    AnalogInput pot;

    AxelState aState = AxelState.STILL;

    public GearMech() {
       pot = new AnalogInput(Ports.POTENTIOMETER); 
       axel = new Victor(Ports.GEAR_AXEL); 
       //roller = new Victor(Ports.GEAR_ROLLER); 
    }

    public void up() {
        aState = AxelState.UP;
    }
    
    public void blockingUp() {
        while(pot.getValue() < UP_POSITION) {
	        if(Math.abs(pot.getValue() - UP_POSITION) < 300) {
	            axel.set(-0.3);
	        } else {
	            axel.set(-1);
	        }
        }
        
        axel.set(-0.1);
        aState = AxelState.STILL;
    }
    
    public void blockingDown() {
    	while(pot.getValue() > DOWN_POSITION) {
	        if(Math.abs(pot.getValue() - DOWN_POSITION) < 300) {
	            axel.set(0.1);
	        } else {
	            axel.set(1);
	        }
    	}
        
    	axel.set(0);
        aState = AxelState.STILL;
    }

    public void down() {
        aState = AxelState.DOWN;
    }

    public void setAxel(double speed) {
        axel.set(speed);
    }

    public void setRoll(double speed) {
        roller.set(speed);
    }

    public void rollerSuckIn() {
        roller.set(1);
    }

    public void rollerStill() {
        roller.set(0);
    }

    public void rollerPushOut() {
        roller.set(-1);
    }

    public void updateSubsystem() {
        switch(aState) {
            case UP:
                if(pot.getValue() > UP_POSITION) {
                    axel.set(0);
                    aState = AxelState.STILL;
                } else if(Math.abs(pot.getValue() - UP_POSITION) < 300) {
                    axel.set(-0.3);
                } else {
                    axel.set(-1);
                }
                break;

            case DOWN:
                if(pot.getValue() < DOWN_POSITION) {
                    axel.set(0);
                    aState = AxelState.STILL;
                } else if(Math.abs(pot.getValue() - DOWN_POSITION) < 300) {
                    axel.set(0.3);
                } else {
                    axel.set(1);
                }
                break;
            case STILL:
                axel.set(0);
                break;
        }
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("potentiometer", pot.getValue());
        SmartDashboard.putNumber("gearAxel", axel.getSpeed());
    }

	@Override
	public void stop() {
		aState = AxelState.STILL;
        axel.set(0);
        roller.set(0);
	}
}
