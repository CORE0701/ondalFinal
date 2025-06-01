package com.soldesk6F.ondal.search;

import org.springframework.boot.ApplicationRunner;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
// import org.springframework.beans.factory.annotation.Autowired;
// import javax.sql.DataSource;
// import org.springframework.jdbc.core.JdbcTemplate; // (if using JDBC to fetch data)

import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrieInitializer implements ApplicationRunner {

	
	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	
	
    @Override
    public void run(ApplicationArguments args) throws Exception {

        String[] categories = {"한식", "중식", "양식","분식","찜","탕"};
        String[] stores    = {"다운타우너", "엽기 떡볶이", "도미노피자"};
        String[] menus     = {
            "패퍼로니피자", "치즈 피자", "치즈버거",
            "치킨 버거",  "김치찌개", "된장찌개", "순두부찌개", "갈비탕", "설렁탕",
            "육개장", "삼계탕", "불고기", "제육볶음", "닭갈비",
            "비빔밥", "돌솥비빔밥", "잡채", "오징어볶음", "낙지볶음",
            "떡볶이", "순대", "튀김", "김밥", "라면",
            "쫄면", "비빔국수", "칼국수", "잔치국수", "물냉면",
            "비빔냉면", "막국수", "콩국수", "삼겹살", "목살",
            "차돌박이", "갈비", "곱창", "대창", "막창",
            "돼지껍데기", "소고기무국", "감자탕", "닭도리탕", "부대찌개",
            "파전", "해물파전", "전", "빈대떡", "깻잎전",
            "만두", "군만두", "찐만두", "짬뽕", "짜장면",
            "탕수육", "깐풍기", "양장피", "팔보채", "마파두부",
            "볶음밥", "잡채밥", "유산슬", "마라탕", "마라샹궈",
            "훠궈", "양꼬치", "치즈 피자", "페퍼로니 피자", "고르곤졸라 피자",
            "파스타", "토마토 파스타", "크림 파스타", "라자냐", "리조또",
            "스테이크", "햄버거", "치킨 버거", "수제버거", "샌드위치",
            "감자튀김", "핫도그", "샐러드", "시저샐러드", "닭가슴살 샐러드",
            "초밥", "참치 롤", "캘리포니아 롤", "우동", "돈카츠",
            "가츠동", "규동", "에비동", "텐동", "오므라이스",
            "딸기 케이크", "초코 케이크", "치즈 케이크", "아이스크림", "붕어빵",
            "호떡", "팥빙수", "망고빙수", "아메리카노", "카페라떼"
            ,"카레라이스", "소떡소떡", "추어탕", "꽃게탕", "해물탕",
            "아구찜", "닭발", "곱창전골", "비지찌개", "감자전",
            "김치전", "계란말이", "스팸마요덮밥", "옥수수치즈", "오삼불고기",
            "탕평채", "궁중떡볶이", "계란국", "미역국", "북엇국",
            "콩나물국", "우엉조림", "멸치볶음", "고등어구이", "삼치조림",
            "치킨마요덮밥", "팟타이", "똠얌꿍", "나시고랭", "사테",
            "반미", "타코", "부리토", "나초", "오니기리",
            "퀘사디아", "야끼소바", "오코노미야키", "타코야키", "샤브샤브",
            "규카츠", "스키야키", "파에야", "감자그라탕", "소프트프레즐",
            "도넛", "마카롱", "티라미수", "크레페", "브라우니",
            "와플", "푸딩", "에그타르트", "밀크티", "비엔나커피",
            "그린스무디", "버블티", "카푸치노", "피스타치오 젤라토", "프렌치 토스트"
            
        };
        
        List<String> dbStores = storeRepository.findAllStoreNames();
        List<String> dbMenus = menuRepository.findAllMenuNames();
        
        
        // 2. Initialize Trie by inserting all entries via JNI calls.
        for (String cat : categories) {
            trieLib.insertCategory(cat);
        }
        for(String dbStore : dbStores) {
        	trieLib.insertCategory(dbStore);
        }
        for (String store : stores) {
        	trieLib.insertStore(store);
        }
        for(String dbMenu : dbMenus) {
        	trieLib.insertStore(dbMenu.replaceAll("\\(.*?\\)", ""));
        }
        for (String menu : menus) {
        	trieLib.insertMenu(menu);
        }

        


        System.out.println("Trie initialization completed. Sample query 'ㅊ': ");
        String[] suggestions = trieLib.getSearchList("ㅊ");
        for (String sug : suggestions) {
            if (sug == null) break;
            System.out.println(" - " + sug);
        }
    }
}