
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>DeadlineTaskInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="DeadlineTaskInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CreateTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FinishAction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PointList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TaskPathId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskPathName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OperationAction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EndTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PlanTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteMode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcutePeriod" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteDay" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IntervalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IntervalValue" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeadlineTaskInfo", propOrder = {
    "createTime",
    "finishAction",
    "pointList",
    "taskPathId",
    "taskPathName",
    "operationAction",
    "startTime",
    "endTime",
    "planTime",
    "excuteMode",
    "excutePeriod",
    "excuteDay",
    "intervalType",
    "intervalValue"
})
public class DeadlineTaskInfo {

    @XmlElement(name = "CreateTime", required = true)
    protected String createTime;
    @XmlElement(name = "FinishAction")
    protected int finishAction;
    @XmlElement(name = "PointList")
    protected List<String> pointList;
    @XmlElement(name = "TaskPathId", required = true)
    protected String taskPathId;
    @XmlElement(name = "TaskPathName", required = true)
    protected String taskPathName;
    @XmlElement(name = "OperationAction")
    protected int operationAction;
    @XmlElement(name = "StartTime", required = true)
    protected String startTime;
    @XmlElement(name = "EndTime", required = true)
    protected String endTime;
    @XmlElement(name = "PlanTime", required = true)
    protected String planTime;
    @XmlElement(name = "ExcuteMode", required = true)
    protected String excuteMode;
    @XmlElement(name = "ExcutePeriod", required = true)
    protected String excutePeriod;
    @XmlElement(name = "ExcuteDay", required = true)
    protected String excuteDay;
    @XmlElement(name = "IntervalType", required = true)
    protected String intervalType;
    @XmlElement(name = "IntervalValue")
    protected int intervalValue;

    /**
     * ��ȡcreateTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * ����createTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreateTime(String value) {
        this.createTime = value;
    }

    /**
     * ��ȡfinishAction���Ե�ֵ��
     *
     */
    public int getFinishAction() {
        return finishAction;
    }

    /**
     * ����finishAction���Ե�ֵ��
     *
     */
    public void setFinishAction(int value) {
        this.finishAction = value;
    }

    /**
     * Gets the value of the pointList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pointList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPointList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPointList() {
        if (pointList == null) {
            pointList = new ArrayList<String>();
        }
        return this.pointList;
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
     * ��ȡtaskPathName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskPathName() {
        return taskPathName;
    }

    /**
     * ����taskPathName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskPathName(String value) {
        this.taskPathName = value;
    }

    /**
     * ��ȡoperationAction���Ե�ֵ��
     *
     */
    public int getOperationAction() {
        return operationAction;
    }

    /**
     * ����operationAction���Ե�ֵ��
     *
     */
    public void setOperationAction(int value) {
        this.operationAction = value;
    }

    /**
     * ��ȡstartTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * ����startTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartTime(String value) {
        this.startTime = value;
    }

    /**
     * ��ȡendTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * ����endTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndTime(String value) {
        this.endTime = value;
    }

    /**
     * ��ȡplanTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPlanTime() {
        return planTime;
    }

    /**
     * ����planTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPlanTime(String value) {
        this.planTime = value;
    }

    /**
     * ��ȡexcuteMode���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteMode() {
        return excuteMode;
    }

    /**
     * ����excuteMode���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteMode(String value) {
        this.excuteMode = value;
    }

    /**
     * ��ȡexcutePeriod���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcutePeriod() {
        return excutePeriod;
    }

    /**
     * ����excutePeriod���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcutePeriod(String value) {
        this.excutePeriod = value;
    }

    /**
     * ��ȡexcuteDay���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteDay() {
        return excuteDay;
    }

    /**
     * ����excuteDay���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteDay(String value) {
        this.excuteDay = value;
    }

    /**
     * ��ȡintervalType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIntervalType() {
        return intervalType;
    }

    /**
     * ����intervalType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIntervalType(String value) {
        this.intervalType = value;
    }

    /**
     * ��ȡintervalValue���Ե�ֵ��
     *
     */
    public int getIntervalValue() {
        return intervalValue;
    }

    /**
     * ����intervalValue���Ե�ֵ��
     *
     */
    public void setIntervalValue(int value) {
        this.intervalValue = value;
    }

}
