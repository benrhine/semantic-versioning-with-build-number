# Semantic Versioning with Build Number

This plugin is designed to replace any and all operations performed on a projects version number in Gradle. I wrote this 
as I wanted something more reliable and portable than just copy/pasted tasks in a `build.gradle`. I have tried a number 
of the other similar plugins over the years but none of them were ever exactly what I wanted or worked the way I wanted
to use them. There was always something that was missing. With that said ... introducing ... drum roll please ...
the `semantic-versioning-with-build-number` plugin.

IMPORTANT!!! I wouldn't exactly call this a warning, but I don't want anyone caught off guard about how certain functionality
in this plugin operates. When this plugin updates the project version it is fully regenerating whatever property file
it is operating on. I realize that to some if they do not understand this up front that it might cause some concern. In
an attempt to mitigate this I have implemented it in such a way that the order of the properties file is maintained and
the only visible changes to the end user is updated versioning properties and a timestamp at the top of the file explaining
that the file has been regenerated. I personally have worked on this part extensively as make heavy use of property files
and wanted to ensure that this would not cause me problems. I have not found this to be an issue but consider yourself
duly informed on how this plugin works operationally.

## How to install
- Declare it in the `plugin` block of gradle like any other plugin.
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
*Note: I normally set it to `0.0.0-LOCAL` to start.

### Available Tasks

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

## Configuration
In addition to the tasks listed above this plugin also supports a number of configurations out of the box.

### Default
By default, there is no need to do any additional configuration, by applying the plugin (and adding the properties) when
your app loads the version will be created using this information and the tasks listed above will be available to you.

### Using an alternate properties file
By default, this application uses the `gradle.properties` file but if you wish you can use a properties file in an alternate
location. To achieve this, add the following block to your `build.gradle`

```groovy
versionConfig {
    customVersionPropertiesPath = "$projectDir/src/main/resources/version.properties"
}
```

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
main class and do not need to be redeclared in the task files.

Increment value and push to git
decrement value - dont know why you would wnat htis but

how to use for smart agile











