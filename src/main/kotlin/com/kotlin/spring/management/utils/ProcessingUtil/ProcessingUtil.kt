package com.kotlin.spring.management.utils.ProcessingUtil

import org.slf4j.LoggerFactory
import java.util.*

/** ProcessingUtil ver alpha 0.0.2 by hchanjune
 *  하나의 함수 내부에서 여러개의 비즈니스 로직을 처리할때,
 *  ProcessingUtil에 등록하여 해당 로직들의 처리여부를 순차적으로 확인
 *  또한 실행 결과에 따라서 리턴 값 결정
 *  사용된 logging library SL4J (원하는 시스템으로 교체. AOS 의 경우 Logcat 등 ...)
 *
 *  Spring MVC 패턴용 사용예시
 *
 *  Controller 단에서
 *  @RequestMapping()
 *  fun someFunction(){
 *  processingUtil = ProcessingUtil("SomeLogic")
 *  service.someLogic(someVar, processingUtil)
 *  return if (processingUnit.compile) "successUrl" else "failUrl"
 *  }
 *
 *  Service 단에서
 *  fun someLogic(someVar, processingUtil: ProcessingUtil){
 *  var process1 = logic1
 *  processingUtil.add("프로세스1", process1, false)
 *  var process2 = logic2
 *  processingUtil.add("프로세스2", process2, false)
 *  var process3 = logic3
 *  processingUtil.add("프로세스3", process3, true)
 *  }
 *
 *  위와 같이 사용. Service 에서 다른 Object를 리턴하여도 상관 없이, ProcessingUtil 은 process의 결과를 logging 하는 용도로 만 사용 하는 것도 가능
 *
 *****************************************Parameters *****************************************
 *     processName : 프로세스명                                                               *
 *     process : 실제 프로세스 로직                                                           *
 *     optional : compile 함수 실행시, 실패 / 성공에 상관 없이 true 리턴 (log만 출력)           *
 *********************************************************************************************
 *
 * updates for alpha 0.0.2
 * - addFunction 기능 추가. processFunction Param 에 람다 함수를 입력하여 해당 프로세스가 실패시 Exception Message 반환 기능 추가
 * - addFunction 기능의 경우 기존 add 와 혼용 가능.
 * - Exception 메시지를 확인하고 싶을때만 사용하거나, 모든 프로세스를 ProcessingUtil 로 관리 하고 싶을때 사용
 * 사용예시
 * processingUtil.addFunction(
 *     processName = "MyProcess",
 *     processFunction = {
 *         // 여기에 MyProcess 의 로직 구현 혹은 함수 실행
 *     },
 *     optional = false
 * )
 *
 * updates for alpha 0.0.3
 * - message 와 exceptionMessage 구분 log 에 동시 출력 가능.
 * - add 및 addFunction 마지막 param 으로 message 추가 가능. 기본값 null 이기 때문에 사용하지 않아도 무관.
 *
 * updates for alpha 0.0.4
 * - ServiceResponse 에서 사용 가능
 * - processUtil 에 process 추가시 log 출력
 * - discard function 추가 processUtil 을 종료하고 지금까지 실행 큐에 담긴 process 들의 log 출력 후 return false
 */

class ProcessingUtil(
    private var methodName: String
): LinkedHashMap<String, ProcessResult>() {

    private var processCounter: Int

    init {
        this.methodName = "ProcessUtil - $methodName"
        this.processCounter = 1
    }

    companion object{
        private val logger = LoggerFactory.getLogger(ProcessingUtil::class.java)
    }

    fun add(
        processName: String,
        process: Boolean,
        optional: Boolean,
        message: String? = null
    ): Boolean {
        val processResult = ProcessResult(
            processName = processName,
            result = process,
            optional = optional,
            message = message
        )
        this["process${processCounter++}"] = processResult
        //logger.info("\nProcess added: $processName - Result: ${processResult.result}, Optional: ${processResult.optional}")
        return processResult.result
    }

    fun addFunction(
        processName: String,
        processFunction: () -> Boolean,
        optional: Boolean,
        message: String? = null
    ): Boolean {
        var processResult: ProcessResult
        try {
            val result = processFunction()
            processResult = ProcessResult(
                processName = processName,
                result = result,
                optional = optional,
                message = message
            )
            //logger.info("\nProcess function added: $processName - Optional: ${processResult.optional}")
        } catch (e: Exception) {
            processResult = ProcessResult(
                processName = processName,
                result = false,
                optional = optional,
                message = e.message,
                exceptionMessage = "${e.toString()}\n => ${e.printStackTrace().toString()}"
            )
            //logger.error("\nProcess function added with exception: $processName - Exception: ${e.message}")
        }
        this["process${processCounter++}"] = processResult
        return processResult.result
    }

    fun <T> addDataFunction(
        processName: String,
        processFunction: () -> T,
        optional: Boolean,
        message: String? = null
    ) : T {
        var processResult: ProcessResult
        try {
            val data = processFunction()
            processResult = ProcessResult(
                processName = processName,
                result = data != null,
                optional = optional,
                message = if (data != null) message else "$processName - Process Returned With Null Data",
            )
            //logger.info("\nProcess function added: $processName - Optional: ${processResult.optional}")
        } catch (e: Exception) {
            processResult = ProcessResult(
                processName = processName,
                result = false,
                optional = optional,
                message = e.message,
                exceptionMessage = "${e.toString()}\n => ${e.printStackTrace().toString()}",
            )
            //logger.error("\nProcess function added with exception: $processName - Exception: ${e.message}")
        }
        this["process${processCounter++}"] = processResult
        return processFunction()
    }








    fun compile(
        logOption: Boolean = true
    ): Boolean {
        if (logOption) this.printLogs()
        return this.filter { !it.value.optional }
            .all { it.value.result }
    }

    fun discard(discardMessage: String): Boolean{
        var logMessage = this.getLogString()
        var discardLog = buildString {
            append("\n")
            append(logMessage)
            append("!!! Process[$methodName] Discarded Because Of ... ")
            append(discardMessage)
        }
        logger.info(discardLog)
        return false
    }

    /*************************************************************
     *  log 출력 형식 예시
     *  ProcessingUtil - $methodName
     *  1.$processName : $result
     *  2.$processName : $result (Optional)
     *  3.$processName : $result
     *  4.$processName : $result
     *  ErrorMessage : $message
     *  5.$processName : $result (Optional)
     *  ErrorMessage : $message
     *************************************************************/
    fun getLogString(): String {
        return buildString {
            append("\n************************************************************\n")
            append("$methodName\n")
            this@ProcessingUtil.forEach { (processKey, processResult) ->
                val processNumber = processKey.substring(7)
                append("$processNumber.${processResult.processName} : ${processResult.result}")
                if (processResult.optional) {
                    append(" (Optional)")
                }
                append("\n")
                if (processResult.message != null) {
                    append("Message : ${processResult.message}")
                    append("\n")
                }
                if (!processResult.result && processResult.exceptionMessage != null) {
                    append("ExceptionMessage : ${processResult.exceptionMessage}\n")
                }
            }
            append("************************************************************\n")
        }
    }


    fun printLogs() {
        logger.info(this.getLogString())
    }

}

data class ProcessResult(
    val processName: String,
    val result: Boolean,
    val optional: Boolean,
    val message: String? = null,
    val exceptionMessage: String? = null,
)
