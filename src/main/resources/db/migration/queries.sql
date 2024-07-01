-- set sku sold value
UPDATE product_skus ps
SET ps.sold = (
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM order_items oi
    WHERE oi.sku_id = ps.sku_id
);


-- set sku quantity