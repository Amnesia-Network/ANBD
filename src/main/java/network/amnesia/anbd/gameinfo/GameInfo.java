package network.amnesia.anbd.gameinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import network.amnesia.anbd.Utils;

import java.util.Objects;
import java.util.stream.Stream;

public class GameInfo {

    @JsonProperty("internalName")
    public String getInternalName() {
        return this.internalName;
    }
    public GameInfo setInternalName(String internalName) {
        this.internalName = internalName;
        return this;
    }
    private String internalName;

    @JsonProperty("title")
    public String getTitle() {
        return this.title;
    }
    public GameInfo setTitle(String title) {
        this.title = title;
        return this;
    }
    private String title;

    @JsonProperty("metacriticLink")
    public String getMetacriticLink() {
        return this.metacriticLink;
    }
    public GameInfo setMetacriticLink(String metacriticLink) {
        this.metacriticLink = metacriticLink;
        return this;
    }
    private String metacriticLink;

    @JsonProperty("dealID")
    public String getDealID() {
        return this.dealID;
    }
    public GameInfo setDealID(String dealID) {
        this.dealID = dealID;
        return this;
    }
    private String dealID;

    @JsonProperty("storeID")
    public String getStoreID() {
        return this.storeID;
    }
    public GameInfo setStoreID(String storeID) {
        this.storeID = storeID;
        return this;
    }
    private String storeID;

    @JsonProperty("gameID")
    public String getGameID() {
        return this.gameID;
    }
    public GameInfo setGameID(String gameID) {
        this.gameID = gameID;
        return this;
    }
    private String gameID;

    @JsonProperty("salePrice")
    public String getSalePrice() {
        return this.salePrice;
    }
    public GameInfo setSalePrice(String salePrice) {
        this.salePrice = salePrice;
        return this;
    }
    private String salePrice;

    @JsonProperty("normalPrice")
    public String getNormalPrice() {
        return this.normalPrice;
    }
    public GameInfo setNormalPrice(String normalPrice) {
        this.normalPrice = normalPrice;
        return this;
    }
    private String normalPrice;

    @JsonProperty("isOnSale")
    public String getIsOnSale() {
        return this.isOnSale;
    }
    public GameInfo setIsOnSale(String isOnSale) {
        this.isOnSale = isOnSale;
        return this;
    }
    private String isOnSale;

    @JsonProperty("savings")
    public String getSavings() {
        return this.savings;
    }
    public GameInfo setSavings(String savings) {
        this.savings = savings;
        return this;
    }
    private String savings;

    @JsonProperty("metacriticScore")
    public String getMetacriticScore() {
        return this.metacriticScore;
    }
    public GameInfo setMetacriticScore(String metacriticScore) {
        this.metacriticScore = metacriticScore;
        return this;
    }
    private String metacriticScore;

    @JsonProperty("steamRatingText")
    public String getSteamRatingText() {
        return this.steamRatingText;
    }
    public GameInfo setSteamRatingText(String steamRatingText) {
        this.steamRatingText = steamRatingText;
        return this;
    }
    private String steamRatingText;

    @JsonProperty("steamRatingPercent")
    public String getSteamRatingPercent() {
        return this.steamRatingPercent;
    }
    public GameInfo setSteamRatingPercent(String steamRatingPercent) {
        this.steamRatingPercent = steamRatingPercent;
        return this;
    }
    private String steamRatingPercent;

    @JsonProperty("steamRatingCount")
    public String getSteamRatingCount() {
        return this.steamRatingCount;
    }
    public GameInfo setSteamRatingCount(String steamRatingCount) {
        this.steamRatingCount = steamRatingCount;
        return this;
    }
    private String steamRatingCount;

    @JsonProperty("steamAppID")
    public String getSteamAppID() {
        return this.steamAppID;
    }
    public GameInfo setSteamAppID(String steamAppID) {
        this.steamAppID = steamAppID;
        return this;
    }
    private String steamAppID;

    @JsonProperty("releaseDate")
    public int getReleaseDate() {
        return this.releaseDate;
    }
    public GameInfo setReleaseDate(int releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }
    private int releaseDate;

    @JsonProperty("lastChange")
    public int getLastChange() {
        return this.lastChange;
    }
    public GameInfo setLastChange(int lastChange) {
        this.lastChange = lastChange;
        return this;
    }
    private int lastChange;

    @JsonProperty("dealRating")
    public String getDealRating() {
        return this.dealRating;
    }
    public GameInfo setDealRating(String dealRating) {
        this.dealRating = dealRating;
        return this;
    }
    private String dealRating;

    @JsonProperty("thumb")
    public String getThumb() {
        return this.thumb;
    }
    public GameInfo setThumb(String thumb) {
        this.thumb = thumb;
        return this;
    }
    private String thumb;

    /**
     * @return the savings with the double data type
     */
    private double savingsDouble = -1;
    public double getSavingsDouble() {
        if (savingsDouble < 0) savingsDouble = Double.parseDouble(savings);
        return savingsDouble;
    }

    /**
     * A {@code GameInfo} is valid if none of the field we use is {@code null}
     * @return the validity of this {@code GameInfo}
     */
    public boolean isValid() {
        return !Stream.of(title, storeID, steamAppID, normalPrice, salePrice, savings, steamRatingPercent, steamRatingCount, steamRatingText)
                .anyMatch(Objects::isNull);
    }

    /**
     * @param jsonObject json {@code String} containing the {@code GameInfo} to parse
     * @return the {@code GameInfo} contained in jsonObject
     */
    public static GameInfo jsonToGameInfo(String jsonObject) {
        try {
            return Utils.objectMapper.readValue(jsonObject, GameInfo.class);
        } catch (JsonProcessingException e) {
            return null; // if JsonObject is not readable
        }
    }

}