package at.ac.hcw.chat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

// for reading data from streams like file, socket or console
// what we use: InputStreamReader, BufferedReader, PrintWriter (auto flush = true)
// => necessary for sending and receiving the text messages
import java.net.*;
import java.net.Socket;
import java.util.Scanner;
// classes for network communication
// ServerSocket for opening a port and listening until client is connected
// Socket for establishing a TCP connection between two computers


public class ChatClient {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        // Host and port to connect to
        System.out.print("Enter ip address: ");
        String host=scanner.nextLine();// since both on same machine
        System.out.print("Enter port: ");
        int port=scanner.nextInt();
        // client connects to the server through the socket (IP and port)
        try (Socket socket = new Socket(host, port)){
            System.out.println("Connected to server successfully!");

            // Input stream to receive messages from server
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Output stream to send text to server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(),
                    true
            ); // auto-flush enabled

            // Reads user input from console
            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            // Thread responsible for receiving messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    String received;
                    while ((received = in.readLine()) != null) {

                        // If server sends "exit", close connection
                        if (received.equalsIgnoreCase("exit")) {
                            System.out.println("Server requested disconnect");
                            try{ socket.close();}catch(IOException ignored){}
                            System.exit(0);//terminate programm hier
                        }
                        System.out.println("Server: " + received);
                    }
                    System.out.println("Server closed to the connection.");
                } catch (IOException e) {
                    System.out.println("Connection closed unexpectedly");
                }
            });

            // Thread responsible for sending messages to the server
            Thread sendThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = userInput.readLine()) != null) {

                        out.println(message); // send message to server
                        System.out.println("Message delivered.");

                        // If client types "exit", close connection
                        if (message.equalsIgnoreCase("exit")) {
                            System.out.println("Client requested disconnect");

                            try{socket.close();}catch(IOException ignored){}

                            System.exit(0);//terminate programm hier
                        }
                    }
                    //after exiting the loop, close the socket so receiveThread sees null.

                } catch (IOException e) {
                    System.err.println("Error while client sending message: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // Start both threads
            receiveThread.start();
            sendThread.start();
            //check thread
            // Wait until both threads finish before ending the program
            receiveThread.join();
            sendThread.join();

            System.out.println("Client stopped safely");

        } catch (IOException | InterruptedException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}