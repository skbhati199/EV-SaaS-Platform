#!/bin/bash

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}EV-SaaS Platform Dependency Fixer${NC}"
echo "==================================="

# Navigate to project root
cd "$(dirname "$0")/../.."

# Find all pom.xml files in service directories
SERVICE_POMS=$(find . -maxdepth 2 -name "pom.xml" -not -path "./parent/*" -not -path "./admin-portal/*")

for POM in $SERVICE_POMS; do
    SERVICE_DIR=$(dirname "$POM")
    SERVICE_NAME=$(basename "$SERVICE_DIR")
    
    echo -e "${YELLOW}Checking $SERVICE_NAME dependencies...${NC}"
    
    # Check for Kafka dependencies
    KAFKA_DEPENDENCY=$(grep -c "spring-kafka" "$POM")
    
    if [ "$KAFKA_DEPENDENCY" -eq 0 ] && grep -q "KafkaTemplate\|EnableKafka" "$SERVICE_DIR/src/main/java" -r 2>/dev/null; then
        echo -e "  ${RED}Missing Kafka dependencies but using Kafka classes${NC}"
        echo -e "  ${GREEN}Adding Kafka dependencies to $POM${NC}"
        
        # Add Kafka dependencies before the closing </dependencies> tag
        sed -i '/<\/dependencies>/i \
        <!-- Added by dependency fixer -->\
        <dependency>\
            <groupId>org.springframework.kafka</groupId>\
            <artifactId>spring-kafka</artifactId>\
        </dependency>\
        <dependency>\
            <groupId>org.apache.kafka</groupId>\
            <artifactId>kafka-clients</artifactId>\
        </dependency>\
        <dependency>\
            <groupId>org.springframework.kafka</groupId>\
            <artifactId>spring-kafka-test</artifactId>\
            <scope>test</scope>\
        </dependency>' "$POM"
    fi
    
    # Check for Redis dependencies
    REDIS_DEPENDENCY=$(grep -c "spring-boot-starter-data-redis" "$POM")
    
    if [ "$REDIS_DEPENDENCY" -eq 0 ] && grep -q "RedisTemplate\|ReactiveRedisTemplate" "$SERVICE_DIR/src/main/java" -r 2>/dev/null; then
        echo -e "  ${RED}Missing Redis dependencies but using Redis classes${NC}"
        echo -e "  ${GREEN}Adding Redis dependencies to $POM${NC}"
        
        # Add Redis dependencies before the closing </dependencies> tag
        sed -i '/<\/dependencies>/i \
        <!-- Added by dependency fixer -->\
        <dependency>\
            <groupId>org.springframework.boot</groupId>\
            <artifactId>spring-boot-starter-data-redis</artifactId>\
        </dependency>\
        <dependency>\
            <groupId>org.springframework.boot</groupId>\
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>\
        </dependency>' "$POM"
    fi
    
    # Check for WebFlux dependencies
    WEBFLUX_DEPENDENCY=$(grep -c "spring-boot-starter-webflux" "$POM")
    
    if [ "$WEBFLUX_DEPENDENCY" -eq 0 ] && grep -q "WebClient\|Mono\|Flux" "$SERVICE_DIR/src/main/java" -r 2>/dev/null; then
        echo -e "  ${RED}Missing WebFlux dependencies but using reactive classes${NC}"
        echo -e "  ${GREEN}Adding WebFlux dependencies to $POM${NC}"
        
        # Add WebFlux dependencies before the closing </dependencies> tag
        sed -i '/<\/dependencies>/i \
        <!-- Added by dependency fixer -->\
        <dependency>\
            <groupId>org.springframework.boot</groupId>\
            <artifactId>spring-boot-starter-webflux</artifactId>\
        </dependency>' "$POM"
    fi
done

echo -e "${GREEN}Dependency check complete!${NC}"
echo -e "${YELLOW}You may need to run 'mvn clean install' to update your dependencies${NC}" 