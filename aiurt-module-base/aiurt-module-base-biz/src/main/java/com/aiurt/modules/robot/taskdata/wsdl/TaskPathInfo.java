
package com.aiurt.modules.robot.taskdata.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>TaskPathInfo complex type�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 *
 * <pre>
 * &lt;complexType name="TaskPathInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskPathId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskPathName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PointList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TaskPathType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CreateTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FinishAction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OperationAction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskPathInfo", propOrder = {
    "taskPathId",
    "taskPathName",
    "pointList",
    "taskPathType",
    "createTime",
    "finishAction",
    "operationAction"
})
public class TaskPathInfo {

    // 任务模板id
    @XmlElement(name = "TaskPathId", required = true)
    protected String taskPathId;
    // 任务模板名称
    @XmlElement(name = "TaskPathName", required = true)
    protected String taskPathName;
    // 巡检点位列表
    @XmlElement(name = "PointList")
    protected List<String> pointList;
    // 任务模板类型
    @XmlElement(name = "TaskPathType", required = true)
    protected String taskPathType;
    // 创建时间
    @XmlElement(name = "CreateTime", required = true)
    protected String createTime;
    // 完成动作
    @XmlElement(name = "FinishAction")
    protected int finishAction;
    @XmlElement(name = "OperationAction")
    protected int operationAction;

    /**
     * ��ȡtaskPathId���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskPathId() {
        return taskPathId;
    }

    /**
     * ����taskPathId���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskPathId(String value) {
        this.taskPathId = value;
    }

    /**
     * ��ȡtaskPathName���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskPathName() {
        return taskPathName;
    }

    /**
     * ����taskPathName���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskPathName(String value) {
        this.taskPathName = value;
    }

    /**
     * Gets the value of the pointList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pointList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPointList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPointList() {
        if (pointList == null) {
            pointList = new ArrayList<String>();
        }
        return this.pointList;
    }

    /**
     * ��ȡtaskPathType���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskPathType() {
        return taskPathType;
    }

    /**
     * ����taskPathType���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskPathType(String value) {
        this.taskPathType = value;
    }

    /**
     * ��ȡcreateTime���Ե�ֵ��
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * ����createTime���Ե�ֵ��
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreateTime(String value) {
        this.createTime = value;
    }

    /**
     * ��ȡfinishAction���Ե�ֵ��
     *
     */
    public int getFinishAction() {
        return finishAction;
    }

    /**
     * ����finishAction���Ե�ֵ��
     *
     */
    public void setFinishAction(int value) {
        this.finishAction = value;
    }

    /**
     * ��ȡoperationAction���Ե�ֵ��
     *
     */
    public int getOperationAction() {
        return operationAction;
    }

    /**
     * ����operationAction���Ե�ֵ��
     *
     */
    public void setOperationAction(int value) {
        this.operationAction = value;
    }

}
