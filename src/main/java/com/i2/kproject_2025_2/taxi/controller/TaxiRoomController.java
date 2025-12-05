package com.i2.kproject_2025_2.taxi.controller;

import com.i2.kproject_2025_2.taxi.dto.CreateRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.JoinRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.LeaveRoomRequest;
import com.i2.kproject_2025_2.taxi.dto.RoomResponse;
import com.i2.kproject_2025_2.taxi.dto.ChatMessageRequest;
import com.i2.kproject_2025_2.taxi.dto.ChatMessageResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationAcceptResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationDepartResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationStartResponse;
import com.i2.kproject_2025_2.taxi.dto.OperationStatusResponse;
import com.i2.kproject_2025_2.taxi.dto.SplitConfirmResponse;
import com.i2.kproject_2025_2.taxi.dto.SplitCreateRequest;
import com.i2.kproject_2025_2.taxi.dto.SplitResponse;
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

    @Operation(summary = "모든 택시 방 목록 조회", description = """
            생성된 모든 택시 방 목록을 조회합니다.
            """)
    @GetMapping
    public ResponseEntity<java.util.List<RoomResponse>> getAllRooms(Principal principal) {
        java.util.List<RoomResponse> res = taxiRoomService.getAllRooms(principal.getName());
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "내가 속한 택시 방 목록 조회", description = """
            현재 내가 참여 중인 택시 방들을 조회합니다.
            """)
    @GetMapping("/my")
    public ResponseEntity<java.util.List<RoomResponse>> getMyRooms(Principal principal) {
        java.util.List<RoomResponse> res = taxiRoomService.getMyRooms(principal.getName());
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

    @Operation(summary = "운행 시작", description = """
            방장이 운행을 시작합니다.
            - 방장만 호출할 수 있습니다.
            - 운행 시작 시 방장은 자동으로 수락된 것으로 처리됩니다.
            """)
    @PostMapping("/{roomCode}/operation/start")
    public ResponseEntity<OperationStartResponse> startOperation(Principal principal,
                                                                 @PathVariable String roomCode) {
        OperationStartResponse res = taxiRoomService.startOperation(principal.getName(), roomCode);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "운행 수락", description = """
            팀원이 운행 출발을 수락합니다.
            - 방장은 자동으로 수락되므로 호출할 수 없습니다.
            - 운행 시작 이후에만 수락할 수 있습니다.
            """)
    @PostMapping("/{roomCode}/operation/accept")
    public ResponseEntity<OperationAcceptResponse> acceptOperation(Principal principal,
                                                                   @PathVariable String roomCode) {
        OperationAcceptResponse res = taxiRoomService.acceptOperation(principal.getName(), roomCode);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "운행 상태 조회", description = """
            운행 상태와 수락 현황을 조회합니다.
            - 동일 방에 참여 중인 사용자만 조회할 수 있습니다.
            """)
    @GetMapping("/{roomCode}/operation/status")
    public ResponseEntity<OperationStatusResponse> getOperationStatus(Principal principal,
                                                                      @PathVariable String roomCode) {
        OperationStatusResponse res = taxiRoomService.getOperationStatus(principal.getName(), roomCode);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "운행 출발 확정", description = """
            모든 팀원이 수락한 뒤, 방장이 출발을 확정합니다.
            - 방장만 호출할 수 있습니다.
            """)
    @PostMapping("/{roomCode}/operation/end")
    public ResponseEntity<OperationDepartResponse> endOperation(Principal principal,
                                                                @PathVariable String roomCode) {
        OperationDepartResponse res = taxiRoomService.endOperation(principal.getName(), roomCode);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "정산 생성", description = """
            방장이 총 택시비를 입력하여 정산을 생성합니다.
            - 이미 정산이 존재하면 생성할 수 없습니다.
            """)
    @PostMapping("/{roomCode}/split")
    public ResponseEntity<SplitResponse> createSplit(Principal principal,
                                                     @PathVariable String roomCode,
                                                     @RequestBody @Valid SplitCreateRequest req) {
        SplitResponse res = taxiRoomService.createSplit(principal.getName(), roomCode, req);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "정산 조회", description = """
            현재 방의 정산 정보를 조회합니다.
            - 동일 방에 참여 중인 사용자만 조회할 수 있습니다.
            """)
    @GetMapping("/{roomCode}/split")
    public ResponseEntity<SplitResponse> getSplit(Principal principal,
                                                  @PathVariable String roomCode) {
        SplitResponse res = taxiRoomService.getSplit(principal.getName(), roomCode);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "송금 완료 체크", description = """
            사용자가 자신의 송금 완료를 체크합니다.
            - 모든 참여자가 체크하면 정산 상태가 COMPLETED로 변경됩니다.
            """)
    @PostMapping("/{roomCode}/split/confirm")
    public ResponseEntity<SplitConfirmResponse> confirmSplit(Principal principal,
                                                             @PathVariable String roomCode) {
        SplitConfirmResponse res = taxiRoomService.confirmSplit(principal.getName(), roomCode);
        return ResponseEntity.ok(res);
    }
}
