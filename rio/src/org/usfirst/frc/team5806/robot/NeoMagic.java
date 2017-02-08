package org.usfirst.frc.team5806.robot;

import edu.wpi.first.wpilibj.SerialPort;

public class NeoMagic {
	SerialPort port;
	Pixel[] pixels = new Pixel[16];
	public NeoMagic(){
		// TODO Auto-generated method stub
		port = new SerialPort(9600, SerialPort.Port.kUSB1);
		for(int i=0; i<pixels.length; i++){
			pixels[i] = new Pixel(0,0,0);
		}
	}
	public void setPixel(int pixelNum, Pixel color){
		pixels[pixelNum] = color;
		reload();
	}
	public void setAll(Pixel color){
		for(int i=0; i<pixels.length; i++){
			pixels[i] = color;
		}
		reload();
	}
	private void reload(){
		String s = "";
		for(int i=0; i<pixels.length; i++){
			s=s+pixels[i].toString()+";";
		}
		port.writeString(s+"\n");
	}
	public static class Pixel{
		int r;
		int g;
		int b;
		public Pixel(int r, int g, int b){
			this.r = r;
			this.g = g;
			this.b = b;
		}
		public String toString(){
			return r+":"+g+":"+b;
		}
	}
}


