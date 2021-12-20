package com.playwright.screenplay;

import com.playwright.screenplay.interfaces.IAction;

public abstract class Action extends AbstractActivity implements IAction {

    @Override
    public Actor and() {
        return actor;
    }

    @Override
    public Actor then() {
        return actor;
    }

}