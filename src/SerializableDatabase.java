import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SerializableDatabase implements Database {
	//Die Attribute werden auf die Datei zugegriffen
	private ObjectInputStream objinput;
	private ObjectOutputStream objoutput;
	private FileInputStream filein;
	private FileOutputStream fileout;
	//Wir müssen eine Datei erstellen
	private String filename;
	//Der Beginn der Daten wird gespeichert
	private long start;
	//Die aktuelle Position wird ebenfalls dadurch gespeichert
	private long currentpos;

	public SerializableDatabase(String filename){
		this.filename = filename;
	}

	@Override
	public void open() throws IOException {
		//Die Streams werden geöffnet und die Reihenfolge ist sehr wichtig
		filein = new FileInputStream(filename);
		fileout = new FileOutputStream(filename);
		objoutput = new ObjectOutputStream(fileout);
		objinput = new ObjectInputStream(filein);
		//Ermittlung der Beginn der Daten
		start = fileout.getChannel().position();
		currentpos = start;

	}

	@Override
	public Person read() throws IOException {
		//Die Position wird gesetzt
		filein.getChannel().position(currentpos);
		//Es wird versucht eine Person zu lesen und wenn es möglich ist, wird Sie zurückgegeben, anderfalls wird null zurückgegeben
		Person in = null;
		try {
			in = (Person) objinput.readObject();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (EOFException g){}
		//aktuelle Position wird gespeichert
		currentpos = filein.getChannel().position();
		return in;
	}

	@Override
	public Person read(String surname, long birthday) throws IOException {
		//Wir fangen an der Datei von vorne an
		currentpos = start;
		Person p2;
		/*wir durchsuchen die Datei bis diese den entsprechenden
		gleichen Nachnamen und Geburtstag hat
	 	wenn dies zutrifft, wird die Person zurückgegeben, wenn nicht, wird null zurückgegeben
		 */
		while (true){
			p2 = read();
			if(p2 == null){
				return null;
			}
			if(surname.equals(p2.getSurname()) && p2.getBirthday() == birthday){
				return p2;
			}
		}
	}

	@Override
	public void seek(int idx) throws IOException {
		//wir fangen wieder von vorne an
		filein.getChannel().position(start);
		//Die Personen werden übersprungen, bis der richtige Perosnenindex erreicht ist
		for(int j = 0; j < idx;j++){
			try {
				objinput.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//Die aktuelle Position wird wieder gespeichert
		currentpos = filein.getChannel().position();
	}

	@Override
	public void write(Person p) throws IOException {
		//wir beginngen von der aktuellen Position und überpsringen sind um 1 Einheit
		filein.getChannel().position(currentpos);
		try {
			objinput.readObject();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(EOFException h){}
		/*Alle Personen, die ab der nächsten Zeile von currentpos kommen
		werden gespeichert
		 */
		List<Person> allpersons = new LinkedList<Person>();
		while (true) {
			try {
				allpersons.add((Person) objinput.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException f){
				break;
			}
		}
		//Wir springen zur aktuellen Position
		fileout.getChannel().position(currentpos);
		//Die Person, die wir eingegeben haben wird gespeichert
		objoutput.writeObject(p);
		//wir speichern die aktuelle Position
		currentpos = fileout.getChannel().position();
		//Die Personen in der LinkedList werden gespeichert
		for (Person allp : allpersons) {
			objoutput.writeObject(allp);
		}
		//Die länge der Datei wird angepasst
		fileout.getChannel().truncate(fileout.getChannel().position());
	}
	@Override
	public void close() throws IOException {
		//Die Datei wird geschlossen
		objoutput.close();
		objinput.close();
	}
}
