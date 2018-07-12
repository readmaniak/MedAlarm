package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import Instruction.Instruction;

public class Repository {
	
	private final String dbName;
	private final String login;
	private final String password;
	private final String url;
	
	public Repository(String login,String password,String dbName){
		this.login=login;
		this.password=password;
		this.dbName=dbName;
		this.url="jdbc:mysql://localhost:3306/"+dbName+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	}
	
	public ArrayList<Instruction.PatientLn> GetAllPatient(){
		ArrayList<Instruction.PatientLn> list=null;
		 try{
	           // Driver driverMySql =new FabricMySQLDriver();
	           // DriverManager.registerDriver(driverMySql);

	        }
	        catch(Exception e) {
	            System.out.println("error1");
	        }
	        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
	            ResultSet resultSet=statement.executeQuery("SELECT * FROM `pacients`;");
	            list=new ArrayList<Instruction.PatientLn>();
	            while(resultSet.next()){
	            	Instruction.PatientLn pacient=new Instruction.PatientLn(resultSet.getInt(1),resultSet.getInt(3),resultSet.getString(2));
	                list.add(pacient);
	            }

	        }catch (SQLException e){
	            System.out.println("error");

	        }
	        return list;
	}
	
	public void AddPacient(String name){
		String query="INSERT INTO `mydb`.`pacients` (`nameOfPatient`) VALUES ('"+name+"');";
		Execute(query);
	}
	
	public void AddPacientWhithRoom(String name,int room){
		String query="INSERT INTO `mydb`.`pacients` (`nameOfPatient`,`room`) VALUES ('"+name+"',"+room+");";
		Execute(query);
	}
	
	public Boolean CheckLogin(String login,String password){//Проверка логина и пароля
    	if(login.equals(this.login)&&password.equals(this.password)){
    		return true;
    	}
    	return false;
    }
	
	private String GetDate(){
		Calendar c=Calendar.getInstance();
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH);
		int day=c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);
		int second=c.get(Calendar.SECOND);
		return String.format("%d-%d-%d %d:%d:%d", year,month+1,day,hour,minute,second);
	}
	
	public void Execute(String query){//Передаёт бд выполнить команду, которая ничего не возвращает
        try{
           // Driver driverMySql =new FabricMySQLDriver();
           // DriverManager.registerDriver(driverMySql);
        }
        catch(Exception e) {
            System.out.println("error");
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            statement.execute(query);

        }catch (SQLException e){
          System.out.println("DatabaseExecutingError");

        }
    }
	public ArrayList<Instruction.ButtonLn> GetButtons(){//Выполняет команду, которая возвращает массив Pacient
        ArrayList<Instruction.ButtonLn> list=new ArrayList<Instruction.ButtonLn>(10);
        try{
           // Driver driverMySql =new FabricMySQLDriver();
           // DriverManager.registerDriver(driverMySql);

        }
        catch(Exception e) {
            System.out.println("error");
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM buttons");
            while(resultSet.next()){
            	Instruction.ButtonLn button=new Instruction.ButtonLn(resultSet.getInt(1),resultSet.getInt(4),resultSet.getBoolean(2),resultSet.getBoolean(3),resultSet.getInt(5));
                list.add(button);
            }

        }catch (SQLException e){
            System.out.println("error");

        }
        return list;
    }
	
	public ArrayList<Instruction.ButtonLn> GetUserButtons(){//Выполняет команду, которая возвращает массив Pacient
        ArrayList<Instruction.ButtonLn> list=new ArrayList<Instruction.ButtonLn>(10);
        try{
           // Driver driverMySql =new FabricMySQLDriver();
           // DriverManager.registerDriver(driverMySql);

        }
        catch(Exception e) {
            System.out.println("error");
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM buttons");
            while(resultSet.next()){
            	Instruction.ButtonLn button=new Instruction.ButtonLn(resultSet.getInt(1),resultSet.getInt(4),resultSet.getBoolean(2),resultSet.getBoolean(3),-1);
                if(button.getIsActive()&&!button.getIsVerifyingButton()){
                	list.add(button);
                }
            }

        }catch (SQLException e){
            System.out.println("error");

        }
        return list;
    }
	
	public Instruction.PatientLn GetPacient(int id){//Выполняет команду, которая возвращает массив Pacient
        Instruction.PatientLn pacient=null;
        try{
           // Driver driverMySql =new FabricMySQLDriver();
           // DriverManager.registerDriver(driverMySql);

        }
        catch(Exception e) {
            System.out.println("error");
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM pacients where idpacient = "+ id);
            while(resultSet.next()){
             pacient=new Instruction.PatientLn(resultSet.getInt(1),resultSet.getInt(3),resultSet.getString(2));
            }

        }catch (SQLException e){
            System.out.println("error");

        }
        return pacient;
    }
	
	public Boolean IsVerifyButton(int id){
		Instruction.ButtonLn button=null;
        try{
            //Driver driverMySql =new FabricMySQLDriver();
            //DriverManager.registerDriver(driverMySql);

        }
        catch(Exception e) {
            System.out.println("error");
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM buttons WHERE idbutton = "+id);
            while(resultSet.next()){
                button=new Instruction.ButtonLn(resultSet.getInt(1),resultSet.getInt(4),resultSet.getBoolean(2),resultSet.getBoolean(3),-1);
            }

        }catch (SQLException e){
            System.out.println("error");

        }
        return button.getIsVerifyingButton();
	}
	
	public Boolean IsActiveButton(int id){
		Instruction.ButtonLn button=null;
        try{
            //Driver driverMySql =new FabricMySQLDriver();
            //DriverManager.registerDriver(driverMySql);
        }
        catch(Exception e) {
            System.out.println("error");
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM buttons WHERE idbutton = "+id);
            while(resultSet.next()){
                button=new Instruction.ButtonLn(resultSet.getInt(1),resultSet.getInt(4),resultSet.getBoolean(2),resultSet.getBoolean(3),-1);
            }

        }catch (SQLException e){
            System.out.println("error");

        }
        return button.getIsActive();
	}
	
	public void SetActive(int id){
		String query="UPDATE `"+dbName+"`.`buttons` SET `is_active`= true WHERE `idbutton`='"+id+"';";
		Execute(query);
	}
	
	public Instruction.ButtonLn GetButton(int id){
		Instruction.ButtonLn button=null;
        try{
            //Driver driverMySql =new FabricMySQLDriver();
            //DriverManager.registerDriver(driverMySql);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        try(Connection connection=DriverManager.getConnection(url,login,password);Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM buttons WHERE idbutton = "+id);
            while(resultSet.next()){
                button=new Instruction.ButtonLn(resultSet.getInt(1),resultSet.getInt(4),resultSet.getBoolean(2),resultSet.getBoolean(3),resultSet.getInt(5));
            }

        }catch (SQLException e){
        	e.printStackTrace();

        }
        return button;
	}
	
	public void DeactivateButton(int id){
		String query="UPDATE `"+dbName+"`.`buttons` SET `is_active`= false WHERE `idbutton`='"+id+"';";
		Execute(query);
	}
	
	public void SetChekButton(int id,int room){
		String query="UPDATE `"+dbName+"`.`buttons` SET `is_check`='1', `room`='"+room+"', PacientID=NULL WHERE `idbutton`='"+id+"';";
		Execute(query);
	}
	
	public void DeverifyButton(int id){
		String query="UPDATE `"+dbName+"`.`buttons` SET `is_check`= false WHERE `idbutton`='"+id+"';";
		Execute(query);
	}
	
	public Boolean AddPacientButton(int buttonId,int userId){
		Instruction.ButtonLn button=GetButton(buttonId);
		if(button.getIsActive()&&!button.getIsVerifyingButton()){
			String query="UPDATE `"+dbName+"`.`buttons` SET `idpacient`= '" +userId+ "', room=NULL WHERE `idbutton`= '"+buttonId+"';";
	        Execute(query);
	        return true;
		}
		return false;
	}

	public void ClearButton(int buttonId){
		String query="UPDATE `"+dbName+"`.`buttons` SET `idpacient`= NULL, room=NULL, is_check = 0 WHERE `idbutton`= '"+buttonId+"';";
        Execute(query);
	}
	
	public void AddEvent(int userId){
		Instruction.PatientLn p=GetPacient(userId);
		String query="INSERT INTO `mydb`.`events` (`idpacient`, `start`, `room`) VALUES ('"+userId+"', '"+GetDate()+"', '"+p.getOrangeID()+"');";
		Execute(query);
	}
	
	public void PushChekButton(int room){
		String date=GetDate();
		String query="UPDATE `"+dbName+"`.`events` SET `finish`= '" +date+ "' WHERE `room`= '"+room+"' AND `finish` IS NULL;";
		System.out.println(query);
		Execute(query);
	}
	
	public void UpdatePacientRoom(int id,int room){
		String query="UPDATE `"+dbName+"`.`pacients` SET room="+room+" WHERE `idpacient`= '"+id+"';";;
        Execute(query);
	}
}