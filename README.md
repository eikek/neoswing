NeoSwing
========

A simple swing-based GUI for [Neo4j](http://neo4j.org/) databases
that uses the [JUNG](http://jung.sourceforge.net/) library for
visuaization.

![screenshot](https://github.com/eikek/neoswing/raw/master/screenshot.png)

Usage
=====

You can [download](/eikek/neoswing/downloads) an executable jar from the download area
or build from source. [Maven](http://maven.apache.org) is used to build
NeoSwing:

    mvn install

This results in 3 artifacts being build to the `target` directory. The
artifact `neoswing-{version}-bin.jar` is an executable jar file that
can be executed:

    java -jar neoswing-{version}-bin.jar [directory]

Optional, a directory can be specified as argument that is either empty
or denotes a valid neo4j database directory. If an empty directory is
specified, a new database is created. A database can always be selected
via the Gui.

The `QuickView` class provides static methods to quickly fire up a frame
or dialog of a database. That is often handy during development, when
you like to view the current graphs state:

``` java
GraphDatabaseService db = ...;
QickView.showModal(db);
```

OSGi
----

NeoSwing' default artifact contains OSGi bundle headers so it can be
deployed to an OSGi container. However, dependencies must be set up properly.
It is necessary to deploy [Neo4j](http://neo4j.org/) and [JUNG](http://jung.sourceforge.net/)
(visualization and algorithms) together with NeoSwing.

To support this, a [Karaf](http://karaf.apache.org) features file is
provided. Add it using the following url

    mvn:org.eknet.neoswing/neoswing/{version}/xml/features

Then deploying NeoSwing can be achieved with

    features:install neoswing

This will install NeoSwing and its dependencies.