import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;

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
    public BufferedImage readImage(int width, int height) {
        BufferedImage img = null;
        try {
            byte[] bytes = readSocketBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            img = ImageIO.read(bais);
        } catch (Exception e) {
            System.out.println("Error creating image from bytes.");
        }
        return img;
    }
    public static void main(String[] args) {
        CameraClient driveStation = new CameraClient("172.17.126.16", 5439);
        driveStation.initSocket();
        
        try {  
            BufferedImage img = driveStation.readImage(1000,1000);
            DisplayImage testImg = new DisplayImage(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static class DisplayImage {
        public DisplayImage(BufferedImage img) throws IOException {
            ImageIcon icon = new ImageIcon(img);
            JFrame frame = new JFrame();
            JLabel lbl = new JLabel(icon);
            frame.setSize(200,300);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
        }
    }
}
