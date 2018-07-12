package Server;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.*;

import Instruction.Instruction;
import Instruction.Instruction.Action;
import Instruction.Instruction.PatientLn;
import database.*;

public class ReadingThread extends Thread {
	
	Socket clientSocket;
	WritingManager wm;
	ObjectOutputStream writer;
	ObjectInputStream reader;
	boolean isOrange;
	String type;
	String login;
	String password;
	Repository rep;
	boolean root;
	Logger logger;
	int orangeID;
	
	
	ReadingThread(Socket clientSocket, WritingManager wm, Repository rep)
	{
		this.clientSocket = clientSocket;
		this.wm = wm;
		login = null;
		password = null;
		type = "default type (client or orange)";
		this.rep = rep;
		root = false;
		logger = wm.log();
		info("new connection: " + clientSocket.toString());
	}
	
	public void run()
	{
      InputStream in = null;
      reader = null;
      OutputStream out = null;
      writer = null;
      try {
         in = clientSocket.getInputStream();
         reader = new ObjectInputStream(in);
         
         out = clientSocket.getOutputStream();
         writer = new ObjectOutputStream(out);
         writer.flush();
         
      } catch (IOException e) {
	     severe("can't get an output/input stream, thread is closing: ", e);    	 
    	 return;
      }
      if (readFirstInstruction())
      {
    	  info(type + " is loginned successfully");
      }
      else 
      {
    	  severe(type + " wasn't loginned successfully, thread is closing");
    	  return;
      }
      
      while (true)
      {
    	  Instruction instruction = null;
    	  try {
  			instruction = (Instruction) reader.readObject();
  			info("from " + type + " was recieved: " + instruction);
  			Action action = instruction.getAction();
  			switch (action){
  				case AddPatient:
  					if (isOrange)
  					{
  						perm(action);
  					} else
  					if (!root)
  					{
  						rperm(action);
  					} else
  					{
  						
						rep.AddPacientWhithRoom(instruction.getPatientName(), instruction.getOrangeID());
						sendSuccess();
  					}
  					break;
  				case BindButtonToPatient:
  					if (isOrange)
  					{
  						perm(action);
  					} else
  					if (!root)
  					{
  						rperm(action);
  					} else
  					{  						
  						rep.AddPacientButton(instruction.getButtonID(), instruction.getPatientID());  						
  						sendSuccess();
  					}
  					break;
  				case ChangeActiveStatus:
  					if (isOrange)
  					{
  						perm(action);
  					} else
  					if (!root)
  					{
  						rperm(action);
  					} else
  					{
  						if (instruction.getB())
  							rep.SetActive(instruction.getButtonID());
  						else
  							rep.DeactivateButton(instruction.getButtonID()); 
  						sendSuccess();
  					}
  					break;
  				case SetVerifyingStatus:
  					if (isOrange)
  					{
  						perm(action);
  					} else
  					if (!root)
  					{
  						rperm(action);
  					} else
  					{
  						rep.SetChekButton(instruction.getButtonID(), instruction.getOrangeID());
  						sendSuccess();
  					}
  					break;
  				case RemoveVerifyingStatus:
  					if (isOrange)
  					{
  						perm(action);
  					} else
  					if (!root)
  					{
  						rperm(action);
  					} else
  					{
  						rep.DeverifyButton(instruction.getButtonID());
  						sendSuccess();
  					}
  					break;
  				case ChangePatientRoom:
  					if (isOrange)
  					{
  						perm(action);
  					} else
  					if (!root)
  					{
  						rperm(action);
  					} else
  					{
  						rep.UpdatePacientRoom(instruction.getPatientID(), instruction.getOrangeID());
  						sendSuccess();
  					}
  					break;
  				case MsgSent:
  					if (!isOrange)
  					{
  						perm(Instruction.Action.MsgSent);
  						break;
  					}  					
  					int _buttonID = instruction.getButtonID();
  					if (rep.IsVerifyButton(_buttonID)) //BUTTON = SPECIAL
  					{
  	  					wm.SendToAllClients(Instruction.CreateMsgVerified(orangeID));
  	  					rep.PushChekButton(rep.GetButton(_buttonID).getRoom());
  	  					break;
  					}
  					PatientLn p = rep.GetPacient(_buttonID);  					
  					int __orangeID = p.getOrangeID();
  					String _patientName = p.getPatientName();
  					wm.SendToAllClients(Instruction.CreateMsgSent_OnServerSide(_buttonID, __orangeID, _patientName));
  					rep.AddEvent(p.getID());
  					break;
   				case MsgReceived:
   					if (isOrange)
   					{
   						perm(Instruction.Action.MsgReceived);
   						break;
   					}
   					int _orangeID = rep.GetPacient(instruction.getButtonID()).getOrangeID();
   					wm.SendToOrange(_orangeID, instruction);
   					break;
  				case Notify:
  					if (isOrange)
  					{
  						perm(Instruction.Action.Notify);
  						break;
  					}
  					wm.SendToAllOranges(instruction);
  					break;
  				case Table:
  					if (isOrange)
  					{
  						perm(Instruction.Action.Table);
  						break;
  					}
  					Instruction instr = Instruction.CreateTable(rep.GetAllPatient(), rep.GetButtons());
  					synchronized (writer) {
  						writer.writeObject(instr);						
					}
  					info("this goes to a client: "+instr);
  					break;
  				case Error:
  					severe("from " + type + " was recieved an error: " +instruction.getError());  					
  					break;
  				default:
  					throw new MyException("wrong type of instruction: " + instruction);
  			}
  	      }
  	      catch (ClassNotFoundException e) {
  	    	  severe("class not found exception when reading instruction from " + type + ". closing this thread.", e);
  	      }
    	  catch (IOException e)
    	  {
    		  info("IOException, probably " + type + " app was closed", e);
    		  if (isOrange)
    		  {
    			  severe("OrangePI app was closed, this is not yet handled on the server side. Restart the server and all the OrangePIs");
    		  }
    		  wm.RemoveClient(writer);
  	    	  try {
				clientSocket.close();
				info("socket closed");
			} catch (IOException e1) {
				info("error while closing socket");
			}
  	    	wm.RemoveThread(this);
  	    	return;
    		  
    	  }
  	      catch (MyException e)
  	      {
  	    	  sendError(e.getMessage());
  	    	  severe("Something went wrong: ", e);
  	      }
      }
      
	}
	
	private void perm(Instruction.Action action)
	{
		sendError("you're " + type + " and you can't use " + action);
		warning(type + (isOrange?" " + orangeID:"") + " triend to use " + action + " and was not allowed to do so");
	}
	
	private void rperm(Instruction.Action action)
	{
		sendError("you're not a root, so you can't use " + action);
		warning("client without root tried to use " + action);
	}
	
	private boolean readFirstInstruction()
	{
		try {
			Instruction instruction = (Instruction) reader.readObject();
			info("as login instruction recieved: " + instruction);
			Action action = instruction.getAction();
			if (action==Action.ClientLogin)
			{
				wm.AddClient(writer);
				isOrange = false;
				type = "client";
				login = instruction.getLogin();
				password = instruction.getPassword();
				if (login==null && password==null)
				{
					root = false;
				} else
				{
					
						if (!rep.CheckLogin(login, password))
						{
							throw new MyException("wrong combination of login and password, please try again");
						}
					
					root = true;
				}
				sendSuccess();			
				
			} else if (action==Action.OrangeLogin)
			{
				orangeID = instruction.getOrangeID();
				wm.AddOrange(orangeID, writer);
				isOrange = true;
				type = "orange";
			} else
			{
				throw new MyException("wrong type of first instruction, please send the correct one");
			}
	      }
	      catch (ClassNotFoundException e) {
	    	  severe("class not found exception when reading first instruction, we're closing this thread", e);
	    	  
	    	  return false;
	      }
	      catch (IOException e) {
	    	  severe("io exception when reading first instruction, we're closing this thread", e);
	    	 
	    	  return false; 
	      }
	      catch (MyException e)
	      {
	    	  sendError(e.getMessage());
	    	  warning("something went wrong: ", e);
	    	  return readFirstInstruction();
	      }
		return true;
	}
	
	private void sendError(String error)
	{		
		try {
			synchronized (writer)
			{
			writer.writeObject(Instruction.CreateError(error));
			}
		} catch (IOException e) {
			severe("Can't send error message to " + type, e);
		}
		
	}
	
	private void info(String str)
	{
		synchronized(logger)
		{
		logger.info(str);
		}
	}
	private void warning(String str)
	{
		synchronized(logger)
		{
		logger.warning(str);
		}
	}
	private void severe(String str)
	{
		synchronized(logger)
		{
		logger.severe(str);
		}
	}
	private void severe(String str, Exception e)
	{
		synchronized(logger)
		{
			logger.log(Level.SEVERE, str, e);
		}
	}
	private void info(String str, Exception e)
	{
		synchronized(logger)
		{
			logger.log(Level.INFO, str, e);
		}
	}
	private void warning(String str, Exception e)
	{
		synchronized(logger)
		{
			logger.log(Level.WARNING, str, e);
		}
	}
	
	
	private void sendSuccess() {
		Instruction instr = Instruction.CreateSuccess();
		try {
			synchronized (writer)
			{
			writer.writeObject(instr);
			}
			info("this was send to " + type + ": " + instr);
		} catch (IOException e) {
			severe("Can't send success message to " + type, e);
		}
	}
	
	
}
