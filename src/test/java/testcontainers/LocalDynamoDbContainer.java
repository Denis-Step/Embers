package testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Singleton;
import java.io.File;

/**
 * Singleton wrapper class for creating and managing the LocalDynamoDbContainer Container.
 */
@Singleton
public class LocalDynamoDbContainer {

    private static LocalDynamoDbContainer singleton;
    private final GenericContainer<?> dockerContainer;
    private final int exposedPort;

    private DockerComposeContainer<?> dockerComposeContainer;

    private static final int INTERNAL_CONTAINER_PORT = 8000;
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDynamoDbContainer.class);

    /**
     * Factory method to enforce singleton on this class.
     * @return wrapper class for docker container with LocalDynamoDb.
     */
    public static LocalDynamoDbContainer getInstance() {
        if (singleton == null) {
            singleton = new LocalDynamoDbContainer(createLocalDynamoDbDockerContainer());
        }
        return singleton;
    }

    /**
     * This is the preferred method for instantiating LDDB.
     * @return LocalDynamoDb container.
     */
    private static GenericContainer<?> createLocalDynamoDbDockerContainer() {
        GenericContainer<?> dynamoContainer =  new GenericContainer<>(
                DockerImageName.parse("amazon/dynamodb-local:latest"))
                .withExposedPorts(8000)
                .withReuse(true)
                .withCommand(String.format("-jar DynamoDBLocal.jar -inMemory -port %s",
                        String.valueOf(INTERNAL_CONTAINER_PORT)))
                .waitingFor(Wait.forLogMessage(".*shouldDelayTransientStatuses.*\n", 1))
                .withLogConsumer(new Slf4jLogConsumer(LOGGER));

        dynamoContainer.start();
        return dynamoContainer;
    }

    /**
     * Uses a docker-compose file for custom configuration.
     * @return docker-compose container.
     */
    private static DockerComposeContainer createDockerComposeContainer() {
        return new DockerComposeContainer(new File("./docker-compose.yml"))
                .withExposedService("dynamodb-local", 8000)
                .waitingFor("dynamodb-local", Wait.forLogMessage("Aing", 1));
    }

    private LocalDynamoDbContainer(GenericContainer<?> container) {
        this.dockerContainer = container;
        LOGGER.info(container.getContainerId());
        LOGGER.info(container.getContainerInfo().toString());
        LOGGER.info(container.getFirstMappedPort().toString());

        this.exposedPort = container.getFirstMappedPort();
    }

    /**
     * TestContainers randomizes the port actually used to communicate with the containers to avoid collisions.
     * @return runtime actual exposed port.
     */
    public int getExposedPort() {
        return exposedPort;
    }
}
