package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;
import database.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MainServerClass{
	
   public static void main(String[] args) {
	  log = Logger.getLogger("main logger");
	  //log.setLevel(Level.SEVERE);
      // if no arguments are used - we use default value
      int port = DEFAULT_PORT;
      if (args.length > 0) {
         port = Integer.parseInt(args[0]);
      }
      
      ServerSocket serverSocket = null;
      try {
    	  serverSocket = new ServerSocket(port,0,InetAddress.getByName("0.0.0.0"));
      } catch (IOException e) {
         log.log(Level.SEVERE, "Exception: ", e);
         System.exit(-1);
      }
      WritingManager wm = new WritingManager(log);
      Repository rep = new Repository("newuser", "root", "mydb");
      
      log.info("Server has started and it is now catching clients");
      while (true)
      {
    	  ReadingThread rt;
      try {
         rt = new ReadingThread(serverSocket.accept(), wm, rep);
         wm.AddThread(rt);
         rt.start();
	      } catch (IOException e) {
	    	  synchronized(log) {
	         log.log(Level.SEVERE, "Error while connecting to port "+port+": ", e);
	    	  }
	         System.exit(-1);
	      }
      }
      
   }

   private static final int DEFAULT_PORT = 9999;
   static Logger log;
}