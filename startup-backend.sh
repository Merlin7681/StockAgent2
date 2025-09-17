
# Check and start mongodb container
MONGODB_CONTAINER_NAME="mongodb"

# Check if container exists
if docker ps -a --format '{{.Names}}' | grep -q "^${MONGODB_CONTAINER_NAME}$"; then
    echo "MongoDB container already exists."
    # Check if container is running
    if docker ps --format '{{.Names}}' | grep -q "^${MONGODB_CONTAINER_NAME}$"; then
        echo "MongoDB container is already running. Skipping."
    else
        echo "MongoDB container exists but is not running. Starting it..."
        docker start ${MONGODB_CONTAINER_NAME}
        echo "MongoDB container started successfully."
    fi
else
    echo "MongoDB container does not exist. Creating and starting..."
    docker run --name ${MONGODB_CONTAINER_NAME} -p 27017:27017 -d mongo:latest
    echo "MongoDB container created and started successfully."
fi

# Check and start aktools container
AKTOOLS_CONTAINER_NAME="my-aktools"

# Check if container exists
if docker ps -a --format '{{.Names}}' | grep -q "^${AKTOOLS_CONTAINER_NAME}$"; then
    echo "AKTools container already exists."
    # Check if container is running
    if docker ps --format '{{.Names}}' | grep -q "^${AKTOOLS_CONTAINER_NAME}$"; then
        echo "AKTools container is already running. Skipping."
    else
        echo "AKTools container exists but is not running. Starting it..."
        docker start ${AKTOOLS_CONTAINER_NAME}
        echo "AKTools container started successfully."
    fi
else
    echo "AKTools container does not exist. Creating and starting..."
    docker run -d --name=${AKTOOLS_CONTAINER_NAME} -p 8080:8080 aktools:v1
    echo "AKTools container created and started successfully."
fi

# Startup stock-agent
cd stock-agent
mvn spring-boot:run
# NOTE: press 'exit' to quit!

cd -