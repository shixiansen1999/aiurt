
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TaskExcuteInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="TaskExcuteInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskPathId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TargetId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteUnit" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HDPicture" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InfraredPicture" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OtherFile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskExcuteInfo", propOrder = {
    "taskId",
    "taskName",
    "taskType",
    "taskPathId",
    "targetId",
    "pointId",
    "pointName",
    "pointType",
    "excuteTime",
    "excuteValue",
    "excuteUnit",
    "excuteState",
    "excuteDesc",
    "hdPicture",
    "infraredPicture",
    "otherFile"
})
public class TaskExcuteInfo {

    // 任务id
    @XmlElement(name = "TaskId", required = true)
    protected String taskId;
    // 任务名称
    @XmlElement(name = "TaskName", required = true)
    protected String taskName;
    // 任务类型
    @XmlElement(name = "TaskType", required = true)
    protected String taskType;
    // 任务模板id
    @XmlElement(name = "TaskPathId", required = true)
    protected String taskPathId;
    // 巡检结果id
    @XmlElement(name = "TargetId", required = true)
    protected String targetId;
    // 巡检点位id
    @XmlElement(name = "PointId", required = true)
    protected String pointId;
    // 巡检点位名称
    @XmlElement(name = "PointName", required = true)
    protected String pointName;
    // 巡检点位类型
    @XmlElement(name = "PointType", required = true)
    protected String pointType;
    // 执行时间
    @XmlElement(name = "ExcuteTime", required = true)
    protected String excuteTime;

    @XmlElement(name = "ExcuteValue", required = true)
    protected String excuteValue;
    // 执行结果值
    @XmlElement(name = "ExcuteUnit", required = true)
    protected String excuteUnit;
    // 执行结果状态
    @XmlElement(name = "ExcuteState", required = true)
    protected String excuteState;
    // 执行结果描述
    @XmlElement(name = "ExcuteDesc", required = true)
    protected String excuteDesc;
    // 高清图片
    @XmlElement(name = "HDPicture", required = true)
    protected String hdPicture;
    // 红外图片
    @XmlElement(name = "InfraredPicture", required = true)
    protected String infraredPicture;
    @XmlElement(name = "OtherFile", required = true)
    protected String otherFile;

    /**
     * ��ȡtaskId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * ����taskId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskId(String value) {
        this.taskId = value;
    }

    /**
     * ��ȡtaskName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * ����taskName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskName(String value) {
        this.taskName = value;
    }

    /**
     * ��ȡtaskType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * ����taskType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskType(String value) {
        this.taskType = value;
    }

    /**
     * ��ȡtaskPathId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskPathId() {
        return taskPathId;
    }

    /**
     * ����taskPathId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskPathId(String value) {
        this.taskPathId = value;
    }

    /**
     * ��ȡtargetId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * ����targetId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTargetId(String value) {
        this.targetId = value;
    }

    /**
     * ��ȡpointId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointId() {
        return pointId;
    }

    /**
     * ����pointId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointId(String value) {
        this.pointId = value;
    }

    /**
     * ��ȡpointName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointName() {
        return pointName;
    }

    /**
     * ����pointName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointName(String value) {
        this.pointName = value;
    }

    /**
     * ��ȡpointType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointType() {
        return pointType;
    }

    /**
     * ����pointType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointType(String value) {
        this.pointType = value;
    }

    /**
     * ��ȡexcuteTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteTime() {
        return excuteTime;
    }

    /**
     * ����excuteTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteTime(String value) {
        this.excuteTime = value;
    }

    /**
     * ��ȡexcuteValue���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteValue() {
        return excuteValue;
    }

    /**
     * ����excuteValue���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteValue(String value) {
        this.excuteValue = value;
    }

    /**
     * ��ȡexcuteUnit���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteUnit() {
        return excuteUnit;
    }

    /**
     * ����excuteUnit���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteUnit(String value) {
        this.excuteUnit = value;
    }

    /**
     * ��ȡexcuteState���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteState() {
        return excuteState;
    }

    /**
     * ����excuteState���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteState(String value) {
        this.excuteState = value;
    }

    /**
     * ��ȡexcuteDesc���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteDesc() {
        return excuteDesc;
    }

    /**
     * ����excuteDesc���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteDesc(String value) {
        this.excuteDesc = value;
    }

    /**
     * ��ȡhdPicture���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHDPicture() {
        return hdPicture;
    }

    /**
     * ����hdPicture���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHDPicture(String value) {
        this.hdPicture = value;
    }

    /**
     * ��ȡinfraredPicture���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInfraredPicture() {
        return infraredPicture;
    }

    /**
     * ����infraredPicture���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInfraredPicture(String value) {
        this.infraredPicture = value;
    }

    /**
     * ��ȡotherFile���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOtherFile() {
        return otherFile;
    }

    /**
     * ����otherFile���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOtherFile(String value) {
        this.otherFile = value;
    }

}
