karaf-3.0.0-websocket
=====================

[Apache Karaf](http://karaf.apache.org) is a great OSGi deployment framework. It provides many features out of the box. 

Karaf supports the deployment of Web applications as war packages, but it is also possible to deploy Web resources and servlets with [Pax Web](https://ops4j1.jira.com/wiki/display/paxweb/Pax+Web) without packaging your application as a war archieve. However, as of Karaf version 3.0.0, websocket support is still not available. This is because the current version of Karaf Web features depends on [Jetty servlet engine](https://www.eclipse.org/jetty/) version 8.x and although Jetty has supported the various WebSocket drafts in the 7.x and 8.x releases, support for JSR-356, the official Java API for working with WebSockets was introduced after version 9.x.

This projects shows how to enable Websocket support on Karaf 3.0.0. It depends on the [GitHub project](https://github.com/ops4j/org.ops4j.pax.web) that demonstrates OSGi R4 Http Service and Web Applications (OSGi Enterprise Release chapter 128) implementation using Jetty 9 or Tomcat 7. 

## Usage
--------

1. Users should first pull the [Pax Web version 4.x SNAPSHOT](https://github.com/ops4j/org.ops4j.pax.web) from GitHub and install it into their local Maven repository. 

2. Than pull this project and build it again using Maven. The project installs a Karaf features descriptor, **net.vitic/karaf-websocket/1.0.0/xml/features**.

3. In your Apacahe Karaf installation disable default features **mvn:org.ops4j.pax.web/pax-web-features/3.0.5/xml/features** and **mvn:org.apache.karaf.features/spring/3.0.0/xml/features** by removing them from the **org.apache.karaf.features.cfg** configuration file under the etc folder. It should now read

````
#
# Comma separated list of features repositories to register by default
#
featuresRepositories=\
 mvn:org.apache.karaf.features/standard/3.0.0/xml/features,\
 mvn:org.apache.karaf.features/enterprise/3.0.0/xml/features
````

4. Start Karaf and install the feature descriptor provided by thsi project and than install the project feature as follows

````
karaf@karaf-3.0.0()> feature:repo-add net.vitic/karaf-websocket/1.0.0/xml/features
karaf@karaf-3.0.0()> feature:install karaf-websocket
````

At http://localhost:8181 you should be able to see the samples provided by the [Pax Web version 4.x SNAPSHOT](https://github.com/ops4j/org.ops4j.pax.web) project plus the websocket example at the bottom of the samples link list. 
