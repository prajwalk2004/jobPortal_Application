CREATE TABLE contacts (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          created_at TIMESTAMP NOT NULL,
                          created_by VARCHAR(20) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          message LONGTEXT NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          status VARCHAR(20) NOT NULL DEFAULT 'NEW',
                          subject VARCHAR(255) NOT NULL,
                          updated_at TIMESTAMP NULL,
                          updated_by VARCHAR(20) NULL,
                          user_type VARCHAR(50) NOT NULL,

                          PRIMARY KEY (id)
);