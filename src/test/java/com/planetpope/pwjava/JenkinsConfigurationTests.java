package com.planetpope.pwjava;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.RecordVideoSize;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class JenkinsConfigurationTests {
    private static Playwright playwright;
    private static Browser browser;
    private static boolean headless;
    private static int viewportHeight;
    private static int viewportWidth;
    private static String testUrl;
    private static String loginUsername;
    private static String loginPassword;
    private static String videosFolder;

    private BrowserContext context;
    private Page page;

    @BeforeAll
    public static void setupRunner() {
        headless = Boolean.valueOf(System.getenv().getOrDefault("HEADLESS", "false"))
                .booleanValue();
        viewportHeight = Integer.valueOf(System.getenv().getOrDefault("VIEWPORT_HEIGHT", "720"))
                .intValue();
        viewportWidth = Integer.valueOf(System.getenv().getOrDefault("VIEWPORT_WIDTH", "1280"))
                .intValue();
        testUrl = System.getenv().get("TEST_JENKINS_URL");
        loginUsername = System.getenv().get("TEST_LOGIN_USR");
        loginPassword = System.getenv().get("TEST_LOGIN_PSW");

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @BeforeEach
    public void openBrowser() {
        context = browser.newContext(new NewContextOptions()
                .setViewportSize(viewportWidth, viewportHeight)
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(new RecordVideoSize(viewportWidth, viewportHeight)));
        page = context.newPage();
        videosFolder = Paths.get("videos/").toString();
    }

    @Test
    public void loginToController() {
        page.navigate(testUrl);
        page.getByLabel("Username").fill(loginUsername);
        page.getByLabel("Password").fill(loginPassword);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in"))
                .click();

        assertThat(page.getByText("Build Executor Status")).isVisible();
        page.waitForTimeout(2000);
        page.navigate(testUrl+"logout");
        page.waitForTimeout(2000);
        writeVideo(page,"LoginLogout.webm");
    }

    private void writeVideo(Page page, String videoFilename) {
      Path originalVideoPath=page.video().path();
      Path videoFilePath = Paths.get(videosFolder,videoFilename);
      try {
        Files.deleteIfExists(videoFilePath);
        Files.move(originalVideoPath, videoFilePath);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    @AfterEach
    public void cleanup() {
        page.close();
        context.close();
    }

    @AfterAll
    public static void teardownRunner() {
        browser.close();
        playwright.close();
    }
}
