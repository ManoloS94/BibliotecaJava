package utilities;

import java.text.SimpleDateFormat;

public class CheckUtilities {
    
    /*
        El correo electronico se compone de dos partes [parte local]@[parte dominio]
        para validar una direccion tenemos que verificar ambas partes.
        comprobarCorreo divide una direccion de email en 2, y las comprueba por separado
        en 2 metodos distintos (uno para la local y otra para el dominio).
        Ya que los caracteres permitidos para cada parte difieren.
    */
    public boolean checkMail(String eMail){
        boolean valido = false;
        int pos = eMail.indexOf("@");
        if(checkMailAddress(eMail.substring(0, pos).toCharArray())){
            if(checkMailDomain(eMail.substring(pos+1).toCharArray())){
                valido = true;
            }else
                valido = false;
        }else
            valido = false;
        return valido;
    }
    
    private boolean checkMailAddress(char[] mailA){//comprueba si un servidor de correo tiene caracteres permitidos
        char[] allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&'*+-/=?^_`{|}~.".toCharArray();
        /*
            Caracteres permitidos LOCAL:
            *ABCDEFGHIJKLMNOPQRSTUVWXYZ
            *abcdefghijklmnopqrstuvwxyz
            *0123456789
            *!#$%&'*+-/=?^_`{|}~
            *El punto "."
        */
        int i = 0;
        for(int c = 0 ; c < mailA.length ; c++){
            if(!find(mailA[c], allowedCharacters))
                i++;
        }
        if(i == 0)
            return true;
        else
            return false;
    }
    
    private boolean checkMailDomain(char[] mailD){//comprueba si un servidor de correo tiene caracteres permitidos
        char[] allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.".toCharArray();
        /*
            Caracteres permitidos LOCAL:
            *abcdefghijklmnopqrstuvwxyz
            *0123456789
            *[]:
            *El punto "."
        */
        int i = 0;
        for(int c = 0 ; c < mailD.length ; c++){
            if(!find(mailD[c], allowedCharacters))
                i++;
        }
        if(i == 0)
            return true;
        else
            return false;
    }
    
    
    public boolean find(String t, String[] a){//comprueba si una cadena esta en un array de cadenas
        for (String c: a) {
            if (c.equals(t))
                return true;
        }
        return false;
    }
    
    public boolean find(char t, char[] a){//comprueba si un integer esta en un array de integers
        for (char c: a) {
            if (c == t)
                return true;
        }
        return false;
    }
    
    public boolean find(int t, int[] a){//comprueba si un integer esta en un array de integers
        for (int c: a) {
            if (c == t)
                return true;
        }
        return false;
    }
    
    public boolean find(float t, float[] a){//comprueba si un decimal esta en un array de decimales
        for (float c: a) {
            if (c == t)
                return true;
        }
        return false;
    }
    
    public boolean find(String t, String[][] a){
       for (String[] a1 : a) {
           for (int c = 0; c < a.length; c++) {
               if (a1[c].equals(t)) {
                   return true;
               }
           }
       }
        return false;
    }
    
    public boolean find(char t, char[][] a){
       for (char[] a1 : a) {
           for (int c = 0; c < a.length; c++) {
               if (a1[c] == t) {
                   return true;
               }
           }
       }
        return false;
    }
    
    public boolean find(int t, int[][] a){
       for (int[] a1 : a) {
           for (int c = 0; c < a.length; c++) {
               if (a1[c] == t) {
                   return true;
               }
           }
       }
        return false;
    }
    
    public boolean find(float t, float[][] a){
       for (float[] a1 : a) {
           for (int c = 0; c < a.length; c++) {
               if (a1[c] == t) {
                   return true;
               }
           }
       }
        return false;
    }
	
	//Comprueba que una string no es nula, tiene un tamaño mayor que 0 y no está en blanco
	public boolean exists (String e) {
		
		if(e != null && e.length() > 0)
			switch(e) {
				case "":
					return false;
				case " ":
					return false;
				default:
					return true;
			}
		else 
			return false;
	}

    //Genera un codigo Hexadecimal del tamaño especificado
	public String HexCodeGenerator(int size){
		String code = "";
		for(int c = 0 ; c < size ; c++) {
			code += Integer.toHexString(0 + (int)(Math.random() * ((16 - 0) + 1)));
		}
		return code;
	}
	
	//Convierte una string en una fecha 
	public java.util.Date parseDate (String format, String date) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	//Convierte un java.util.Date en un java.sql.Date 
	public java.sql.Date parseDatea (java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
}
