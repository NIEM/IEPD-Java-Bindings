IEPD-Java-Bindings
==================

This is a Java project that uses Maven and JAXB to generate a jar of Java class files based on an IEPD that can be used to read and generate instances.

[![Build Status](https://travis-ci.org/jtmrice/IEPD-Java-Bindings.svg?branch=master)](https://travis-ci.org/jtmrice/IEPD-Java-Bindings)

To build this project:
`$ mvn clean install`

The IEPD is unzipped into: `java-bindings/src/main/xsd` and after building the project the jar is available here: `java-bindings/target/JAXB-Template-0.2-SNAPSHOT.jar`. The generated source files are put here: `target/generated-sources/jaxb` and the used in the compilation. For testing there is a sample instance here `java-bindings/src/test/resources/sample-exchange.xml` and after running tests a generated instance at `java-bindings/target/testInstanceRandom.xml`.

This project is based on the Template IEPD project here: `https://github.com/niem/Template-IEPD`


### Note
This is a beta release with known issues that limit its current capabilities:
* Cannot work with GML - far too many issues than we can resolve right now.
* Cannot randomly generate references (i.e., structures:ref and structures:id) that make logical sense yet.
* Cannot randomly generate with augmentation points or abstract elements, these could be added .


### Current Features
1. Generates Java class files from a simple IEPD with JAXB.
2. Uses those generated sources in compiling the whole project.
3. Runs tests that generate an instance and read in an instance.
4. Updates namespace instance generation.
5. Tests with an example round trip read and write.
6. Uses a JAXB config file for more complicated schema issues.
7. Generates tests with random data depending on types.
8. Creates some generic REST Web services that can be generated from an IEPD.
9. Has Maven plugin to create `package-info.java` and JAXB bindings file customized from the schema documents.


### Usage
* Update schema files with IEPD schemas here: `java-bindings/src/main/xsd`
* Update `java-bindings/pom.xml` to reference new IEPD schemas
* Update tests in `java-bindings` to use new IEPD objects
* Update tests in `rest-services` to use new IEPD objects


### Future capabilities
* Setup unpacking IEPD from ZIP or URL, so you do not have to copy schema document files in.
* Create some generic Web services that can be generated from an IEPD (possibly another project).
* Update Random data generator to handle NIEM/XML specific cases
* Setup randomly generated dates to use reasonable values.


### Project structure
* This project is a Maven reactor project with submodules.
  * `java-bindings` - generated Java source files and jar with sample tests of static and randomly generated exchanges.
  * `jaxb-config-maven-plugin` - Maven plugin to generate `package-info.java` and `generated-bindings.xjb` for generating bindings.
  * `xml-randomizer` - Adds random `XMLGregorianCalender` to random data generation library.
  * `rest-services` - Sample REST services to send and receive exchanges based on the IEPD.
