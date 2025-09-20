package com.example.teamcity.api.extensions;

import com.example.teamcity.api.annotations.UserSession;
import com.example.teamcity.api.models.User;
import com.example.teamcity.ui.BaseUiTest;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class UserSessionListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod() && (
                method.getTestMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(UserSession.class) ||
                        method.getTestMethod().getTestClass().getRealClass().isAnnotationPresent(UserSession.class))) {
            Object testInstance = testResult.getInstance();
            if (testInstance instanceof BaseUiTest baseUiTest) {
                User user = baseUiTest.testData.getUser();
                baseUiTest.loginAs(user);
            } else {
                throw new IllegalStateException("Test instance must extend BaseUiTest");
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    }
}