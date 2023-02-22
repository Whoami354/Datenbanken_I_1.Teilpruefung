import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Classes of this contract implements standard and simple methods to handle data in different manner.
 * @author Dipl. Inf. (FH) J. Ott
 *
 */
public interface Database {
    public final Database DEFAULT_RECORDDB = new RecordDatabase(new File("default.rec"));
    /**
     * Field that descripe the default character set.
     * In this case it means UTF-8.
     */
    public final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    /**
     * Opens the underlaying stream.u
     * @throws IOException
     */
    public void open() throws IOException;
    
    /**
     * Reads one person object from current position.
     * @return the read person object
     * @throws IOException
     */
    public Person read() throws IOException;
    
    /**
     * Reads from current position till a person within surname and birthday were found.
     * @param surname
     * @param birthday the date of birth in milli seconds
     * @return the read person object
     * @throws IOException
     * @{@link System#currentTimeMillis()}
     */
    public Person read(String surname, long birthday) throws IOException;
    
    /**
     * Seek the current to a given record index.
     * @param idx index of record
     * @throws IOException
     */
    public void seek(int idx) throws IOException;
    
    /**
     * Write the given object to the current position.
     * If there is already some data, they will be 'updated'.
     * @param p
     * @throws IOException
     */
    public void write(Person p) throws IOException;
    
    /**
     * Closes the underlaying stream.
     * @throws IOException
     */
    public void close() throws IOException;
}
