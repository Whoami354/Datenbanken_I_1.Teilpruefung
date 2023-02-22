import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class implements a simple colon seperated value store.
 * The current implementation is for sample data only and have to be completed.
 * @author Dipl. Inf. (FH) J. Ott
 *
 */
public class CSVDatabase implements Database {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private File file;
    private RandomAccessFile rdata;

    public CSVDatabase(File f) {
        this.file = f;
    }

    /**
     * Read until a \\n or \\r has reached and convert every byte to the default charset.
     * @see Database
     * @return
     * @throws IOException
     */
    public final String readLine() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = rdata.read()) {
            case -1:
            case '\n':
                eol = true;
                break;
            case '\r':
                eol = true;
                long cur = rdata.getFilePointer();
                if ((rdata.read()) != '\n') {
                    rdata.seek(cur);
                }
                break;
            default:
                bout.write((byte)c);
                break;
            }
        }
        
        bout.close();
        String s = bout.toString(DEFAULT_CHARSET.name());
        if ((c == -1) && (s.length() == 0)) {
            return null;
        }
        return s;
    }
    
    @Override
    public void open() throws IOException {
        rdata = new RandomAccessFile(file, "rw");
        rdata.readLine();
    }

    @Override
    public Person read() throws IOException {
        String line = readLine();
        if (line != null) {
            Person p = new Person();
            String[] rec = line.split(";");
            p.setSurname(rec[2]);
            p.setGivenName(rec[1]);
            try {
                p.setBirthday(dateFormat.parse(rec[3]).getTime());
            } catch (ParseException e) {
            }
            p.setStreet(rec[4] + " " + rec[5]);
            p.setPostal(rec[6]);
            p.setCity(rec[7]);

            return p;
        }
        
        return null;
    }

    @Override
    public Person read(String surname, long birthday) throws IOException {
        //Man liegt eine variable "Person" fest, um auf die Personen zugreifen zu können
        Person p;
        //Wir suchen die Datei von Anfang an ab
        seek(0);
        //Es wird die Datei solange durchsucht bis Surname und birthday übereinstimmen
        while((p = read()) != null){
            if (surname.equals(p.getSurname()) && p.getBirthday() == birthday){
                return p;
            }
        }
        return null;
    }

    @Override
    public void seek(int idx) throws IOException {
        //Wir beginnen am Anfang der Datei an
        rdata.seek(0);
        //Wir überspringen die erste Zeile, weil die "erste" Zeile eine Header ist
        rdata.readLine();
        //Die Zeilen der Datei wird übersürungen bis der Index erreicht ist
        for (int i=0; i<idx;i++){
            rdata.readLine();
        }

    }

    @Override
    public void write(Person p) throws IOException {
        //Wir müssen den Index überspringen, damit wir die Attribute von Personen richtig ausgeben können
        int a;
        while(true){
            a = rdata.read();
            if(a == ';'){
                break;
            }
        }
        //Alle Attribute von Personen werden ausgeschrieben
        StringWriter writer = new StringWriter();
        writer.write(p.getGivenName() + ";");
        writer.write( p.getSurname() + ";");
        Date date = new Date(p.getBirthday());
        writer.write( dateFormat.format(date) + ";");
        writer.write( p.getStreet().split(" ")[0] + ";");
        writer.write( p.getStreet().split(" ")[1] + ";");
        writer.write( p.getPostal() + ";");
        writer.write(p.getCity() + "\n");
        //Wir müssen uns merken wo man in der Datei genau ist
        long currentpos = rdata.getFilePointer();
        //Wir wollen die restliche Datei speichern, aber nicht mit der aktuellen Zeile
        rdata.readLine();
        //Es legt einen Buffer mit genug Speicher für den Rest der Datei an
        long diff;
        long length = rdata.length();
        long current = rdata.getFilePointer();
        diff = length-current;
        byte[] b = new byte[(int) diff];
        //Den Rest der Datei speichern wir in einem Buffer
        rdata.read(b);
        //Hier wird die Person an der akutellen Position der Datei ausgeschrieben
        rdata.seek(currentpos);
        rdata.write(writer.toString().getBytes(StandardCharsets.UTF_8));
        //Der Rest der Datei wird nun hinten drangehängt
        rdata.write(b);
        //Wir passen die länge der Datei an
        rdata.setLength(rdata.getFilePointer());
        //Wir gehen an die ursprüngliche Position, aber überspringen Sie
        rdata.seek(currentpos);
        rdata.readLine();
    }

    @Override
    public void close() throws IOException {
        //Die Datei wird geschlossen
        rdata.close();
    }

}
