CREATE TABLE scheduled_tasks (
    id SERIAL PRIMARY KEY,
    task_name VARCHAR(255) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    cron_expression VARCHAR(100) NOT NULL,
    scheduled_time TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    target_service_url VARCHAR(255),
    target_endpoint VARCHAR(255),
    last_execution_time TIMESTAMP,
    last_execution_status VARCHAR(255)
);

CREATE TABLE task_parameters (
    task_id BIGINT NOT NULL,
    param_value VARCHAR(255),
    param_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (task_id, param_key),
    CONSTRAINT fk_task_params FOREIGN KEY (task_id) REFERENCES scheduled_tasks(id) ON DELETE CASCADE
); 