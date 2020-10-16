package http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpSessionController {
	HttpSession session;
	
	public HttpSessionController (HttpServletRequest request){
		session = request.getSession();
	}
	
	public HttpSessionController (HttpSession session){
		this.session = session;
	}
	
	public void addAttribute (String name, Object value) {
		session.setAttribute(name, value);
	}
	
	public Object getAtributte(String name) {
		return session.getAttribute(name);
	}

	//Guarda un array de attributos en la sesion;
	public void addAttribute(String[] names, Object[] values) {
		for(int c = 0 ; c < names.length ; c++) {
			session.setAttribute(names[c], values[c]);
		}
	}
	
	//Devuelve un grupo indefinido de attributos de la sesion;
	public Object[] getAtributte(String[] names) {
		Object[] resultados = new Object[names.length];
		for(int c = 0 ; c < names.length ; c++) {
			resultados[c] = session.getAttribute(names[c]);
		}
		return resultados;
	}

	//Elimina un attributo de la sesion;
	public void delAtributte(String name) {
		session.removeAttribute(name);
	}

	//Elimina un grupo indefinido de attributos de la sesion;
	public void delAtributte(String[] names) {
		for(int c = 0 ; c < names.length ; c++) {
			session.removeAttribute(names[c]);
		}
	}
	
	public void closeSession () {
		session.invalidate();
	}

}
