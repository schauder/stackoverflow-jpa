/*
 * Copyright 2017 the original author or authors.
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
package de.schauderhaft.stackoverflow.postgresjsonb;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import java.io.IOException;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6;

// https://stackoverflow.com/q/47806851/66686
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:postgresjsonb.properties")
public class PostgresjsonbTest {


	@Autowired
	JdbcTemplate template;


	@Test(expected = DataIntegrityViolationException.class)
	public void straightJdbcTemplateJsonbInternalParameter() {

		String sql = "SELECT * FROM thgcop_order_placement WHERE \"order_info\" @> '{\"parentOrderNumber\":\" :param \"}'";

		template.queryForObject(sql, new String[]{"value"}, String.class);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void straightJdbcTemplateFullJsonb() {

		String sql = "SELECT * FROM thgcop_order_placement WHERE \"order_info\" @> :param";

		template.queryForObject(sql, new String[]{"value"}, String.class);
	}

	@Configuration
	@AutoConfigureJdbc
	static class Config {

		EmbeddedPostgres postgres = null;

		@Bean
		DataSource dataSource()  {
			try {
				return embeddedPostgres();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		private DataSource embeddedPostgres() throws IOException {

			Assert.isNull(postgres, "postgres should get intialized only once");
			// starting Postgres
			postgres = new EmbeddedPostgres(V9_6);
// predefined data directory
// final EmbeddedPostgres postgres = new EmbeddedPostgres(V9_6, "/path/to/predefined/data/directory");
			final String url = postgres.start("localhost", 5435, "dbName", "userName", "password");

			PGSimpleDataSource dataSource = new PGSimpleDataSource();
			dataSource.setUrl(url);

			return dataSource;
		}


		@PreDestroy
		public void cleanUp() {

			if (postgres!=  null) {
				postgres.stop();
			}
		}
	}
}
