import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * This is the entry point of our JSON parsing program.
 */
public class TCPServer {

    private Socket socket   = null;
    private ServerSocket server   = null;
    private BufferedReader in       =  null;

    public TCPServer(int port) throws SocketException
    {
        try
        {
            server = new ServerSocket(port);
            socket = server.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            IOHandler io = new IOHandler(in);

            String line = "";
            String input = "";

            while (true)
            {
                try
                {
                    line = in.readLine();
                    if (!line.equals("EOF")) {
                        break;
                    } else {
                        input += line;
                    }
                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            io.printOutput(input);
            socket.close();
            in.close();

        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    /**
     * Creates instance of Inpot/Output handler that will parse json and display input.
     * @param args run-time arguments: -up or -down.
     */
    public static void main (String[] args) throws SocketException {
        TCPServer server = new TCPServer(8000);
    }
}
