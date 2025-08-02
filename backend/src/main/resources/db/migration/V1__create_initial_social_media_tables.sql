CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	username VARCHAR(50) UNIQUE NOT NULL,
	email VARCHAR(100) UNIQUE NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	full_name VARCHAR(100), --optional
	bio TEXT, --optional
	profile_picture_url VARCHAR(255),
	created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);


CREATE TABLE posts (
	id BIGSERIAL PRIMARY KEY,
	user_id INTEGER NOT NULL, --FK user
	content TEXT NOT NULL,
	image_url VARCHAR(255),
	video_url VARCHAR(255),
	is_public BOOLEAN DEFAULT TRUE,
	created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT fk_user 
		FOREIGN KEY (user_id) 
		REFERENCES users(id)
		ON DELETE CASCADE 
);
CREATE INDEX idx_posts_user_id ON posts (user_id);
CREATE INDEX idx_posts_created_at ON posts (created_at DESC);


CREATE TABLE comments (
	id BIGSERIAL PRIMARY KEY,
	post_id BIGINT NOT NULL,
	user_id INTEGER NOT NULL,
	content TEXT NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT fk_post
		FOREIGN KEY (post_id)
		REFERENCES posts (id)
		ON DELETE CASCADE,

	CONSTRAINT fk_user
		FOREIGN KEY (user_id)
		REFERENCES users (id)
		ON DELETE CASCADE
);
CREATE INDEX idx_comments_post_id ON comments (post_id);
CREATE INDEX idx_comments_user_id ON comments (user_id);


CREATE TABLE likes (
	user_id BIGINT NOT NULL,
	post_id BIGINT NOT NULL,
	created_at TIMESTAMP WITH ZONE DEFAULT CURRENT_TIMESTAMP,

	PRIMARY KEY (user_id,post_id), --COMPOSITE PRIMARY KEY: ensures a user to like a post once

	CONSTRAINT fk_user
		FOREIGN KEY (user_id)
		REFERENCES users (id)
		ON DELETE CASCADE,

	CONSTRAINT fk_post
		FOREIGN KEY (post_id)
		REFERENCES posts (id)
		ON DELETE CASCADE
);