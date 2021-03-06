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
package de.schauderhaft.stackoverflow.fetchtuning;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;

/**
 * @author Jens Schauder
 */
@Data
@Entity(name = "FetchPerson")
@NamedEntityGraph(name = "joined", includeAllAttributes = true)
public class Person {
	@Id
	private Long id;

	private String name;

	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "role_id")
	private Role role;
}
