# JBehave

JBehave is a BDD framework for Java and Groovy, mirrored [at Github](https://github.com/jbehave/jbehave-core), definitive repo [at Codehaus](http://xircles.codehaus.org/projects/jbehave).

<img src="http://jbehave.org/reference/preview/images/jbehave-logo.png" alt="JBehave logo" align="right" />

## Using

Canonical information for JBehave:

1. [News](http://jbehave.org).
2. [Documentation](http://jbehave.org/documentation/).
3. [User mail-list](http://xircles.codehaus.org/lists/user@jbehave.codehaus.org)
4. Jars in [Maven Repositories](http://mvnrepository.com/search.html?query=jbehave)

## Contributing and Developing

Please report issues, feature requests on the [Codehaus issue
tracker](http://jira.codehaus.org/browse/JBEHAVE) or discuss them on the
[dev mail-list](http://xircles.codehaus.org/lists/dev@jbehave.codehaus.org). 

###Depended-on Technologies

JDK required: 5.0 (or above)
[Maven](http://maven.apache.org) required (2.2.1 or above).

###Encoding

Configure IDE to use UTF-8 for all files
Configure Maven by adding "-Dfile.encoding=UTF-8" to $MAVEN_OPTS 
 
###IDE Integration

Maven is supported in Intellij IDEA out-of-the-box 
Maven is supported in Eclipse via [m2eclipse plugin](http://m2eclipse.sonatype.org/)

### Building

The first time you run the Maven build (Maven 2.2.1 or above required), do:

    mvn install -s settings.xml

After that, it is necessary to only do the following:

    mvn install

###Maven Build Profiles

- default: builds all releasable modules
- reporting: builds reports
- distribution: builds distribution (documentation)
- examples: builds all headless examples
- gui: builds examples that require gui (i.e. non-headless) mode (separated as they do not run on [Bamboo CI](http://builds.codehaus.org/browse/JBEHAVE)
- nt: no-test, builds skipping unit-test behaviors 

Note:  profiles are additive and the default profile is always active.

###Example Profile Usages

####Build Core and all Examples

    mvn install -Pexamples

####Build with Reporting and Distribution

    mvn install -Preporting,distribution 

####Building a Release with Maven

    mvn release:prepare -Preporting,distribution 
    mvn release:perform -Preporting,distribution

## Related

See also the 'jbehave-web' sister project for web extensions to JBehave, and 'jbehave-tutorial' for a decent example of JBehave testing a web app.

## License

See adjacent LICENSE.txt file (BSD).  