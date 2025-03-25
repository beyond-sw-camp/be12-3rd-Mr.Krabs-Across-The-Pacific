package com.example.atp_back.user;

import com.example.atp_back.common.BaseResponse;
import com.example.atp_back.portfolio.model.response.PortfolioInstanceResp;
import com.example.atp_back.portfolio.model.response.PortfolioPageResp;
import com.example.atp_back.user.model.User;
import com.example.atp_back.user.model.request.SignupReq;
import com.example.atp_back.user.model.follow.response.FolloweeResp;
import com.example.atp_back.user.model.follow.response.FollowerResp;
import com.example.atp_back.user.model.follow.request.UserFollowReq;
import com.example.atp_back.user.model.request.UserUpdateReq;
import com.example.atp_back.user.model.response.UserInfoResp;
import com.example.atp_back.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name="회원 기능", description = "회원 가입, 다른 사용자 팔로우 등 회원 관련 기능들")
public class UserController {
    private final UserService userService;

    @Operation(summary="회원가입", description = "회원 가입을 합니다")
    @ApiResponse(responseCode="200", description="정상가입, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="가입 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<String>> signup(
            @Parameter(description="SignupReq 데이터 전송 객체를 사용합니다")
            @Valid
            @RequestBody SignupReq request) {
        userService.RegisterUser(request);
        return ResponseEntity.ok(BaseResponse.success("가입 성공"));
    }

    @Operation(summary="유저 정보 조회", description = "자신의 유저 정보를 가져옵니다")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="403", description="허가되지 않은 유저 정보 조회 행위입니다")
    @GetMapping("/mypage")
    public ResponseEntity<BaseResponse<UserInfoResp>> getUserInformation(@AuthenticationPrincipal User user) {
        UserInfoResp result = userService.getUserInfo(user.getEmail());
        return ResponseEntity.ok(BaseResponse.success(result));
    }


    @Operation(summary="사용자 팔로우", description="사용자 간 팔로우 기능")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="잘못된 팔로우 양식입니다")
    @PostMapping("/follow")
    public ResponseEntity<BaseResponse<String>> follow(
            @Parameter(description="UserFollowReq 데이터 전송 객체를 사용합니다")
            @Valid
            @RequestBody UserFollowReq reqBody,
            @AuthenticationPrincipal User user) {
        userService.follow(reqBody.getEmail(), user.getEmail());
        return ResponseEntity.ok(BaseResponse.<String>success("팔로우 성공"));
    }

    @Operation(summary="팔로워 조회", description="나를 팔로우 중인 사람 조회")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="잘못된 요청 양식입니다")
    @GetMapping("/follower")
    public ResponseEntity<BaseResponse<FollowerResp>> getFollowers(@AuthenticationPrincipal User user) {
        FollowerResp result = userService.getFollowers(user.getEmail());
        return ResponseEntity.ok(BaseResponse.<FollowerResp>success(result));
    }
    @Operation(summary="팔로우 중 조회", description="내가 팔로우 중인 사람 조회")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="잘못된 요청 양식입니다")
    @GetMapping("/followee")
    public ResponseEntity<BaseResponse<FolloweeResp>> getFollowees(@AuthenticationPrincipal User user) {
        FolloweeResp result = userService.getFollowees(user.getEmail());
        return ResponseEntity.ok(BaseResponse.<FolloweeResp>success(result));
    }


    @Operation(summary="언팔로우", description="사용자 간 언팔로우 기능")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="잘못된 언팔로우 양식입니다")
    @PostMapping("/unfollow")
    public ResponseEntity<BaseResponse<String>> unfollow(
            @Parameter(description="UserFollowReq 데이터 전송 객체를 사용합니다")
            @Valid
            @RequestBody UserFollowReq reqBody,
            @AuthenticationPrincipal User user) {
        userService.unfollow(reqBody.getEmail(), user.getEmail());
        return ResponseEntity.ok(BaseResponse.<String>success("언팔로우 성공"));
    }
    
    @Operation(summary="회원 정보 수정", description="회원 정보 일부를 업데이트합니다.")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="회원 정보 갱신에 실패했습니다")
    @PutMapping("/update")
    public ResponseEntity<BaseResponse<String>> update(
            @Parameter(description="UserUpdateReq 데이터 전송 객체를 사용합니다")
            @RequestBody UserUpdateReq request,
            @AuthenticationPrincipal User user) {
        userService.UpdateUser(request, user.getEmail());
        return ResponseEntity.ok(BaseResponse.<String>success("회원 정보 갱신 성공"));
    }

    @Operation(summary="로그아웃 리다이렉션", description="로그아웃 리다이렉션용 임시 URL")
    @ApiResponse(responseCode="200", description="정상적으로 반환합니다. 'result'의 메세지는 '로그아웃에 성공했습니다' 문자열로 고정됩니다")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> successfulLogout() {
        return ResponseEntity.ok(BaseResponse.<String>success( "로그아웃에 성공했습니다"));
    }

    @Operation(summary="내 포트폴리오 조회(페이징)", description="내가 작성한 포트폴리오의 특정 페이지 목록 조회")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="잘못된 요청 양식입니다")
    @ApiResponse(responseCode="404", description="요청하신 리소스를 찾을 수 없습니다.")
    @GetMapping("/portfolio/{pagenum}")
    public ResponseEntity<BaseResponse<PortfolioPageResp>> getMyPortfolio(@AuthenticationPrincipal User user, @PathVariable Integer pagenum) {
        if (pagenum == null) pagenum = 0;
        PortfolioPageResp result = userService.getMyPortfolio(user, pagenum);
        return ResponseEntity.ok(BaseResponse.<PortfolioPageResp>success(result));
    }
    @Operation(summary="내 포트폴리오 조회", description="내가 작성한 포트폴리오 목록 조회")
    @ApiResponse(responseCode="200", description="정상적으로 반환하였습니다")
    @ApiResponse(responseCode="400", description="잘못된 요청 양식입니다")
    @ApiResponse(responseCode="404", description="요청하신 리소스를 찾을 수 없습니다.")
    @GetMapping("/portfolio")
    public ResponseEntity<BaseResponse<PortfolioPageResp>> getMyPortfolio(@AuthenticationPrincipal User user) {
        log.info("요청한 자: {}", user.getIdx());
        PortfolioPageResp result = userService.getMyPortfolio(user, 0);
        return ResponseEntity.ok(BaseResponse.<PortfolioPageResp>success(result));
    }

}