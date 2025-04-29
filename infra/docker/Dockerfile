FROM timescale/timescaledb:latest-pg14

# Install PostGIS
RUN apt-get update && \
    apt-get install -y postgis postgresql-14-postgis-3

# Enable PostGIS extension
RUN docker-entrypoint.sh postgres -c 'shared_preload_libraries=timescaledb,postgis'

# Initialize database with PostGIS extension
COPY init.sql /docker-entrypoint-initdb.d/
