package de.schauderhaft.stackoverflow.inefficientjoin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * for debugging the effect of mapping alternatives on the join behavior of generated queries.
 */
// https://jira.spring.io/browse/DATAJPA-1238
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class InefficientJoinApplicationTests {

	@Autowired
	ManyRepository repository;

	@Autowired
	EntityManager entityManager;

	private long id;

	Map<Long, Many> manies = new HashMap<>();
	One one;

	@Before
	public void before() {

		one = create();
	}

	private One create() {

		One one = new One();
		one.setId(++id);
		one.setName("the one and only");

		entityManager.persist(one);

		one.getMany().add(create(one, one.getName() + " - " + 1));
		one.getMany().add(create(one, one.getName() + " - " + 2));

		return one;
	}

	private Many create(One one, String name) {

		Many many = new Many();
		many.setId(++id);
		many.setOne(one);
		many.setName(name);

		manies.put(many.id, many);

		entityManager.persist(many);

		return many;
	}


	@Test
	public void valueInSessionCausesSingleSelect() {
		System.out.println("-------------------------------- single statement value in session ------------------------------------");
		repository.findAllByOne(one);
		System.out.println("-------------------------------------------------------------------------------------------------------");
	}

}
