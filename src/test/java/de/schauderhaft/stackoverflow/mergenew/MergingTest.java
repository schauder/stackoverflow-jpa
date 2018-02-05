package de.schauderhaft.stackoverflow.mergenew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;


// https://jira.spring.io/browse/BATCH-2678
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MergingTest {

	@Autowired
	EntityManager em;

	@Test(expected = EntityExistsException.class)
	public void persistingTwiceThrowsException() {

		Person one = new Person();
		Person two = new Person();

		one.setId(23L);
		two.setId(23L);

		one.setFirst("Jens");
		two.setLast("Schauder");

		one.setMiddle("First");
		two.setMiddle("Second");


		em.persist(one);
		em.persist(two);
	}

	@Test
	public void mergingTwiceOverwrites() {

		Person one = new Person();
		Person two = new Person();

		one.setId(23L);
		two.setId(23L);

		one.setFirst("Jens");
		two.setLast("Schauder");

		one.setMiddle("First");
		two.setMiddle("Second");

		Person mergedOne = em.merge(one);

		assertThat(em.contains(two)).isFalse();

		Person mergedTwo = em.merge(two);

		assertThat(mergedOne).isSameAs(mergedTwo);

		assertThat(mergedOne)
				.extracting("first", "middle", "last")
				.containsExactly(null, "Second", "Schauder");

	}

	@Test
	public void persistAndMergeDoesOverwrite() {

		Person one = new Person();
		Person two = new Person();

		one.setId(23L);
		two.setId(23L);

		one.setFirst("Jens");
		two.setLast("Schauder");

		one.setMiddle("First");
		two.setMiddle("Second");

		em.persist(one);

		assertThat(em.contains(two)).isFalse();

		Person mergedTwo = em.merge(two);

		assertThat(mergedTwo)
				.extracting("first", "middle", "last")
				.containsExactly(null, "Second", "Schauder");
	}
}
