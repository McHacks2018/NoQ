package ca.mcgill.mchacks2018.noq.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import ca.mcgill.mchacks2018.noq.model.User;
import ca.mcgill.mchacks2018.noq.model.Location;
import ca.mcgill.mchacks2018.noq.model.RegistrationManager;
import ca.mcgill.mchacks2018.noq.persistence.PersistenceXStream;

@Service
public class NoQService {
	private RegistrationManager rm;

	public NoQService(RegistrationManager rm) {
		this.rm = rm;
	}

	public List<User> findAllUsers() {
		return rm.getUsers();
	}

	public List<Location> findAllLocations() {
		return rm.getLocations();
	}

	public void resetDatabase() {
		rm.delete();
	}

	public User createUser(String username, String password, int age, int points, JSONObject favs) throws InvalidInputException {
		if (username == null || username.trim().length() == 0)
			throw new InvalidInputException("Username cannot be empty!");
		if (password == null || password.trim().length() == 0)
			throw new InvalidInputException("Password cannot be empty!");
		if (age < 0)
			throw new InvalidInputException("Age must be positive!");

		User u = new User(username, password, age, points, favs);
		for (User user : rm.getUsers()) {
			if (user.getUsername().equals(u.getUsername())) {
				throw new InvalidInputException("Username is already taken!");
			}
		}

		rm.addUser(u);
		PersistenceXStream.sql.insertUser(u.getId(), u.getUsername(), u.getPassword(), u.getAge(), u.getPoints(), u.getFavs().toString());
		return u;
	}

	public Location createLocation(String id, String name, String strtNum, String address, int qTime, JSONObject checkTimes) throws InvalidInputException {
		if (id == null || name.trim().length() == 0)
			throw new InvalidInputException("Location name cannot be empty!");
		if (name == null || name.trim().length() == 0)
			throw new InvalidInputException("Location name cannot be empty!");
		if (strtNum == null || strtNum.trim().length() == 0)
			throw new InvalidInputException("Location street number cannot be empty!");
		if (address == null || address.trim().length() == 0)
			throw new InvalidInputException("Location address cannot be empty!");
		if (qTime < -1)
			throw new InvalidInputException("Location queue times cannot be negative!");
		if (checkTimes == null)
			throw new InvalidInputException("Location check times cannot be null!");

		Location l = new Location(id, name, strtNum, address, qTime, checkTimes);
		for (Location location : rm.getLocations()) {
			if (location.getId().equals(l.getId()) && location.getName().equals(l.getName())
				&& location.getStrtNum().equals(l.getStrtNum()) && location.getAddress().equals(l.getAddress())
				&& location.getQTime() == l.getQTime() && location.getCheckTimes().toString().equals(l.getCheckTimes().toString())) {
				throw new InvalidInputException("Cannot create identical locations!");
			}
		}

		rm.addLocation(l);
		PersistenceXStream.sql.insertLocation(l.getId(), l.getName(), l.getStrtNum(), l.getAddress(), l.getQTime(), l.getCheckTimes().toString());
		return l;
	}

	public User getUserByName(String username) throws InvalidInputException {
		for (User user : rm.getUsers()) {
			if (user.getUsername().equals(username))
				return user;
		}
		throw new InvalidInputException("User does not exist!");
	}

	public Location getLocationById(String id) throws InvalidInputException {
		for (Location location : rm.getLocations()) {
			if (location.getName().equals(id))
				return location;
		}
		throw new InvalidInputException("Location does not exist!");
	}
}
