
package com.aiurt.modules.robot.robotdata.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>FilrControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="FilrControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FocusUp"/>
 *     &lt;enumeration value="FocusDown"/>
 *     &lt;enumeration value="AutoFocus"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "FilrControlType")
@XmlEnum
public enum FilrControlType {

    @XmlEnumValue("FocusUp")
    FOCUS_UP("FocusUp"),
    @XmlEnumValue("FocusDown")
    FOCUS_DOWN("FocusDown"),
    @XmlEnumValue("AutoFocus")
    AUTO_FOCUS("AutoFocus");
    private final String value;

    FilrControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FilrControlType fromValue(String v) {
        for (FilrControlType c: FilrControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
