/**
 * ========================================
 * PERSON.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class Person {
	private int id;
	private String firstName;
	private String lastName;
	private String email;

	public Person(int id, String firstName, String lastName, String email) {
		try {
			setId(id);
			setFirstName(firstName);
			setLastName(lastName);
			setEmail(email);
		} catch (PersonException pe) {
			pe.printStackTrace();
			System.exit(1);
		}
	}

	public void setId(int id) throws PersonException {
		if (id < 0) {
			throw new PersonException("ID cannot be negative");
		}
		this.id = id;
	}

	public void setFirstName(String firstName) throws PersonException {
		if (firstName == null || firstName.isEmpty()) {
			throw new PersonException("First name cannot be empty");
		}
		this.firstName = firstName;
	}

	public void setLastName(String lastName) throws PersonException {
		if (lastName == null || lastName.isEmpty()) {
			throw new PersonException("Last name cannot be empty");
		}
		this.lastName = lastName;
	}

	public void setEmail(String email) throws PersonException {
		if (email == null || email.isEmpty()) {
			throw new PersonException("Email cannot be empty");
		}
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		return id + "|" + firstName + "|" + lastName + "|" + email + "\n";
	}
}

