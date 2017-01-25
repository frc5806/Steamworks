import java.net.*;
import java.io.*;
import java.awt.image.BufferedImage;

public class CameraClient {
    private String serverName;
    private InetAddress staticIP;
    private int serverPort;
    private Socket client;
    private OutputStream out;
    private InputStream in;
    
    public CameraClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    public CameraClient(InetAddress staticIP, int serverPort) {
        //Alternative constructor for referencing cam by a static ip rather
        //than a saved name on the LAN.
        this.staticIP = staticIP;
        this.serverPort = serverPort;
    }
    public CameraClient() {}
    public void setServerName(String name) {
        this.serverName = name;
    }
    public void setStaticIP(InetAddress staticIP) {
        this.staticIP = staticIP;
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
            if (staticIP == null) {
                this.client = new Socket(serverName, serverPort);
            } else {
                this.client = new Socket(staticIP, serverPort);
            }
            this.out = this.client.getOutputStream();
            this.in = this.client.getInputStream();
        } catch (IOException e) {
            System.out.println("Error Initializing Socket or Input/Output Streams");
        }
        System.out.println("Client socket successfully initialized.");
    }
    public void writeString(String s) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(s);
            bw.close();
        } catch (IOException e) {
            System.out.println("Error writing string to socket.");
        }
    }
    public String readSocketString() {
        String s = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            s = br.readLine();
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading string from socket.");
        }
        return s;
    }
    private byte[] readSocketBytes() {
        ByteArrayOutputStream bs = null;
        try {   
            bs = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int s = 0; (s = in.read(buffer)) != -1; ) {
                bs.write(buffer, 0, s);
            }
        } catch (IOException e) {
            System.out.println("Error reading bytes from socket.");
        }
        return bs.toByteArray();
    }
    /*public BufferedImage readImage(int width, int height) {
        byte[] inBytes = readSocketData();
        
    }*/
    public static void main(String[] args) {
        CameraClient driveStation = new CameraClient("", 999);
        String line = driveStation.readSocketString();
        System.out.println(line);
    }
}
