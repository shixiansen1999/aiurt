
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>TaskFinishInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="TaskFinishInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskPathId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EndTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FinishState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExcuteRobot" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskFinishInfo", propOrder = {
    "taskId",
    "taskName",
    "taskType",
    "taskPathId",
    "pointList",
    "startTime",
    "endTime",
    "finishState",
    "excuteRobot"
})
public class TaskFinishInfo {

    @XmlElement(name = "TaskId", required = true)
    protected String taskId;
    @XmlElement(name = "TaskName", required = true)
    protected String taskName;
    @XmlElement(name = "TaskType", required = true)
    protected String taskType;
    @XmlElement(name = "TaskPathId", required = true)
    protected String taskPathId;
    @XmlElement(name = "PointList")
    protected List<String> pointList;
    @XmlElement(name = "StartTime", required = true)
    protected String startTime;
    @XmlElement(name = "EndTime", required = true)
    protected String endTime;
    @XmlElement(name = "FinishState", required = true)
    protected String finishState;
    @XmlElement(name = "ExcuteRobot", required = true)
    protected String excuteRobot;

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
     * ��ȡfinishState���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFinishState() {
        return finishState;
    }

    /**
     * ����finishState���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFinishState(String value) {
        this.finishState = value;
    }

    /**
     * ��ȡexcuteRobot���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcuteRobot() {
        return excuteRobot;
    }

    /**
     * ����excuteRobot���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcuteRobot(String value) {
        this.excuteRobot = value;
    }

}
