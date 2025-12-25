package com.glee.xjpa.lifecycle;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 桥接
 */
public class XjpaRepositoryAppBridge {

    private final XjpaRepositoryAppLifecycle xjpaRepositoryAppLifecycle;

    public XjpaRepositoryAppBridge(XjpaRepositoryAppLifecycle xjpaRepositoryAppLifecycle) {
        this.xjpaRepositoryAppLifecycle = xjpaRepositoryAppLifecycle;
    }

    /**
     * @return 注册器
     */
    public XjpaRepositoryRegister getRegister() {
        return xjpaRepositoryAppLifecycle.register;
    }

}
