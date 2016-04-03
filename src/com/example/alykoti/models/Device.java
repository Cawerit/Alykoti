package com.example.alykoti.models;

import com.example.alykoti.models.devices.DeviceStatus;
import com.example.alykoti.models.devices.DeviceType;
import com.example.alykoti.services.DatabaseService;

import java.sql.*;
import java.util.*;

/**
 * Sisältää tiedot laitteista. Toisin kuin muut tietokannasta datansa saavat luokat,
 * tämä käyttää monta taulua yhdistävää näkymää lähteenään eikä siksi voi käyttää
 * suoraan Resource-luokkaa pohjana vaan vaatii hieman lisätyötä käsitelläkseen dataa
 * eri lähteistä.
 */
public class Device implements IResource<Device> {

	private Integer id;

	private String name;
	private DeviceType type;
	private Integer room;
	private java.sql.Date updated;
	public final AbstractMap<DeviceStatus.Type, DeviceStatus> statuses = new HashMap<>();
	public final List<User> users = new ArrayList<>();

	public Device(Integer id){
		setId(id);
	}
	public Device(){
		this(null);
	}


	@Override
	public List<Device> query() throws SQLException {
		//Rakennetaan query sen mukaan mitä dataa tässä oliossa on
		StringJoiner sql = new StringJoiner(" AND ");
		if(name != null) sql.add("name = ?");
		if(type != null) sql.add("type = ?");
		if(room != null) sql.add("room = ?");
		if(updated != null) sql.add("updated > ?");

		StringJoiner userIn = new StringJoiner(",");
		for(Object o : users) userIn.add("?");

		if(userIn.length() != 0) {
			sql.add("id IN (SELECT device FROM device_users WHERE user IN (" + userIn.toString() + "))");
		}

		//Nyt StringJoiner sql on esimerkiksi "room = ? AND userId IN (?,?,?)"
		String query = "SELECT * FROM device_full_info";
		if(sql.length() != 0) query += " WHERE " + sql.toString();
		query += ";";

		try(
			Connection conn = DatabaseService.getInstance().getConnection();
			PreparedStatement statement = conn.prepareStatement(query)
		){
			int index = 0;
			//Täytetään arvot (HUOM! Tässä on ylläpidettävä sama järjestys kuin yllä)
			if(name != null) statement.setString(++index, name);
			if(type != null) statement.setString(++index, type.toString());
			if(room != null) statement.setInt(++index, room);
			if(updated != null) statement.setDate(++index, updated);
			for(User u : users) statement.setInt(++index, u.getId());

			return fromResultSet(statement.executeQuery());
		}
	}

	@Override
	public void pull() throws SQLException {
		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM device_full_info WHERE id = ? ORDER BY id, statusType, userId"
				)
		){
			statement.setInt(1, getId());
			List<Device> result = fromResultSet(statement.executeQuery());
			if(result.size() == 1){
				Device d = result.get(0);
				statuses.clear();
				statuses.putAll(d.statuses);
				users.clear();
				users.addAll(d.users);
				name = d.name;
				room = d.room;
				updated = d.updated;
			}
		}
	}

	/**
	 * Muodostaa listan device-olioita tietokannasta hetun ResultSetin pohjalta.
	 * @param res
	 * @return
	 * @throws SQLException
	 */
	private static List<Device> fromResultSet(ResultSet res) throws SQLException {
		//Titokannassa laitteet ovat näkymässä, jossa on laitteet, niiden käyttäjät ja niiden tila
		//on yhdistetty joinilla. Rivien yhdistäminen järkevästi vaatii hieman lisäkikkailua.
		List<Device> devices = new ArrayList<>();
		Integer currentId = null;
		Device d = null;
		List<Integer> userIds = new ArrayList<>();//Lista josta on nopea tarkistaa onko käyttäjä lisätty jo käyttäjälistaan
		while(res.next()){
			Integer nextId = res.getInt("id");
			if(currentId == null || !currentId.equals(nextId)){//Nämä tiedot eivät ole listoja, eli ei tarvitse käsitellä joka riville uudestaan
				d = new Device(nextId);
				userIds.clear();
				devices.add(d);
				currentId = nextId;
				d.name = res.getString("name");
				d.room = res.getInt("room");
				d.updated = res.getDate("updated");
				d.type = DeviceType.fromString(res.getString("type"));
			}
			Integer userId = res.getInt("userId");
			if(!userId.equals(0) && !userIds.contains(userId)){
				userIds.add(userId);
				User u = new User();
				u.setId(userId);
				u.setUsername(res.getString("userName"));
				u.setOnline(res.getBoolean("userOnline"));
				d.users.add(u);
			}
			DeviceStatus.Type statusType = DeviceStatus.Type.fromString(res.getString("statusType"));
			if(statusType != null && !d.statuses.containsKey(statusType)){
				String valueStr = res.getString("statusValueStr");
				Integer valueNumber = res.getInt("statusValueNumber");
				DeviceStatus status;
				if(valueStr == null){
					status = new DeviceStatus(statusType, valueNumber);
				} else {
					status = new DeviceStatus(statusType, valueStr);
				}
				d.statuses.put(statusType, status);
			}
		}
		return devices;
	}

	@Override
	public void create() throws SQLException {
		try(Connection conn = DatabaseService.getInstance().getConnection()){
			String createDeviceSql = "INSERT INTO devices (room, name, type) VALUES (?,?,?)";

			PreparedStatement createStatement = conn.prepareStatement(createDeviceSql, Statement.RETURN_GENERATED_KEYS);
			createStatement.setInt(1, room);
			createStatement.setString(2, name);
			createStatement.setString(3, type.toString());
			createStatement.executeUpdate();

			try(ResultSet idRes = createStatement.getGeneratedKeys()) {
				if (idRes.first()) {
					this.id = idRes.getInt(1);
				}
			}
			//Tietokannassa on after insert trigger joka lisää tälle oliolle lähtöstatukset
			//Haetaan statukset jms lisätty data kannasta
			this.pull();
		}
	}
	/**
	 * Vaihtaa laitteen tilaa
	 * @param stat
	 * @throws SQLException
	 */
	public void setStatus(DeviceStatus stat) throws SQLException {
		statuses.put(stat.statusType, stat);
		final String sql = "INSERT INTO device_status (device, status_type, value_str, value_number, updated) " +
				"VALUES(?, ?, ?, ?, NOW()) " +
				"ON DUPLICATE KEY UPDATE " +
				"value_str = ?," +
				"value_number = ?," +
				"updated = NOW();";
		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(sql)
		){
			statement.setInt(1, getId());
			statement.setString(2, stat.statusType.toString());
			statement.setString(3, stat.valueStr);
			statement.setInt(4, stat.valueNumber);
			statement.setString(5, stat.valueStr);
			statement.setInt(6, stat.valueNumber);
			statement.executeUpdate();
		}
	}

	/**
	 * Antaa käyttäjälle oikeuden hallita laitteen tilaa
	 * @param user
	 * @throws SQLException
	 */
	public void addUser(User user) throws SQLException {
		users.add(user);
		executeUserUpdate("INSERT INTO device_users (device, user) VALUES(?, ?);", user);
	}

	/**
	 * Poistaa annetulta käyttäjältä oikeuden hallita laitteen tilaa
	 * @param user
	 * @throws SQLException
	 */
	public void removeUser(User user) throws SQLException {
		executeUserUpdate("DELETE FROM device_users WHERE device = ? AND user = ?", user);
	}

	private void executeUserUpdate(String sql, User user) throws SQLException {
		try(
			Connection conn = DatabaseService.getInstance().getConnection();
			PreparedStatement statement = conn.prepareStatement(sql)
		){
			statement.setInt(1, getId());
			statement.setInt(2, user.getId());
			statement.executeUpdate();
		}
	}

	@Override
	public String toString(){
		return "Device { id: " + this.id +
				", type: " + this.type +
				", name: " + this.name +
				", status: " + Arrays.toString(this.statuses.values().toArray()) +
				", users: " + users.toString() +
				" }";
	}

	@Override
	public boolean equals(Object o){
		//System.out.println("no " + (oId == null ? getId() == null : oId.equals(getId())));
		if(o != null && o instanceof Device){
			Integer oId = ((Device) o).getId();
			return oId == null ? getId() == null : oId.equals(getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public void setRoom(Integer room){
		this.room = room;
	}

	public Integer getRoom(){
		return this.room;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.sql.Date getUpdated() {
		return updated;
	}

	public void setUpdated(java.sql.Date updated) {
		this.updated = updated;
	}

	public void setType(DeviceType type){
		this.type = type;
	}

	public DeviceType getType(){
		return type;
	}

}
