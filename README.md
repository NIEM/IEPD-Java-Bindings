IEPD-Java-Bindings
==================

This is a Java project that uses Maven and JAXB to generate a jar of java class files based on an IEPD that can be used to read and generate instances.

[![Build Status](https://travis-ci.org/jtmrice/IEPD-Java-Bindings.svg?branch=master)](https://travis-ci.org/jtmrice/IEPD-Java-Bindings)

To build this project:
`$ mvn clean install`

The IEPD is unzipped at: `src/main/xsd` and after building the project the jar is available here: `target/JAXB-Template-0.1-SNAPSHOT.jar`. The generated source files are put here: `target/generated-sources/jaxb` and the used in the compilation. For Testing there is a sample instance here `src/test/resources/sample-exchange.xml` and after running tests a generated instance at `target/instance.xml`.

This project is based on the Template IEPD project here: https://github.com/niem/Template-IEPD

### Current Features
1. generate java class files from a simple IEPD with JAXB,
2. use those generated sources in compiling the whole project
3. Runs tests that generate an instance and read in an instance.

### Future capabilities
1. Update namespace instance generation 
2. Test a round trip read and write
3. Use a JAXB config file for more complicated schema issues
4. Setup unpacking IEPD from zip or URL, so you don't have to copy schema files in
5. Create some generic web services that can be generated from an IEPD (possibly another project)
