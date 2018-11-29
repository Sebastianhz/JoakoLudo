/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Login;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Pyther
 */
public class DAOLOGIN {
private LoggDao personDAO;

	public void setPersonDAO(LoggDao personDAO) {
		this.personDAO = personDAO;
	}

	@Transactional
	public void addPerson(Login p) {
		this.LoggDao.LoggDao(p);
	}

	@Transactional
	public void updatePerson(Login p) {
		this.LoggDao.updatePerson(p);
	}

	@Override
	@Transactional
	public List<Login> listPersons() {
		return this.LoggDao.listPersons();
	}

	@Override
	@Transactional
	public Person getPersonById(int id) {
		return this.personDAO.getPersonById(id);
	}

	@Override
	@Transactional
	public void removePerson(int id) {
		this.personDAO.removePerson(id);
	}

}
