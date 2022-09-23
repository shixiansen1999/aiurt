
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RobotAlarmInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="RobotAlarmInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AlarmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AlarmType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AlarmDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AlarmTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotAlarmInfo", propOrder = {
    "alarmId",
    "robotIp",
    "alarmType",
    "alarmDesc",
    "alarmTime"
})
public class RobotAlarmInfo {

    @XmlElement(name = "AlarmId", required = true)
    protected String alarmId;
    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "AlarmType", required = true)
    protected String alarmType;
    @XmlElement(name = "AlarmDesc", required = true)
    protected String alarmDesc;
    @XmlElement(name = "AlarmTime", required = true)
    protected String alarmTime;

    /**
     * ��ȡalarmId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlarmId() {
        return alarmId;
    }

    /**
     * ����alarmId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlarmId(String value) {
        this.alarmId = value;
    }

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
     * ��ȡalarmType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlarmType() {
        return alarmType;
    }

    /**
     * ����alarmType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlarmType(String value) {
        this.alarmType = value;
    }

    /**
     * ��ȡalarmDesc���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlarmDesc() {
        return alarmDesc;
    }

    /**
     * ����alarmDesc���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlarmDesc(String value) {
        this.alarmDesc = value;
    }

    /**
     * ��ȡalarmTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlarmTime() {
        return alarmTime;
    }

    /**
     * ����alarmTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlarmTime(String value) {
        this.alarmTime = value;
    }

}
