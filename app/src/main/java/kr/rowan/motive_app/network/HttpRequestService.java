package kr.rowan.motive_app.network;

import com.google.gson.JsonObject;

import kr.rowan.motive_app.network.dto.AddRelationRequest;
import kr.rowan.motive_app.network.dto.EmailRequest;
import kr.rowan.motive_app.network.dto.FamilyLoginRequest;
import kr.rowan.motive_app.network.dto.FamilyRegistrationRequest;
import kr.rowan.motive_app.network.dto.FamilyWithDrawalRequest;
import kr.rowan.motive_app.network.dto.FindInstitutionRequest;
import kr.rowan.motive_app.network.dto.GetAllMedalInfoRequest;
import kr.rowan.motive_app.network.dto.GetParentsInfoRequest;
import kr.rowan.motive_app.network.dto.GetUserAlarmRequest;
import kr.rowan.motive_app.network.dto.GetUserScheduleRequest;
import kr.rowan.motive_app.network.dto.GroupCodeRequest;
import kr.rowan.motive_app.network.dto.LoginRequest;
import kr.rowan.motive_app.network.dto.LogoutRequest;
import kr.rowan.motive_app.network.dto.PutMedalSelectResultRequest;
import kr.rowan.motive_app.network.dto.PutProfileImageRequest;
import kr.rowan.motive_app.network.dto.RegistrationRequest;
import kr.rowan.motive_app.network.dto.RegistrationTokenRequest;
import kr.rowan.motive_app.network.dto.RemoveUserFamilyRelationRequest;
import kr.rowan.motive_app.network.dto.SearchPswdRequest;
import kr.rowan.motive_app.network.dto.UpdatePswdRequest;
import kr.rowan.motive_app.network.dto.UploadVideoRequest;
import kr.rowan.motive_app.network.dto.UserIdRequest;
import kr.rowan.motive_app.network.dto.UserPhoneRequest;
import kr.rowan.motive_app.network.dto.WithDrawalRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpRequestService {

    String URL = "http://175.125.21.68:8811"; //서버
    //String URL = "http://192.168.0.28:8080"; //로컬

    //common
    /**
     *
     * @param emailRequest 이메일
     * @return 암호화 된 인증번호
     */
    @POST("/common/authEmail")
    Call<JsonObject> authEmailRequest(@Body EmailRequest emailRequest);

    /**
     *
     * @param emailRequest 이메일
     * @return 암호화 된 인증번호, 아이디
     */
    @POST("/common/searchId")
    Call<JsonObject> searchIdRequest(@Body EmailRequest emailRequest);

    /**
     *
     * @param searchPswdRequest 이메일, 아이디
     * @return 임시 비밀번호 발송
     */
    @POST("/common/searchPswd")
    Call<JsonObject> searchPswdRequest(@Body SearchPswdRequest searchPswdRequest);

    /**
     *
     * @param updatePswdRequest 비밀번호 변경
     * @return 가입 여부
     */
    @POST("/common/updatePswd")
    Call<JsonObject> updatePswdRequest(@Body UpdatePswdRequest updatePswdRequest);

    /**
     *
     * @param putProfileImageRequest 프로필 사진 업로드 요청
     * @return 등록 여부
     */
    @POST("/common/putProfileImage")
    Call<JsonObject> putProfileImageRequest(@Body PutProfileImageRequest putProfileImageRequest);

    /**
     *
     * @param groupCodeRequest 그룹코드
     * @return 그룹 정보 및 환자 정보
     */
    @POST("/common/getGroupInfo")
    Call<JsonObject> getGroupInfoRequest(@Body GroupCodeRequest groupCodeRequest);

    /**
     *
     * @param logoutRequest 로그아웃 정보
     * @return 로그아웃
     */
    @POST("/common/logout")
    Call<JsonObject> logOut(@Body LogoutRequest logoutRequest);

    /**
     *
     * @param addRelationRequest 추가 가족 정보
     * @return 가족 추가
     */
    @POST("/common/addRelation")
    Call<JsonObject> addRelationRequest(@Body AddRelationRequest addRelationRequest);

    /**
     *
     * @param removeUserFamilyRelationRequest 삭제 가족 정보
     * @return 가족 삭제
     */
    @POST("/common/removeUserFamilyRelation")
    Call<JsonObject> removeUserFamilyRelationRequest(@Body RemoveUserFamilyRelationRequest removeUserFamilyRelationRequest);



    //family
    /**
     *
     * @param familyLoginRequest 아이디, 패스워드
     * @return 가족 로그인
     */
    @POST("/family/login")
    Call<JsonObject> familyLoginRequest(@Body FamilyLoginRequest familyLoginRequest);

    /**
     *
     * @param familyRegistrationRequest 가족 회원가입 정보
     * @return 가입 여부
     */
    @POST("/family/registration")
    Call<JsonObject> familyRegistrationRequest(@Body FamilyRegistrationRequest familyRegistrationRequest);

    /**
     *
     * @param getParentsInfoRequest 부양목록 요청
     * @return ok or error
     */
    @POST("/family/getParentsInfo")
    Call<JsonObject> getParentsInfoRequest(@Body GetParentsInfoRequest getParentsInfoRequest);

    /**
     *
     * @param uploadVideoRequest 영상 업로드 요청
     * @return 업로드 성공 여부
     */
    @POST("/video/uploadVideo")
    Call<JsonObject> uploadVideoRequest(@Body UploadVideoRequest uploadVideoRequest);

    /**
     *
     * @param familyWithDrawalRequest 가족 회원 탈퇴
     * @return 가입 여부
     */
    @POST("/family/withDrawal")
    Call<JsonObject> familyWithDrawalRequest(@Body FamilyWithDrawalRequest familyWithDrawalRequest);





    //members
    /**
     *
     * @param loginRequest 아이디, 패스워드
     * @return 회원 로그인
     */
    @POST("/members/login")
    Call<JsonObject> loginRequest(@Body LoginRequest loginRequest);

    /**
     *
     * @param userIdRequest 중복아이디 검색
     * @return 가입 여부
     */
    @POST("/members/searchDuplicatedId")
    Call<JsonObject> searchDuplicatedIdRequest(@Body UserIdRequest userIdRequest);

    /**
     *
     * @param registrationRequest 회원가입 정보
     * @return 가입 여부
     */
    @POST("/members/registration")
    Call<JsonObject> registrationRequest(@Body RegistrationRequest registrationRequest);

    /**
     *
     * @param withDrawalRequest 회원 탈퇴
     * @return 가입 여부
     */
    @POST("/members/withDrawal")
    Call<JsonObject> withDrawalRequest(@Body WithDrawalRequest withDrawalRequest);

    /**
     *
     * @param userPhoneRequest 핸드폰 번호로 검색
     * @return 회원 정보
     */
    @POST("/members/search")
    Call<JsonObject> userPhoneRequest(@Body UserPhoneRequest userPhoneRequest);

    /**
     *
     * @param getUserScheduleRequest 회원 스케줄 검색
     * @return 회원 스케줄 정보
     */
    @POST("/members/getUserScheduleList")
    Call<JsonObject> getUserScheduleRequest(@Body GetUserScheduleRequest getUserScheduleRequest);

    /**
     *
     * @param userIdRequest 아이디로 검색
     * @return 회원 정보
     */
    @POST("/members/getVideoList")
    Call<JsonObject> getVideoListRequest(@Body UserIdRequest userIdRequest);


    /**
     *
     * @param putMedalSelectResultRequest 메달 요청하기
     * @return 회원 정보
     */
    @POST("/members/putMedalSelectResult")
    Call<JsonObject> putMedalSelectResultRequest(@Body PutMedalSelectResultRequest putMedalSelectResultRequest);





    //organization
    /**
     *
     * @param findInstitutionRequest 기관 관리자 이름
     * @return 관리자 담당 기관 목록
     */
    @POST("/organization/getOrganizationList")
    Call<JsonObject> getOrganizationListRequest(@Body FindInstitutionRequest findInstitutionRequest);

    //token
    /**
     *
     * @param registrationTokenRequest 기기 토근
     * @return ok or error
     */
    @POST("/token/registration")
    Call<JsonObject> registrationTokenRequest(@Body RegistrationTokenRequest registrationTokenRequest);


    //alarm
    /**
     *
     * @param getUserAlarmRequest 유저아이디
     * @return 알람시간
     */
    @POST("/alarm/getUserAlarm")
    Call<JsonObject> getUserAlarmRequest(@Body GetUserAlarmRequest getUserAlarmRequest);


    //all medal info
    /**
     *
     * @param getAllMedalInfoRequest 유저아이디, 그룹코드
     * @return 유저 메달정보
     */
    @POST("/members/getMedalInfo")
    Call<JsonObject> getAllMedalInfoRequest(@Body GetAllMedalInfoRequest getAllMedalInfoRequest);
}