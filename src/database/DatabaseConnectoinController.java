package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utilities.LoggerUtility;

public class DatabaseConnectoinController {
	protected final DatabaseType dbType;
	private final String urlType, driverType;
	private Connection connection = null;
	
	public enum DatabaseType {
		MYSQL,
		SQLSERVER,
		POSTGRESQL,
	}
	
	public DatabaseConnectoinController(DatabaseType type) {
		dbType = type;
		
		switch (dbType) {
			case MYSQL:
				driverType = "com.mysql.jdbc.Driver";
				urlType = "jdbc:mysql://";
				break;
			case SQLSERVER:
				driverType = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				urlType = "jdbc:postgresql://";
				break;
	
			default:
				driverType = "org.postgresql.Driver";
				urlType = "jdbc:postgresql://";
				break;
		}
	}
	
	//
	public Connection connect(String[] params) {
		//String[] params = 
		//url = url del servidor
		//port = puerto de conexion
		//schema = schema sobre el cuál se trabajará
		//user = usuario para acceder a la bd
		//pass = contraseña para acceder a la bd
		//...
		try {
			Class.forName(driverType);
			switch (dbType) {
				case MYSQL:
					connection = DriverManager.getConnection(urlType+""+params[0]+":"+params[1]+"/"+params[2], params[3], params[4]);
					break;
				case SQLSERVER:
					connection = DriverManager.getConnection(urlType);
					break;
				default:
					connection = DriverManager.getConnection(urlType+""+params[0]+":"+params[1]+"/"+params[2], params[3], params[4]);
					break;
			}
		} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al conectar con la base de datos: "+e);
		}
		return connection;
	}
	
	public void closeConnection() throws SQLException {
		connection.close();
	}
}
