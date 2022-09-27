
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
 *         &lt;element name="info" type="{http://tempuri.org/taskdata.xsd}TaskPathInfo"/>
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
@XmlRootElement(name = "SetTaskPathInfo")
public class SetTaskPathInfo {

    @XmlElement(required = true)
    protected TaskPathInfo info;

    /**
     * ��ȡinfo���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link TaskPathInfo }
     *
     */
    public TaskPathInfo getInfo() {
        return info;
    }

    /**
     * ����info���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link TaskPathInfo }
     *
     */
    public void setInfo(TaskPathInfo value) {
        this.info = value;
    }

}
