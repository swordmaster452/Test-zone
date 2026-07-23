import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmuLandTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        // Настройка параметров браузера
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
        // Подключение к вашему Selenium Standalone серверу
        driver = new RemoteWebDriver(new URL("http://localhost:4444"), options);

        // Неявное ожидание элементов (чтобы сайт успевал прогружаться)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
    }
    @Test
    void chromeTest() throws Exception {
        ChromeOptions options = new ChromeOptions();

        WebDriver driver = new RemoteWebDriver(
                new URL("http://localhost:4444"),
                options
        );

        driver.get("https://google.com");

        assertEquals("Google", driver.getTitle());

        driver.quit();
    }

    @Test
    @DisplayName("Тест 1: Проверка функции поиска по сайту")
    public void testSearchFunction() throws InterruptedException {
        driver.get("https://emu-land.net");

        // Находим поле поиска по name="q"
        WebElement searchInput = driver.findElement(By.name("q"));
        searchInput.clear();

        // Искомая строка для ввода и проверки
        String targetGame = "Tekken 2 Ver.B (US, TES3/VER.D)";

        // Вводим строку в поле поиска
        searchInput.sendKeys(targetGame);
        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);

        // Ждем 1.5 секунды, чтобы страница успела обновить DOM-дерево
        Thread.sleep(1500);

        // Получаем исходный код страницы в нижнем регистре
        String pageSource = driver.getPageSource().toLowerCase();

        // Проверяем наличие точной фразы в коде страницы
        boolean isSuccess = pageSource.contains(targetGame.toLowerCase());

        // Вывод сообщения в консоль IntelliJ IDEA
        if (isSuccess) {
            System.out.println(">>> ТЕСТ УСПЕШНО ПРОЙДЕН: наш файл '" + targetGame + "' успешно найден в результатах поиска сайта!");
        }

        // Проверка для JUnit
        Assertions.assertTrue(isSuccess,
                "Файл '" + targetGame + "' НЕ был обнаружен на странице результатов поиска!");
    }
    @Test
    @DisplayName("Тест 2: Навигация в раздел Аркадные")
    public void testNavigationToSega() throws InterruptedException {
        driver.get("https://emu-land.net");

        // Кликаем по кнопке главного меню "АРКАДНЫЕ" (берём точный регистр с вашего скриншота)
        String targetSection = "АРКАДНЫЕ";
        WebElement menuLink = driver.findElement(By.linkText(targetSection));
        menuLink.click();

        // Небольшая пауза для гарантированной смены страницы
        Thread.sleep(1500);

        // Получаем текущий URL-адрес
        String currentUrl = driver.getCurrentUrl().toLowerCase();

        // Проверяем, что в URL появилось ключевое слово раздела (обычно 'arcade' или 'arcadnye')
        boolean isSuccess = currentUrl.contains("arc");

        // Вывод красивого сообщения об успехе в консоль IntelliJ IDEA
        if (isSuccess) {
            System.out.println(">>> ТЕСТ УСПЕШНО ПРОЙДЕН: Навигация сайта исправна. Выполнен успешный переход в раздел '" + targetSection + "'!");
        }

        // Зеленая галочка для JUnit
        Assertions.assertTrue(isSuccess,
                "Переход в раздел '" + targetSection + "' не был выполнен! Текущий URL: " + driver.getCurrentUrl());
    }
    //
    @Test
    @DisplayName("Тест 3: Негативный сценарий авторизации")
    public void testNegativeLogin() {
        // Открываем главную страницу сайта
        driver.get("https://emu-land.net");

        // Находим поле Логин по атрибуту placeholder и вводим некорректные данные
        WebElement loginInput = driver.findElement(By.cssSelector("input[placeholder='Логин']"));
        loginInput.clear();
        loginInput.sendKeys("FakeUser452");

        // Находим поле Пароль по атрибуту placeholder и вводим неверный пароль
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='Пароль']"));
        passwordInput.clear();
        passwordInput.sendKeys("1234567890");

        // Находим кнопку "Войти" по тегу button и типу submit, после чего кликаем
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // Профессиональное явное ожидание на максимум 10 секунд
        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10));

        // ОЖИДАНИЕ КОНКРЕТНОГО ТЕКСТА: Ждем появление надписи "Такого пользователя не существует"
        WebElement errorElement = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'Такого пользователя не существует')]")
                )
        );

        // Проверяем, что элемент успешно найден и отображается
        boolean isErrorDisplayed = errorElement.isDisplayed();

        // Вывод информативного сообщения об успехе в консоль IntelliJ IDEA
        if (isErrorDisplayed) {
            System.out.println(">>> ТЕСТ УСПЕШНО ПРОЙДЕН: Негативный сценарий авторизации подтвержден. Найдена ошибка 'Такого пользователя не существует'!");
        }

        // Утверждение JUnit для получения зеленой галочки в отчете
        Assertions.assertTrue(isErrorDisplayed, "На странице не обнаружено сообщение об ошибке валидации пользователя!");
    }


    @AfterEach
    public void tearDown() {
        // Обязательно закрываем браузер после каждого теста
        if (driver != null) {
            driver.quit();
        }
    }
}
