
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TaskExcuteData complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="TaskExcuteData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PatrolDeviceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PatrolDeviceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TotalDeviceSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ErrorDeviceSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FinishDeviceSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TaskFinishPercentage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskExcuteData", propOrder = {
    "robotIp",
    "taskType",
    "taskId",
    "taskName",
    "patrolDeviceName",
    "patrolDeviceId",
    "totalDeviceSize",
    "errorDeviceSize",
    "finishDeviceSize",
    "taskFinishPercentage"
})
public class TaskExcuteData {

    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "TaskType", required = true)
    protected String taskType;
    @XmlElement(name = "TaskId", required = true)
    protected String taskId;
    @XmlElement(name = "TaskName", required = true)
    protected String taskName;
    @XmlElement(name = "PatrolDeviceName", required = true)
    protected String patrolDeviceName;
    @XmlElement(name = "PatrolDeviceId", required = true)
    protected String patrolDeviceId;
    @XmlElement(name = "TotalDeviceSize")
    protected int totalDeviceSize;
    @XmlElement(name = "ErrorDeviceSize")
    protected int errorDeviceSize;
    @XmlElement(name = "FinishDeviceSize")
    protected int finishDeviceSize;
    @XmlElement(name = "TaskFinishPercentage")
    protected int taskFinishPercentage;

    /**
     * ��ȡrobotIp���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRobotIp() {
        return robotIp;
    }

    /**
     * ����robotIp���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRobotIp(String value) {
        this.robotIp = value;
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
     * ��ȡpatrolDeviceName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPatrolDeviceName() {
        return patrolDeviceName;
    }

    /**
     * ����patrolDeviceName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPatrolDeviceName(String value) {
        this.patrolDeviceName = value;
    }

    /**
     * ��ȡpatrolDeviceId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPatrolDeviceId() {
        return patrolDeviceId;
    }

    /**
     * ����patrolDeviceId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPatrolDeviceId(String value) {
        this.patrolDeviceId = value;
    }

    /**
     * ��ȡtotalDeviceSize���Ե�ֵ��
     *
     */
    public int getTotalDeviceSize() {
        return totalDeviceSize;
    }

    /**
     * ����totalDeviceSize���Ե�ֵ��
     *
     */
    public void setTotalDeviceSize(int value) {
        this.totalDeviceSize = value;
    }

    /**
     * ��ȡerrorDeviceSize���Ե�ֵ��
     *
     */
    public int getErrorDeviceSize() {
        return errorDeviceSize;
    }

    /**
     * ����errorDeviceSize���Ե�ֵ��
     *
     */
    public void setErrorDeviceSize(int value) {
        this.errorDeviceSize = value;
    }

    /**
     * ��ȡfinishDeviceSize���Ե�ֵ��
     *
     */
    public int getFinishDeviceSize() {
        return finishDeviceSize;
    }

    /**
     * ����finishDeviceSize���Ե�ֵ��
     *
     */
    public void setFinishDeviceSize(int value) {
        this.finishDeviceSize = value;
    }

    /**
     * ��ȡtaskFinishPercentage���Ե�ֵ��
     *
     */
    public int getTaskFinishPercentage() {
        return taskFinishPercentage;
    }

    /**
     * ����taskFinishPercentage���Ե�ֵ��
     *
     */
    public void setTaskFinishPercentage(int value) {
        this.taskFinishPercentage = value;
    }

}
