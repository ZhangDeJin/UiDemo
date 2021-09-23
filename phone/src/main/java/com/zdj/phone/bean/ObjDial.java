package com.zdj.phone.bean;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/13
 *     desc : 拨号实体
 * </pre>
 */
public class ObjDial {
    private String mainText;
    private String secondaryText;

    public ObjDial() {
    }

    public ObjDial(String mainText, String secondaryText) {
        this.mainText = mainText;
        this.secondaryText = secondaryText;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }
}
