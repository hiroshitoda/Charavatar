Charavatar
=========

making avater from your photo with characters composing recent tweets.

Requirements
============

* JDK 1.7
* Maven 3
* OpenCV 2.4.9
* PostgreSQL 9.3

Quick Start
============

1. build project by Maven.

    $ mvn clean package

2. make settings.yml from settings.yml.example copy.
3. execute migration.

    $ java -jar target/Charavatar*.jar db migrate settings.yml

4. start server.

    $ java -jar target/Charavatar*.jar server settings.yml

License
=======

Apache License Version 2.0
