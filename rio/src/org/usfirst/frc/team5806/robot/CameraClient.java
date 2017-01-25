import java.net.*;
import java.io.*;

public class CameraClient {
    private String serverName;
    private INetAddress staticIP;
    private int serverPort;
    private Socket client;
    private OutputStream out;
    private InputStream in;
    
    public CameraClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    public CameraClient(INetAddress staticIP, int serverPort) {
        //Alternative constructor for referencing cam by a static ip rather
        //than a saved name on the LAN.
        this.staticIP = staticIP;
        this.serverPort = serverPort;
    }
    public CameraClient() {}
    public void setServerName(String name) {
        this.serverName = name;
    }
    public void setStaticIP(INetAddress staticIP) {
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
            if (INetAddress == null) {
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
    private byte[] readSocketData() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int s = 0; (s = in.read(buffer)) != -1; ) {
            bs.write(buffer, 0, s);
        }
        return bs.toByteArray();
    }
    public BufferedImage readImage(int width, int height) {
        byte[] inBytes = readSocketData();
        //FINISH THIS
    }
}
