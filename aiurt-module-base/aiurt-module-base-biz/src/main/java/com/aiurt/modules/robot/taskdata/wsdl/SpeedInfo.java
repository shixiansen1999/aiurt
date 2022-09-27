
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>SpeedInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="SpeedInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpeedX" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="SpeedW" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpeedInfo", propOrder = {
    "robotIp",
    "speedX",
    "speedW"
})
public class SpeedInfo {

    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "SpeedX")
    protected double speedX;
    @XmlElement(name = "SpeedW")
    protected double speedW;

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
     * ��ȡspeedX���Ե�ֵ��
     *
     */
    public double getSpeedX() {
        return speedX;
    }

    /**
     * ����speedX���Ե�ֵ��
     *
     */
    public void setSpeedX(double value) {
        this.speedX = value;
    }

    /**
     * ��ȡspeedW���Ե�ֵ��
     *
     */
    public double getSpeedW() {
        return speedW;
    }

    /**
     * ����speedW���Ե�ֵ��
     *
     */
    public void setSpeedW(double value) {
        this.speedW = value;
    }

}
