-- increment_product_field --
DELIMITER //

CREATE PROCEDURE increment_product_field(
    IN p_product_id BIGINT,
    IN p_field_name VARCHAR(50)
)
BEGIN
    SET @sql = CONCAT('UPDATE products SET ', p_field_name, ' = COALESCE(', p_field_name, ', 0) + 1 WHERE product_id = ?');
PREPARE stmt FROM @sql;
EXECUTE stmt USING p_product_id;
DEALLOCATE PREPARE stmt;
END //

DELIMITER ;