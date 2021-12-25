package com.jplay.screenplay;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitUntilState;

import java.util.List;

public class Actor {

    private Configuration configuration = new Configuration(this);
    private BrowserManager browserManager = new BrowserManager();

    private static ThreadLocal<Actor> actor = ThreadLocal.withInitial(() -> new Actor());

    private Actor() {
    }

    public static Actor actor() {
        return actor.get();
    }

    // Browser manager methods

    BrowserManager getBrowserManager() {
        return browserManager;
    }

    // Config methods

    public Configuration config() {
        return configuration;
    }

    public Configuration cleanConfig() {
        return configuration = new Configuration(this);
    }

    //Browser methods

    public Actor createBrowser() {
        this.getBrowserManager().create(this.configuration);
        return actor.get();
    }

    public Actor createPureBrowser() {
        this.getBrowserManager().createBrowser(this.configuration);
        return actor.get();
    }

    public Actor closeBrowser() {
        this.getBrowserManager().closeBrowser();
        this.getBrowserManager().setBrowser(null);
        this.getBrowserManager().setBrowserContext(null);
        this.getBrowserManager().setPage(null);
        return this;
    }

    // Context methods

    public Actor createContextAndTab() {
        this.getBrowserManager().createContextAndTab(this.configuration);
        return actor.get();
    }

    public Actor closeCurrentContext() {
        this.getBrowserManager().getBrowserContext().close();
        return this;
    }

    private List<BrowserContext> getContextsFromBrowser() {
        List<BrowserContext> contexts = this.getBrowserManager().getBrowser().contexts();
        if (contexts.isEmpty()) {
            throw new RuntimeException("Browser does not have contexts, please start one using method " +
                    "'createContextAndTab()' or use 'createBrowser()' to create browser with context and tab.");
        }
        return contexts;
    }

    public Actor switchContextByIndex(int index) {
        BrowserContext context = this.getContextsFromBrowser().get(index);
        this.getBrowserManager().setBrowserContext(context);
        return this;
    }

    // Page methods

    public Page currentPage() {
        return this.getBrowserManager().getPage();
    }

    public Actor openNewTab() {
        BrowserContext context = this.getBrowserManager().getBrowserContext();
        if (context == null) {
            throw new RuntimeException("You can not open new tab without context. " +
                    "Please use 'createContextAndTab()' instead of 'openNewTab()' " +
                    "or 'createBrowser()' instead of 'createPureBrowser()', it will create browser with tab.");
        }
        this.getBrowserManager().setPage(context.newPage());
        return actor.get();
    }

    public Actor closeCurrentTab() {
        this.getBrowserManager().getPage().close();
        return actor.get();
    }

    private List<Page> getPagesFromCurrentContext() {
        List<Page> pages = this.getBrowserManager().getPage().context().pages();
        if (pages.isEmpty()) {
            throw new RuntimeException("Current context does not have pages, " +
                    "please start one with method 'openNewTab()' or change current context with 'switchContextByIndex()'" +
                    "or create new context with tab using 'createContextAndTab()'.");
        }
        return pages;
    }

    public Actor switchTabByIndex(int index) {
        Page page = this.getPagesFromCurrentContext().get(index);
        this.getBrowserManager().setPage(page);
        page.bringToFront();
        return actor.get();
    }

    public Actor switchTabByTitle(String title) {
        List<Page> pages = this.getPagesFromCurrentContext().stream()
                .filter(tab -> tab.title().equals(title)).toList();
        if (pages.size() > 1) {
            throw new RuntimeException("More then one tab in current context has title '" + title +
                    "', in such cases better to use switchTabByIndex(int index).");
        } else if (pages.size() == 0) {
            throw new RuntimeException("None of tabs in current context has title '" + title + "'");
        }
        this.getBrowserManager().setPage(pages.get(0));
        return actor.get();
    }

    // Waits

    /**
     * Waits till network activity will be still 500 milliseconds.
     * @return Actor
     */
    public Actor waitTillNetworkIdle() {
        this.getBrowserManager().getPage().waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }

    /**
     * Waits till HTML document will be loaded.
     * @return Actor
     */
    public Actor waitTillDocumentLoaded() {
        this.getBrowserManager().getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        return this;
    }

    // Navigations

    /**
     * Navigates to url and wait till document loaded.
     * @param url
     * @return Actor
     */
    public Actor navigateTo(String url) {
        this.getBrowserManager().getPage()
                .navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        return this;
    }

    /**
     * Click back browser button and wait till document loaded.
     * @return Actor
     */
    public Actor goBack() {
        this.getBrowserManager().getPage()
                .goBack(new Page.GoBackOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        return this;
    }

    /**
     * Click forward browser button and wait till document loaded.
     * @return Actor
     */
    public Actor goForward() {
        this.getBrowserManager().getPage()
                .goForward(new Page.GoForwardOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        return this;
    }

    // Actions

    /**
     * Click on element.
     * @param selector
     * @return Actor
     */
    public Actor click(String selector) {
        this.getBrowserManager().getPage().click(selector);
        return this;
    }

    /**
     * Right button click on element.
     * @param selector
     * @return Actor
     */
    public Actor rightClick(String selector) {
        this.getBrowserManager().getPage().click(selector, new Page.ClickOptions().setButton(MouseButton.RIGHT));
        return this;
    }

    /**
     * Double button click on element.
     * @param selector
     * @return Actor
     */
    public Actor doubleClick(String selector) {
        this.getBrowserManager().getPage().dblclick(selector);
        return this;
    }

    /**
     * Drag and drop element to another element.
     * @param sourceSelector
     * @return Actor
     */
    public Actor dragAndDrop(String sourceSelector, String targetSelector) {
        this.getBrowserManager().getPage().dragAndDrop(sourceSelector, targetSelector);
        return this;
    }

    /**
     * Check checkbox.
     * @param selector
     * @return Actor
     */
    public Actor check(String selector) {
        this.getBrowserManager().getPage().check(selector);
        return this;
    }

    /**
     * Uncheck checkbox.
     * @param selector
     * @return Actor
     */
    public Actor uncheck(String selector) {
        this.getBrowserManager().getPage().uncheck(selector);
        return this;
    }

    // Helpers

    /**
     * Set content in tab
     * @param html
     * @return
     */
    public Actor setContent(String html) {
        this.getBrowserManager().getPage().setContent(html);
        return this;
    }

    // Execute actions and tasks methods

    protected final <T extends Action> T executeAction(T action) {
        action.setActor(this);
        return action;
    }

    protected final Actor executeTask(Task task) {
        task.setActor(this);
        task.perform();
        return this;
    }

    // Execute syntax sugar
    public <T extends Action> T attemptTo(T action) {
        return this.executeAction(action);
    }

    public Actor attemptTo(Task task) {
        return this.executeTask(task);
    }

    public <T extends Action> T does(T action) {
        return this.executeAction(action);
    }

    public Actor does(Task task) {
        return this.executeTask(task);
    }

    public <T extends Action> T expectThat(T action) {
        return this.executeAction(action);
    }

    public Actor expectThat(Task task) {
        return this.executeTask(task);
    }
}
