package Server;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.*;

import Instruction.Instruction;

public class WritingManager {
	
	HashMap<Integer, ObjectOutputStream> oranges;
	Set<ReadingThread> threads;
	LinkedList<ObjectOutputStream> clients;
	Logger logger;
	
	WritingManager(Logger log)
	{
		oranges = new HashMap<Integer, ObjectOutputStream>();
		clients = new LinkedList<>();
		logger = log;
		threads = new HashSet<>();
	}
	
	public Logger log()
	{
		return logger;
	}
	
	public void AddClient(ObjectOutputStream cl) 
	{
		synchronized (clients)
		{
			clients.add(cl);
		}
	}
	
	public void AddOrange(int id, ObjectOutputStream oos)
	{
		synchronized (oranges)
		{
			oranges.put(id, oos);
		}
	}
	
	public void SendToAllOranges(Instruction instruction)
	{
		synchronized (logger)
		{
			logger.info("started sending to all oranges this: " + instruction);
		}
		
		Set<Integer> set;
		synchronized (oranges)
		{
			set = oranges.keySet();
		}
		for (Integer i : set)
		{
			try {
				SendToOrange(i, instruction);			
			}
			catch (MyException e) 
			{
				synchronized(logger)
				{
					logger.log(Level.SEVERE, "Orange with id " + i + " doesn't exist while sending instruction " + instruction, e);
				}
			}
		}
	}
	
	public void SendToAllClients(Instruction instruction)
	{
		synchronized (logger)
		{
			logger.info("started sending to all clients this: " + instruction);
		}
		synchronized (clients) {
			for (ObjectOutputStream oos : clients)
			{				
				try {
					synchronized (oos)
					{
					oos.writeObject(instruction);
					}
				} catch (IOException e) {
					synchronized(logger)
					{
					logger.log(Level.SEVERE, "can't send to a client while sending this: " + instruction, e);
					}
				}				
			}
		}
	}
	
	public void SendToOrange(int id, Instruction instruction) throws MyException
	{
		ObjectOutputStream oos;
		synchronized(oranges)
		{
			if (!oranges.containsKey(id))
				throw new MyException("orange with number "+ id + "doesn't exist");
			oos = oranges.get(id);
		}	
		try {
			synchronized(oos)
			{
			oos.writeObject(instruction);
			}
		} catch (IOException e) {
			synchronized(logger)
			{
				logger.log(Level.SEVERE, "can't send to orange number " + id + " while sending this: "+instruction, e);
			}
		}		
	}
	
	public void RemoveClient(ObjectOutputStream oos)
	{
		synchronized (clients)
		{
			clients.remove(oos);
		}
	}
	
	public void AddThread(ReadingThread thread)
	{
		synchronized (threads)
		{
			threads.add(thread);
		}		
	}
	
	public void RemoveThread(ReadingThread thread)
	{
		synchronized(threads)
		{
			threads.remove(thread);
		}
	}
}
