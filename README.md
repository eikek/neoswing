NeoSwing
========

A simple swing-based GUI for [Bluepints](https://github.com/tinkerpop/blueprints)
enabled databases, that uses the [JUNG](http://jung.sourceforge.net/) library for
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

    java -jar neoswing-{version}-bin.jar

The "big jar" contains the orientdb and titan embedded graph databases. Neo4j
is excluded due to license incompatibility. You can quickly download `neo4j-kernel`
and `blueprints-neo4j-graph` jar files and place them right next to the neoswing
jar file (or in `lib` folder).

The `QuickView` class provides static methods to quickly fire up a frame
or dialog of a database. That is often handy during development, when
you like to view the current graphs state:

    GraphDatabaseService db = ...;
    QickView.showModal(db);


OSGi
----

NeoSwing' default artifact contains OSGi bundle headers so it can be
deployed to an OSGi container. However, dependencies must be set up properly.
It is necessary to deploy [JUNG](http://jung.sourceforge.net/) (visualization
and algorithms) and Blueprints-Core together with NeoSwing.

To support this, a [Karaf](http://karaf.apache.org) features file is
provided. Add it using the following url

    mvn:org.eknet.neoswing/neoswing/{version}/xml/features

Then deploying NeoSwing can be achieved with

    features:install neoswing

This will install NeoSwing and its dependencies.

Note, the concrete database bundles are not mentioned in the features file.