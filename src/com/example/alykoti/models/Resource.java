package com.example.alykoti.models;

import com.example.alykoti.services.DatabaseService;
import com.mysql.fabric.xmlrpc.base.Data;
import com.sun.istack.internal.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Yläluokka kaikille tietokantaan tallennettaville objekteille.
 * Sisältää muutamia hyödyllisiä apuvälineitä perus CRUD operaatioihin.
 * Ei mikään täydellinen querybuilderkirjasto, mutta toimii tässä projektissa.
 * HUOM! Perivän luokan on täytettävä myös seuraavat vaatimukset:
 *  - Parametriton konstruktori (uusia instansseja luodaan Constructor.newInstance()-kutsulla)
 *  - Joukko luokkamuuttujia joiden näkyvyys on vähintään `protected` ja joissa on annotaatio Resource.Column
 *  - Integer id on kaikille Resource-objekteille pakollinen tieto eikä sitä tule erikseen määrittää annotaatiolla
 */
public abstract class Resource<T extends Resource> implements IResource<T> {

	private final String tableName;
	private Class<T> resourceType;
	public Resource(@NotNull Class<T> resourceType, @NotNull String tableName){
		this.resourceType = resourceType;
		this.tableName = tableName;
	}

	@Target(value = ElementType.FIELD)
	@Retention(value = RetentionPolicy.RUNTIME)
	/**
	 * Annotaatio, jolla merkataan tietyn luokkamuuttujan olevan tietokannasta löytyvä sarake.
	 */
	public @interface Column {
		/**
		 * @return java.sql.Types-tyyppi joka vastaa sarakkeen tyyppiä tietokannassa
		 */
		int sqlType() default Types.VARCHAR;

		/**
		 * @return Vertailuoperaatio jota käytetään vertaillessa tämän sarakkeen
		 * arvoja toisiinsa queryssa.
		 */
		String compareWith() default "=";
	}


	/**
	 * Täyttää tämän Resource-olion tietokannasta id:n perusteella haettavalla datalla.
	 * AE: this.getId() != null
	 * @throws SQLException
	 */
	public void pull() throws SQLException {

		assert getId() != null : "Cannot execute get without id! Use query instead.";

		List<Field> fields = getColumnFields();
		Connection conn = null;
		try {
			conn = DatabaseService.getInstance().getConnection();
			String sql = "SELECT " + getColumnString(fields, true) + " FROM " + tableName +
					" WHERE id = ? LIMIT 1";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, getId());
			ResultSet results = statement.executeQuery();
			if(results.first()) {
				try {
					for (Field f : fields) {
						f.set(this, results.getObject(f.getName(), f.getType()));
					}
					setSynced(true);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			logQuery(statement);
		} finally {
			if(conn != null){
				try{
					conn.close();
				} catch (Exception ignored){}
			}
		}
	}

	public List<T> query() throws SQLException {
		return query(null);
	}

	/**
	 * Hakee tietokannasta listan tämän Resourcen kaltaisia objekteja.
	 * Jokainen tämän objektin Column-annotaatiolla varustettu luokkamuuttuja toimii AND-ehtona querylle.
	 * Esimerkiksi jos tämän olion muuttuja "name" olisi "Jaska", haettaisiin tietokannasta kaikki Jaska-nimiset.
	 * @param ids Query voidaan tehdä myös id:n perusteella natamalla tähän lista toivotuista id:stä. Tällöin hakua rajataan
	 *            sekä id listalla että muilla olion arvoilla.
	 * @return
	 * @throws SQLException
	 */
	public List<T> query(List<Integer> ids) throws SQLException {
		List<Field> fields = getColumnFields();
		int fieldsSize = fields.size();
		Object[] queryValues = new Object[fieldsSize];
		int[] queryTypes = new int[fieldsSize];
		StringJoiner placeholders = new StringJoiner(" AND ");
		int index = 0;
		try {
			for (Field f : fields) {
				Object value = f.get(this);
				if(value != null){
					Column annotation = f.getAnnotation(Column.class);
					placeholders.add(f.getName() + annotation.compareWith() + " ?");
					queryValues[index] = value;
					queryTypes[index] = annotation.sqlType();
					index++;
				}
			}
		} catch (IllegalAccessException e){
			e.printStackTrace();
		}

		boolean hasParams = index > 0;//Kysely vaatii WHERE-lauseen jos yllä lisättiin parametreja

		if (ids != null) {
			hasParams = true;
			//Luodaan idstä lause "id IN (?,?,?)"
			StringJoiner idPlaceholders = new StringJoiner(",");
			for(Integer id : ids) idPlaceholders.add("?");
			placeholders.add("id IN (" + idPlaceholders.toString() + ")");
		}

		//Nyt kaikki tarvittavat ?-parametrit on luotu PreparedStatementia varten.
		//Muodostetaan suoritettava sql
		String sql = "SELECT " + getColumnString(fields, true) + " FROM " + tableName;
		if(hasParams) {
			sql += " WHERE " + placeholders.toString() + ";";
		} else sql += ";";

		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(sql)
		){
			if(hasParams) {
				for (int i = 0; i < index; i++) {
					Object value = queryValues[i];
					if (value == null) {
						break;
					} else {
						statement.setObject(i + 1, value, queryTypes[i]);
					}
				}
				if(ids != null){
					for(int i=0, n=ids.size(); i<n; i++){
						//Huom! Tässä pitää huomioida että yllä asetetaan index-määrä parametreja
						//ja näin ollen id parametrien kohdan laskenta pitää aloittaa kohdasta index
						statement.setInt(index + i + 1, ids.get(i));
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
						f.set(obj, results.getObject(f.getName(), f.getType()));
					}
					obj.setSynced(true);
					queryResults.add(obj);
				}
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e){
				//Devausvirheitä, eipä näihin kummemmin tarvitse UI:ssa asti ottaa kantaa
				e.printStackTrace();
			}
			logQuery(statement);
			return queryResults;
		}
	}

	/**
	 * Päivittää tietokannan tietueen vastaamaan tämän olion tilaa.
	 * AE: Tietokannassa on oltava samalla id:llä varustettu tietue.
	 * @throws SQLException
	 */
	public void update() throws SQLException {
		assert getId() != null : "Can't execute update without id! Use create() instead.";
		save(false, false);
		setSynced(true);
	}

	/**
	 * Päivittää tietokannan tietueen vastaamaan tämän olion tilaa, jättäen kuitenkin
	 * huomiotta arvot jotka ovat tässä oliosssa `null`.
	 * @throws SQLException
	 */
	public void execute() throws SQLException {
		save(false, true);
	}

	/**
	 * Luo tietokantaan uuden tietueen, jonka kentät vastaavat tämän olion tilaa.
	 * @throws SQLException
	 */
	public void create() throws SQLException {
		save(true, false);
		setSynced(true);
	}

	/**
	 * Yhteinen tallennusmetodi sekä create- että update-metodeille. Erottelu näiden kahden
	 * toiminnallisuuden välillä tehdään newItem-parametrilla.
	 * @param newItem Onko kyseessä uuden tietueen luonti? Jos false, tulee tietokannasta löytyä olion id:llä tietue.
	 * @throws SQLException
	 */
	private void save(boolean newItem, boolean skipNulls) throws SQLException {

		Connection conn = null;
		try {
			conn = DatabaseService.getInstance().getConnection();
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
				int totalParamLength = 0;
				StringJoiner sets = new StringJoiner(",");
				if(skipNulls){
					for(int i=0, n=fields.size(); i<n; i++){
						Object value = null;
						Field f = fields.get(i);
						try {
							value = f.get(this);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						if(value != null){
							sets.add(f.getName() + " = ?");
							totalParamLength++;
						}
					}
				} else {
					totalParamLength = fields.size();
					for (Field f : fields) {
						sets.add(f.getName() + " = ?");
					}
				}
				sql = "UPDATE " + tableName + " SET " + sets.toString() + " WHERE id = ?;";
				statement = conn.prepareStatement(sql);
				statement.setInt(totalParamLength+1, getId());
			}

			try {
				int index = 1;
				for (Field f : fields) {
					Object value = f.get(this);
					if(value == null){
						if(!skipNulls) {
							int type = f.getAnnotation(Column.class).sqlType();
							statement.setNull(index, type);
							index++;
						}
					} else {
						int type = f.getAnnotation(Column.class).sqlType();
						statement.setObject(index, value, type);
						index++;
					}
				}
			} catch(IllegalAccessException e){
				e.printStackTrace();
			}

			logQuery(statement);

			//Statement valmis, suoritetaan
			statement.execute();

			if(newItem) {//Jos luotiin uusi rivi, haetaan generoitu id
				ResultSet results = statement.getGeneratedKeys();
				if (results.first()) {
					this.setId(results.getInt(1));
				}
			}
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (Exception ignored){}
			}
		}
	}

	/**
	 * Hakee kaikki tämän olion luokkamuuttujat joissa on annotaatio Resource.Column
	 * @return
	 */
	private List<Field> getColumnFields(){
		List<Field> result = new ArrayList<>();

		for(Field field : resourceType.getDeclaredFields()){
			if(field.isAnnotationPresent(Column.class)) {
				result.add(field);
			}
		}
		return result;
	}

	/**
	 * Palauttaa ","-merkillä erotellun listan kentistä joissa on Column-annotaatio.
	 * Listaan lisätään myös "id" jos `incluldeId` on true.
	 * @param fields
	 * @param includeId
	 * @return
	 */
	private String getColumnString(List<Field> fields, boolean includeId){
		StringJoiner result = new StringJoiner(",");
		for(Field f : fields){
			result.add(f.getName());
		}
		if(includeId) result.add("id");
		return result.toString();
	}

	private void logQuery(PreparedStatement statement){
		//System.out.println("Ran query:\n" + statement.toString());
	}

	private boolean synced = false;
	public boolean isSynced(){
		return synced;
	}
	protected void setSynced(boolean value){
		this.synced = value;
	}

	public abstract Integer getId();
	public abstract void setId(Integer id);

	@Override
	public String toString(){
		List<Field> fields = getColumnFields();
		StringJoiner result = new StringJoiner(", ");
		for(Field f : fields){
			Object val = null;
			try {
				val = f.get(this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			result.add(f.getName() + ": " + (val == null ? "null" : val.toString()));
		}
		return "{ id: " + getId() + ", " + result.toString() + " }";
	}

	/**
	 * Kaksi Resource-objektia ovat samanlaiset jos niiden taulu ja id ovat samat
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o){
		if(o != null && o instanceof Resource){
			Resource r = (Resource) o;
			Integer otherId = r.getId();
			Integer myId = getId();
			return (otherId == null ? myId == null : otherId.equals(myId))
					&& r.tableName.equals(tableName);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = tableName.hashCode();
		Integer id = getId();
		result = 31 * result + (id != null ? id.hashCode() : 0);
		return result;
	}


}
