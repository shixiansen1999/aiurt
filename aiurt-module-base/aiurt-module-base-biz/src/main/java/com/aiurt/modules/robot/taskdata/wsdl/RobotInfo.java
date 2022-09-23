
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RobotInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="RobotInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CameraIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CameraPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FlirIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FlirPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CameraUser" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CameraPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FlirUser" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FlirPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotInfo", propOrder = {
    "robotName",
    "robotIp",
    "cameraIp",
    "cameraPort",
    "flirIp",
    "flirPort",
    "cameraUser",
    "cameraPassword",
    "flirUser",
    "flirPassword"
})
public class RobotInfo {

    @XmlElement(name = "RobotName", required = true)
    protected String robotName;
    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "CameraIp", required = true)
    protected String cameraIp;
    @XmlElement(name = "CameraPort")
    protected int cameraPort;
    @XmlElement(name = "FlirIp", required = true)
    protected String flirIp;
    @XmlElement(name = "FlirPort")
    protected int flirPort;
    @XmlElement(name = "CameraUser", required = true)
    protected String cameraUser;
    @XmlElement(name = "CameraPassword", required = true)
    protected String cameraPassword;
    @XmlElement(name = "FlirUser", required = true)
    protected String flirUser;
    @XmlElement(name = "FlirPassword", required = true)
    protected String flirPassword;

    /**
     * ��ȡrobotName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRobotName() {
        return robotName;
    }

    /**
     * ����robotName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRobotName(String value) {
        this.robotName = value;
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
     * ��ȡcameraIp���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCameraIp() {
        return cameraIp;
    }

    /**
     * ����cameraIp���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCameraIp(String value) {
        this.cameraIp = value;
    }

    /**
     * ��ȡcameraPort���Ե�ֵ��
     *
     */
    public int getCameraPort() {
        return cameraPort;
    }

    /**
     * ����cameraPort���Ե�ֵ��
     *
     */
    public void setCameraPort(int value) {
        this.cameraPort = value;
    }

    /**
     * ��ȡflirIp���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFlirIp() {
        return flirIp;
    }

    /**
     * ����flirIp���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFlirIp(String value) {
        this.flirIp = value;
    }

    /**
     * ��ȡflirPort���Ե�ֵ��
     *
     */
    public int getFlirPort() {
        return flirPort;
    }

    /**
     * ����flirPort���Ե�ֵ��
     *
     */
    public void setFlirPort(int value) {
        this.flirPort = value;
    }

    /**
     * ��ȡcameraUser���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCameraUser() {
        return cameraUser;
    }

    /**
     * ����cameraUser���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCameraUser(String value) {
        this.cameraUser = value;
    }

    /**
     * ��ȡcameraPassword���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCameraPassword() {
        return cameraPassword;
    }

    /**
     * ����cameraPassword���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCameraPassword(String value) {
        this.cameraPassword = value;
    }

    /**
     * ��ȡflirUser���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFlirUser() {
        return flirUser;
    }

    /**
     * ����flirUser���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFlirUser(String value) {
        this.flirUser = value;
    }

    /**
     * ��ȡflirPassword���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFlirPassword() {
        return flirPassword;
    }

    /**
     * ����flirPassword���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFlirPassword(String value) {
        this.flirPassword = value;
    }

}
