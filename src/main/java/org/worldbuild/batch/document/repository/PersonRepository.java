package org.worldbuild.batch.document.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.worldbuild.batch.document.domain.Person;
import org.worldbuild.batch.document.service.PersonService;
@Repository
public interface PersonRepository extends MongoRepository<Person,String> {
}
