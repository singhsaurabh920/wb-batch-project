package org.worldbuild.batch.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;
import java.util.Objects;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "person")
public class Person {

	@Id
	private String id;
	private String name;

	public Person(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Person person = (Person) o;
		return Objects.equals(id, person.id) &&
				Objects.equals(name, person.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
