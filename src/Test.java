import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is for testing purpose.
 * Start Your examination from here.
 * @author Dipl. Inf. (FH) J. Ott
 */
public class Test {

	
	/**
	 * Records are from migano.
	 * @see //https://migano.de/testdaten.php
	 * @return
	 * @throws IOException
	 */
	static List<Person> getRandomData() throws IOException {
		List<Person> persons = new LinkedList<Person>();
		Database db = new CSVDatabase(new File("sample.csv"));
		db.open();
		Person p;
		while ((p = db.read()) != null) {
			persons.add(p);
		}
		db.close();

		return persons;
	}
	//Die Methode, um die Klassen RecordDatabase, SerializableDatabase und CSVDatabase
	//zu testen
	public static void BigTestDB(Database db) throws Exception {
		//Datenbank wird geöffnet
		db.open();
		//Datenbank wir mit zufälligen Daten gefühlt
		for (Person p : getRandomData()) {
			db.write(p);
		}
		//es wird vom Index 0 begonnen
		db.seek(0);
		Person p;
		//Solange wir nicht am ende der Datei angekommen sind, wird die Person ausgegeben
		while ((p = db.read()) != null) {
			System.out.println(p);
		}
		//Teste suche nach der bestimmten Person
		db.seek(0);
		p = db.read();
		p.setCity("Nürnberg");
		System.out.println(db.read(p.getSurname(), p.getBirthday()));
		//Datenbank wird schließlich geschlossen
		db.close();
	}


	public static void main(String[] args) throws Exception {
		//Alle 3 Datenbanken werden nun getestet

		new File("default").createNewFile();
		BigTestDB(Database.DEFAULT_RECORDDB);
		BigTestDB(new CSVDatabase(new File("sample.csv")));

		BigTestDB(new SerializableDatabase("default"));


	}

}
