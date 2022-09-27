
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ConnectInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="ConnectInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ConnectDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConnectInfo", propOrder = {
    "robotIp",
    "state",
    "connectDesc"
})
public class ConnectInfo {

    /**
     * 机器人ip
     */
    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    /**
     * 是否正常
     */
    @XmlElement(name = "State")
    protected boolean state;
    /**
     * 描述
     */
    @XmlElement(name = "ConnectDesc", required = true)
    protected String connectDesc;

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
     * ��ȡstate���Ե�ֵ��
     *
     */
    public boolean isState() {
        return state;
    }

    /**
     * ����state���Ե�ֵ��
     *
     */
    public void setState(boolean value) {
        this.state = value;
    }

    /**
     * ��ȡconnectDesc���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getConnectDesc() {
        return connectDesc;
    }

    /**
     * ����connectDesc���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setConnectDesc(String value) {
        this.connectDesc = value;
    }


}
