
package com.aiurt.modules.robot.taskfinish.wsdl;

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
 *         &lt;element name="Data" type="{http://tempuri.org/taskfinish.xsd}TaskExcuteInfos"/>
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
    "data"
})
@XmlRootElement(name = "GetTaskExcuteInfoByTaskIdResponse")
public class GetTaskExcuteInfoByTaskIdResponse {

    @XmlElement(name = "Data", required = true)
    protected TaskExcuteInfos data;

    /**
     * ��ȡdata���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link TaskExcuteInfos }
     *
     */
    public TaskExcuteInfos getData() {
        return data;
    }

    /**
     * ����data���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link TaskExcuteInfos }
     *
     */
    public void setData(TaskExcuteInfos value) {
        this.data = value;
    }

}
