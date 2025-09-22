package com.letzgo.LetzgoBe.global.initData.utils;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentRequest;
import com.letzgo.LetzgoBe.domain.community.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentUtils {
    private final CommentService commentService;

    public void createSampleCommentsForPosts(List<Member> members) {
        createCommentsForPost1(members);
        createCommentsForPost2(members);
        createCommentsForPost3(members);
        createCommentsForPost4(members);
        createCommentsForPost5(members);
    }

    private void createCommentsForPost1(List<Member> members) {
        Long postId = 1L;
        Member user1 = members.get(0);
        Member user2 = members.get(1);
        Member user3 = members.get(2);
        Member user4 = members.get(3);
        Member user5 = members.get(4);

        addComment(postId, "더현대서울 진짜 갈 때마다 힐링되는 느낌이에요.. 다음엔 꼭 카페 투어 후기 올려주세요!", user2);
        addComment(postId, "햄버거 패티 두툼한 거 완전 취향인데요? 담에 친구들이랑 갈 때 참고해야겠어요ㅋㅋ", user2);
        addReply(postId, 2L, "완전 추천해요!! 고기 육즙 장난 아니고 소스 조합이 진짜 찰떡이에요👍 주말에 가시면 햇살 좋은 시간에 가보세요, 분위기까지 더 좋아요☺️", user1);

        addComment(postId, "햇살 가득한 실내라니 상상만 해도 기분 좋아지네요🌞", user3);
        addComment(postId, "다운타운버거 소스 찰떡이라는 말에 바로 검색해봤어요ㅋㅋ 다음 서울 나들이 때 코스에 추가!", user3);
        addReply(postId, 5L, "오 친구들이랑 가면 더 즐거우실 듯요ㅎㅎ 패티 진짜 두툼해서 한 입 베어물 때 식감 최고였어요! 맛있게 드시고 오세요😄", user1);

        addComment(postId, "쇼핑도 좋지만 이렇게 여유롭게 즐기는 게 더 현대서울의 매력인 것 같아요ㅎㅎ 글만 읽어도 힐링되는 느낌!", user4);
        addComment(postId, "햄버거 좋아하는데 다운타운버거는 아직 못 가봤네요ㅠ 담에 꼭 가봐야겠어요 추천 감사해요👍", user4);

        addComment(postId, "더현대서울 사진마다 분위기 다 다른데 햇살 좋은 날은 진짜 따뜻해 보이네요☺️ 다음에 저도 노려볼게요!", user5);
        addComment(postId, "저도 햄버거 덕후라서 다운타운버거 계속 찜만 해놓고 못 갔는데 이번엔 꼭 가야겠어요ㅋㅋ", user5);
    }

    private void createCommentsForPost2(List<Member> members) {
        Long postId = 2L;
        Member user1 = members.get(0);
        Member user2 = members.get(1);
        Member user3 = members.get(2);
        Member user4 = members.get(3);
        Member user5 = members.get(4);

        addComment(postId, "와 송도 센트럴파크 날씨 좋을 때 가면 진짜 힐링이죠🌳 포스코타워 사진도 멋질 것 같아요!", user1);
        addComment(postId, "GTS햄버거 저도 예전에 먹었는데 소스 조합 인정ㅋㅋ 또 생각나네요", user1);
        addReply(postId, 12L, "헉 그렇게 맛있어요? 저도 담에 가면 꼭 먹어봐야겠어요ㅋㅋ 추천 감사해요👍", user2);

        addComment(postId, "오 저도 송도 가면 산책 코스로 센트럴파크 고정이에요ㅋㅋ 공원 분위기 최고죠☺️", user3);
        addComment(postId, "햄버거 두툼하다니 완전 제 취향인데요? 다음 송도 나들이 때 무조건 들러야겠어요ㅋㅋ", user3);
        addReply(postId, 15L, "ㅋㅋ 저도 지금 바로 지도 검색해봤어요! 우리 다음에 같이 햄버거 투어 갈까요?", user2);

        addComment(postId, "푸른 하늘이랑 포스코타워 조합이라니 벌써부터 사진 궁금해지네요.. 가보고 싶다!", user4);
        addComment(postId, "오리배 타는 것도 재밌어요ㅎㅎ 다음엔 꼭 체험해보세요👍", user4);

        addComment(postId, "힐링 제대로 하고 오셨네요☺️ 저도 요즘 바람 쐬고 싶었는데 송도 뽐뿌 오네요ㅋㅋ", user5);
        addComment(postId, "GTS햄버거 소문만 들었는데 이렇게 또 추천받으니 더 궁금해졌어요! 다음에 가봐야겠어요.", user5);
    }

    private void createCommentsForPost3(List<Member> members) {
        Long postId = 3L;
        Member user1 = members.get(0);
        Member user2 = members.get(1);
        Member user3 = members.get(2);
        Member user4 = members.get(3);
        Member user5 = members.get(4);

        addComment(postId, "주문진 해수욕장 진짜 숨은 힐링 스팟이죠🌊 바다 바람 맞으면서 걷는 거 최고!", user1);
        addComment(postId, "회에 대게에 해물라면이라니... 이 조합은 못 참죠ㅋㅋ 담에 강릉 가면 꼭 이렇게 먹어야겠어요😋", user1);
        addReply(postId, 22L, "ㅋㅋㅋ 완전 공감요! 저도 사진만 봐도 침 고이더라구요… 우리 다음에 강릉 같이 가요!", user3);

        addComment(postId, "파도 소리 들으면서 힐링이라니 부럽네요ㅠ 저도 당장 바다 가고 싶어졌어요..", user2);
        addComment(postId, "해산물 파티에 소주 한 잔까지 완벽 그 자체ㅋㅋ 다음엔 저도 친구들이랑 코스로 따라가야겠어요!", user2);
        addReply(postId, 25L, "ㅋㅋㅋㅋ 찐 먹방 투어네요! 진짜 다음번에 다 같이 가면 꿀잼일 듯요😂", user3);

        addComment(postId, "강릉 바다 뷰는 언제 봐도 힐링이죠… 사진 봤는데 너무 예쁘더라구요!", user4);
        addComment(postId, "해물라면에 바다 보면서 소주라니 분위기 미쳤다ㅠ 저도 가고 싶어요!!", user4);

        addComment(postId, "이 글 보니까 갑자기 강릉행 기차표 예매하고 싶어지네요ㅋㅋ 완전 취향저격 여행코스!", user5);
        addComment(postId, "싱싱한 회에 대게까지… 바다 보면서 먹으면 진짜 꿀맛일 것 같아요🤤 추천 감사합니다!", user5);
    }

    private void createCommentsForPost4(List<Member> members) {
        Long postId = 4L;
        Member user1 = members.get(0);
        Member user2 = members.get(1);
        Member user3 = members.get(2);
        Member user4 = members.get(3);
        Member user5 = members.get(4);

        addComment(postId, "엘시티 진짜 외관부터 압도적이죠.. 저도 부산 갔을 때 계속 올려다봤던 기억 나네요ㅎㅎ", user1);
        addComment(postId, "전망대 뷰 미쳤다… 바다랑 도시가 한눈에 보이는 그 느낌 저도 다시 느끼고 싶어요ㅠㅠ", user1);
        addReply(postId, 32L, "진짜요ㅠ 저도 전망대 사진 볼 때마다 가고 싶어져요ㅋㅋ 우리 이번 여름에 같이 가볼까요?😆", user4);

        addComment(postId, "해운대 풍경은 언제 봐도 시원하고 멋져요🌊 전망대에서 보면 또 다른 매력일 듯!", user2);
        addComment(postId, "부산은 도시랑 바다가 같이 있는 게 매력인 듯요ㅋㅋ 저도 엘시티 꼭 가봐야겠어요!", user2);
        addReply(postId, 35L, "ㅋㅋㅋ 맞아요 부산만의 그 vibe! 엘시티 진짜 추천해요 뷰 보고 감탄하게 될 걸요", user4);

        addComment(postId, "엘시티 전망대 가면 사진 몇백 장은 기본이죠ㅋㅋ 풍경이 너무 예쁘니까요.", user3);
        addComment(postId, "바다랑 도시 조합은 진짜 부산만의 매력… 다음에 가면 저도 꼭 들러야겠어요!", user3);

        addComment(postId, "엘시티 외관 사진만 봐도 웅장하네요ㅋㅋ 다음 부산 여행 때 코스에 추가해야겠어요!", user5);
        addComment(postId, "해운대 바다뷰는 매번 봐도 새롭고 좋죠☺️ 전망대에서 보면 더 감동일 듯요!", user5);
    }

    private void createCommentsForPost5(List<Member> members) {
        Long postId = 5L;
        Member user1 = members.get(0);
        Member user2 = members.get(1);
        Member user3 = members.get(2);
        Member user4 = members.get(3);
        Member user5 = members.get(4);

        addComment(postId, "더클리프 뷰 진짜 힐링 그 자체죠🌊 저도 제주 가면 꼭 들르는 코스예요ㅎㅎ", user1);
        addComment(postId, "고기국수에 편육 조합은 못 참죠ㅋㅋ 제주 가면 무조건 먹어야 하는 메뉴!!😋", user1);
        addReply(postId, 42L, "ㅋㅋㅋ 인정이요! 고기국수는 진짜 제주 가야 제맛 나는 것 같아요ㅠ 같이 먹으러 가요!", user5);

        addComment(postId, "바다 바라보면서 여유롭게 시간 보내는 거 완전 로망… 사진만 봐도 힐링돼요ㅎㅎ", user2);
        addComment(postId, "국수바다 고기국수 맛있다던데 부럽네요ㅋㅋ 저도 다음 제주 여행 때 리스트에 추가해야겠어요!", user2);
        addReply(postId, 45L, "꼭 가세요ㅋㅋ 저도 갔었는데 국물 진짜 깊고 고소해서 계속 생각나요🤤 추천드려요!", user5);

        addComment(postId, "제주 바다랑 고기국수라니 완벽한 조합이네요ㅋㅋ 보는 것만으로도 여행 욕구 폭발!", user3);
        addComment(postId, "편육까지 곁들이면 그건 진짜 제주 스타일… 저도 빨리 다시 가고 싶어졌어요ㅠ", user3);

        addComment(postId, "더클리프에서 보는 바다 사진 너무 예쁘더라구요☺️ 이번에 제주 가면 저도 가볼래요!", user4);
        addComment(postId, "제주 느낌 가득한 여행 코스네요ㅎㅎ 다음 휴가 때 참고해야겠어요!", user4);
    }

    private void addComment(Long postId, String content, Member member) {
        CommentRequest form = CommentRequest.builder().content(content).build();
        commentService.addComment(postId, form, LoginUserDto.ConvertToLoginUserDto(member));
    }

    private void addReply(Long postId, Long superCommentId, String content, Member member) {
        CommentRequest form = CommentRequest.builder().content(content).superCommentId(superCommentId).build();
        commentService.addComment(postId, form, LoginUserDto.ConvertToLoginUserDto(member));
    }
}
