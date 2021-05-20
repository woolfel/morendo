/*
 * Copyright 2002-2009 Jamocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.mapping;

import java.util.ArrayList;
import java.util.List;

public class Defmapping implements Mapping {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String className;
	@SuppressWarnings("rawtypes")
	private List keyProperties = new ArrayList();
	private String mappingName;
	@SuppressWarnings("rawtypes")
	private List properties = new ArrayList();
	private String sqlQuery;
	private String tableName;
	private String template;
	
	public Defmapping() {
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@SuppressWarnings("rawtypes")
	public List getKeyProperties() {
		return keyProperties;
	}

	@SuppressWarnings("rawtypes")
	public void setKeyProperties(List keyProperties) {
		this.keyProperties = keyProperties;
	}

	public String getMappingName() {
		return mappingName;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	@SuppressWarnings("rawtypes")
	public List getProperties() {
		return properties;
	}

	@SuppressWarnings("rawtypes")
	public void setProperties(List properties) {
		this.properties = properties;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String toPPString() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public void addKeyProperty(KeyProperty key) {
		keyProperties.add(key);
	}
	
	@SuppressWarnings("unchecked")
	public void addProperty(Property property) {
		properties.add(property);
	}
	
	public boolean usesCompositeKey() {
		return this.keyProperties.size() > 1;
	}
}
