
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>BatteryInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="BatteryInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BatteryValue" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="BatteryPercent" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatteryInfo", propOrder = {
    "robotIp",
    "batteryValue",
    "batteryPercent"
})
public class BatteryInfo {

    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "BatteryValue")
    protected double batteryValue;
    @XmlElement(name = "BatteryPercent")
    protected double batteryPercent;

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
     * ��ȡbatteryValue���Ե�ֵ��
     *
     */
    public double getBatteryValue() {
        return batteryValue;
    }

    /**
     * ����batteryValue���Ե�ֵ��
     *
     */
    public void setBatteryValue(double value) {
        this.batteryValue = value;
    }

    /**
     * ��ȡbatteryPercent���Ե�ֵ��
     *
     */
    public double getBatteryPercent() {
        return batteryPercent;
    }

    /**
     * ����batteryPercent���Ե�ֵ��
     *
     */
    public void setBatteryPercent(double value) {
        this.batteryPercent = value;
    }

}
