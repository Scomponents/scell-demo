# SCell API Demo Project #

SCell - Java spreadsheet library.

Official site - https://scalable-components.com

Javadocs - https://scomponents.github.io/scell-public-docs/index.html

### How to run ###

- Create file `app/gradle.properties` using the `app/gradle.properties.template` as a template
```
cp app/gradle.properties.template app/gradle.properties
```
- Fill the `app/gradle.properties` in necessary values with your favorite text editor
- Run the app with Java versions from 17
```
gradle run
```
- Java 8 and 11 also support, but it needs some kind of this project settings changes, try it yourself
- To change API version, edit file `gradle/libs.versions.toml`