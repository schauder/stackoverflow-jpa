package de.schauderhaft.stackoverflow.fetchtuning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


// https://stackoverflow.com/q/48196290/66686
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@EnableJpaRepositories
public class FetchTuningTest {

	@Autowired
	PersonRepository repository;

	@Autowired
	EntityManager em;

	private long personId;
	private long roleId;

	@Before
	public void before() {

		createAndSave("Alfred");
		createAndSave("Berta");
		createAndSave("Carl");
		em.flush();
		em.clear();
	}

	private void createAndSave(String name) {

		Person alfred = person(name);
		em.persist(alfred.getRole());
		repository.save(alfred);
	}

	private Person person(String name) {

		Person person = new Person();
		person.setId(++personId);
		person.setName(name);
		person.setRole(role(name + "'s role"));

		return person;
	}

	private Role role(String name) {

		Role role = new Role();
		role.setId(++roleId);
		role.setName(name);

		return role;
	}

	@Test
	public void findById() {

		System.out.println("---------- before findOne --------------");
		Person one = repository.findOne(personId);
		System.out.println(one.getRole().getName());
		System.out.println("---------- after findOne --------------");

	}

	@Test
	public void findByName() {

		System.out.println("---------- before findOne --------------");
		Person one = repository.findByName("Carl").get(0);
		System.out.println(one.getRole().getName());
		System.out.println("---------- after findOne --------------");

	}

}
