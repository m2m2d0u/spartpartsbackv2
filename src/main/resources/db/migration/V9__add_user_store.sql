-- Migration to add user_store table for linking STORE_MANAGER users to stores
-- This enables STORE_MANAGER users to be assigned to multiple stores

CREATE TABLE user_store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    store_id UUID NOT NULL REFERENCES store(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_store UNIQUE (user_id, store_id)
);

-- Indexes for performance
CREATE INDEX idx_user_store_user_id ON user_store(user_id);
CREATE INDEX idx_user_store_store_id ON user_store(store_id);

-- Add comments for documentation
COMMENT ON TABLE user_store IS 'Links STORE_MANAGER users to stores they can manage';
COMMENT ON COLUMN user_store.user_id IS 'Reference to user with STORE_MANAGER role';
COMMENT ON COLUMN user_store.store_id IS 'Reference to store that user can manage';
