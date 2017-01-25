import java.net.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class CameraClient {
    private String hostIP;
    private int serverPort;
    private Socket client;
    private OutputStream out;
    private InputStream in;
    
    public CameraClient(String hostIP, int serverPort) {
        this.hostIP = hostIP;
        this.serverPort = serverPort;
    }
    public CameraClient() {}
    public void sethostIP(String hostIP) {
        this.hostIP = hostIP;
    }
    public void setServerPort(int port) {
        this.serverPort = port;
    }
    public String getHostIP() {
        return hostIP;
    }
    public int getServerPort() {
        return serverPort;
    }
    public void initSocket() {
        try {
            this.client = new Socket(hostIP, serverPort);
            this.out = this.client.getOutputStream();
            this.in = this.client.getInputStream();
            System.out.println("Client socket successfully initialized.");
        } catch (IOException e) {
            System.out.println("Error Initializing Socket or Input/Output Streams");
        }
        
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
    public BufferedImage readImage() {
        BufferedImage img = null;
        try {
            byte[] inBytes = readSocketBytes();
            InputStream imageIn = new ByteArrayInputStream(inBytes);
            img = ImageIO.read(imageIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }
    public static void main(String[] args) {
        CameraClient driveStation = new CameraClient("172.17.125.210", 5439);
        driveStation.initSocket();
        byte[] bytes = driveStation.readSocketBytes();
        
        System.out.println(Arrays.toString(bytes));
        
        try {  
            BufferedImage img = driveStation.readImage();
            ImageIO.write(img, "jpg", new File("socketTest.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
