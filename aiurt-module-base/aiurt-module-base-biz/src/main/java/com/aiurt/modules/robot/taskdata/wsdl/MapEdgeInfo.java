
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>MapEdgeInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="MapEdgeInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EdgeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartNodeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EndNodeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartNodeX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartNodeY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EndNodeX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EndNodeY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstControlX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstControlY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SecondControlX" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SecondControlY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapEdgeInfo", propOrder = {
    "edgeId",
    "startNodeId",
    "endNodeId",
    "startNodeX",
    "startNodeY",
    "endNodeX",
    "endNodeY",
    "firstControlX",
    "firstControlY",
    "secondControlX",
    "secondControlY"
})
public class MapEdgeInfo {

    @XmlElement(name = "EdgeId", required = true)
    protected String edgeId;
    @XmlElement(name = "StartNodeId", required = true)
    protected String startNodeId;
    @XmlElement(name = "EndNodeId", required = true)
    protected String endNodeId;
    @XmlElement(name = "StartNodeX", required = true)
    protected String startNodeX;
    @XmlElement(name = "StartNodeY", required = true)
    protected String startNodeY;
    @XmlElement(name = "EndNodeX", required = true)
    protected String endNodeX;
    @XmlElement(name = "EndNodeY", required = true)
    protected String endNodeY;
    @XmlElement(name = "FirstControlX", required = true)
    protected String firstControlX;
    @XmlElement(name = "FirstControlY", required = true)
    protected String firstControlY;
    @XmlElement(name = "SecondControlX", required = true)
    protected String secondControlX;
    @XmlElement(name = "SecondControlY", required = true)
    protected String secondControlY;

    /**
     * ��ȡedgeId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEdgeId() {
        return edgeId;
    }

    /**
     * ����edgeId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEdgeId(String value) {
        this.edgeId = value;
    }

    /**
     * ��ȡstartNodeId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartNodeId() {
        return startNodeId;
    }

    /**
     * ����startNodeId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartNodeId(String value) {
        this.startNodeId = value;
    }

    /**
     * ��ȡendNodeId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndNodeId() {
        return endNodeId;
    }

    /**
     * ����endNodeId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndNodeId(String value) {
        this.endNodeId = value;
    }

    /**
     * ��ȡstartNodeX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartNodeX() {
        return startNodeX;
    }

    /**
     * ����startNodeX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartNodeX(String value) {
        this.startNodeX = value;
    }

    /**
     * ��ȡstartNodeY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartNodeY() {
        return startNodeY;
    }

    /**
     * ����startNodeY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartNodeY(String value) {
        this.startNodeY = value;
    }

    /**
     * ��ȡendNodeX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndNodeX() {
        return endNodeX;
    }

    /**
     * ����endNodeX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndNodeX(String value) {
        this.endNodeX = value;
    }

    /**
     * ��ȡendNodeY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndNodeY() {
        return endNodeY;
    }

    /**
     * ����endNodeY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndNodeY(String value) {
        this.endNodeY = value;
    }

    /**
     * ��ȡfirstControlX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFirstControlX() {
        return firstControlX;
    }

    /**
     * ����firstControlX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFirstControlX(String value) {
        this.firstControlX = value;
    }

    /**
     * ��ȡfirstControlY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFirstControlY() {
        return firstControlY;
    }

    /**
     * ����firstControlY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFirstControlY(String value) {
        this.firstControlY = value;
    }

    /**
     * ��ȡsecondControlX���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSecondControlX() {
        return secondControlX;
    }

    /**
     * ����secondControlX���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSecondControlX(String value) {
        this.secondControlX = value;
    }

    /**
     * ��ȡsecondControlY���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSecondControlY() {
        return secondControlY;
    }

    /**
     * ����secondControlY���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSecondControlY(String value) {
        this.secondControlY = value;
    }

}
