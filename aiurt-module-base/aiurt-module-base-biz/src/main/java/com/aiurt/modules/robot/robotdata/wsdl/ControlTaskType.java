
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ControlTaskType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="ControlTaskType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CancelTask"/>
 *     &lt;enumeration value="PauseTask"/>
 *     &lt;enumeration value="ResumeTask"/>
 *     &lt;enumeration value="ChargeTask"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ControlTaskType")
@XmlEnum
public enum ControlTaskType {

    @XmlEnumValue("CancelTask")
    CANCEL_TASK("CancelTask"),
    @XmlEnumValue("PauseTask")
    PAUSE_TASK("PauseTask"),
    @XmlEnumValue("ResumeTask")
    RESUME_TASK("ResumeTask"),
    @XmlEnumValue("ChargeTask")
    CHARGE_TASK("ChargeTask");
    private final String value;

    ControlTaskType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ControlTaskType fromValue(String v) {
        for (ControlTaskType c: ControlTaskType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
