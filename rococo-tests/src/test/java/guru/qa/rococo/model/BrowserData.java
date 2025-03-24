package guru.qa.rococo.model;


import guru.qa.rococo.config.Config;

public enum BrowserData {

    CHROME("chrome", Config.getInstance().chromeVersion()),

    FIREFOX("firefox", Config.getInstance().firefoxVersion());


    private final String name;

    private final String version;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    BrowserData(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
