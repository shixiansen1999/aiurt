
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RobotYunTaiInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="RobotYunTaiInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PosPan" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PosTile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotYunTaiInfo", propOrder = {
    "robotIp",
    "posPan",
    "posTile"
})
public class RobotYunTaiInfo {

    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "PosPan", required = true)
    protected String posPan;
    @XmlElement(name = "PosTile", required = true)
    protected String posTile;

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
     * ��ȡposPan���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPosPan() {
        return posPan;
    }

    /**
     * ����posPan���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPosPan(String value) {
        this.posPan = value;
    }

    /**
     * ��ȡposTile���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPosTile() {
        return posTile;
    }

    /**
     * ����posTile���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPosTile(String value) {
        this.posTile = value;
    }

}
