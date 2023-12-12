-- create_tables.sql

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY, -- for 유지보수 편의성!
    name VARCHAR(30),
    password VARCHAR(20),
    phone VARCHAR(15),
    email VARCHAR(30),
    CONSTRAINT users_name_password_key UNIQUE (name, password)
);

CREATE TABLE events (
    event_id SERIAL PRIMARY KEY, -- 이것 또한 유지보수.
    title VARCHAR(30),
    participants VARCHAR(50),
    location VARCHAR(20),
    description VARCHAR(100),
    creator_id INTEGER REFERENCES users(user_id),
    start_time TIMESTAMP WITHOUT TIME ZONE,
    end_time TIMESTAMP WITHOUT TIME ZONE
);


CREATE TYPE interval_enum AS ENUM ('0', '15', '30', '45', '60');

CREATE TABLE reminderinfo (
    event_id INTEGER PRIMARY KEY REFERENCES events(event_id),
    time_frame INTEGER,
    interval_q interval_enum
);

CREATE TABLE reminders (
    event_id INTEGER REFERENCES events(event_id),
    time_to_send TIMESTAMP,
    PRIMARY KEY (event_id, time_to_send) --,
    -- CONSTRAINT unique_event_time UNIQUE (event_id, time_to_send)
);