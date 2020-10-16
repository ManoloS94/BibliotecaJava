package utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

import java.util.Properties;

import javax.servlet.ServletContext;

public class PropertiesUtility {
	
	public void setProperties(String file, String[] values) {
			
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream(file);
			
			// añade los valores y las claves al archivo
			for(int c = 0 ; c < values.length ; c+=2) {
				prop.setProperty(values[c], values[c+1]);
			}
			
			// Guarda el archivo properties en el directorio raiz
			prop.store(output, null);
		} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al crear archivo properties: "+e);
		}
	}
	
	
	public Properties getProperties(String file) {
		Properties prop = new Properties();
		try {
			InputStream input =  new FileInputStream(file);
	
			prop.load(input);
			input.close();
		} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al leer archivo properties: "+e);
		}
		
		return prop;	
	}

	public Properties getProperties(String file, ServletContext sContext) {
		Properties prop = new Properties();

		try {
			//Carga el archivo en el flujo de entrada
			InputStream input = sContext.getResourceAsStream(file);
			prop.load(input);
			input.close();
		} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al leer archivo properties: "+e);
		}
		
		return prop;	
	}
	
	public String getProperty(String file, String name) {
		String resultado = null;
		Properties prop = new Properties();
		try {
			InputStream input =  new FileInputStream(file);
	
			prop.load(input);
			
			//Por ultimo devuelve solo la propiedad solicitada
			resultado = prop.getProperty(name);
			input.close();
		} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al leer archivo properties: "+e);
		}
		
		return resultado;	
		
	}
	
	public String getProperty(String file, String name, ServletContext sContext) {
		String resultado = null;

		try {
			InputStream input = sContext.getResourceAsStream(file);
			
			Properties prop = new Properties();
			prop.load(input);
			
			//Por ultimo devuelve solo la propiedad solicitada
			resultado = prop.getProperty(name);
			input.close();
		} catch (Exception e) {
			new LoggerUtility().getLogger().severe("Error al leer archivo properties: "+e);
		}
		
		return resultado;	
	}
}
