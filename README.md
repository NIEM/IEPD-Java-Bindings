IEPD-Java-Bindings
==================

This is a Java project that uses Maven and JAXB to generate a jar of java class files based on an IEPD that can be used to read and generate instances.


This project is based on the Template IEPD project here: https://github.com/niem/Template-IEPD

### Current Features
1. generate java class files from a simple IEPD with JAXB,
2. use those generated sources in compiling the whole project
3. Runs tests that generate an instance and read in an instance.

Future capabilities:
1. Update namespace instance generation 
2. Test a round trip read and write
3. Use a JAXB config file for more complicated schema issues
4. Setup unpacking IEPD from zip or URL, so you don't have to copy schema files in
5. Create some generic web services that can be generated from an IEPD (possibly another project)
