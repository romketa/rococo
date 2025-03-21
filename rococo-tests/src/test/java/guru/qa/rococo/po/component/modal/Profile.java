package guru.qa.rococo.po.component.modal;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import guru.qa.rococo.po.component.BaseComponent;
import utils.ScreenDiffResult;

@ParametersAreNonnullByDefault
public class Profile extends BaseComponent<Profile> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);
  private final SelenideElement signOutBtn = self.$(".variant-ghost");
  private final SelenideElement usernameEl = self.$("h4");
  private final SelenideElement avatarEl = self.$(".avatar-image");
  private final SelenideElement loadAvatarInput = self.$("input[type='file']");
  private final SelenideElement firstNameInput = self.$("input[name='firstname']");
  private final SelenideElement lastNameInput = self.$("input[name='surname']");
  private final SelenideElement closePopUpBtn = self.$(".variant-ringed");
  private final SelenideElement updateProfileBtn = self.$(".variant-filled-primary");
  private final SelenideElement header = self.$("header");

  public Profile() {
    super($(".modal"));
  }

  @Step("Check that Profile modal opened")
  @Nonnull
  public Profile checkThatModalOpened() {
    LOGGER.info("Check that Profile modal opened");
    header.shouldHave(visible, text("Профиль"));
    return this;
  }

  @Step("Check that Profile modal closed")
  @Nonnull
  public Profile checkThatModalClosed() {
    LOGGER.info("Check that Profile modal closed");
    header.shouldBe(disappear);
    return this;
  }

  @Step("Sign out")
  @Nonnull
  public <Page> Page signOut(Page expectedPage) {
    LOGGER.info("Sign out");
    signOutBtn.shouldBe(visible, enabled).click();
    return expectedPage;
  }

  @Step("Check username {0} in profile")
  @Nonnull
  public Profile checkUsername(String username) {
    LOGGER.info("Check username {} in profile", username);
    usernameEl.shouldBe(exactText("@" + username));
    return this;
  }

  @Step("Fill text profile form with first name {0} and last name {1}")
  @Nonnull
  public Profile fillTextProfileForm(String firstName, String lastName) {
    LOGGER.info("Fill text profile form with first name {} and last name {}", firstName, lastName);
    firstNameInput.setValue(firstName);
    lastNameInput.setValue(lastName);
    return this;
  }

  @Step("Upload avatar from {0}")
  @Nonnull
  public Profile uploadAvatar(String path) {
    LOGGER.info("Upload avatar from {}", path);
    loadAvatarInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Click by button Update profile")
  public Profile updateProfile() {
    LOGGER.info("Click by button Update profile");
    updateProfileBtn.click();
    return this;
  }

  @Step("Check first name and last name")
  @Nonnull
  public Profile checkNames(String firstName, String lastName) {
    LOGGER.info("Check first name and last name");
    firstNameInput.shouldHave(value(firstName));
    lastNameInput.shouldHave(value(lastName));
    return this;
  }

  @Step("Check that photo exist")
  @Nonnull
  public Profile checkPhotoExist() {
    LOGGER.info("Check that photo exist");
    avatarEl.shouldBe(attributeMatching("src", "data:image.*"), visible);
    return this;
  }

  @Step("Check avatar image matches the expected image")
  @Nonnull
  public Profile checkAvatarImg(BufferedImage expectedAvatar)
      throws IOException, InterruptedException {
    LOGGER.info("Check avatar image matches the expected image");
    Thread.sleep(3000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(avatarEl.screenshot()));
    assertFalse(new ScreenDiffResult(
        actual,
        expectedAvatar
    ));
    return this;
  }
}
