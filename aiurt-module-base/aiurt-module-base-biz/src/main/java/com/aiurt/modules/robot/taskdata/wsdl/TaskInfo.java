
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TaskInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="TaskInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskInfo", propOrder = {
    "taskId",
    "result"
})
public class TaskInfo {

    /**
     * 任务id
     */
    @XmlElement(name = "TaskId", required = true)
    protected String taskId;
    /**
     * 操作是否成功0成功，1失败
     */
    @XmlElement(name = "Result")
    protected int result;

    /**
     * ��ȡtaskId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * ����taskId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskId(String value) {
        this.taskId = value;
    }

    /**
     * ��ȡresult���Ե�ֵ��
     *
     */
    public int getResult() {
        return result;
    }

    /**
     * ����result���Ե�ֵ��
     *
     */
    public void setResult(int value) {
        this.result = value;
    }

}
