/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

/**
 *
 * @author Pyther
 */
import Model.Login;
import java.util.List;


public interface LoggDao {

	public void LoggDao(Login p);
	public void updateLogin(Login p);
	public List<Login> listLogin();
	public Login getLoingById(int id);
	public void removeLogin(int id);
}
