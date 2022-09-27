
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.*;


/**
 * <p>anonymous complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://tempuri.org/robotdata.xsd}WiperControlType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "type"
})
@XmlRootElement(name = "RobotWiperControl")
public class RobotWiperControl {

    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected WiperControlType type;

    /**
     * ��ȡtype���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link WiperControlType }
     *
     */
    public WiperControlType getType() {
        return type;
    }

    /**
     * ����type���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link WiperControlType }
     *
     */
    public void setType(WiperControlType value) {
        this.type = value;
    }

}
