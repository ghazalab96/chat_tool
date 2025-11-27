package at.ac.hcw.chat;

import java.net.*;
import java.util.*;
import java.io.*;

// for reading data from streams like file, socket or console
// what we use: InputStreamReader, BufferedReader, PrintWriter (auto flush = true)
// => necessary for sending and receiving the text messages

import java.net.ServerSocket;
import java.net.Socket;
// classes for network communication
// ServerSocket for opening a port and listening until client is connected
// Socket for establishing a TCP connection between two computers


// defining the main class called ChatServer
public class ChatServer {
    public static void main(String[] args) throws Exception {
        //the port where server will listen for connections
        Scanner scanner = new Scanner(System.in);
        System.out.print("Port : ");
        int port = scanner.nextInt();// port input
        String ip = getLocalIPv4();
        System.out.println("Ip is : " + ip);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            //wait until a client connects, this is a blocking call
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected successfully!");

            //These streams allow the server to send and receive text messages
            InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader in = new BufferedReader(inputStreamReader);

            // better option than BuffereWriter because of: 1- println 2-autoFlush 3-speed is not important here cause the messages are short.
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(),
                    true
            ); // auto-flush enabled

            // Read user input from server console
            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            //Thread responsible for receiving messages from the client
            Thread receiveThread = new Thread(() -> {
                try {
                    String received;
                    while ((received = in.readLine()) != null) {
                        //if client sends "exit", stop hte connection
                        if (received.equalsIgnoreCase("exit")) {
                            System.out.println("Client requested disconnect");
                            try{clientSocket.close();}catch(IOException ignored){}
                            System.exit(0);//terminate programm hier
                        }
                        System.out.println("Client: " + received);
                    }
                    System.out.println("Client closed the connection");
                } catch (IOException e) {
                    // socket closed is expected when exit is called
                    System.out.println("Connection closed by server or client.");
                }
            });

            // Thread responsible for sending messages from server user to the client
            Thread sendThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = userInput.readLine()) != null) {
                        out.println(message); // send message to the client
                        System.out.println("Message delivered.");
                        //if server types exit, close connection
                        if (message.equalsIgnoreCase("exit")) {
                            System.out.println("Server requested disconnect");
                            try{clientSocket.close();}catch(IOException ignored){}
                            System.exit(0);//terminate programm hier
                        }
                    }
                    //after exiting the loop, close the socket so receiveThread sees null.

                } catch (IOException e) {
                    System.err.println("Error happened while server was communicating with the client: " + e.getMessage());
                    e.printStackTrace();   // optional for debugging
                }
            });

            //start both threads simultaneously
            receiveThread.start();
            sendThread.start();

            //wait until both threads finish before closing socket
            try {
                receiveThread.join();
                sendThread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // preserve interrupt status
            }


            // guaranties the safe port kill (so we don't get the "Already in use" message.

            //close server
            // serverSocket.close();
            System.out.println("Server stopped safely");

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }

    }
    // Radi

    public static String getLocalIPv4() throws SocketException { // get ipv4 address function
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        while (nets.hasMoreElements()) {
            NetworkInterface nif = nets.nextElement();

            if (!nif.isUp() || nif.isLoopback() || nif.isVirtual())
                continue;

            for (InterfaceAddress ia : nif.getInterfaceAddresses()) {
                InetAddress addr = ia.getAddress();

                if (addr instanceof Inet4Address) {
                    String ip = addr.getHostAddress();

                    // nur private lokale IPv4-Adressen zurückgeben
                    if (isPrivateIPv4(ip)) {
                        return ip;
                    }
                }
            }
        }
        return null;
    }
//check private ip
    private static boolean isPrivateIPv4(String ip) { // to check if its a private ip address or just loopback
        return ip.startsWith("10.") ||
                ip.startsWith("192.168.") ||
                ip.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*");
    }
}