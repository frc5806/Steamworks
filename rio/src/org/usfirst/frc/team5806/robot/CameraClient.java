import java.net.*;
import java.io.*;

public class CameraClient {
	private String serverName;
	private INetAddress staticIP;
	private int serverPort;
	private Socket client;
	private BufferedWriter out;
	private BufferedReader in;
	
	public DriveStationCameraClient(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;
	}
	public DriveStationCameraClient(String staticIP, int serverPort) {
		//Alternative constructor for referencing cam by a static ip rather
		//than a saved name on the LAN.
		this.staticIP = INetAddress.getByAddress(staticIP);
		this.serverPort = serverPort;
	}
	public DriverStationCameraClient() {}
	public void setServerName(String name) {
		this.serverName = name;
	}
	public void setStaticIP(String staticIP) {
		this.staticIP = INetAddress.getByAddress(staticIP);
	}
	public void setServerPort(int port) {
		this.serverPort = port;
	}
	public String getServerName() {
		return serverName;
	}
	public String getStaticIPName() {
		return staticIP.toString();
	}
	public int getServerPort() {
		return serverPort;
	}
	public void initSocket() {
		try {
			if (INetAddress == null) {
				this.client = new Socket(serverName, serverPort);
			} else {
				this.client = new Socket(staticIP, serverPort);
			}
			this.out = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));
			this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
		} catch (IOException e) {
			System.out.println("Error Initializing Socket or Input/Output Streams");
		}
		System.out.println("Client socket successfully initialized.");
	}
	private String readLine() {
		return in.readLine();
	}
	private int[] readLineToIntArray() {
		//The image is being passed as stream of unsigned chars
		//from the c++, but Java client receives it as String
		//and should turn it into an array of ints, which can be
		//used to construct an Image object.
		String line = readLine();
		String[] lineSplit = line.split(" ");
		int[] a = new int[lineSplit.length];
		for (int i = 0; i < a.length; i++) {
			try {
				a[i] = Integer.parseInt(lineSplit[i]);
			} catch (NumberFormatException e) {
				System.out.println("There was an error parsing the number data from the server.");
			}
		}
		return a;
	}
	public BufferedImage readImage() {
		//FINISH THIS
	}
}
