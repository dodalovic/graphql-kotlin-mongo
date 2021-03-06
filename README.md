# graphql-kotlin-mongo project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Starting mongo locally

You can start mongo + mongo-express servers by running provided `docker-compose` configuration:

```
docker-compose up -d
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Accessing graphql APIs

Visit http://localhost:8080/graphql-ui in your browser, and query the schema with the provided UI

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `graphql-kotlin-mongo-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/graphql-kotlin-mongo-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/graphql-kotlin-mongo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.