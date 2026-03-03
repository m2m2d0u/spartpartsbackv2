-- Tag table
CREATE TABLE tag (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Part-Tag join table (many-to-many)
CREATE TABLE part_tag (
    part_id UUID NOT NULL REFERENCES part(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (part_id, tag_id)
);

CREATE INDEX idx_part_tag_part ON part_tag(part_id);
CREATE INDEX idx_part_tag_tag ON part_tag(tag_id);

-- Car Brand table
CREATE TABLE car_brand (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Car Model table
CREATE TABLE car_model (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    brand_id UUID NOT NULL REFERENCES car_brand(id) ON DELETE CASCADE,
    year_from INTEGER,
    year_to INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (name, brand_id)
);

CREATE INDEX idx_car_model_brand ON car_model(brand_id);

-- Add car_brand_id and car_model_id to part table
ALTER TABLE part ADD COLUMN car_brand_id UUID REFERENCES car_brand(id) ON DELETE SET NULL;
ALTER TABLE part ADD COLUMN car_model_id UUID REFERENCES car_model(id) ON DELETE SET NULL;

CREATE INDEX idx_part_car_brand ON part(car_brand_id);
CREATE INDEX idx_part_car_model ON part(car_model_id);
