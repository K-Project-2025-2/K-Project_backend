package com.i2.kproject_2025_2.taxi.service;

import com.i2.kproject_2025_2.auth.VerificationCodeUtil;
import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.repository.UserRepository;
import com.i2.kproject_2025_2.taxi.dto.CreateRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.JoinRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.RoomResponse;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoom;
import com.i2.kproject_2025_2.taxi.entity.TaxiRoomMember;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomMemberRepository;
import com.i2.kproject_2025_2.taxi.repository.TaxiRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TaxiRoomService {

    private final TaxiRoomRepository roomRepo;
    private final TaxiRoomMemberRepository memberRepo;
    private final UserRepository userRepo;

    @Transactional
    public RoomResponse createRoom(String principalName, CreateRoomRequest req) {
        User leader = userRepo.findByUsername(principalName)
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
    public RoomResponse joinRoom(String principalName, JoinRoomRequest req) {
        User user = userRepo.findByUsername(principalName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        TaxiRoom room = roomRepo.findByRoomCode(req.roomCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."));

        if (memberRepo.existsByRoom_IdAndUser_Id(room.getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 참여한 방입니다.");
        }

        long count = memberRepo.countByRoom_Id(room.getId());
        if (count >= room.getCapacity()) {
            room.setStatus(TaxiRoom.Status.FULL);
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

