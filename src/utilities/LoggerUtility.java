package utilities;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtility {
	private SimpleFormatter format = new SimpleFormatter();
	private static FileHandler fH;
	private static final Logger log = Logger.getLogger("ProyectLog");
	
	public LoggerUtility () {

		try {
			fH = new FileHandler("./amsantos.log");
			log.addHandler(fH);
			fH.setFormatter(format);
		} catch (Exception e) {
			System.out.println("Error al iniciar el logger, "+e);
		}
	}

	public Logger getLogger () {
		return log;
	}

}
