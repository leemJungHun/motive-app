package com.example.motive_app.network;

import com.example.motive_app.network.DTO.EmailRequest;
import com.example.motive_app.network.DTO.FamilyLoginRequest;
import com.example.motive_app.network.DTO.FamilyRegistrationRequest;
import com.example.motive_app.network.DTO.FamilyWithDrawalRequest;
import com.example.motive_app.network.DTO.FindInstitutionRequest;
import com.example.motive_app.network.DTO.GetParentsInfoRequest;
import com.example.motive_app.network.DTO.GetUserAlarmRequest;
import com.example.motive_app.network.DTO.GetUserScheduleRequest;
import com.example.motive_app.network.DTO.LoginRequest;
import com.example.motive_app.network.DTO.PutProfileImageRequest;
import com.example.motive_app.network.DTO.RegistrationRequest;
import com.example.motive_app.network.DTO.RegistrationTokenRequest;
import com.example.motive_app.network.DTO.SearchPswdRequest;
import com.example.motive_app.network.DTO.UpdatePswdRequest;
import com.example.motive_app.network.DTO.UploadVideoRequest;
import com.example.motive_app.network.DTO.UserIdRequest;
import com.example.motive_app.network.DTO.UserPhoneRequest;
import com.example.motive_app.network.DTO.WithDrawalRequest;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpRequestService {

    String URL = "http://192.168.0.28:8080";

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
}