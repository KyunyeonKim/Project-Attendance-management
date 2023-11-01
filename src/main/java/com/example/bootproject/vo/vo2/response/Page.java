package com.example.bootproject.vo.vo2.response;

import lombok.Data;

//        페이지네이션을 위한 쿼리 파라미터 목록 : page=int, sort=string, desc=boolean
@Data
public class Page<T> {
    //제네릭으로 여러 데이터 타입을 처리
    private T data;
    //사이즈는 10으로 고정
    private int size = 10;
    //다음 페이지가 남았는지
    private boolean hasNext;
    //정렬 대상 컬럼 이름
    private String sort;
    //내림차순 적용 유무
    private String desc;
    //현재 페이지 번호
    private int page;
    //전체 요소 개수
    private int totalElement;
}