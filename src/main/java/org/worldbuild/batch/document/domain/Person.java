package org.worldbuild.batch.document.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "person")
public class Person {

	@Id
	private String id;
	private String age;
	private String name;

	public Person(String age,String name) {
		this.age=age;
		this.name=name;
	}
}
