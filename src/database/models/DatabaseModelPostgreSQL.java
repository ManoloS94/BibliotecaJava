package database.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import utilities.LoggerUtility;

public class DatabaseModelPostgreSQL {
	public Connection connection;

	public DatabaseModelPostgreSQL (Connection connection) {
		this.connection = connection;
	}
	@SuppressWarnings("unchecked")
	public JSONObject[] search (String table, String[] fields, String[] condFields, String[] valueNames, String[]types, Object[] condValues, int offset, int limit) { 
		ArrayList<JSONObject> jsonA = new ArrayList<JSONObject>();
			try {
				//Primero comprobamos, que todos los objetos recividos no sean nulos
				if (table != null && table.trim().length() > 0 && fields != null && condFields != null && types != null && condValues != null && valueNames != null) {
					//Despues comprovamos que el tamaño de todos los arrays coinciden
					if (fields.length > 0 && condFields.length > 0 && types.length > 0 && condValues.length > 0 && valueNames.length > 0) {
						if (condFields.length == condValues.length && valueNames.length+condFields.length == types.length && fields.length == valueNames.length) {
							String fieldS = "";
							String conditionS = "";
							String from = "";
							String to = "";
							for (int i = 0; i < fields.length; i++) {
								if (i == 0) {
									//Si solo tiene un elemento, este no necesita coma
									fieldS += fields[i];
								}else {
									//Los demas elementos se separan por comas
									fieldS += ", "+fields[i];
								}
							}
							
							for (int i = 0; i < condFields.length; i++) {
								if (i == 0) {
									switch (types[i]) {
										case "s":
										case "d":
										case "8a":
											conditionS += condFields[i]+" ~* ?";
											break;
										case "!s":
										case "!d":
										case "!8a":
											conditionS += condFields[i]+" !~* ?";
											break;
										case "!":
											conditionS += condFields[i]+" != ?";
											break;
										default:
											conditionS += condFields[i]+" = ?";
											break;
									}
								} else {
									switch (types[i]) {
									case "s":
									case "d":
									case "8a":
										conditionS += " AND "+condFields[i]+" ~ ?";
										break;
									case "!s":
									case "!d":
									case "!8a":
										conditionS += " AND "+condFields[i]+" !~ ?";
										break;
									case "!":
										conditionS += " AND "+condFields[i]+" != ?";
										break;
									default:
										conditionS +=" AND "+condFields[i]+" = ?";
										break;
									}
								}
							}
							
							//posicion de la consulta desde donde empezar� a devolver resultados 
							if (offset > -1) {
								from = " OFFSET "+offset;
							}
		
							//En caso de que sea quiera limitar la consulta a un numero de resultados
							if (limit > -1) {
								to = " LIMIT "+limit;
							}
							
							//Incluimos las strings de campos y variables de valores en la consulta precompilada
							PreparedStatement ps = connection.prepareStatement("SELECT "+fieldS+" FROM "+table+" WHERE "+conditionS+" "+from+" "+to+";");
							int c;
							for (int i = 0; i < condValues.length; i++) {
								c = i+1;
								//Por cada elemento del array, se añade a la consulta precompilada según su tipo
								switch (types[i]) {
									case "i":
										ps.setInt(c, (int) condValues[i]);
										break;
									case "n":
										ps.setFloat(c, (float) condValues[i]);
										break;
									case "8a":
										ps.setBytes(c, (byte[]) condValues[i]);
										break;
									case "b":
										ps.setBoolean(c, (condValues[i].equals("true")) ? true: false);
										break;
									case "d":
										ps.setDate(c, (Date) condValues[i]);
										break;
									default:
										ps.setString(c, (String) condValues[i]);
										break;
								}//Fin condicional switch
							}//Fin bucle for
							ps.executeQuery();//Se ejecuta la consulta
							
							ResultSet rs = ps.executeQuery();
							
							while (rs.next()) {
								JSONObject json = new JSONObject();
								for (int t = 0 ; t < types.length-condFields.length ; t++) {
									switch (types[t+condFields.length]) {
										case "i":
											json.put(valueNames[t], rs.getInt(fields[t]));
											break;
										case "n":
											json.put(valueNames[t], rs.getFloat(fields[t]));
											break;
										case "8a":
											json.put(valueNames[t], rs.getBytes(fields[t]));
											break;
										case "b":
											json.put(valueNames[t], rs.getBoolean(fields[t]));
											break;
										case "d":
											json.put(valueNames[t], ""+rs.getDate(fields[t]));
											break;
										default:
											json.put(valueNames[t], rs.getString(fields[t]));
											break;
									}//Fin condicional switch
								}
								jsonA.add(json);
							}

							PreparedStatement pStmnt = connection.prepareStatement("SELECT count("+fields[0]+") AS maxPags FROM "+table+" WHERE "+conditionS+";");
							for (int i = 0; i < condValues.length; i++) {
								c = i+1;
								//Por cada elemento del array, se añade a la consulta precompilada según su tipo
								switch (types[i]) {
									case "i":
										pStmnt.setInt(c, (int) condValues[i]);
										break;
									case "n":
										pStmnt.setFloat(c, (float) condValues[i]);
										break;
									case "8a":
										pStmnt.setBytes(c, (byte[]) condValues[i]);
										break;
									case "b":
										pStmnt.setBoolean(c, (condValues[i].equals("true")) ? true: false);
										break;
									case "d":
										pStmnt.setDate(c, (Date) condValues[i]);
										break;
									default:
										pStmnt.setString(c, (String) condValues[i]);
										break;
								}//Fin condicional switch
							}//Fin bucle for
							ResultSet rStmnt = pStmnt.executeQuery();
							
							rStmnt.next();
							JSONObject jsonPags = new JSONObject();
							jsonPags.put("maxPags", rStmnt.getInt("maxPags"));
							jsonA.add(jsonPags);
							
						}
					}//Fin condicional if
				}//Fin condicional if
			} catch (Exception e) {
				jsonA = null;
				new LoggerUtility().getLogger().severe("Error al conectar con la base de datos: "+e);
			}
			return jsonA.toArray(new JSONObject[jsonA.size()]);
		}//Fin metodo search
	
	//El metodo search, recibe:
	//el nombre de la tabla afectada
	//un String[] fields que contiene todos los campos afectados
	//un String[] types que indica de que tipo son los valores con los que se trabaja
	//un Object[] values que contiene los distintos valores
	//inserta una fila una determinada tabla
	@SuppressWarnings("unchecked")
	public String searchS (String table, String[] fields, String[] condFields, String[] valueNames, String[]types, Object[] condValues, int offset, int limit) {
			String respuesta = null;
			try {
				//Primero comprobamos, que todos los objetos recividos no sean nulos
				if (table != null && table.trim().length() > 0 && fields != null && condFields != null && types != null && condValues != null && valueNames != null) {
					//Despues comprovamos que el tamaño de todos los arrays coinciden
					if (fields.length > 0 && condFields.length > 0 && types.length > 0 && condValues.length > 0 && valueNames.length > 0) {
						if (condFields.length == condValues.length && valueNames.length+condFields.length == types.length && fields.length == valueNames.length) {
							String fieldS = "";
							String conditionS = "";
							String from = "";
							String to = "";
							for (int i = 0; i < fields.length; i++) {
								if (i == 0) {
									//Si solo tiene un elemento, este no necesita coma
									fieldS += fields[i];
								}else {
									//Los demas elementos se separan por comas
									fieldS += ", "+fields[i];
								}
							}
							
							for (int i = 0; i < condFields.length; i++) {
								if (i == 0) {
									switch (types[i]) {
										case "s":
										case "d":
										case "8a":
											conditionS += condFields[i]+" ~ ?";
											break;
										case "!ns":
										case "!d":
										case "!8a":
											conditionS += condFields[i]+" !~ ?";
											break;
										case "!":
											conditionS += condFields[i]+" != ?";
											break;
										default:
											conditionS += condFields[i]+" = ?";
											break;
									}
								} else {
									switch (types[i]) {
									case "s":
									case "d":
									case "8a":
										conditionS += " AND "+condFields[i]+" ~ ?";
										break;
									case "!s":
									case "!d":
									case "!8a":
										conditionS += " AND "+condFields[i]+" !~ ?";
										break;
									case "!":
										conditionS += " AND "+condFields[i]+" != ?";
										break;
									default:
										conditionS +=" AND "+condFields[i]+" = ?";
										break;
									}
								}
							}
							
							//posicion de la consulta desde donde empezar� a devolver resultados 
							if (offset > -1) {
								from = " OFFSET "+offset;
							}
		
							//En caso de que sea quiera limitar la consulta a un numero de resultados
							if (limit > -1) {
								to = " LIMIT "+limit;
							}
							
							//Incluimos las strings de campos y variables de valores en la consulta precompilada
							PreparedStatement ps = connection.prepareStatement("SELECT "+fieldS+" FROM "+table+" WHERE "+conditionS+" "+from+" "+to+";");
							int c;
							for (int i = 0; i < condValues.length; i++) {
								c = i+1;
								//Por cada elemento del array, se añade a la consulta precompilada según su tipo
								switch (types[i]) {
									case "i":
										ps.setInt(c, (int) condValues[i]);
										break;
									case "n":
										ps.setFloat(c, (float) condValues[i]);
										break;
									case "8a":
										ps.setBytes(c, (byte[]) condValues[i]);
										break;
									case "b":
										ps.setBoolean(c, (condValues[i].equals("true")) ? true: false);
										break;
									case "d":
										ps.setDate(c, (Date) condValues[i]);
										break;
									default:
										ps.setString(c, (String) condValues[i]);
										break;
								}//Fin condicional switch
							}//Fin bucle for
							ps.executeQuery();//Se ejecuta la consulta
							
							ResultSet rs = ps.executeQuery();
							respuesta = "";
							
							while (rs.next()) {
								JSONObject json = new JSONObject();
								for (int t = 0 ; t < types.length-condFields.length ; t++) {
									switch (types[t+condFields.length]) {
										case "i":
											json.put(valueNames[t], rs.getInt(fields[t]));
											break;
										case "n":
											json.put(valueNames[t], rs.getFloat(fields[t]));
											break;
										case "8a":
											json.put(valueNames[t], rs.getBytes(fields[t]));
											break;
										case "b":
											json.put(valueNames[t], rs.getBoolean(fields[t]));
											break;
										case "d":
											json.put(valueNames[t], ""+rs.getDate(fields[t]));
											break;
										default:
											json.put(valueNames[t], rs.getString(fields[t]));
											break;
									}//Fin condicional switch
								}
								if (respuesta.length() > 0) {
									respuesta += "/"+json.toJSONString();
								} else {
									respuesta += json.toJSONString();
								}
							}
						}
					}//Fin condicional if
				}//Fin condicional if
			} catch (Exception e) {
				respuesta = null;
				new LoggerUtility().getLogger().severe("Error al conectar con la base de datos: "+e);
			}
			return respuesta;
		}//Fin metodo search
	
	//El metodo insertar, recibe:
	//el nombre de la tabla afectada
	//un String[] fields que contiene todos los campos afectados
	//un String[] types que indica de que tipo son los valores con los que se trabaja
	//un Object[] values que contiene los distintos valores
	//inserta una fila una determinada tabla
	public void insert (String table, String[] fields, String[]types, Object[] values) throws SQLException {
		//Primero comprobamos, que todos los objetos recividos no sean nulos
		if (table != null && table.length() > 0 && fields != null && values != null && types != null) {
			//Despues comprobamos que el tamaño de todos los arrays coinciden
			if (fields.length > 0 && types.length > 0 && values.length > 0  && fields.length == types.length  && fields.length == values.length) {
				String fieldS = "";
				String valueS = "";
				for (int i = 0; i < fields.length; i++) {
					if (i < fields.length-1) {
						fieldS += ""+fields[i]+", ";//Cada elemento del array, se añade a una string para despues a la consulta
						valueS += "?, ";//Por cada elemento, se añade una variable, la cuál se añade a una string para despues a la consulta
					}else {
						//El ultimo elemnto no puede acabar con coma
						fieldS += ""+fields[i];
						valueS += "?";
					}
				}
				//Incluimos las strings de campos y variables de valores en la consulta precompilada
				PreparedStatement ps = connection.prepareStatement("INSERT INTO "+table+" ("+fieldS+") VALUES ("+valueS+");");
				int c;
				for (int i = 0; i < fields.length; i++) {
					c = i+1;
					//Por cada elemento del array, se añade a la consulta precompilada según su tipo
					switch (types[i]) {
					case "i":
						ps.setInt(c, (int) values[i]);
						break;
					case "n":
						ps.setFloat(c, (float) values[i]);
						break;
					case "b":
						ps.setBytes(c, (byte[]) values[i]);
						break;
					case "d":
						ps.setDate(c, (Date) values[i]);
						break;
					default:
						ps.setString(c, (String) values[i]);
						break;
					}//Fin condicional switch
				}//Fin bucle for
				
				ps.executeUpdate();//Se ejecuta la consulta
			}//Fin condicional if
		}//Fin condicional if
	}//Fin metodo insert
	
	//El metodo update, recibe:
	//el nombre de la tabla afectada
	//un String[] fields que contiene todos los campos afectados
	//un Object[] values que contiene los distintos valores afectados
	//un String[] types que indica de que tipo son los valores con los que se trabaja
	//un Object[] condFields que contiene todos los campos para filtrar
	//un Object[] condValues que contiene todos los campos para filtrar
	//modifica una entradas fila en una determinada tabla
	public void update (String table, String[] fields, String[] condFields, String[]types, Object[] values, Object[] condValues) throws SQLException {
		//Primero comprobamos, que todos los objetos recividos no sean nulos
		if (table != null && table.trim().length() > 0 && fields != null && condFields != null && types != null && values != null && condValues != null) {
			//Despues comprovamos que el tamaño de todos los arrays coinciden
			if (fields.length > 0 && condFields.length > 0 && types.length > 0 && values.length > 0 && condValues.length > 0) {
				if (fields.length == values.length && condFields.length == condValues.length && fields.length+condFields.length == types.length) {
					String fieldS = "";
					String conditionS = "";
					for (int i = 0; i < fields.length; i++) {
						if (i == 0) {
							//Si solo tiene un elemento, este no necesita coma
							fieldS += fields[i]+" = ?";
						}else {
							//Los demas elementos se separan por comas
							fieldS += ", "+fields[i]+" = ?";
						}
					}
					for (int i = 0+fields.length; i < types.length; i++) {
						if (i == 0+fields.length) {
							//Si solo tiene un elemento, este no necesita coma
							if (types[i].equals("s") || types[i].equals("b")) {
								conditionS += condFields[i-fields.length]+" ~ ?";}
							else {
								conditionS += condFields[i-fields.length]+" = ?";
								}
						}else {
							//Los demas elementos se separan por comas
							if (types[i].equals("s") || types[i].equals("b")) {
								conditionS += " AND "+condFields[i-fields.length]+" ~ ?";}
							else {
								conditionS += " AND "+condFields[i-fields.length]+" = ?";
							}
						}
					}
					//Incluimos las strings de campos y variables de valores en la consulta precompilada
					PreparedStatement ps = connection.prepareStatement("UPDATE "+table+" SET "+fieldS+" WHERE "+conditionS+";");
					int c, cV;
					for (int i = 0; i < (fields.length+condFields.length); i++) {
						c = i+1;
						cV = i-fields.length;
						//Por cada elemento en los arrays (uno con los campos a modificary otro con las condiciones), 
						//se añade a la consulta precompilada según su tipo
						switch (types[i]) {
						case "i":
							if (i < fields.length) {
								if (values[i] == null)
									ps.setNull(c, Types.INTEGER);
								else
									ps.setInt(c, (int) values[i]);
							} else {
								ps.setInt(c, (int) condValues[cV]);
							}
							break;
						case "n":
							if (i < fields.length) {
								ps.setFloat(c, (float) values[i]);
							} else {
								ps.setFloat(c, (float) condValues[cV]);
							}
							break;
						case "8a":
							if (i < fields.length) {
								ps.setBytes(c, (byte[]) values[i]);
							} else {
								ps.setBytes(c, (byte[]) condValues[cV]);
							}
							break;
						case "b":
							if (i < fields.length) {
								ps.setBoolean(c, (boolean) values[i]);
							} else {
								ps.setBoolean(c, (boolean) condValues[cV]);
							}
							break;
						case "d":
							if (i < fields.length) {
								ps.setDate(c, (Date) values[i]);
							} else {
								ps.setDate(c, (Date) condValues[cV]);
							}
							break;
						default:
							if (i < fields.length) {
								ps.setString(c, (String) values[i]);
							} else {
								ps.setString(c, (String) condValues[cV]);
							}
							break;
						}//Fin condicional switch
					}//Fin bucle for
					ps.executeUpdate();//Se ejecuta la consulta
				}
			}//Fin condicional if
		}//Fin condicional if
	}//Fin metodo update
	
	//El metodo delete, recibe:
	//el nombre de la tabla afectada
	//un String[] fields que contiene todos los campos afectados
	//un String[] types que indica de que tipo son los valores con los que se trabaja
	//un Object[] values que contiene los distintos valores
	//borra una o varias entradas de una determinada tabla
	public void delete (String table, String[] fields, String[]types, Object[] values) throws SQLException {
		//Primero comprobamos, que todos los objetos recividos no sean nulos
		if (table != null && table.length() > 0 && fields != null && values != null && types != null) {
			//Despues comprovamos que el tamaño de todos los arrays coinciden
			if (fields.length > 0 && types.length > 0 && values.length > 0  && fields.length == types.length  && fields.length == values.length) {
				String conditions = "";
				for (int i = 0; i < fields.length; i++) {
					if (1 < fields.length-1) {
						conditions += ""+fields[i]+" = ?, ";//Cada elemento del array, se añade a una string para despues a la consulta
					}else {
						//El ultimo elemnto no puede acabar con coma
						conditions += fields[i]+" = ?";
					}
				}
				//Incluimos las strings de campos y variables de valores en la consulta precompilada
				PreparedStatement ps = connection.prepareStatement("DELETE FROM "+table+" WHERE "+conditions+";");
				int c;
				for (int i = 0; i < fields.length; i++) {
					c = i+1;
					//Por cada elemento del array, se añade a la consulta precompilada según su tipo
					switch (types[i]) {
					case "i":
						ps.setInt(c, (int) values[i]);
						break;
					case "n":
						ps.setFloat(c, (float) values[i]);
						break;
					case "b":
						ps.setBytes(c, (byte[]) values[i]);
						break;
					case "d":
						ps.setDate(c, (Date) values[i]);
						break;
					default:
						ps.setString(c, (String) values[i]);
						break;
					}//Fin condicional switch
				}//Fin bucle for
				
				ps.executeUpdate();//Se ejecuta la consulta
			}//Fin condicional if
		}//Fin condicional if
	}//Fin metodo delete
}
