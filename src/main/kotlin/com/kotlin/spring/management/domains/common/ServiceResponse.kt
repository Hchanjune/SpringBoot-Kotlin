package com.kotlin.spring.management.domains.common

import com.kotlin.spring.management.annotations.NoArgsConstructor
import com.kotlin.spring.management.domains.common.apiResponse.ResponseAdapter
import com.kotlin.spring.management.domains.common.apiResponse.ResponseVo
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.mybatis.spring.MyBatisSystemException
import org.slf4j.LoggerFactory
import org.springframework.ui.Model

@NoArgsConstructor
class ServiceResponse<T>(
    val serviceName: String? = null,
    val status: String? = null,
    val message: String? = null,
    val errorDetail: String? = null,
    val data: T? = null,
    private val booleanAble: Boolean = false
): ResponseAdapter {

    companion object {

        private val logger = LoggerFactory.getLogger(ServiceResponse::class.java)

        fun <T> generateData(
            serviceName: String? = null,
            dataSupplier: () -> T?,
            customSuccessMessage: String? = null,
            customFailureMessage: String? = null,
            processingUtil: ProcessingUtil? = null
        ) : ServiceResponse<T> {
            return try {
                val data = dataSupplier()
                if (data != null){
                    ServiceResponse(
                        serviceName = serviceName ?: "Service Name Not Defined",
                        status = "success",
                        message = customSuccessMessage ?: "Data Successfully Loaded.",
                        errorDetail = null,
                        data = data
                    )
                } else {
                    processingUtil?.discard("Failed To Proceed The Process - $customFailureMessage")
                    ServiceResponse(
                        serviceName = serviceName ?: "Service Name Not Defined",
                        status = "fail",
                        message = customFailureMessage ?: "Failed To Load Data.",
                        errorDetail = null,
                        data = null
                    )
                }

            } catch (e: MyBatisSystemException) {
                processingUtil?.discard(e.localizedMessage,)
                ServiceResponse(
                    serviceName = serviceName ?: "Service Name Not Defined",
                    status = "error",
                    message = "MyBatis Error Occurred Check The Queries",
                    errorDetail = e.localizedMessage,
                    data = null
                )
            } catch (e: Exception) {
                processingUtil?.discard(e.localizedMessage,)
                ServiceResponse(
                    serviceName = serviceName ?: "Service Name Not Defined",
                    status = "error",
                    message = customFailureMessage ?: "Error Occurred While Processing Data",
                    errorDetail = e.localizedMessage,
                    data = null
                )
            }
        }

        fun <Boolean> simpleFalse(
            serviceName: String? = null,
            customFailureMessage: String? = null,
        ) : ServiceResponse<kotlin.Boolean> {
            return ServiceResponse(
                serviceName = serviceName ?: "Service Name Not Defined",
                status = "error",
                message = customFailureMessage ?: "Error Occurred While Processing Data",
                errorDetail = customFailureMessage,
                data = false
            )
        }

        fun <T> simpleError(
            serviceName: String? = null,
            customFailureMessage: String? = null,
        ) : ServiceResponse<T> {
            return ServiceResponse(
                serviceName = serviceName ?: "Service Name Not Defined",
                status = "error",
                message = customFailureMessage ?: "Error Occurred While Processing Data",
                errorDetail = customFailureMessage,
                data = null
            )
        }

        fun <T> badRequest(
            serviceName: String? = null,
            customFailureMessage: String? = null,
        ) : ServiceResponse<T> {
            return ServiceResponse(
                serviceName = serviceName ?: "Service Name Not Defined",
                status = "bad_request",
                message = customFailureMessage ?: "Error Occurred While Processing Data",
                errorDetail = customFailureMessage,
                data = null
            )
        }

        fun <Boolean> simpleStatus(
            serviceName: String? = null,
            dataSupplier: () -> Boolean?,
            customSuccessMessage: String? = null,
            customFailureMessage: String? = null,
            processingUtil: ProcessingUtil? = null
        ): ServiceResponse<Boolean> {
            return try {
                val data = dataSupplier()
                if (data != null && data == true){
                    ServiceResponse(
                        serviceName = serviceName,
                        status = "success",
                        message = customSuccessMessage ?: "The Processing Logic Successfully Managed",
                        errorDetail = null,
                        data = data,
                        booleanAble = true
                    )
                } else {
                    processingUtil?.discard(customFailureMessage ?: "Failed To Proceed The Process")
                    ServiceResponse(
                        serviceName = serviceName,
                        status = "fail",
                        message = customFailureMessage ?: "Failed To Proceed The Process",
                        errorDetail = null,
                        data = data,
                        booleanAble = true
                    )
                }
            }
            catch (e: Exception){
                e.message?.let { processingUtil?.discard(it) }
                ServiceResponse(
                    serviceName = serviceName,
                    status = "error",
                    message = customFailureMessage ?: "Error Occurred While Processing Logic",
                    errorDetail = e.message,
                    data = null,
                    booleanAble = true
                )
            }
        }
    }


    /**
     *  Object Functions (Starts With 'return')
     */
    fun <T> returnData(
        dataSupplier: () -> T?,
        customSuccessMessage: String? = null,
        customFailureMessage: String? = null,
        processingUtil: ProcessingUtil? = null
    ) : ServiceResponse<T> {
        return try {
            val data = dataSupplier()
            if (data != null){
                ServiceResponse(
                    serviceName = serviceName ?: "Service Name Not Defined",
                    status = "success",
                    message = customSuccessMessage ?: "Data Successfully Loaded.",
                    errorDetail = null,
                    data = data
                )
            } else {
                processingUtil?.discard(customFailureMessage ?: "Failed To Proceed The Process")
                ServiceResponse(
                    serviceName = serviceName ?: "Service Name Not Defined",
                    status = "fail",
                    message = customFailureMessage ?: "Failed To Load Data.",
                    errorDetail = null,
                    data = null
                )
            }

        } catch (e: MyBatisSystemException) {
            processingUtil?.discard(e.localizedMessage,)
            ServiceResponse(
                serviceName = serviceName ?: "Service Name Not Defined",
                status = "error",
                message = "MyBatis Error Occurred Check The Queries",
                errorDetail = e.localizedMessage,
                data = null
            )
        } catch (e: Exception) {
            processingUtil?.discard(e.localizedMessage,)
            ServiceResponse(
                serviceName = serviceName ?: "Service Name Not Defined",
                status = "error",
                message = customFailureMessage ?: "Error Occurred While Processing Data",
                errorDetail = e.localizedMessage,
                data = null
            )
        }
    }

    fun <Boolean> returnStatus(
        dataSupplier: () -> Boolean?,
        customSuccessMessage: String? = null,
        customFailureMessage: String? = null,
        processingUtil: ProcessingUtil? = null
    ): ServiceResponse<Boolean> {
        return try {
            val data = dataSupplier()
            if (data != null && data == true){
                ServiceResponse(
                    serviceName = serviceName,
                    status = "success",
                    message = customSuccessMessage ?: "The Processing Logic Successfully Managed",
                    errorDetail = null,
                    data = data,
                    booleanAble = true
                )
            } else {
                processingUtil?.discard(customFailureMessage ?: "Failed To Proceed The Process")
                ServiceResponse(
                    serviceName = serviceName,
                    status = "fail",
                    message = customFailureMessage ?: "Failed To Proceed The Process",
                    errorDetail = null,
                    data = data,
                    booleanAble = true
                )
            }
        }
        catch (e: Exception){
            e.message?.let { processingUtil?.discard(it) }
            ServiceResponse(
                serviceName = serviceName,
                status = "error",
                message = customFailureMessage ?: "Error Occurred While Processing Logic",
                errorDetail = e.message,
                data = null,
                booleanAble = true
            )
        }
    }


    fun <T> returnError(
        customFailureMessage: String? = null,
    ) : ServiceResponse<T> {
        return ServiceResponse(
            serviceName = this.serviceName,
            status = "error",
            message = customFailureMessage ?: "Error Occurred While Processing Data",
            errorDetail = customFailureMessage,
            data = null
        )
    }

    fun returnFalse(
        customFailureMessage: String? = null,
    ) : ServiceResponse<Boolean> {
        return ServiceResponse(
            serviceName = this.serviceName,
            status = "fail",
            message = customFailureMessage ?: "Error Occurred While Processing Data",
            errorDetail = customFailureMessage,
            data = false
        )
    }


    /**
     *  Utility Functions
     */

    fun extractData(logShow: Boolean = true): T {
        if (logShow) {
            printLogs()
        }
        if (this.data != null) {
            return this.data as T
        } else {
            logger.error("Error : ${this.errorDetail}")
            throw Exception(this.message)
        }
    }

    fun extractStatus(logShow: Boolean = true): Boolean {
        if (logShow) {
            printLogs()
        }
        return if (this.booleanAble) {
            data as Boolean
        } else {
            when (this.status) {
                "success " -> true
                "fail" -> {
                    logger.info(this.errorDetail)
                    false
                }
                "error" -> {
                    logger.info(this.errorDetail)
                    false
                }
                else -> {
                    logger.info("Status Can not be Transformed To Boolean")
                    throw Exception("Error - ${this.errorDetail}")
                }
            }
        }
    }

    fun getLogString(): String{
        return buildString {
            append("\n***************************** ServiceResponse ************************************")
            append("\nService : $serviceName")
            append("\nStatus : $status")
            append("\nmessage : $message")
            if (errorDetail != null) append("\nerrorDetail : $errorDetail")
            append("\ndata : ${data.toString()}")
            append("\n**********************************************************************************")
        }
    }

    fun printLogs(){
        logger.info(this.getLogString())
    }

    override fun appendModel(model: Model, modelName: String) {
        model.addAttribute(modelName, data)
    }

    override fun toApi(): ResponseVo {
        return ResponseVo(
            status = this.status,
            message = this.message,
            errorDetail = this.errorDetail,
            data = this.data
        )
    }

}