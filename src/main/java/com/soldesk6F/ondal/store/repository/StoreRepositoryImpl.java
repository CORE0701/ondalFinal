package com.soldesk6F.ondal.store.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.soldesk6F.ondal.store.entity.Store;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class StoreRepositoryImpl implements StoreRepositoryCustom {
	
	
    @PersistenceContext
    private EntityManager entityManager;
	
	
	
    @Override
	public Page<Store> searchNearbyStoresByDistance(
		    double lon,
		    double lat,
		    String bbox,
		    int radiusMeters,
		    String original,
		    List<String> bestMatchers,
		    Pageable pageable
		) {
		    StringBuilder sql = new StringBuilder();
		    sql.append("""
		        SELECT s.*, 
		               ST_Distance_Sphere(
		                   s.store_location, 
		                   ST_SRID(Point(:lon, :lat), 4326)
		               ) AS dist
		        FROM store s
		        WHERE MBRContains(
		                  ST_GeomFromText(:bbox, 4326, 'axis-order=long-lat'), 
		                  s.store_location
		              )
		          AND ST_Distance_Sphere(
		                  s.store_location, 
		                  ST_SRID(Point(:lon, :lat), 4326)
		              ) <= :radius
		          AND (
		                  s.store_name LIKE CONCAT('%', :original, '%')
		    """);

		    // s.store_name LIKE
		    for (int i = 0; i < bestMatchers.size(); i++) {
		        sql.append(" OR s.store_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
		    }

		    // menu 서브쿼리
		    sql.append("""
		          OR EXISTS (
		                SELECT 1
		                FROM menu m
		                WHERE m.store_id = s.store_id
		                  AND (
		                      m.menu_name LIKE CONCAT('%', :original, '%')
		    """);

		    for (int i = 0; i < bestMatchers.size(); i++) {
		        sql.append(" OR m.menu_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
		    }

		    sql.append("""
		                  )
		          )
		        )
		        ORDER BY dist ASC
		    """);

		    Query query = entityManager.createNativeQuery(sql.toString(), Store.class);
		    query.setParameter("lon", lon);
		    query.setParameter("lat", lat);
		    query.setParameter("bbox", bbox);
		    query.setParameter("radius", radiusMeters);
		    query.setParameter("original", original);

		    for (int i = 0; i < bestMatchers.size(); i++) {
		        query.setParameter("bm" + i, bestMatchers.get(i));
		    }

		    // 페이징 처리
		    query.setFirstResult((int) pageable.getOffset());
		    query.setMaxResults(pageable.getPageSize());

		    List<Store> resultList = query.getResultList();

		    // countQuery 조립
		    StringBuilder countSql = new StringBuilder();
		    countSql.append("""
		        SELECT COUNT(*)
		        FROM store s
		        WHERE MBRContains(
		                  ST_GeomFromText(:bbox, 4326, 'axis-order=long-lat'), 
		                  s.store_location
		              )
		          AND ST_Distance_Sphere(
		                  s.store_location, 
		                  ST_SRID(Point(:lon, :lat), 4326)
		              ) <= :radius
		          AND (
		                  s.store_name LIKE CONCAT('%', :original, '%')
		    """);

		    for (int i = 0; i < bestMatchers.size(); i++) {
		        countSql.append(" OR s.store_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
		    }

		    countSql.append("""
		          OR EXISTS (
		                SELECT 1
		                FROM menu m
		                WHERE m.store_id = s.store_id
		                  AND (
		                      m.menu_name LIKE CONCAT('%', :original, '%')
		    """);

		    for (int i = 0; i < bestMatchers.size(); i++) {
		        countSql.append(" OR m.menu_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
		    }

		    countSql.append("""
		                  )
		          )
		        )
		    """);

		    Query countQuery = entityManager.createNativeQuery(countSql.toString());
		    countQuery.setParameter("lon", lon);
		    countQuery.setParameter("lat", lat);
		    countQuery.setParameter("bbox", bbox);
		    countQuery.setParameter("radius", radiusMeters);
		    countQuery.setParameter("original", original);
		    for (int i = 0; i < bestMatchers.size(); i++) {
		        countQuery.setParameter("bm" + i, bestMatchers.get(i));
		    }

		    long total = ((Number) countQuery.getSingleResult()).longValue();
		    return new PageImpl<>(resultList, pageable, total);
		}
    
    @Override
    public Page<Store> searchNearbyStoresByDistanceWithCategory(
            double lon,
            double lat,
            String bbox,
            int radiusMeters,
            String original,
            List<String> bestMatchers,
            String category,
            Pageable pageable
    ) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT s.*, 
                   ST_Distance_Sphere(s.store_location, ST_SRID(Point(:lon, :lat), 4326)) AS dist
            FROM store s
            WHERE MBRContains(ST_GeomFromText(:bbox, 4326, 'axis-order=long-lat'), s.store_location)
              AND ST_Distance_Sphere(s.store_location, ST_SRID(Point(:lon, :lat), 4326)) <= :radius
              AND s.category = :category
              AND (
                    s.store_name LIKE CONCAT('%', :original, '%')
        """);

        // OR 조건 for bestMatchers
        for (int i = 0; i < bestMatchers.size(); i++) {
            sql.append(" OR s.store_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
        }

        // menu 서브쿼리
        sql.append("""
              OR EXISTS (
                    SELECT 1 FROM menu m
                    WHERE m.store_id = s.store_id
                      AND (
                          m.menu_name LIKE CONCAT('%', :original, '%')
        """);

        for (int i = 0; i < bestMatchers.size(); i++) {
            sql.append(" OR m.menu_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
        }

        sql.append("""
                      )
              )
            )
            ORDER BY dist ASC
        """);

        Query query = entityManager.createNativeQuery(sql.toString(), Store.class);
        query.setParameter("lon", lon);
        query.setParameter("lat", lat);
        query.setParameter("bbox", bbox);
        query.setParameter("radius", radiusMeters);
        query.setParameter("original", original);
        query.setParameter("category", category);
        for (int i = 0; i < bestMatchers.size(); i++) {
            query.setParameter("bm" + i, bestMatchers.get(i));
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Store> resultList = query.getResultList();

        // countQuery
        StringBuilder countSql = new StringBuilder();
        countSql.append("""
            SELECT COUNT(*)
            FROM store s
            WHERE MBRContains(ST_GeomFromText(:bbox, 4326, 'axis-order=long-lat'), s.store_location)
              AND ST_Distance_Sphere(s.store_location, ST_SRID(Point(:lon, :lat), 4326)) <= :radius
              AND s.category = :category
              AND (
                    s.store_name LIKE CONCAT('%', :original, '%')
        """);

        for (int i = 0; i < bestMatchers.size(); i++) {
            countSql.append(" OR s.store_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
        }

        countSql.append("""
              OR EXISTS (
                    SELECT 1 FROM menu m
                    WHERE m.store_id = s.store_id
                      AND (
                          m.menu_name LIKE CONCAT('%', :original, '%')
        """);

        for (int i = 0; i < bestMatchers.size(); i++) {
            countSql.append(" OR m.menu_name LIKE CONCAT('%', :bm").append(i).append(", '%')");
        }

        countSql.append("""
                      )
              )
            )
        """);

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        countQuery.setParameter("lon", lon);
        countQuery.setParameter("lat", lat);
        countQuery.setParameter("bbox", bbox);
        countQuery.setParameter("radius", radiusMeters);
        countQuery.setParameter("original", original);
        countQuery.setParameter("category", category);
        for (int i = 0; i < bestMatchers.size(); i++) {
            countQuery.setParameter("bm" + i, bestMatchers.get(i));
        }

        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PageImpl<>(resultList, pageable, total);
    }

}
