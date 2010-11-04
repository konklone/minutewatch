package com.extendsandroid.minutewatch.publish;

public class WhitelistedNumberItem { 

  String number; 
  String name; 
  int keyID;
  
  public String getNumber() { 
	  return number; 
  } 
  
  public String getName() { 
	  return name; 
  } 
  
  public int getKeyID() { 
	  return keyID; 
  } 
  
  public WhitelistedNumberItem(String _number) { 
	 number = _number;
	 name = "";
  } 
  
  public WhitelistedNumberItem(String _number, String _name) { 
    number = _number; 
    name = _name; 
  }
  
  public WhitelistedNumberItem(String _number, String _name, int _keyID) { 
	number = _number; 
	name = _name; 
	keyID = _keyID; 
  } 
  
  @Override 
  public String toString() { 
	return name + " " + number; 
  } 
}