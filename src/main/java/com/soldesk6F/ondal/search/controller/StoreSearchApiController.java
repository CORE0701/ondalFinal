package com.soldesk6F.ondal.search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soldesk6F.ondal.search.StoreSearchService;
import com.soldesk6F.ondal.search.StoreSortType;
import com.soldesk6F.ondal.store.entity.StoreDto;

import lombok.RequiredArgsConstructor;

@RestController // JSON 반환을 위해 RestController 또는 @ResponseBody 사용
@RequiredArgsConstructor
public class StoreSearchApiController {

    private final StoreSearchService storeSearchService;

    @GetMapping("/search/api/storeInRadius")
    public List<StoreDto> searchStoreInRadius(
            @RequestParam("orignal") String originalSearchQuery,
            @RequestParam("bestMatchers") List<String> bestMatcher,
            @RequestParam("category") String category,
            @RequestParam("orderBy") String orderBy
    ) {
    	
//    	List<StoreDto> listSt = storeSearchService.searchByRadiusWithCond(
//				5000,
//				originalSearchQuery,
//				bestMatcher,
//				StoreSortType.DISTANCE,
//				0,
//				20,
//				category
//				);
//    	if(listSt==null) {
//    	
//		System.out.println("널입니다");
//    	
//    	}else {
//    	
//    		for(StoreDto sd : listSt) {
//    			System.out.println("식당이름:" + sd.getStoreName());
//    		}
//    		
//    		
//    	}
    		return storeSearchService.searchByRadiusWithCond(
    				4000,
    				originalSearchQuery,
    				bestMatcher,
    				StoreSortType.DISTANCE,
    				0,
    				20,
    				category
    				);
    			
    	}
    	
    	
    }
	
	
	
	

