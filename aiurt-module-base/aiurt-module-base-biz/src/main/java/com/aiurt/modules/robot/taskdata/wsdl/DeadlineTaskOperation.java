
package com.aiurt.modules.robot.taskdata.wsdl;

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
 *         &lt;element name="info" type="{http://tempuri.org/taskdata.xsd}DeadlineTaskInfo"/>
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
    "info"
})
@XmlRootElement(name = "DeadlineTaskOperation")
public class DeadlineTaskOperation {

    @XmlElement(required = true)
    protected DeadlineTaskInfo info;

    /**
     * ��ȡinfo���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link DeadlineTaskInfo }
     *
     */
    public DeadlineTaskInfo getInfo() {
        return info;
    }

    /**
     * ����info���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link DeadlineTaskInfo }
     *
     */
    public void setInfo(DeadlineTaskInfo value) {
        this.info = value;
    }

}
