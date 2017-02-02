import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
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
    private byte[] readImage() {
        ByteArrayOutputStream bs = null;
        try {   
            bs = new ByteArrayOutputStream();
            byte[] buffer = new byte[10000];
            int s = 0;
            while ((s = in.read(buffer, 0, 10000)) != -1) {
                if ((char)buffer[s-1] == 'e'){
                    bs.write(buffer, 0, s-1);
                    break;
                } else {
                    bs.write(buffer, 0, s);
                }
            }
            if (s == -1) {
                return null;
            }
        } catch (IOException e) {
            System.out.println("Error reading bytes from socket.");
        }
        return bs.toByteArray();
    }
    public static void main(String[] args) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int[] topLeft1 = {0,0};
        int[] topLeft2 = {(int)(screen.width / 2.0), 0};
        int[] dims = {(int)(screen.width / 2.0), (int)(screen.height / 2.0)};
        CameraClient cam1 = new CameraClient("172.17.126.199", 5808);
        CameraClient cam2 = new CameraClient("172.17.126.199", 5809);
        cam1.initSocket();
        cam2.initSocket();
        
        byte[] camFrame1 = null;
        byte[] camFrame2 = null;
        
        DisplayImage window1 = null;
        DisplayImage window2 = null;
        
        try {
            camFrame1 = cam1.readImage();
            camFrame2 = cam2.readImage();
            window1 = new DisplayImage(camFrame1, topLeft1, dims);
            window2 = new DisplayImage(camFrame2, topLeft2, dims);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int fps = 0;
        long time0 = System.currentTimeMillis();
        
        while (true) {
            try {
                camFrame1 = cam1.readImage();
                camFrame2 = cam2.readImage();
                if (camFrame1 == null || camFrame2 == null) {
                    continue;
                }
                window1.updateImage(camFrame1);
                window2.updateImage(camFrame2);
                fps++;
                if (System.currentTimeMillis() - time0 >= 1000) {
                    System.out.println(fps + " Frames in Last Second");
                    fps = 0;
                    time0 = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static class DisplayImage {
        private JLabel lbl;
        public DisplayImage(byte[] img, int[] topLeft, int[] dims) throws IOException {
            ImageIcon icon = new ImageIcon(img);
            JFrame frame = new JFrame();
            lbl = new JLabel();
            lbl.setIcon(icon);
            frame.setSize(dims[0], dims[1]);
            frame.setLocation(topLeft[0], topLeft[1]);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
        }
        public void updateImage(byte[] img) {
            lbl.setIcon(new ImageIcon(img));
        }
    }
}
