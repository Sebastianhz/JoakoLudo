/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;


import Model.Login;
import java.util.List;


public interface LoginService {

	public void addPerson(Login p);
	public void updatePerson(Login p);
	public List<Login> listLogin();
	public Login getLoginById(int id);
	public void removeLogin(int id);
	
}