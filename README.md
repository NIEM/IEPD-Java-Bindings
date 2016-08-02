IEPD-Java-Bindings
==================

This is a Java project that uses Maven and JAXB to generate a jar of java class files based on an IEPD that can be used to read and generate instances.

[![Build Status](https://travis-ci.org/jtmrice/IEPD-Java-Bindings.svg?branch=master)](https://travis-ci.org/jtmrice/IEPD-Java-Bindings)

To build this project:
`$ mvn clean install`

The IEPD is unzipped into: `java-bindings/src/main/xsd` and after building the project the jar is available here: `java-bindings/target/JAXB-Template-0.2-SNAPSHOT.jar`. The generated source files are put here: `target/generated-sources/jaxb` and the used in the compilation. For Testing there is a sample instance here `java-bindings/src/test/resources/sample-exchange.xml` and after running tests a generated instance at `java-bindings/target/instance.xml`.

This project is based on the Template IEPD project here: https://github.com/niem/Template-IEPD


### Note
This is a beta release with known issues that limit its current capabilities:
* Cannot work with GML - far too many issues than we can resolve right now. 
* Cannot randomly generate references (i.e., structures:ref and structures:id) that make logical sense yet. 
* Cannot randomly generate with augmentation points.


### Current Features
1. generate java class files from a simple IEPD with JAXB,
2. use those generated sources in compiling the whole project
3. Runs tests that generate an instance and read in an instance.
4. Update namespace instance generation
5. Test with an example round trip read and write
6. Use a JAXB config file for more complicated schema issues
Exceptions:
  * gml
  * Augmentation points
  * Some parts of structures
7. Generate tests with random data depending on types
8. Create some generic REST web services that can be generated from an IEPD.
9. Maven plugin to create package-info.java and jaxb bindings file custom from the schemas


### Usage
* Update schema files with IEPD schemas here: `java-bindings/src/main/xsd`
* Update `java-bindings/pom.xml` to reference new IEPD schemas
* Update tests in java-bindings to use new IEPD objects
* Update tests in rest-services to use new IEPD objects


### Future capabilities
* Setup unpacking IEPD from zip or URL, so you don't have to copy schema files in.
* Create some generic web services that can be generated from an IEPD (possibly another project)
* Setup randomly generated dates to use reasonable values


### Project structure
* The project is a maven reactor project with submodules.
  * java-bindings - generated java source files and jar with sample tests of static and randomly generated exchanges
  * jaxb-config-maven-plugin - maven plugin to generate package-info.java and generated-bindings.xjb for generating bindings
  * xml-randomizer - Adds random XMLGregorianCalender to random data generation library
  * rest-services - Sample REST services to send and receive exchanges based on the IEPD 
 
