package com.i2.kproject_2025_2.taxi.service;

import com.i2.kproject_2025_2.auth.VerificationCodeUtil;
import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.repository.UserRepository;
import com.i2.kproject_2025_2.taxi.dto.ChatMessageRequest;
import com.i2.kproject_2025_2.taxi.dto.ChatMessageResponse;
import com.i2.kproject_2025_2.taxi.dto.CreateRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.JoinRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.LeaveRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.OperationAcceptResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationAcceptedMember;
import com.i2.kproject_2025_2.taxi.dto.OperationDepartResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationStartResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationStatusResponse;
import com.i2.kproject_2025_2.taxi.dto.RoomResponse;
import com.i2.kproject_2025_2.taxi.dto.SplitConfirmResponse;
import com.i2.kproject_2025_2.taxi.dto.SplitCreateRequest;
import com.i2.kproject_2025_2.taxi.dto.SplitResponse;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoom;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomMember;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomMessage;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomOperationAcceptance;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomSplit;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomSplitPayment;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomMemberRepository;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomMessageRepository;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomOperationAcceptanceRepository;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomRepository;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomSplitPaymentRepository;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaxiRoomService {

    private final TaxiRoomRepository roomRepo;
    private final TaxiRoomMemberRepository memberRepo;
    private final TaxiRoomMessageRepository messageRepo;
    private final TaxiRoomOperationAcceptanceRepository acceptanceRepo;
    private final TaxiRoomSplitRepository splitRepo;
    private final TaxiRoomSplitPaymentRepository splitPaymentRepo;
    private final UserRepository userRepo;

    @Transactional
    public RoomResponse createRoom(String email, CreateRoomRequest req) {
        User leader = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        int capacity = req.capacity() == null ? 0 : req.capacity();
        if (capacity < 2 || capacity > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정원은 2~4명이어야 합니다.");
        }

        // 6자리 숫자 코드 중복 방지 생성
        String code;
        int guard = 0;
        do {
            code = VerificationCodeUtil.numeric6();
            guard++;
            if (guard > 10) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "방 코드 생성 오류");
        } while (roomRepo.existsByRoomCode(code));

        TaxiRoom room = new TaxiRoom();
        room.setRoomCode(code);
        room.setMeetingPoint(req.meetingPoint());
        room.setDestination(req.destination());
        room.setMeetingTime(req.meetingTime());
        room.setCapacity(capacity);
        room.setLeader(leader);
        room.setStatus(TaxiRoom.Status.OPEN);
        roomRepo.save(room);

        // 리더 자동 참여
        TaxiRoomMember m = new TaxiRoomMember();
        m.setRoom(room);
        m.setUser(leader);
        memberRepo.save(m);

        long count = memberRepo.countByRoom_Id(room.getId());
        if (count >= capacity) {
            room.setStatus(TaxiRoom.Status.FULL);
            roomRepo.save(room);
        }

        return toResponse(room, (int) count);
    }

    @Transactional
    public RoomResponse joinRoom(String email, JoinRoomRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(req.roomCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 참여한 방입니다.");
        }

        long count = memberRepo.countByRoom_Id(room.getId());
        if (count >= room.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "정원이 가득 찼습니다.");
        }

        TaxiRoomMember m = new TaxiRoomMember();
        m.setRoom(room);
        m.setUser(user);
        memberRepo.save(m);

        count = memberRepo.countByRoom_Id(room.getId());
        if (count >= room.getCapacity() && room.getStatus() != TaxiRoom.Status.FULL) {
            room.setStatus(TaxiRoom.Status.FULL);
            roomRepo.save(room);
        }

        return toResponse(room, (int) count);
    }

    @Transactional
    public RoomResponse leaveRoom(String email, LeaveRoomRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(req.roomCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (room.getLeader().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "방장은 퇴장할 수 없습니다.");
        }

        TaxiRoomMember member = memberRepo.findByRoom_IdAndUser_Id(room.getId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "참여 중인 방이 아닙니다."));

        memberRepo.delete(member);

        long count = memberRepo.countByRoom_Id(room.getId());
        if (room.getStatus() == TaxiRoom.Status.FULL && count < room.getCapacity()) {
            room.setStatus(TaxiRoom.Status.OPEN);
            roomRepo.save(room);
        }

        return toResponse(room, (int) count);
    }

    @Transactional
    public OperationStartResponse startOperation(String email, String roomCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!room.getLeader().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "방장만 운행을 시작할 수 있습니다.");
        }
        if (room.getOperationStatus() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 운행이 시작되었습니다.");
        }

        acceptanceRepo.deleteByRoom_Id(room.getId());
        room.setOperationStatus(TaxiRoom.OperationStatus.STARTED);
        room.setOperationStartedAt(LocalDateTime.now());
        roomRepo.save(room);

        // 방장은 자동 수락
        TaxiRoomOperationAcceptance leaderAccept = new TaxiRoomOperationAcceptance();
        leaderAccept.setRoom(room);
        leaderAccept.setUser(user);
        acceptanceRepo.save(leaderAccept);

        return new OperationStartResponse(
                room.getRoomCode(),
                room.getOperationStatus().name(),
                room.getOperationStartedAt(),
                "운행이 시작되었습니다."
        );
    }

    @Transactional
    public OperationAcceptResponse acceptOperation(String email, String roomCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (room.getOperationStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "운행이 시작되지 않았습니다.");
        }
        if (room.getOperationStatus() == TaxiRoom.OperationStatus.DEPARTED || room.getOperationStatus() == TaxiRoom.OperationStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 출발했습니다.");
        }
        if (room.getLeader().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "방장은 이미 수락된 상태입니다.");
        }
        if (!memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여 중인 방이 아닙니다.");
        }
        if (acceptanceRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 수락했습니다.");
        }

        TaxiRoomOperationAcceptance accepted = new TaxiRoomOperationAcceptance();
        accepted.setRoom(room);
        accepted.setUser(user);
        TaxiRoomOperationAcceptance saved = acceptanceRepo.save(accepted);

        int totalMembers = (int) memberRepo.countByRoom_Id(room.getId());
        long acceptedCount = acceptanceRepo.countByRoom_Id(room.getId());
        boolean allAccepted = acceptedCount >= totalMembers;
        if (allAccepted && room.getOperationStatus() != TaxiRoom.OperationStatus.ACCEPTED) {
            room.setOperationStatus(TaxiRoom.OperationStatus.ACCEPTED);
            roomRepo.save(room);
        }

        return new OperationAcceptResponse(
                room.getRoomCode(),
                true,
                saved.getAcceptedAt(),
                totalMembers,
                (int) acceptedCount,
                "운행 출발을 수락했습니다."
        );
    }

    @Transactional(readOnly = true)
    public OperationStatusResponse getOperationStatus(String email, String roomCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여 중인 방이 아닙니다.");
        }

        List<OperationAcceptedMember> acceptedMembers = acceptanceRepo.findByRoom(room).stream()
                .sorted(Comparator.comparing(TaxiRoomOperationAcceptance::getAcceptedAt))
                .map(a -> new OperationAcceptedMember(
                        a.getUser().getId(),
                        a.getUser().getName() != null ? a.getUser().getName() : a.getUser().getEmail(),
                        a.getAcceptedAt()
                )).toList();

        int totalMembers = (int) memberRepo.countByRoom_Id(room.getId());
        int acceptedCount = acceptedMembers.size();
        boolean isAllAccepted = totalMembers > 0 && acceptedCount >= totalMembers;

        return new OperationStatusResponse(
                room.getRoomCode(),
                room.getOperationStatus() == null ? null : room.getOperationStatus().name(),
                room.getOperationStartedAt(),
                room.getOperationDepartedAt(),
                room.getOperationEndedAt(),
                totalMembers,
                acceptedMembers,
                acceptedCount,
                isAllAccepted
        );
    }

    @Transactional
    public OperationDepartResponse endOperation(String email, String roomCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!room.getLeader().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "방장만 출발을 확정할 수 있습니다.");
        }
        if (room.getOperationStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "운행이 시작되지 않았습니다.");
        }
        if (room.getOperationStatus() == TaxiRoom.OperationStatus.DEPARTED || room.getOperationStatus() == TaxiRoom.OperationStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 출발이 확정되었습니다.");
        }

        int totalMembers = (int) memberRepo.countByRoom_Id(room.getId());
        long acceptedCount = acceptanceRepo.countByRoom_Id(room.getId());
        if (acceptedCount < totalMembers) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모든 인원이 수락해야 합니다.");
        }

        room.setOperationStatus(TaxiRoom.OperationStatus.DEPARTED);
        room.setOperationDepartedAt(LocalDateTime.now());
        roomRepo.save(room);

        return new OperationDepartResponse(
                room.getRoomCode(),
                room.getOperationStatus().name(),
                room.getOperationDepartedAt(),
                "운행이 출발했습니다."
        );
    }

    @Transactional
    public SplitResponse createSplit(String email, String roomCode, SplitCreateRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!room.getLeader().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "방장만 정산을 생성할 수 있습니다.");
        }
        if (req.totalAmount() == null || req.totalAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "총 금액은 1원 이상이어야 합니다.");
        }
        if (splitRepo.existsByRoom(room)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 정산이 생성되었습니다.");
        }

        List<TaxiRoomMember> members = memberRepo.findByRoom_Id(room.getId());
        if (members.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "참여 인원이 없습니다.");
        }

        int memberCount = members.size();
        Map<String, Integer> individualCosts = calculateIndividualCosts(req.totalAmount(), members);
        int base = req.totalAmount() / memberCount;
        int remainder = req.totalAmount() % memberCount;
        int amountPerPerson = base + (remainder > 0 ? 1 : 0);

        TaxiRoomSplit split = new TaxiRoomSplit();
        split.setRoom(room);
        split.setTotalAmount(req.totalAmount());
        split.setMemberCount(memberCount);
        split.setAmountPerPerson(amountPerPerson);
        split.setStatus(TaxiRoomSplit.Status.PENDING);
        TaxiRoomSplit saved = splitRepo.save(split);

        return new SplitResponse(
                room.getRoomCode(),
                saved.getId(),
                saved.getTotalAmount(),
                saved.getMemberCount(),
                saved.getAmountPerPerson(),
                individualCosts,
                java.util.Collections.emptyList(),
                saved.getCreatedAt(),
                saved.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public SplitResponse getSplit(String email, String roomCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여 중인 방이 아닙니다.");
        }

        TaxiRoomSplit split = splitRepo.findByRoom(room)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "정산이 생성되지 않았습니다."));

        List<TaxiRoomMember> members = memberRepo.findByRoom_Id(room.getId());
        Map<String, Integer> individualCosts = calculateIndividualCosts(split.getTotalAmount(), members);
        List<Long> paidMembers = splitPaymentRepo.findBySplit(split).stream()
                .map(p -> p.getUser().getId())
                .toList();

        return new SplitResponse(
                room.getRoomCode(),
                split.getId(),
                split.getTotalAmount(),
                split.getMemberCount(),
                split.getAmountPerPerson(),
                individualCosts,
                paidMembers,
                split.getCreatedAt(),
                split.getStatus().name()
        );
    }

    @Transactional
    public SplitConfirmResponse confirmSplit(String email, String roomCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여 중인 방이 아닙니다.");
        }

        TaxiRoomSplit split = splitRepo.findByRoom(room)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "정산이 생성되지 않았습니다."));

        if (splitPaymentRepo.existsBySplit_IdAndUser_Id(split.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 송금 완료를 체크했습니다.");
        }

        TaxiRoomSplitPayment payment = new TaxiRoomSplitPayment();
        payment.setSplit(split);
        payment.setUser(user);
        TaxiRoomSplitPayment saved = splitPaymentRepo.save(payment);

        long paidCount = splitPaymentRepo.countBySplit_Id(split.getId());
        int totalMembers = split.getMemberCount();
        boolean allPaid = paidCount >= totalMembers;
        if (allPaid && split.getStatus() != TaxiRoomSplit.Status.COMPLETED) {
            split.setStatus(TaxiRoomSplit.Status.COMPLETED);
            splitRepo.save(split);
        }

        return new SplitConfirmResponse(
                room.getRoomCode(),
                split.getId(),
                user.getId(),
                saved.getPaidAt(),
                allPaid,
                (int) paidCount,
                totalMembers,
                "송금 완료가 확인되었습니다."
        );
    }

    @Transactional
    public ChatMessageResponse sendMessage(String email, String roomCode, ChatMessageRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여 중인 방이 아닙니다.");
        }

        if (req.content() == null || req.content().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메시지를 입력하세요.");
        }

        TaxiRoomMessage msg = new TaxiRoomMessage();
        msg.setRoom(room);
        msg.setSender(user);
        msg.setContent(req.content().trim());
        TaxiRoomMessage saved = messageRepo.save(msg);

        return toChatResponse(saved);
    }

    @Transactional(readOnly = true)
    public java.util.List<ChatMessageResponse> getMessages(String email, String roomCode, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (!memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여 중인 방이 아닙니다.");
        }

        int pageIndex = Math.max(page, 0);
        int pageSize = size <= 0 ? 50 : Math.min(size, 200);
        var messages = messageRepo.findByRoom_RoomCodeOrderByCreatedAtDesc(roomCode, PageRequest.of(pageIndex, pageSize));
        return messages.stream().map(this::toChatResponse).toList();
    }

    private Map<String, Integer> calculateIndividualCosts(int totalAmount, List<TaxiRoomMember> members) {
        int memberCount = members.size();
        int base = totalAmount / memberCount;
        int remainder = totalAmount % memberCount;

        Map<String, Integer> costs = new HashMap<>();
        members.stream()
                .sorted(Comparator.comparing(m -> m.getUser().getId()))
                .forEachOrdered(m -> {
                    int idx = costs.size();
                    int amount = base + (idx < remainder ? 1 : 0);
                    costs.put(String.valueOf(m.getUser().getId()), amount);
                });
        return costs;
    }

    private ChatMessageResponse toChatResponse(TaxiRoomMessage msg) {
        return new ChatMessageResponse(
                msg.getId(),
                msg.getRoom().getRoomCode(),
                msg.getSender().getId(),
                msg.getSender().getEmail(),
                msg.getContent(),
                msg.getCreatedAt()
        );
    }

    private RoomResponse toResponse(TaxiRoom room, int memberCount) {
        return new RoomResponse(
                room.getId(),
                room.getRoomCode(),
                room.getMeetingPoint(),
                room.getDestination(),
                room.getMeetingTime(),
                room.getCapacity(),
                room.getStatus().name(),
                memberCount,
                room.getLeader().getId()
        );
    }
}
