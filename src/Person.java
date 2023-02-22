import java.io.Serializable;

/**
 * A simple data class to check out basic operations on persisted data.
 * @author Dipl. Inf. (FH) J. Ott
 *
 */
public class Person implements Serializable {
    public static final Byte SIZE = 123;
    
    private String surname, givenName, street, city, postal; // 30, 30, 30, 20, 5
    private long birthday; // 8
    
    public Person() {}

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }
    
    @Override
    public String toString() {
        return String.format("%1$s, %2$s born %3$tY-%3$tm-%3$td lives at %4$s %5$s %6$s", getSurname(), getGivenName(), getBirthday(), getStreet(), getPostal(), getCity());
    }
}
