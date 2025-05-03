CREATE TABLE v2g_schedules (
    id SERIAL PRIMARY KEY,
    vehicle_id VARCHAR(100) NOT NULL,
    station_id VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    power_kw DECIMAL(10, 2) NOT NULL,
    schedule_type VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    last_updated TIMESTAMP,
    user_id VARCHAR(100)
); 