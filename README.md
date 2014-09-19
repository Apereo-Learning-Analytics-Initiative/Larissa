# Larissa LRS

## Introduction

Larissa LRS is a free-to-use LRS implementation in progress. 
Its aim is to provide a free-to-use locally deployable LRS that scales well beyond the ADL Reference Implementation.
The target specification is at https://github.com/adlnet/xAPI-Spec/blob/1.0.1/xAPI.md.

## Requirements

#### Building
* JDK 7+
* Maven 3.0.4+

#### Deployment
* Tomcat 6.x/7.x (other containers may work)
* CouchDB 1.5.0+ (earlier versions probably will work, but testing with 1.5.0)

## Building
* Run `mvn package` from the root of this folder.

You may run an integration-test if you have a CouchDB running
at port _5984_ (default CouchDB port). You do this by running the command
`mvn verify` from the root of this folder.

Note that the commonly used command `mvn install` also runs the 'verify' 
phase. Therefore, use `mvn package` if you do not want this to happen.

## Deployment
* Copy _target/larissa-&lt;version&gt;.war_ to _&lt;Tomcat&gt;/webapps/larissa.war_.

* Add users with role 'user', e.g.
```
<role rolename="user"/>
<user username="larissa" password="lrstester" roles="user"/>
```
to _&lt;Tomcat&gt;/conf/Tomcat-users.xml_.


* First start CouchDB and then start Tomcat.

## Configuration
Configurable properties are in _WEB-INF/lrs.conf_. 

The location of the properties file is  determined by the value of the 
context-parameter _lrs.config_, relative to location of the webapp folder.

Note that you cannot currently configure credentials to connect to CouchDB.
This probably makes it a bad idea to connect to a non-localhost instance!

## xAPI Coverage

### General
* no OAUth support yet
* _X-Experience-API-Version_ header is ignored
 
### Statement API
* no support for attachments
* a filtered query using parameter _agent_ or _activity_ will not return referring statements (_StatementRef_)
* _agent_ query-parameter only accept Agents, not Groups
* _format=ids_ queries only work when using filter-parameters (e.g. not _statementId_)
* _format=canonical_ queries do not work

### Document APIs (State, Activity Profile, Agent Profile)
* not there yet

### About Resource
* not there yet

