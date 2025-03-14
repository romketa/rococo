package guru.qa.rococo.test.web;

import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.po.ArtistsPage;
import guru.qa.rococo.po.MainPage;
import guru.qa.rococo.po.MuseumsPage;
import guru.qa.rococo.po.PaintingsPage;
import guru.qa.rococo.po.RegisterPage;

@WebTest
public abstract class BaseTest {

    protected final MainPage mainPage = new MainPage();
    protected final RegisterPage registerPage = new RegisterPage();
    protected final ArtistsPage artistsPage = new ArtistsPage();
    protected final MuseumsPage museumsPage = new MuseumsPage();
    protected final PaintingsPage paintingsPage = new PaintingsPage();
}
