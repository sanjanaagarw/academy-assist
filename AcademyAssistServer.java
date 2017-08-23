package com.nss.academyassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class AcademyAssistServer {
	public static ServerSocket serverSocket;
	public static Socket clientSocket;

	static final int PORT = 4444;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			serverSocket = new ServerSocket(PORT); // Server socket			
		} catch (IOException e) {
			System.out.println("Could not listen on port: "+PORT+" \n");
		}
		System.out.println("Server started. Listening to the port "+PORT);
	
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				//System.out.println("Hi");// accept the client connection
				System.out.println("New connection accepted.");
			} catch (IOException ex) {
				System.out.println("Problem in message reading");
			}
			//new thread for a client				
			new EchoThread(clientSocket).start();
		}
	}
}

class EchoThread extends Thread {
	
	InputStreamReader inputStreamReader;
	private PrintWriter outToClient;
	BufferedReader bufferedReader;
	String fromClient;
    Socket clientSocket;
    
	// Initialize the tagger
	MaxentTagger tagger = new MaxentTagger("taggers/english-caseless-left3words-distsim.tagger");

    public EchoThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
        	inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader); // get the client message
			
			outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        
        while (true) {//!Thread.currentThread().isInterrupted()) {
        	
            try {
				fromClient = bufferedReader.readLine();
				String tagged="";
                if ((fromClient == null) || fromClient.equalsIgnoreCase("ok thanks?")) {
                	System.out.println("Bye Thread " + Thread.currentThread().getId() + "!" ); 
                	Thread.currentThread().interrupt();               	
                    break;
                    //return;
                } else {
                	//Per thread processing here...
    		    	tagged = tagger.tagString(fromClient);
                	System.out.println(tagged);
                	
                	outToClient.write(tagged);  
                	outToClient.write("\n"); //delimiter   				
                	outToClient.flush();
                	
                    System.out.println("I am thread " + Thread.currentThread().getId());                	                                   	
                }
                                               
            } catch (IOException e) {
                System.out.println("reading Error");//e.printStackTrace();
                return;
            }
            
        }
        
        if(Thread.currentThread().isInterrupted())
        {
        	System.out.println("is interrupted");
        }
        
        try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}