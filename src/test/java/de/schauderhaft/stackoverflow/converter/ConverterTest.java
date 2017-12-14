package de.schauderhaft.stackoverflow.converter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest

public class ConverterTest {

	@Autowired
	JdbcTemplate template;

	@Autowired
	PersonRepository repository;

	@Test
	public void contextLoads() {

		template.execute("INSERT INTO withBigInt VALUES (23)");

		Date aDate = repository.getSomethingInNeedOfConversion();
	}

}
