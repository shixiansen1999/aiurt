
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>LifterControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="LifterControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LifterUp"/>
 *     &lt;enumeration value="LifterDown"/>
 *     &lt;enumeration value="LifterStop"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "LifterControlType")
@XmlEnum
public enum LifterControlType {

    @XmlEnumValue("LifterUp")
    LIFTER_UP("LifterUp"),
    @XmlEnumValue("LifterDown")
    LIFTER_DOWN("LifterDown"),
    @XmlEnumValue("LifterStop")
    LIFTER_STOP("LifterStop");
    private final String value;

    LifterControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LifterControlType fromValue(String v) {
        for (LifterControlType c: LifterControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
