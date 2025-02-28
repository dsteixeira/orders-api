CREATE TABLE user (
    user_id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(45) NOT NULL
);

CREATE TABLE user_order (
    order_id BIGINT NOT NULL PRIMARY KEY,
    order_date DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE user_order_product (
    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (product_id, order_id),
    FOREIGN KEY (order_id) REFERENCES user_order(order_id)
);
