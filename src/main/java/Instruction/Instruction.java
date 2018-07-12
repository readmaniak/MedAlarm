package Instruction;

import java.io.Serializable;
import java.util.ArrayList;

public class Instruction implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4300633798622429390L;

	public enum Action {
		//login
		OrangeLogin, //first instruction sent by OrangePie
		ClientLogin, //first instruction sent by Client
		
		//table changing, requires Root
		AddPatient, //set new patient
		BindButtonToPatient, //changes patientID of an active and non-verifying button
		ChangeActiveStatus, //changes isActive field of the button
		RemoveVerifyingStatus, //makes isVerifying field of the button "false"
		SetVerifyingStatus, //makes isVerifying field of the button "true" and sets the room for it
		ChangePatientRoom, //changes the room of a Patient
		
		//signals
		MsgSent, //this instruction goes from OrangePie on Server, and from Server on Clients
		MsgReceived, //this instruction goes from Client on Server, and from Server on OrangePie
		Error, //server will send this if something goes wrong
		Success, //server will send this if bind/unbind/clientLogin goes ok
		Notify, //notify all patients that it's dinner time, lol
		Table, /*this instruction can go from Client on Server, in response to that Server
				will send another Table instruction with DataBase in patients and buttons;
		 		*/
		MsgVerified /*special button pressed in a room to verify accepting of
				all requests from this room. such instruction goes only from Server to Clients,
				because orangePI always uses MsgSent for all types of buttons
				*/
		
	}
	
	static public class PatientLn implements Serializable
	{			
		/**
		 * 
		 */
		private static final long serialVersionUID = 1219785329987307817L;
		private int ID;
		private int orangeID;
		private String patientName;
		
		public PatientLn(int ID, int orangeID, String patientName)
		{
			this.ID = ID;
			this.orangeID = orangeID;
			this.patientName = patientName;
		}
		
		public int getID()
		{
			return ID;
		}
		
		public int getOrangeID() // == room
		{
			return orangeID;
		}
		public String getPatientName()
		{
			return patientName;
		}
	}
	
	static public class ButtonLn implements Serializable
	{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6200414185731156630L;
		private int ID;
		private boolean isActive;
		private boolean isVerifyingButton;
		private int patientID;
		private int room;
		
		public ButtonLn(int ID, int patientID, boolean isActive, boolean isCheckButton, int room){
			this.ID=ID;
			this.patientID = patientID;
			this.isActive = isActive;
			this.isVerifyingButton = isCheckButton;
			this.room = room;
		}
		
		public int getID()
		{
			return ID;
		}
		
		public int getPatientID()
		{
			return patientID;
		}
		
		public boolean getIsActive()
		{
			return isActive;
		}
		
		public boolean getIsVerifyingButton()
		{
			return isVerifyingButton;
		}
		
		public int getRoom()
		{
			return room;
		}
	}
	
	private Action action;
	private int buttonID;
	private int orangeID; // == room
	private int patientID;
	private boolean b; //just bool, required for some methods
	private String patientName;
	private String login;
	private String password;
	private String error;
	private ArrayList<PatientLn> patients;
	private ArrayList<ButtonLn> buttons;
	
	public Action getAction()
	{
		return action;
	}
	
	public int getButtonID()
	{
		return buttonID;
	}
	
	public int getOrangeID()
	{
		return orangeID;
	}
	
	public int getPatientID()
	{
		return patientID;
	}
	
	public boolean getB()
	{
		return b;
	}
	
	public String getPatientName()
	{
		return patientName;
	}
	
	public String getLogin()
	{
		return login;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getError()
	{
		return error;
	}
	
	public ArrayList<PatientLn> getPatients()
	{
		return patients;
	}
	
	public ArrayList<ButtonLn> getButtons()
	{
		return buttons;
	}
	
	private Instruction(Action action)
	{
		this.action = action;
		buttonID = -1;
		orangeID = -1;
		patientID = -1;
		patientName = null;
		login = null;
		password = null;
		error = null;
		patients = null;
		buttons = null;
	}
	
	private Instruction setButtonID(int buttonID)
	{
		this.buttonID = buttonID;
		return this;
	}
	
	private Instruction setOrangeID(int orangeID)
	{
		this.orangeID = orangeID;
		return this;
	}
	
	private Instruction setPatientID(int patientID)
	{
		this.patientID = patientID;
		return this;
	}
	
	private Instruction setB(boolean b)
	{
		this.b = b;
		return this;
	}
	
	private Instruction setPatientName(String patientName)
	{
		this.patientName = patientName;
		return this;
	}
	
	private Instruction setLogin(String login)
	{
		this.login = login;
		return this;
	}
	
	private Instruction setPassword(String password)
	{
		this.password = password;
		return this;
	}
	
	private Instruction setError(String error)
	{
		this.error = error;
		return this;
	}
	
	private Instruction setPatients(ArrayList<PatientLn> patients)
	{
		this.patients = patients;
		return this;
	}
	
	private Instruction setButtons(ArrayList<ButtonLn> buttons)
	{
		this.buttons = buttons;
		return this;
	}
	
	static public Instruction CreateOrangeLogin(int orangeID)
	{
		return new Instruction(Action.OrangeLogin).setOrangeID(orangeID);
	}
	
	static public Instruction CreateClientLogin(String login, String password)
	{
		return new Instruction(Action.ClientLogin).setLogin(login).setPassword(password);
	}
	
	static public Instruction CreateAddPatient(String patientName, int orangeID)
	{
		return new Instruction(Action.AddPatient).setPatientName(patientName).setOrangeID(orangeID);
	}
	
	static public Instruction CreateBindButtonToPatient(int buttonID, int patientID)
	{
		return new Instruction(Action.BindButtonToPatient).setButtonID(buttonID).setPatientID(patientID);
	}
	
	static public Instruction CreateChangeActiveStatus(int buttonID, boolean b)
	{
		return new Instruction(Action.ChangeActiveStatus).setB(b);
	}
	
	static public Instruction CreateRemoveVerifyingStatus(int buttonID)
	{
		return new Instruction(Action.RemoveVerifyingStatus).setButtonID(buttonID);
	}
	
	static public Instruction CreateSetVerifyingStatus(int buttonID, int orangeID)
	{
		return new Instruction(Action.SetVerifyingStatus).setButtonID(buttonID).setOrangeID(orangeID);
	}
	
	static public Instruction CreateChangePatientRoom(int patientID, int orangeID)
	{
		return new Instruction(Action.ChangePatientRoom).setPatientID(patientID).setOrangeID(orangeID);
	}
	
	static public Instruction CreateMsgSent_OnOrangeSide(int buttonID)
	{
		return new Instruction(Action.MsgSent).setButtonID(buttonID);
	}
	
	static public Instruction CreateMsgSent_OnServerSide(int buttonID, int orangeID, String patientName)
	{
		return new Instruction(Action.MsgSent).setButtonID(buttonID).setOrangeID(orangeID).setPatientName(patientName);
	}
	
	static public Instruction CreateMsgReceived(int buttonID)
	{
		return new Instruction(Action.MsgReceived).setButtonID(buttonID);
	}
	
	static public Instruction CreateMsgVerified(int orangeID)
	{
		return new Instruction(Action.MsgVerified).setOrangeID(orangeID);
	}
	
	static public Instruction CreateNotify()
	{
		return new Instruction(Action.Notify);
	}
	
	static public Instruction CreateError(String error)
	{
		return new Instruction(Action.Error).setError(error);
	}
	
	static public Instruction CreateTable(ArrayList<PatientLn> patients, ArrayList<ButtonLn> buttons)
	{
		return new Instruction(Action.Table).setPatients(patients).setButtons(buttons);
	}
	
	static public Instruction CreateSuccess()
	{
		return new Instruction(Action.Success);
	}
	
	public String toString() //only for testing and debug
	{
		return action.toString() + " " + buttonID + " " + orangeID + " " + patientName + " " + login + " " + password + " " + error;
	}
}