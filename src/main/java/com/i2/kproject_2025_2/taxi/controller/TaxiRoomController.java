package com.i2.kproject_2025_2.taxi.controller;

import com.i2.kproject_2025_2.taxi.dto.CreateRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.JoinRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.LeaveRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.RoomResponse;
import com.i2.kproject_2025_2.taxi.dto.ChatMessageRequest;
import com.i2.kproject_2025_2.taxi.dto.ChatMessageResponse;
import com.i2.kproject_2025_2.taxi.service.TaxiRoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/taxi/rooms")
@RequiredArgsConstructor
public class TaxiRoomController {

    private final TaxiRoomService taxiRoomService;

    @Operation(summary = "택시 합승 방 생성", description = """
            새로운 택시 합승 방을 생성합니다.
            - 요청한 사용자는 자동으로 방장이 됩니다.
            - 정원은 2명 이상 4명 이하여야 합니다.
            """)
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(Principal principal,
                                                   @RequestBody @Valid CreateRoomRequest req) {
        RoomResponse res = taxiRoomService.createRoom(principal.getName(), req);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "택시 합승 방 참여", description = """
            기존에 생성된 택시 합승 방에 참여합니다.
            - 방 코드를 통해 참여할 방을 지정합니다.
            - 방이 가득 찼거나, 이미 참여한 방일 경우 에러가 발생합니다.
            """)
    @PostMapping("/join")
    public ResponseEntity<RoomResponse> joinRoom(Principal principal,
                                                 @RequestBody @Valid JoinRoomRequest req) {
        RoomResponse res = taxiRoomService.joinRoom(principal.getName(), req);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "택시 합승 방 퇴장", description = """
            참여 중인 택시 합승 방에서 나갑니다.
            - 방장은 퇴장할 수 없습니다.
            - 방 코드로 지정하며, 퇴장 후 정원이 남으면 상태가 OPEN으로 갱신됩니다.
            """)
    @PostMapping("/leave")
    public ResponseEntity<RoomResponse> leaveRoom(Principal principal,
                                                  @RequestBody @Valid LeaveRoomRequest req) {
        RoomResponse res = taxiRoomService.leaveRoom(principal.getName(), req);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "택시 합승 방 채팅 전송", description = """
            동일 방 코드에 참여 중인 사용자만 메시지를 전송할 수 있습니다.
            - roomCode는 경로 변수로 전달합니다.
            - 본문 content는 비어 있을 수 없습니다.
            """)
    @PostMapping("/{roomCode}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(Principal principal,
                                                           @PathVariable String roomCode,
                                                           @RequestBody @Valid ChatMessageRequest req) {
        ChatMessageResponse res = taxiRoomService.sendMessage(principal.getName(), roomCode, req);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "택시 합승 방 채팅 조회", description = """
            동일 방 코드에 참여 중인 사용자만 메시지를 조회할 수 있습니다.
            - 최신 메시지부터 페이지네이션으로 반환됩니다.
            - page는 0부터 시작, size 기본 50 (최대 200).
            """)
    @GetMapping("/{roomCode}/messages")
    public ResponseEntity<java.util.List<ChatMessageResponse>> getMessages(Principal principal,
                                                                           @PathVariable String roomCode,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "50") int size) {
        java.util.List<ChatMessageResponse> res = taxiRoomService.getMessages(principal.getName(), roomCode, page, size);
        return ResponseEntity.ok(res);
    }
}
