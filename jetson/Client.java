import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    public static void main(String args[]) {
        (new Thread(new Client())).start(); // enables it to be run alone, or called from other file
    }

    public void run() {

        String hostName = "tegra-ubuntu.local"; // <-- this hostname untested, but code works with my comp
        int portNumber = 5806;

        try (
            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        ) {
            out.println("Begin OpenCV");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
