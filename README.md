## Hoverdb2

This project is a rewrite of [hoverdb](https://github.com/sultanofcardio/hoverdb)

## Getting Started

Add the repository to your pom.xml

```xml
<repositories>
    <repository>
        <id>sultanofcardio</id>
        <url>https://repo.sultanofcardio.com/artifactory/sultanofcardio</url>
    </repository>
</repositories>
```

Or build.gradle

```groovy
repositories {
    maven { url "https://repo.sultanofcardio.com/artifactory/sultanofcardio" }
}
```

Then add a dependency

```xml
<dependency>
    <groupId>com.sultanofcardio</groupId>
        <artifactId>hoverdb2</artifactId>
    <version>1.0.1</version>
</dependency>
```

```groovy
implementation 'com.sultanofcardio:hoverdb2:1.0.1'
``` 

The core library doesn't provide any specific database support out of the box. Vendor database support can be found 
in the following packages:

|     Vendor     |      artifact       |
|----------------|---------------------|
|    MySQL       | hoverdb2-mysql      |
|    PostgreSQL  | hoverdb2-postgresql |
|    H2          | hoverdb2-h2         |
|    Oracle      | hoverdb2-oracle     |
|    SQLite      | hoverdb2-sqlite     |
|    SQLServer   | hoverdb2-sqlserver  |

Vendor support packages have the same version as the core library and can be used in place of it. Instead of the above,
you would just have:

```groovy
implementation 'com.sultanofcardio:hoverdb2-h2:1.0.1'
``` 

## Usage

### Querying

Using the H2 vendor package, we can create an in-memory database

```kotlin
val h2 = H2.Memory("h2db")
```

You can perform normal CRUD operations with the database object

#### Select
```kotlin
h2.select("name")
    .from("my_table")
    .whereEquals("id", 45)
    .execute { resultSet ->
        prinln(resultSet.getString("name"))
    }
```
which is the equivalent of
```sql
SELECT name FROM my_table where id = 45;
```

#### Raw SQL

You can also run raw SQL directly on the database
```kotlin
h2.run("DELETE FROM my_table WHERE id = 45")
```

## Custom Databases

If support for you your target database is not included, you can add it by implementing the `Database` interface

```kotlin
class MyCustomDatabase: Database<MyCustomDatabase> {
    //...
}
```

This library uses [Semantic Versioning](http://semver.org/)
