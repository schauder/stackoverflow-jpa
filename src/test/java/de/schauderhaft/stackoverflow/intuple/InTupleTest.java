/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.stackoverflow.intuple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;

/**
 * @author Jens Schauder
 */
// https://stackoverflow.com/q/48032920/66686
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories
public class InTupleTest {

	long currentId =1;


	@Autowired
	PersonRepository repository;

	@Autowired
	NamedParameterJdbcTemplate template;

	@Before
	public void before() {

		repository.save(person("Alfred", "Zar"));
		repository.save(person("Berta", "Zar"));
		repository.save(person("Berta", "Yeast"));
	}

	@Test(expected = RuntimeException.class)
	public void notSupportedByNativeQueries() {


		repository.getByFirstAndLast(asList(
				new Object[]{"Alfred", "Zar"},
				new Object[]{"Berta", "Yeast"})
		);
	}

	@Test
	public void templateBased() {

		Map<String, List<Object[]>> params = new HashMap<>();
		params.put("one", asList(
				new Object[]{"Alfred", "Zar"},
				new Object[]{"Berta", "Yeast"}));

		List<Person> result = template.query("SELECT * FROM Person WHERE (first, last) IN (:one)", params, new BeanPropertyRowMapper<>(Person.class));


	}



	private Person person(String first, String last) {

		Person person = new Person();
		person.setId(currentId++);
		person.setFirst(first);
		person.setLast(last);

		return person;
	}
}
