# Semantic Versioning with Build Number

This plugin is designed to replace any and all operations performed on a projects version number in Gradle. I wrote this 
as I wanted something more reliable and portable than just copy/pasted tasks in a `build.gradle`. I have tried a number 
of the other similar plugins over the years but none of them were ever exactly what I wanted or worked the way I wanted
to use them. There was always something that was missing. With that said ... introducing ... drum roll please ...
the `semantic-versioning-with-build-number` plugin. To see my release blog post take a look [here](https://benrhine.com/blog/semantic-versioning-with-build-number/).

### Why would I want this?
By using this plugin you are able to add items to your build script such as updating your version number programmatically.
For example, you could structure your `build.gradle` file such that if you are running a CI build in a certain environment
that you want any artifacts to be generated with the `BETA` artifact type.
```groovy
versionConfig {
  ...
  if (environment == "UAT") {
    artifactType = "BETA"
  }
  ...
}
```
You could also use it if you are practicing SAFE Agile to match the version to PI's and Increments of work (see [below](#safe-agile)).

#### IMPORTANT!!!
*I wouldn't exactly call this a warning, but I don't want anyone caught off guard about how certain functionality
in this plugin operates. When this plugin updates the project version it is fully regenerating whatever property file
it is operating on. I realize that to some if they do not understand this up front that it might cause some concern. In
an attempt to mitigate this I have implemented it in such a way that the order of the properties file is maintained and
the only visible changes to the end user is updated versioning properties and a timestamp at the top of the file explaining
that the file has been regenerated. I personally have worked on this part extensively as I make heavy use of property files
and wanted to ensure that this would not cause me problems. I have not found this to be an issue but consider yourself
duly informed on how this plugin works operationally.*

- [How to install](#how-to-install)
- [What does it do?](#what-does-it-do)
    - [Print the current version](#print-the-current-version)
  - [What is the default version?](#what-is-the-default-version)
  - [Available Tasks](#available-tasks)
    - [Print the current version](#print-the-current-version-1)
    - [Increment the patch portion of the version](#increment-the-patch-portion-of-the-version)
    - [Increment the minor portion of the version](#increment-the-minor-portion-of-the-version)
    - [Increment the major portion of the version](#increment-the-major-portion-of-the-version)
    - [Decrement the patch portion of the version](#decrement-the-patch-portion-of-the-version)
    - [Decrement the minor portion of the version](#decrement-the-minor-portion-of-the-version)
    - [Decrement the major portion of the version](#decrement-the-major-portion-of-the-version)
- [Configuration](#configuration)
  - [Default](#default)
  - [Using an alternate properties file](#using-an-alternate-properties-file)
    - [version.properties](#versionproperties)
  - [Setting artifact type](#setting-artifact-type)
    - [Supported artifact types](#supported-artifact-types)
  - [CI/CD Builds](#cicd-builds)
    - [remoteBuild (Or how to include your build number)](#remotebuild--or-how-to-include-your-build-number-)
    - [ciBuildNumberEnvVarName (Or how to get the build number from your provider)](#cibuildnumberenvvarname--or-how-to-get-the-build-number-from-your-provider-)
    - [includeReleaseTag && includeBuildNumber](#includereleasetag--includebuildnumber)
- [Future Ideas](#future-ideas)
- [SAFE Agile](#safe-agile)
- [Development](#development)

## How to install
- Declare it in the `plugin` block of gradle like any other plugin.
```groovy
plugins {
    id 'com.benrhine.semantic-versioning-with-build-number' version '0.0.1'
}
```
- Add the following properties to your `gradle.properties` file.
```properties
major=0
minor=0
patch=1
buildNumber=0
artifact-type=LOCAL
remote-build=false
include-release-tag=false
```
### Legacy Install
Or if you are manually building from source.
```groovy
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath 'com.benrhine:semantic-versioning-with-build-number:0.0.1'
    }
}

plugins {
  
}

apply plugin: 'com.benrhine.semantic-versioning-with-build-number'
```
Note: These properties are required for the plugin to work correctly. If do not set these properties you will get error(s)
similar to the following.
```shell
A problem occurred configuring root project 'semantic-versioning-with-build-number'.
> Could not set unknown property 'major' for root project 'semantic-versioning-with-build-number'
```

## What does it do?
Once the plugin is applied it will replace any / all the operations that you would expect to do on a projects version number.
With just applying the plugin your version will now be set to whatever you specified in the `gradle.properties` file and
will be available from gradles default `version` variable. To inspect the version after applying the plugin.

#### Print the current version
```shell
./gradlew printVersion
```

**Note: Make sure you are not assigning to the projects `version` anywhere else in your `build.gradle` otherwise that
will mess up the plugins function.**

### What is the default version?
The default version will be whatever was set in the `gradle.properties` (see above [reference](#how-to-install)). 
*Note: I normally set it to `0.0.0-LOCAL` to start.*

### Available Tasks
This plugin includes the ability to print the current version at any point of your development process as well as increment
or decrement major, minor, or patch programmatically.

#### Print the current version
```shell
./gradlew printVersion
```

#### Increment the patch portion of the version
```shell
./gradlew incrementPatchVersion
```

#### Increment the minor portion of the version
```shell
./gradlew incrementMinorVersion
```

#### Increment the major portion of the version
```shell
./gradlew incrementMajorVersion
```

#### Decrement the patch portion of the version
```shell
./gradlew decrementPatchVersion
```

#### Decrement the minor portion of the version
```shell
./gradlew decrementMinorVersion
```

#### Decrement the major portion of the version
```shell
./gradlew decrementMajorVersion
```

## Configuration
In addition to the tasks listed above this plugin also supports a number of configurations out of the box.

### Default
By default, there is no need to do any additional configuration, by applying the plugin (and adding the properties) when
your app loads the version will be created using this information and the tasks listed above will be available to you.

**WARNING: Do not use the default if you use comments in your `gradle.properties` file as they WILL BE REMOVED when the
file is re-written. If having comments in your property file is important to you use the [alternate properties file](#using-an-alternate-properties-file)
approach. Using the alternate properties file approach ensures that the separate versioning properties are the only 
file that is re-written**

_Note: I am looking for an approach that will allow both maintaining order and comments but have not yet found one. The
`org.apache.commons:commons-configuration2` in theory allows for maintaining comments but in my experimentation did not
function as expected. It is possible to add header and footer comments, as well as a comment before every property but
not before blocks of properties and this does not seem to maintain property order._

### Using an alternate properties file
By default, this application uses the `gradle.properties` file but if you wish you can use a properties file in an alternate
location. To achieve this, add the following block to your `build.gradle`

```groovy
versionConfig {
    customVersionPropertiesPath = "$projectDir/src/main/resources/version.properties"
}
```

_Note: Use this approach if you need to maintain comments in your primary property file._

#### version.properties
I had experimented with using a `version.properties` instead of `gradle.properties` when I started writing this plugin, 
I like the explicit nature of it but dislike the verbosity. The original reason I tried this is I was unsure of how much
of a challenge it would be to keep the order of the properties file (which is super important to me and how I write code).
As stated above, it ended up not being too challenging to keep the properties ordered as written, so I did away with this
approach. You can still use it if you like by applying it as a custom path as stated [above](#using-an-alternate-properties-file).

**Note: When doing this you still need to add the property keys to the `gradle.properties` otherwise the plugin will
prevent the project from starting up. While the property keys in the `gradle.properties` will not be updated they are
required to get the necessary property keys in the gradle property context.**

Follow-up note: This was another interesting thing I ran across while working on this plugin is that the property keys
essentially have to be initialized from the very beginning of the applications' operation. While it is possible to see and
work with the existing property keys I was unable to find a way that would allow me to successfully add them into the gradle
context at operation time.

### Setting artifact type
By default, the application will set your artifact to whatever has been defined in the properties file or `LOCAL`.  To update
this for your needs you can assign the type as follows ...
```groovy
versionConfig {
    artifactType = "SNAPSHOT"
}
```
#### Supported artifact types
- LOCAL
- SNAPSHOT
- CANARY
- ALPHA
- BETA
- EXPERIMENTAL
- RELEASE
These are the artifacts that are validated as part of the plugin, but it doesn't have to be one of the above. You can choose
to assign any value you wish to the `artifactType` but will receive a warning if it is something not in the listed types above.

### CI/CD Builds
If you are using this plugin while performing remote builds (who isn't?) the following configuration is necessary.

```groovy
versionConfig {
    remoteBuild = true                              // Required (optionally) for CI/CD
    ciBuildNumberEnvVarName = "BUILD_RUN_NUMBER"    // Other known CI/CD predefined ENV's [BUILD_RUN_NUMBER, BITBUCKET_BUILD_NUMBER]
    includeReleaseTag = true
    includeBuildNumber = true
}
```

#### remoteBuild (Or how to include your build number)
While technically not required the plugin looses functionality without this option and I assume if you made it this far
this might be a feature you are looking for. Without enabling `remoteBuild` it is not possible to include the build number
in your version.

#### ciBuildNumberEnvVarName (Or how to get the build number from your provider)
To specify which ENV VAR to get the build number from specify the expected ENV VAR name here. This will validate if it
is GitHub Actions or BitBucket and warn for anything else (please feel free to send me other known good names and I will
include them in future releases).

If this is not set it will warn you and will default to the provided version with an artifact but NO BUILD NUMBER.

#### includeReleaseTag && includeBuildNumber
These properties can be declared at any and or all the time. These only take effect when creating a release artifact AND
when `artifactType` is set to `RELEASE`. If neither `includeReleaseTag` or `includeBuildNumber` is set in the `versionConfig`
they default to `false` and the version number will set to `X.X.X`. If `includeReleaseTag` is set to true the resulting
build number will be `X.X.X-RELEASE`. If `includeBuildNumber` is set to true the resulting build number will be `X.X.X.X`.
finally if both `includeReleaseTag` or `includeBuildNumber` is set to true the resulting build number will be `X.X.X.X-RELEASE`.

## Future Ideas
I believe there is a duplicate load of the local properties. I have tried to fix this but so far when I try to make these
changes the properties fail to update correctly. I believe that the initial property load can happen a single time in the
main class and do not need to be redeclared in the task files. While I found reference on how to convert between properties
and map [here](https://www.rgagnon.com/javadetails/java-convert-map-to-properties.html), the issue with trying to do this
is there are other items in gradle properties context that you would not want written back to the properties file.

Increment value and push to git

## SAFE Agile
Semantic versioning lends itself to the SAFE Agile practice. When operating under SAFE methodology you loosely plan a PI
at a time (i.e. you plan a quarter at a time) then have standard sprints within that PI which are known as iterations.
Understanding this allows you to use semantic versioning to match your agile cycle. See the following
- Major version = PI version
- Minor version = PI Sprint version
- Patch version = PI Daily Version
- Build Number
OR
- Major version = Release Year
- Minor version = PI version
- Patch version = PI Sprint version
- Build Number
If you use semantic versioning as above it will allow you to closely tie your work to your process.

## Development
When working on this plugin it is possible to self reference the plugin within the `build.gradle` file by updating to the
following. I have removed the self-referencing portions for publishing cleanliness

```groovy
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath 'com.benrhine:semantic-versioning-with-build-number:0.0.1-SNAPSHOT'
    }
}

plugins {
    id 'com.gradle.plugin-publish' version '1.1.0'
}

group 'com.benrhine'
version '0.0.1-SNAPSHOT'

apply plugin: 'com.benrhine.semantic-versioning-with-build-number'


gradlePlugin {
    website = 'https://benrhine.com'
    vcsUrl = 'https://github.com/benrhine/semantic-versioning-with-build-number'
    plugins {
        semanticVersioningWithBuildNumberPlugin {
            id = 'com.benrhine.semantic-versioning-with-build-number'
            displayName = 'Semantic versioning with build number'
            description = 'Flexible semantic versioning with the ability to include a build number of use for SAFE Agile'
            //tags.set(['semantic', 'version', 'build', 'number', 'build number', 'safe', 'agile', 'safe agile'])
            implementationClass = 'com.benrhine.plugins.v1.SemanticVersioningWithBuildNumberPlugin'
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

test {
    useJUnitPlatform()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}

versionConfig {
    remoteBuild = true
    ciBuildNumberEnvVarName = "BUILD_RUN_NUMBER" //BITBUCKET_BUILD_NUMBER
    artifactType = "SNAPSHOT"
    includeReleaseTag = true
    includeBuildNumber = true
    customVersionPropertiesPath = "$projectDir/src/main/resources/version.properties"
}

```









