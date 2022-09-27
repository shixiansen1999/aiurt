
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 *
 */
@WebService(name = "ServicePortType", targetNamespace = "http://tempuri.org/taskfinish.xsd/Service.wsdl")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ServicePortType {


    /**
     * Service definition of function taskfinish__GetTaskFinishInfoByTime
     *
     * @param startTime
     * @param endTime
     * @return
     *     returns com.wgpthree.TaskFinishInfos
     */
    @WebMethod(operationName = "GetTaskFinishInfoByTime")
    @WebResult(name = "Data", targetNamespace = "")
    @RequestWrapper(localName = "GetTaskFinishInfoByTime", targetNamespace = "http://tempuri.org/taskfinish.xsd", className = "com.wgpthree.GetTaskFinishInfoByTime")
    @ResponseWrapper(localName = "GetTaskFinishInfoByTimeResponse", targetNamespace = "http://tempuri.org/taskfinish.xsd", className = "com.wgpthree.GetTaskFinishInfoByTimeResponse")
    public TaskFinishInfos getTaskFinishInfoByTime(
            @WebParam(name = "StartTime", targetNamespace = "")
                    String startTime,
            @WebParam(name = "EndTime", targetNamespace = "")
                    String endTime);

    /**
     * Service definition of function taskfinish__GetTaskExcuteInfoByTaskId
     *
     * @param taskId
     * @return
     *     returns com.wgpthree.TaskExcuteInfos
     */
    @WebMethod(operationName = "GetTaskExcuteInfoByTaskId")
    @WebResult(name = "Data", targetNamespace = "")
    @RequestWrapper(localName = "GetTaskExcuteInfoByTaskId", targetNamespace = "http://tempuri.org/taskfinish.xsd", className = "com.wgpthree.GetTaskExcuteInfoByTaskId")
    @ResponseWrapper(localName = "GetTaskExcuteInfoByTaskIdResponse", targetNamespace = "http://tempuri.org/taskfinish.xsd", className = "com.wgpthree.GetTaskExcuteInfoByTaskIdResponse")
    public TaskExcuteInfos getTaskExcuteInfoByTaskId(
            @WebParam(name = "TaskId", targetNamespace = "")
                    String taskId);

}
