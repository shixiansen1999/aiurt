
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RobotPosInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="RobotPosInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RobotIp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PosX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PosY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Angle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EdgeID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Precent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotPosInfo", propOrder = {
    "robotIp",
    "posX",
    "posY",
    "angle",
    "edgeID",
    "precent"
})
public class RobotPosInfo {

    @XmlElement(name = "RobotIp", required = true)
    protected String robotIp;
    @XmlElement(name = "PosX", required = true)
    protected String posX;
    @XmlElement(name = "PosY", required = true)
    protected String posY;
    @XmlElement(name = "Angle", required = true)
    protected String angle;
    @XmlElement(name = "EdgeID", required = true)
    protected String edgeID;
    @XmlElement(name = "Precent", required = true)
    protected String precent;

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
     * ��ȡposX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPosX() {
        return posX;
    }

    /**
     * ����posX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPosX(String value) {
        this.posX = value;
    }

    /**
     * ��ȡposY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPosY() {
        return posY;
    }

    /**
     * ����posY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPosY(String value) {
        this.posY = value;
    }

    /**
     * ��ȡangle���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAngle() {
        return angle;
    }

    /**
     * ����angle���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAngle(String value) {
        this.angle = value;
    }

    /**
     * ��ȡedgeID���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEdgeID() {
        return edgeID;
    }

    /**
     * ����edgeID���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEdgeID(String value) {
        this.edgeID = value;
    }

    /**
     * ��ȡprecent���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrecent() {
        return precent;
    }

    /**
     * ����precent���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrecent(String value) {
        this.precent = value;
    }

}
