package com.soldesk6F.ondal.store.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.soldesk6F.ondal.store.entity.Store;


public interface StoreRepositoryCustom {

	
    Page<Store> searchNearbyStoresByDistance(
            double lon,
            double lat,
            String bbox,
            int radiusMeters,
            String original,
            List<String> bestMatchers,
            Pageable pageable
        );
    
    
    Page<Store> searchNearbyStoresByDistanceWithCategory(
    	    double lon,
    	    double lat,
    	    String bbox,
    	    int radiusMeters,
    	    String original,
    	    List<String> bestMatchers,
    	    String category,            
    	    Pageable pageable
    	);
	
	
}
