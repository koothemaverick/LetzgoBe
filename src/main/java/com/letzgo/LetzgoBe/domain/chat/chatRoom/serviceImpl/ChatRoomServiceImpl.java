package com.letzgo.LetzgoBe.domain.chat.chatRoom.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.MessageContent;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.repository.ChatMessageRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.repository.MessageContentRepository;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.MemberInfoDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomPage;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.repository.ChatRoomMemberRepository;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.repository.ChatRoomRepository;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageService chatMessageService;
    private final MessageContentRepository messageContentRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    // 사용자의 모든 채팅방 조회
    @Override
    @Transactional
    public Page<ChatRoomDto> getChatRoom(Pageable pageable, LoginUserDto loginUser) {
        checkPageSize(pageable.getPageSize());
        Page<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMemberOrderByLatestMessage(pageable, loginUser.ConvertToMember());
        return chatRooms.map(this::convertToChatRoomDto);
    }

    // 채팅방 생성(DM/그룹)
    @Override
    @Transactional
    public ChatRoomDto addChatRoom(ChatRoomForm chatRoomForm, LoginUserDto loginUser) {
        // 제한 인원 초과 여부 확인 (본인 제외)
        if (chatRoomForm.getChatRoomMembers().size() > ChatRoom.ROOM_MEMBER_LIMIT - 1) {
            throw new ServiceException(ReturnCode.CHATROOM_LIMIT_EXCEEDED);
        }

        // 1대1 채팅방 중복 방지 로직
        if (chatRoomForm.getChatRoomMembers().size() == 1) { // 1대1 채팅인지 확인
            Long otherMemberId = chatRoomForm.getChatRoomMembers().get(0).getMember().getId();

            // 현재 사용자가 otherMember와 이미 1대1 채팅방이 존재하는지 확인
            boolean exists = chatRoomRepository.existsOneOnOneChatRoom(loginUser.getId(), otherMemberId);
            if (exists) {
                throw new ServiceException(ReturnCode.CHATROOM_ALREADY_EXISTS);
            }
        }

        // 새로운 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .title(chatRoomForm.getTitle())
                .member(loginUser.ConvertToMember()) // 방장 지정
                .build();
        // 본인을 맨 앞에 추가
        List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

        ChatRoomMember enterChatRoomMyself = ChatRoomMember.builder()
                .member(loginUser.ConvertToMember())
                .chatRoom(chatRoom)
                .build();
        chatRoomMembers.add(enterChatRoomMyself);

        // 추가하려는 멤버들 중 중복되지 않는 멤버만 추가
        Set<Long> addedMemberIds = new HashSet<>();
        addedMemberIds.add(loginUser.getId()); // 본인 ID 추가
        for (ChatRoomMember chatRoomMember : chatRoomForm.getChatRoomMembers()) {
            Long memberId = chatRoomMember.getMember().getId();
            if (!addedMemberIds.contains(memberId)) { // 중복 방지
                ChatRoomMember memberInChatRoom = ChatRoomMember.builder()
                        .member(chatRoomMember.getMember())
                        .chatRoom(chatRoom)
                        .build();
                chatRoomMembers.add(memberInChatRoom);
                //중복 체크용
                addedMemberIds.add(memberId);
            } else {
                throw new ServiceException(ReturnCode.CHATROOM_ALREADY_EXISTS);
            }
        }
        chatRoom.setChatRoomMembers(chatRoomMembers);
        chatRoomRepository.save(chatRoom);
        return convertToChatRoomDto(chatRoom);
    }

    // 채팅방 이름 수정(그룹)
    @Override
    @Transactional
    public void updateChatRoomTitle(Long chatRoomId, ChatRoomForm chatRoomForm, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));

        // 채팅방 멤버 누구나 수정 가능함
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }
        chatRoom.setTitle(chatRoomForm.getTitle());
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방에 초대(그룹)
    @Override
    @Transactional
    public void inviteChatRoomMember(Long chatRoomId, ChatRoomForm chatRoomForm, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        // 채팅방 멤버 누구나 초대 가능함
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }
        // 제한 인원 초과 여부 확인 (본인 제외)
        if (chatRoomForm.getChatRoomMembers().size() > ChatRoom.ROOM_MEMBER_LIMIT - 1) {
            throw new ServiceException(ReturnCode.CHATROOM_LIMIT_EXCEEDED);
        }
        // 초대할 멤버가 기존 멤버인지 확인
        boolean hasExistingMember = chatRoomForm.getChatRoomMembers().stream()
                .anyMatch(newMember -> chatRoom.getChatRoomMembers().stream()
                        .anyMatch(existingMember -> existingMember.getMember().getId().equals(newMember.getMember().getId())));
        if (hasExistingMember) {
            throw new ServiceException(ReturnCode.MEMBER_ALREADY_EXISTS);
        }

        // 초대할 멤버 추가(기존 멤버 아닌 경우에만 추가)
        List<ChatRoomMember> newMembers = chatRoomForm.getChatRoomMembers().stream()
                .filter(newMember -> chatRoom.getChatRoomMembers().stream()
                        .noneMatch(existingMember -> existingMember.getMember().getId().equals(newMember.getMember().getId())))
                .peek(newMember -> newMember.setChatRoom(chatRoom)) // chatRoom 설정
                .collect(Collectors.toList());
        chatRoom.getChatRoomMembers().addAll(newMembers);
        chatRoomRepository.save(chatRoom);
    }

    // 방장 권한 위임(그룹)
    @Override
    @Transactional
    public void delegateChatRoomManager(Long chatRoomId, ChatRoomForm chatRoomForm, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        // 방장인지 확인 (방장만 위임 가능)
        if (!chatRoom.getMember().getId().equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 현재 채팅방에 참여 중인 멤버 ID 리스트
        List<Long> joinMemberIds = chatRoom.getChatRoomMembers().stream()
                .map(member -> member.getMember().getId())
                .collect(Collectors.toList());

        // 위임할 멤버 목록 검증 (1명만 가능)
        List<ChatRoomMember> delegateMembers = chatRoomForm.getChatRoomMembers();
        if (delegateMembers.size() != 1) {
            throw new ServiceException(ReturnCode.INVALID_DELEGATE_MEMBER);
        }
        Long delegateMemberId = delegateMembers.get(0).getMember().getId();

        // 방장은 자기자신을 위임하지 못함
        if (delegateMemberId.equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.INVALID_DELEGATE_MEMBER);
        }

        // 위임 대상이 채팅방 참여자가 맞는지 검증
        if (!joinMemberIds.contains(delegateMemberId)) {
            throw new ServiceException(ReturnCode.INVALID_DELEGATE_MEMBER);
        }

        Member newManager = memberRepository.findById(delegateMemberId)
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
        chatRoom.setMember(newManager);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방에서 강퇴(그룹)
    @Override
    @Transactional
    public void kickOutChatRoomMember(Long chatRoomId, ChatRoomForm chatRoomForm, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        // 방장인지 확인 (방장만 강퇴 가능)
        if (!chatRoom.getMember().getId().equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 현재 채팅방에 참여 중인 멤버 ID 리스트
        List<Long> joinMemberIds = chatRoom.getChatRoomMembers().stream()
                .map(member -> member.getMember().getId())
                .collect(Collectors.toList());

        // 강퇴할 멤버 목록 가져오기
        List<Long> kickMemberIds = chatRoomForm.getChatRoomMembers().stream()
                .map(member -> member.getMember().getId())
                .collect(Collectors.toList());

        // 방장은 자기자신을 강퇴하지 못함
        if (kickMemberIds.contains(loginUser.getId())) {
            throw new ServiceException(ReturnCode.INVALID_KICK_MEMBER);
        }

        // 강퇴 대상이 채팅방 참여자가 맞는지 검증
        List<Long> invalidKickMembers = kickMemberIds.stream()
                .filter(id -> !joinMemberIds.contains(id)) // 기존 멤버에 없는 ID 필터링
                .collect(Collectors.toList());
        if (!invalidKickMembers.isEmpty()) {
            throw new ServiceException(ReturnCode.INVALID_KICK_MEMBER);
        }

        chatRoom.getChatRoomMembers().removeIf(existingMember ->
                kickMemberIds.contains(existingMember.getMember().getId()));
        // 강퇴 후 남은 인원이 2명 미만이면 채팅방 삭제
        if (chatRoom.getChatRoomMembers().size() < 2) {
            chatMessageService.deleteAllChatMessages(chatRoomId);
            chatRoomRepository.delete(chatRoom);
        } else {
            chatRoomRepository.save(chatRoom);
        }
    }

    // 채팅방 나가기(DM/그룹)
    @Override
    @Transactional
    public void leaveChatRoomMember(Long chatRoomId, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        // 현재 멤버가 속한 ChatRoomMember 찾기
        ChatRoomMember chatRoomMember = chatRoom.getChatRoomMembers().stream()
                .filter(member -> member.getMember().getId().equals(loginUser.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
        chatRoom.getChatRoomMembers().remove(chatRoomMember);  // ChatRoomMember 제거

        // 방장인지 확인
        if (chatRoom.getMember().getId().equals(loginUser.getId())) {
            if (chatRoom.getChatRoomMembers().size() > 1) { // 2명 이상 남아있으면 다음 방장 지정
                ChatRoomMember nextOwner = chatRoom.getChatRoomMembers().get(0);
                chatRoom.setMember(nextOwner.getMember());
            } else { // 1명 미만(0명)이면 채팅방 삭제
                chatMessageService.deleteAllChatMessages(chatRoomId);
                chatRoomRepository.delete(chatRoom);
                return;
            }
        } else if (chatRoom.getChatRoomMembers().size() < 2) { // 방장이 아닌데 나갔을 때 1명이 되면 삭제
            chatMessageService.deleteAllChatMessages(chatRoomId);
            chatRoomRepository.delete(chatRoom);
            return;
        }
        chatRoomRepository.save(chatRoom);
    }

    // 멤버가 참여중인 모든 채팅방 나가기(DM/그룹)
    @Override
    @Transactional
    public void leaveAllChatRooms(Long memberId){
        List<ChatRoom> chatRooms = chatRoomRepository.findByMemberId(memberId);
        for (ChatRoom chatRoom : chatRooms) {
            // 현재 멤버가 속한 ChatRoomMember 찾기
            ChatRoomMember chatRoomMember = chatRoom.getChatRoomMembers().stream()
                    .filter(member -> member.getMember().getId().equals(memberId))
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
            chatRoom.getChatRoomMembers().remove(chatRoomMember);  // ChatRoomMember 제거
            chatRoomMemberRepository.delete(chatRoomMember);

            // 방장인지 확인
            if (chatRoom.getMember().getId().equals(memberId)) {
                if (chatRoom.getChatRoomMembers().size() > 1) { // 2명 이상 남아있으면 다음 방장 지정
                    ChatRoomMember nextOwner = chatRoom.getChatRoomMembers().get(0);
                    // 영속 상태 보장
                    Member persistedMember = memberRepository.findById(nextOwner.getMember().getId())
                            .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
                    chatRoom.setMember(persistedMember);
                } else { // 1명 미만(0명)이면 채팅방 삭제
                    chatMessageService.deleteAllChatMessages(chatRoom.getId());
                    chatRoomRepository.delete(chatRoom);
                    continue;
                }
            } else if (chatRoom.getChatRoomMembers().size() < 2) { // 방장이 아닌데 나갔을 때 1명이 되면 삭제
                chatMessageService.deleteAllChatMessages(chatRoom.getId());
                chatRoomRepository.delete(chatRoom);
                continue;
            }
            chatRoomRepository.save(chatRoom);
        }
    }

    // 요청 페이지 수 제한
    private void checkPageSize(int pageSize) {
        int maxPageSize = ChatRoomPage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // ChatRoom을 ChatRoomDto로 변환
    private ChatRoomDto convertToChatRoomDto(ChatRoom chatRoom) {
        // Fetch Join으로 가져온 ChatRoomMembers 사용
        List<ChatRoomMember> chatRoomMembers = chatRoomRepository.findChatRoomMembersWithMember(chatRoom.getId());

        // 가장 최근 메시지의 ID 조회
        Optional<Long> latestMessageId = chatMessageRepository.findLatestMessageIdByChatRoomId(chatRoom.getId());

        // 해당 메시지 ID로 MongoDB에서 메시지 내용 조회
        String lastMessage = latestMessageId
                .flatMap(id -> messageContentRepository.findById(id.toString())) // MongoDB에서 메시지 조회
                .map(MessageContent::getContent) // content 값 가져오기
                .orElse(""); // 메시지가 없으면 빈 문자열 반환

        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .memberId(chatRoom.getMember().getId())
                .title(chatRoom.getTitle())
                .memberCount(chatRoomMembers.size()) // 여기서 변경
                .chatRoomMembers(chatRoomMembers.stream() // 여기서도 변경
                        .map(chatRoomMember -> MemberInfoDto.builder()
                                .userId(chatRoomMember.getMember().getId())
                                .userNickname(chatRoomMember.getMember().getNickname()) // 이제 정상적으로 가져올 수 있음
                                .profileImageUrl(chatRoomMember.getMember().getProfileImageUrl())
                                .build()
                        )
                        .collect(Collectors.toList()))
                .lastMessage(lastMessage)
                .build();
    }
}
