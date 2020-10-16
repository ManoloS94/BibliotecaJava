package utilities;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtility {
	HttpServletRequest request;
	HttpServletResponse response;
	
	public CookieUtility(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public CookieUtility(HttpServletRequest request) {
		this.request = request;
	}
	
	public CookieUtility(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setCookie(String nombre, int duracion, String contenido) {
		Cookie cookie = new Cookie(nombre, contenido);
		cookie.setMaxAge(duracion);
		
		response.addCookie(cookie);
	}
	
	public String getCookie(String nombre) {
		String resultado = null;
		Cookie[] cookies = request.getCookies();
		
		for(Cookie c : cookies) {
			if(c.getName().equals(nombre)) {
				resultado = c.getValue();
				break;
			}
		}
		return resultado;
	}
	
	public void supCookie(String nombre) {
		Cookie[] cookies = request.getCookies();
		
		for(Cookie c : cookies) {
			if(c.getName().equals(nombre)) {
				Cookie cookie = new Cookie(c.getName(), "");
				cookie.setMaxAge(0);
				
				response.addCookie(cookie);
				break;
			}
		}
	}
	
	public void supCookies() {
		Cookie[] cookies = request.getCookies();
		
		for(Cookie c : cookies) {
			Cookie cookie = new Cookie(c.getName(), "");
			cookie.setMaxAge(0);
			
			response.addCookie(cookie);
		}
	}
}
