import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A sample implementation of the Database contract.
 * It uses data as byte array with fixed length.
 * Objects has to wrapped by the methods and could read/write randomly.
 * @author Dipl. Inf. (FH) J. Ott
 *
 */
public class RecordDatabase implements Database {
	private File data;
	private RandomAccessFile stream;

	public RecordDatabase(File data) {
		this.data = data;
	}

	@Override
	public void open() throws IOException {
		stream = new RandomAccessFile(data, "rw");
		seek(0);
	}

	@Override
	public Person read() throws IOException {
		byte[] buffer = new byte[Person.SIZE];
		if (stream.read(buffer) == Person.SIZE) {
			Person p = new Person();
			
			p.setSurname(convert(buffer, 0, 30));
			p.setGivenName(convert(buffer, 30, 30));
			p.setStreet(convert(buffer, 60, 30));
			p.setCity(convert(buffer, 90, 20));
			p.setPostal(convert(buffer, 110, 5));
			
			ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(Long.BYTES).put(buffer, 115, Long.BYTES).rewind();
			p.setBirthday(bb.getLong());
			
			return p;
		}
		
		return null;
	}

	@Override
	public Person read(String surname, long birthday) throws IOException {
		try {
			Person p;
			do{
				p = read();
			}while (p.getSurname().equals(surname)&&p.getBirthday() == birthday);

			return p;

		}catch (Exception e){}
		
		return null;
	}

	@Override
	public void seek(int idx) throws IOException {
		stream.seek(idx*Person.SIZE);
	}

	private byte[] convert(String s, int length) {
		byte[] b = new byte[length];
		byte[] c = s.getBytes(DEFAULT_CHARSET);
		Arrays.fill(b, (byte) 0);
		System.arraycopy(c, 0, b, 0, c.length < length ? c.length : length);
		return b;
	}

	private String convert(byte[] buffer, int offset, int length) {
		StringWriter sw = new StringWriter();
		for (char c: new String(buffer, offset, length, DEFAULT_CHARSET).toCharArray()) {
			if (c == 0) {
				break;
			} else {
				sw.write(c);
			}
		}

		return sw.toString();
	}

	@Override
	public void write(Person p) throws IOException {
		stream.write(convert(p.getSurname(), 30));
		stream.write(convert(p.getGivenName(), 30));
		stream.write(convert(p.getStreet(), 30));
		stream.write(convert(p.getCity(), 20));
		stream.write(convert(p.getPostal(), 5));
		stream.write(ByteBuffer.allocate(Long.BYTES).putLong(p.getBirthday()).array());
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

}
