# comfortable-data
Java based REST service for movies, books and similar data using jpa in its backend.

[![Build Status](https://travis-ci.org/Nachtfeuer/comfortable-data.svg?branch=master)](https://travis-ci.org/Nachtfeuer/comfortable-data)
[![Coverage Status](https://coveralls.io/repos/github/Nachtfeuer/comfortable-data/badge.svg?branch=master)](https://coveralls.io/github/Nachtfeuer/comfortable-data?branch=master)
[![CodeFactor](https://www.codefactor.io/repository/github/nachtfeuer/comfortable-data/badge)](https://www.codefactor.io/repository/github/nachtfeuer/comfortable-data)
[![BCH compliance](https://bettercodehub.com/edge/badge/Nachtfeuer/comfortable-data?branch=master)](https://bettercodehub.com/)
[![codebeat badge](https://codebeat.co/badges/4ca02579-c36c-4400-8367-7155734a17b1)](https://codebeat.co/projects/github-com-nachtfeuer-comfortable-data-master)
[![Known Vulnerabilities](https://snyk.io/test/github/nachtfeuer/comfortable-data/badge.svg)](https://snyk.io/test/github/nachtfeuer/comfortable-data) 

## Goals

 - a service in a local environment; no provider since this might raise
   a lot of legal issues.
 - myself I intend to store my own bought books, movies, music and data like
   this to solve following problems:
   - full control over the data
   - being flexible in extending for required functionality
   - supporting YAML, JSON and XML to import and export data in any direction
   - providing also useful UI to search, filter and to add new data

## Quickstart

```bash
./mvnw clean package
```

## Adjusted for using Maven Wrapper

```bash
mvn -N io.takari:maven:0.7.5:wrapper
```

## Links

### Build
 - https://github.com/takari/maven-wrapper
 
### Database
 - https://www.h2database.com/html/main.html
 
### UI
 - https://jquery.com/
 - https://vuejs.org/
 - https://getbootstrap.com/
 - https://bootstrap-vue.js.org/
 - https://www.baeldung.com/running-setup-logic-on-startup-in-spring
 - https://www.baeldung.com/spring-mvc-static-resources

### Documentation
 - https://spring.io/guides/gs/testing-restdocs/
 - https://asciidoctor.org/docs/asciidoctor-maven-plugin/
 - https://asciidoctor.org/
 - https://scacap.github.io/spring-auto-restdocs/
 - https://docs.spring.io/spring-restdocs/docs/1.2.6.RELEASE/reference/html5/
 
### Request Handling
 - https://github.com/tkaczmarzyk/specification-arg-resolver
 
### Data (de-)serialization
 - https://msgpack.org/
 - https://www.json.org/
 - https://yaml.org/
 - https://www.w3.org/TR/2008/REC-xml-20081126/
