IEPD-Java-Bindings
==================

This is a Java project that uses Maven and JAXB to generate a jar of Java class files based on an IEPD that can be used to read and generate instances.
This project is meant to be an **example** of how one may go about creating a JAXB-based project for using Java to manipulate
a NIEM based exchange.  While we have attempted to accommodate all sorts of NIEM-based schema idioms, please understand that
not all schema models will translate into perfect Java beans.

[![Build Status](https://travis-ci.org/jtmrice/IEPD-Java-Bindings.svg?branch=master)](https://travis-ci.org/jtmrice/IEPD-Java-Bindings)

### Usage
* Update schema files with IEPD schemas here: `${basedir}/iepd-source/`
  * Note that schemas go in the "./xsd" directory.  ALL schema files from this directory will be used by this project.
  * Note that instance XML files go in the "./xml" directory.  Each XML file found in here will be used as the basis for a round trip test later on.
* Run the command `$ mvn clean install`
* Your jar file containing JAXB Beans will be placed at `${basedir}/java-bindings-<version>.jar`
  * The source jar file will be placed there as well.


This project is based on the Template IEPD project here: [Template IEPD](https://github.com/niem/Template-IEPD).  Note that
the project has been modified (potentially substantially) to include fringe error cases that exist with JAXB and NIEM, to
illustrate and provide tests for this project.

Additionally, the full NIEM release was added to directory niem-3.2, and some example configuration files exist in there.
Note that this version of the NIEM release was modified to remove any external dependencies (an xsd:any was placed there instead).


### Note
This is a **beta** release with known issues that limit its current capabilities:
* Cannot work with GML (and other external schemas)
* Random Generation is a "best-effort" service.

### Features
1. Generates Java class files from a simple IEPD with JAXB.
2. Uses those generated sources in compiling the whole project.
3. Runs tests that generate an instance and read in an instance.
4. Updates namespace instance generation.
5. Tests with an example round trip read and write.
6. Uses a JAXB config file for more complicated schema issues.
7. Generates tests with random data depending on types.
8. Creates some generic REST Web services that can be generated from an IEPD.
9. Has Maven plugin to create `package-info.java` and JAXB bindings file customized from the schema documents.
10. Provides sample NIEM binding information to JAXB


### Planned Future Capabilities
* Provide the ability to configure which exchange elements are used to generate web services, so services are more specific.
* Generation of Web Service classes from schemas (and config) which is more specific and less generic.
* Improve random generation of instances


### Giving Feedback
XML Schemas have many different complexities that JAXB does not like.  As a result, some schemas won't translate properly
to Java objects the way you intend.  Please provide feedback to us and we will attempt to create your scenario in our
test IEPD in this project and update how our JAXB bindings are generated.
