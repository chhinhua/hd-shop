-- set sku sold value
UPDATE product_skus ps
SET ps.sold = (
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM order_items oi
    WHERE oi.sku_id = ps.sku_id
);


-- set sku price
UPDATE product_skus
SET price = ROUND(original_price - (original_price * percent_discount / 100));

-- set sku percent_discount
UPDATE product_skus AS sku
SET percent_discount = (
    SELECT p.percent_discount
    FROM products p
    WHERE sku.product_id = p.product_id
)
WHERE percent_discount IS NULL;

--

