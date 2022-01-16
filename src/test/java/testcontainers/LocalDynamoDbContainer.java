package testcontainers;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

/**
 * Singleton class for creating and managing the LocalDynamoDbContainer Container.
 */
@Testcontainers
public class LocalDynamoDbContainer {
    @Container private static GenericContainer<?> dockerContainer;
    private DockerComposeContainer<?> dockerComposeContainer;
    private static LocalDynamoDbContainer singleton;

    private static final int CONTAINER_PORT = 8000;

    public static GenericContainer<?> getInstance() {
        if (dockerContainer == null) {
            dockerContainer = createDockerContainer();
        }
        System.out.println(dockerContainer);
        return dockerContainer;
    }

    private static GenericContainer<?> createDockerContainer() {
        System.out.println("creating newl");
        GenericContainer<?> dynamoContainer =  new GenericContainer<>(
                DockerImageName.parse("amazon/dynamodb-local:latest"))
                .withExposedPorts(8000)
                .withReuse(true)
                .withCommand(String.format("-jar DynamoDBLocal.jar -inMemory -port %s", String.valueOf(CONTAINER_PORT)))
                .waitingFor(Wait.forLogMessage("Initializing DynamoDB Local with the following configuration:\n", 1));

        dynamoContainer.start();
        return dynamoContainer;
    }

    private static DockerComposeContainer createDockerComposeContainer() {
        return new DockerComposeContainer(new File("./docker-compose.yml"))
                .withExposedService("dynamodb-local", 8000)
                .waitingFor("dynamodb-local", Wait.forLogMessage("Aing", 1));
    }

    private LocalDynamoDbContainer() {
        dockerComposeContainer = createDockerComposeContainer();
    }
}
