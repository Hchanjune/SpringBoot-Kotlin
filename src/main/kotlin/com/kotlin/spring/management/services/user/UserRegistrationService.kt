package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.configurations.security.SecurityConfiguration
import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserRegistrationForm
import com.kotlin.spring.management.repositories.mappers.user.UserRegistrationMapper
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *     UserRegistrationForm
 *     @Schema(description = "아이디")
 *     var id: String,
 *     @Schema(description = "평문 비밀번호")
 *     var password : String,
 *     @Schema(description = "평문 비밀번호 확인")
 *     var passwordCheck: String,
 *     @Schema(description = "이름")
 *     var name: String,
 *     @Schema(description = "회사")
 *     var company: String?,
 *     @Schema(description = "직책")
 *     var position: String?,
 *     @Schema(description = "전화번호")
 *     var phone: String,
 *     @Schema(description = "이메일")
 *     var email: String
 */

@Service
class UserRegistrationService(
    private val userBasicService: UserBasicService,
    private val userRegistrationMapper: UserRegistrationMapper
) {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    // Regex List
    companion object {
        // 아이디 :
        private val REGEX_ID = Regex("^[a-z][a-z0-9]{3,19}$")
        // 비밀번호 : 최소 10자리 이상의 영어 대문자, 소문자, 숫자, 특수문자 중 2종류 조합
        private val REGEX_PASSWORD = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}|(?=.*[A-Za-z])(?=.*[^A-Za-z\\d])[A-Za-z\\W]{10,}|(?=.*\\d)(?=.*[^A-Za-z\\d])\\d[\\W\\d]{9,}|(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d\\W]{10,}$")
        // 이름 : 최소 1글자 이상의 한글문자
        private val REGEX_NAME = Regex("""[\uAC00-\uD7A3]+""")
        // 전화번호 : 02로 시작시 8글자이상, 그 이외의 경우 9글자 이상 의 숫자
        private val REGEX_PHONE_NUMBER = Regex("""^(02\d{6,}|0\d{8,})$""")
        // 이메일 : example@example.com 과 같은 이메일 형식
        private val REGEX_EMAIL = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    @Transactional
    fun registerNewUser(
        processingUtil: ProcessingUtil,
        registrationForm: UserRegistrationForm
    ): ServiceResponse<Boolean> {
        if (this.validateUserRegistrationForm(processingUtil, registrationForm).extractStatus()) {
            processingUtil.addFunction(
                "사용자 정보 DB 저장",
                {
                    userRegistrationMapper.insertNewUser(registrationForm) != null
                },
                false
            ).let { process->
                if (!process) {
                    return ServiceResponse.simpleStatus(
                        "UserRegistrationService - registerNewUser",
                        {false},
                        null,
                        "사용자 정보를 DB에 저장 하는도중 오류가 발생하였습니다.",
                        processingUtil
                    )
                }
            }
            processingUtil.addFunction(
                "비밀번호 암호화 후 DB 저장",
                {
                    (userRegistrationMapper.insertNewUserCredentials(registrationForm.id, passwordEncoder.encode(registrationForm.password))== 1)
                },
                false
            ).let { process->
                if (!process) {
                    return ServiceResponse.simpleStatus(
                        "UserRegistrationService - registerNewUser",
                        {false},
                        null,
                        "사용자 비밀번호를 암호화 하여 DB에 저장 하는도중 오류가 발생하였습니다.",
                        processingUtil
                    )
                }
            }
            processingUtil.addFunction(
                "비밀번호 만료일 DB 설정",
                {
                    userBasicService.extendUserPasswordExpiration(registrationForm.id).extractStatus(false)
                },
                false
            ).let { process->
                if (!process) {
                    return ServiceResponse.simpleStatus(
                        "UserRegistrationService - registerNewUser",
                        {false},
                        null,
                        "비밀번호 만료일을 DB에 저장 하는도중 오류가 발생하였습니다.",
                        processingUtil
                    )
                }
            }
            processingUtil.addFunction(
                "유저 권한 DB 저장",
                {
                    (userRegistrationMapper.insertNewUserRoleGuest(registrationForm.id) == 1)

                },
                false
            ).let { process->
                if (!process) {
                    return ServiceResponse.simpleStatus(
                        "UserRegistrationService - registerNewUser",
                        {false},
                        null,
                        "유저 권한을 DB에 저장하는 도중 오류가 발생하였습니다.",
                        processingUtil
                    )
                }
            }
        }
        return ServiceResponse.simpleStatus(
            "UserRegistrationService - registerNewUser",
            {
                processingUtil.compile(true)
            },
            "성공적으로 등록 되었습니다.",
            "사용자 등록 중 오류가 발생하였습니다."
        )
    }


    private fun validateUserRegistrationForm(
        processingUtil: ProcessingUtil,
        registrationForm: UserRegistrationForm
    ): ServiceResponse<Boolean> {

        // ID Duplication Check
        processingUtil.addFunction(
            "아이디 중복 체크",
            processFunction = {
                !userBasicService.isUserExistsInDatabase(registrationForm.id).extractStatus()
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "아이디 중복 체크 중 오류가 발생하였습니다.",
                    processingUtil
                )
            }
        }

        // ID Validation Check
        processingUtil.addFunction(
            "아이디 유효성 체크",
            processFunction = {
                registrationForm.id.matches(REGEX_ID)
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "아이디 작성 규칙을 만족하지 못했습니다.",
                    processingUtil
                )
            }
        }

        // Password & Password Check Equals
        processingUtil.addFunction(
            "비밀번호 값, 비밀번호 확인 값 일치",
            processFunction = {
                registrationForm.password == registrationForm.passwordCheck
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "비밀번호와 비밀번호 확인값이 일치 하지 않습니다.",
                    processingUtil
                )
            }
        }

        // Password Validation Check
        processingUtil.addFunction(
            "비밀번호 정규식 만족",
            processFunction = {
                registrationForm.password.matches(REGEX_PASSWORD)
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "비밀번호 작성 조건이 일치하지 않습니다.",
                    processingUtil
                )
            }
        }

        // Name Validation Check
        processingUtil.addFunction(
            "이름 정규식 만족",
            processFunction = {
                registrationForm.name.matches(REGEX_NAME)
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "이름은 1글자 이상의 한글 문자여야 합니다.",
                    processingUtil
                )
            }
        }

        // Company
        processingUtil.addFunction(
            "회사 설정 추후 적용",
            processFunction = {
                false
            },
            true
        )

        // Position
        processingUtil.addFunction(
            "직책 설정 추후 적용",
            processFunction = {
                false
            },
            true
        )

        // Phone
        processingUtil.addFunction(
            "전화번호 정규식 만족",
            processFunction = {
                registrationForm.phone.matches(REGEX_PHONE_NUMBER)
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "전화번호의 경우 02로 시작하는 8자리이상 혹은 그외 9자리 이상의 숫자이며 하이픈을 제외하여야 합니다.",
                    processingUtil
                )
            }
        }

        // E-Mail Validation Check
        processingUtil.addFunction(
            "이메일 정규식 만족",
            processFunction = {
                registrationForm.email.matches(REGEX_EMAIL)
            },
            false
        ).let { process->
            if (!process) {
                return ServiceResponse.simpleStatus(
                    "UserRegistrationService - validateUserRegistrationForm",
                    {false},
                    null,
                    "올바른 이메일 형식이 아닙니다.",
                    processingUtil
                )
            }
        }

        return if (processingUtil.compile()) {
            ServiceResponse.simpleStatus(
                "UserRegistrationService - validateUserRegistrationForm",
                {true}
            )
        } else {
            ServiceResponse.simpleStatus(
                "UserRegistrationService - validateUserRegistrationForm",
                {false}
            )
        }
    }



}