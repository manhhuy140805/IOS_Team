CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    role VARCHAR(50) NOT NULL DEFAULT 'VOLUNTEER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    total_points INTEGER NOT NULL DEFAULT 0,
    address VARCHAR(500),
    date_of_birth DATE,
    gender VARCHAR(20),
    violation BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_role CHECK (role IN ('VOLUNTEER', 'ADMIN', 'ORGANIZATION')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED')),
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_total_points CHECK (total_points >= 0)
);

-- ================================================
-- TABLE: event_type
-- Description: Store event categories/types
-- ================================================
CREATE TABLE event_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    
    CONSTRAINT uq_event_type_name UNIQUE (name)
);

-- ================================================
-- TABLE: reward_type
-- Description: Store reward categories/types
-- ================================================
CREATE TABLE reward_type (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    
    CONSTRAINT uq_reward_type_title UNIQUE (title)
);

-- ================================================
-- TABLE: event
-- Description: Store volunteer events
-- ================================================
CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(500),
    image_url VARCHAR(500),
    event_start_time DATE NOT NULL,
    event_end_time DATE NOT NULL,
    num_of_volunteers INTEGER NOT NULL,
    reward_points INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    category VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id INTEGER NOT NULL,
    event_type_id INTEGER NOT NULL,
    
    CONSTRAINT fk_event_creator FOREIGN KEY (creator_id) 
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_event_type FOREIGN KEY (event_type_id) 
        REFERENCES event_type(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_event_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_event_dates CHECK (event_end_time >= event_start_time),
    CONSTRAINT chk_num_volunteers CHECK (num_of_volunteers > 0),
    CONSTRAINT chk_reward_points CHECK (reward_points >= 0)
);


CREATE TABLE reward (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL DEFAULT 'GIFT',
    image_url VARCHAR(500),
    points_required INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    expiry_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reward_type_id INTEGER,
    provider_id INTEGER,
    
    CONSTRAINT fk_reward_type FOREIGN KEY (reward_type_id) 
        REFERENCES reward_type(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_reward_provider FOREIGN KEY (provider_id) 
        REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_reward_type CHECK (type IN ('GIFT', 'VOUCHER', 'DISCOUNT', 'SERVICE')),
    CONSTRAINT chk_reward_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK')),
    CONSTRAINT chk_points_required CHECK (points_required > 0),
    CONSTRAINT chk_quantity CHECK (quantity >= 0)
);

-- ================================================
-- TABLE: event_registration
-- Description: Store user registrations for events
-- ================================================
CREATE TABLE event_registration (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    join_date DATE NOT NULL,
    checked_in BOOLEAN NOT NULL DEFAULT FALSE,
    checked_in_at TIMESTAMP,
    notification_content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    
    CONSTRAINT fk_registration_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) 
        REFERENCES event(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_registration_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT uq_user_event UNIQUE (user_id, event_id)
);

-- ================================================
-- TABLE: user_reward
-- Description: Store user reward redemption history
-- ================================================
CREATE TABLE user_reward (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    points_spent INTEGER NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    reward_id INTEGER NOT NULL,
    
    CONSTRAINT fk_user_reward_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_reward_reward FOREIGN KEY (reward_id) 
        REFERENCES reward(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_user_reward_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT chk_points_spent CHECK (points_spent > 0)
);

