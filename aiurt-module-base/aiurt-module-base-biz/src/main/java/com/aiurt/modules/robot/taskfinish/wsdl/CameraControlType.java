
package com.aiurt.modules.robot.taskfinish.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>CameraControlType�� Java �ࡣ
 *
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * <p>
 * <pre>
 * &lt;simpleType name="CameraControlType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ZoomUp"/>
 *     &lt;enumeration value="ZoomUpStop"/>
 *     &lt;enumeration value="ZoomDown"/>
 *     &lt;enumeration value="ZoomDownStop"/>
 *     &lt;enumeration value="FocusNear"/>
 *     &lt;enumeration value="FocusNearStop"/>
 *     &lt;enumeration value="FocusFar"/>
 *     &lt;enumeration value="FocusFarStop"/>
 *     &lt;enumeration value="CameraReset"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "CameraControlType")
@XmlEnum
public enum CameraControlType {

    @XmlEnumValue("ZoomUp")
    ZOOM_UP("ZoomUp"),
    @XmlEnumValue("ZoomUpStop")
    ZOOM_UP_STOP("ZoomUpStop"),
    @XmlEnumValue("ZoomDown")
    ZOOM_DOWN("ZoomDown"),
    @XmlEnumValue("ZoomDownStop")
    ZOOM_DOWN_STOP("ZoomDownStop"),
    @XmlEnumValue("FocusNear")
    FOCUS_NEAR("FocusNear"),
    @XmlEnumValue("FocusNearStop")
    FOCUS_NEAR_STOP("FocusNearStop"),
    @XmlEnumValue("FocusFar")
    FOCUS_FAR("FocusFar"),
    @XmlEnumValue("FocusFarStop")
    FOCUS_FAR_STOP("FocusFarStop"),
    @XmlEnumValue("CameraReset")
    CAMERA_RESET("CameraReset");
    private final String value;

    CameraControlType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CameraControlType fromValue(String v) {
        for (CameraControlType c: CameraControlType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
