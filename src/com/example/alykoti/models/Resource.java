package com.example.alykoti.models;

import com.example.alykoti.services.DatabaseService;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.istack.internal.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Yläluokka kaikille tietokantaan tallennettaville objekteille.
 * Sisältää muutamia hyödyllisiä apuvälineitä perus CRUD operaatioihin.
 * Ei mikään täydellinen querybuilderkirjasto, mutta toimii tässä projektissa.
 */
public abstract class Resource<T extends Resource> {

	private final String tableName;
	private Class<T> resourceType;

	public Resource(Class<T> resourceType, @NotNull String tableName){
		this.resourceType = resourceType;
		this.tableName = tableName;
	}

	public void pull() throws SQLException {
		assert getId() != null : "Cannot execute get without id! Use query instead.";

		List<Field> fields = getColumnFields();
		DatabaseService.getInstance().useConnection(conn -> {
			String sql = "SELECT " + getColumnString(fields, true) + " FROM " + tableName +
					" WHERE id = ?";
			System.out.println("Queryyyyy: " + sql);
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, getId());
			ResultSet results = statement.executeQuery();
			if(results.first()) {
				try {
					for (Field f : fields) {
						f.set(this, results.getObject(f.getName(), f.getType()));
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return null;
		});
	}

	public List<T> query() throws SQLException {
		List<Field> fields = getColumnFields();
		int fieldsSize = fields.size();
		Object[] queryValues = new Object[fieldsSize];
		int[] queryTypes = new int[fieldsSize];
		StringJoiner placeholders = new StringJoiner(" AND ");

		try {
			for (Field f : fields) {
				Object value = f.get(this);
				if(value != null){
					placeholders.add(f.getName() + " = ?");
					int index = queryValues.length;
					queryValues[index] = value;
					queryTypes[index] = f.getAnnotation(Column.class).sqlType();
				}
			}
		} catch (IllegalAccessException e){
			e.printStackTrace();
		}
		boolean hasParams = placeholders.length() > 0;
		Connection conn = null;
		try {
			conn = DatabaseService.getInstance().getConnection();
			String sql = "SELECT " + getColumnString(fields, true) + " FROM " + tableName;
			if(hasParams) {
				sql += " WHERE " + placeholders.toString() + ";";
			} else sql += ";";
			System.out.println("query " + sql);
			PreparedStatement statement = conn
					.prepareStatement(sql);
			if(hasParams) {
				for (int i = 0, n = queryValues.length; i < n; i++) {
					Object value = queryValues[i];
					if (value == null) {
						break;
					} else {
						statement.setObject(i + 1, value, queryTypes[i]);
					}
				}
			}

			ResultSet results = statement.executeQuery();
			List<T> queryResults = new ArrayList<>();

			try {
				Constructor<T> constr = resourceType.getConstructor();
				while (results.next()) {
					T obj = constr.newInstance();
					obj.setId(results.getInt("id"));
					for(Field f : fields) {
						//System.out.println("Get field " + results.getString(f.getName()));
						f.set(obj, results.getObject(f.getName(), f.getType()));
					}
					queryResults.add(obj);
				}
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e){
				e.printStackTrace();
			}

			return queryResults;

		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (Exception ignored){}
			}
		}

	}

	public void update() throws SQLException {
		save(false);
	}

	public void create() throws SQLException {
		save(true);
	}

	private void save(boolean newItem) throws SQLException {
		DatabaseService.getInstance().useConnection(conn -> {
			List<Field> fields = getColumnFields();

			//Generoidaan tallennuksessa käytettävä sql
			String sql;
			PreparedStatement statement;

			if(newItem) {
				//Asetetaan PreparedStatementiin tarvittava määrä ?-merkkejä
				StringJoiner placeholders = new StringJoiner(",");
				for(int i=0, n=fields.size(); i<n; i++) placeholders.add("?");
				String fieldStr = getColumnString(fields, false);
				sql = "INSERT INTO " + tableName + " (" + fieldStr + ") VALUES (" + placeholders.toString() + ");";
				statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				StringJoiner sets = new StringJoiner(",");
				for(Field f : fields){
					sets.add(f.getName() + " = ?");
				}
				sql = "UPDATE " + tableName + " SET " + sets.toString() + " WHERE id = ?;";
				statement = conn.prepareStatement(sql);
			}
			System.out.println("Suoritetaan save: " + sql);

			try {
				for (int i=0, n=fields.size(); i<n; i++) {
					Field f = fields.get(i);
					Object value = f.get(this);
					int type = f.getAnnotation(Column.class).sqlType();

					if(value == null){
						statement.setNull(i+1, type);
					} else {
						statement.setObject(i+1, value, type);
					}
				}
			} catch(IllegalAccessException e){
				e.printStackTrace();
			}

			//Statement valmis, suoritetaan
			statement.execute();

			if(newItem) {
				ResultSet results = statement.getGeneratedKeys();
				if (results.first()) {
					this.setId(results.getInt(1));
				}
			}
			return null;
		});
	}

	private List<Field> getColumnFields(){
		List<Field> result = new ArrayList<>();
		for(Field field : resourceType.getDeclaredFields()){
			if(field.isAnnotationPresent(Column.class)) {
				result.add(field);
			}
		}
		return result;
	}

	private String getColumnString(List<Field> fields, boolean includeId){
		StringJoiner result = new StringJoiner(",");
		for(Field f : fields){
			result.add(f.getName());
		}
		if(includeId) result.add("id");
		return result.toString();
	}

	public abstract Integer getId();
	public abstract void setId(Integer id);

	@Target(value = ElementType.FIELD)
	@Retention(value = RetentionPolicy.RUNTIME)
	public @interface Column {
		int sqlType();
	}

}
