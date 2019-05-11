# comfortable-data
Java based REST service for movies, books and similar data using jpa in its backend.

[![Build Status](https://travis-ci.org/Nachtfeuer/comfortable-data.svg?branch=master)](https://travis-ci.org/Nachtfeuer/comfortable-data)
[![Coverage Status](https://coveralls.io/repos/github/Nachtfeuer/comfortable-data/badge.svg?branch=master)](https://coveralls.io/github/Nachtfeuer/comfortable-data?branch=master)
[![CodeFactor](https://www.codefactor.io/repository/github/nachtfeuer/comfortable-data/badge)](https://www.codefactor.io/repository/github/nachtfeuer/comfortable-data)
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
 - https://github.com/takari/maven-wrapper
 - https://www.h2database.com/html/main.html
 - https://jquery.com/
 - https://vuejs.org/
 - https://getbootstrap.com/
 - https://bootstrap-vue.js.org/
 - https://www.baeldung.com/running-setup-logic-on-startup-in-spring
 - https://www.baeldung.com/spring-mvc-static-resources
 - https://github.com/tkaczmarzyk/specification-arg-resolver
