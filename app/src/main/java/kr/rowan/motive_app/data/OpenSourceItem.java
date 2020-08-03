package kr.rowan.motive_app.data;

public class OpenSourceItem {

    private String name;
    private String url;
    private String copyRighter;
    private String organization;

    public OpenSourceItem(String name, String url, String copyRighter, String organization) {
        this.name = name;
        this.url = url;
        this.copyRighter = copyRighter;
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCopyRighter() {
        return copyRighter;
    }

    public void setCopyRighter(String copyRighter) {
        this.copyRighter = copyRighter;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
