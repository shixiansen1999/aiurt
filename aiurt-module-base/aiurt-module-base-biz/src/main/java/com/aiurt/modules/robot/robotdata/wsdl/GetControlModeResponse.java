
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
 *         &lt;element name="ControlType" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "controlType"
})
@XmlRootElement(name = "GetControlModeResponse")
public class GetControlModeResponse {

    @XmlElement(name = "ControlType")
    protected int controlType;

    /**
     * ��ȡcontrolType���Ե�ֵ��
     *
     */
    public int getControlType() {
        return controlType;
    }

    /**
     * ����controlType���Ե�ֵ��
     *
     */
    public void setControlType(int value) {
        this.controlType = value;
    }

}
