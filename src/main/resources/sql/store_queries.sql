DELIMITER $$

CREATE PROCEDURE find_nearby_stores(
    IN userId BIGINT,
    IN lat DOUBLE,
    IN lng DOUBLE,
    IN radius DOUBLE,
    IN category VARCHAR(255),
    IN keyword VARCHAR(255)
)
BEGIN
SELECT s.*,
       ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(lng, lat)) AS distance
FROM wallet w
         JOIN voucher_ownership vo ON w.id = vo.wallet_id
         JOIN voucher_store vs ON vo.voucher_id = vs.voucher_id
         JOIN store s ON vs.store_id = s.id
WHERE w.user_id = userId
  AND ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(lng, lat)) <= radius
  AND (category IS NULL OR s.category = category)
  AND (keyword IS NULL OR s.name LIKE CONCAT('%', keyword, '%') OR s.address LIKE CONCAT('%', keyword, '%'))
ORDER BY distance;
END$$

DELIMITER ;