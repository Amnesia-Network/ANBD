package network.amnesia.anbd.gameinfo;


import network.amnesia.anbd.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GameInfoList {

    private static final String dealUrlBase = GameLookupManager.CHEAP_SHARK_API_LINK + "/deals?";

    // search fields (in case we want to display them later)
    private final String searchTitle;
    private final SortingCriteria sortingCriteria;

    // storage of gameInfos
    private final List<GameInfo> gameInfos;
    private int currentGameInfoIndex = 0;


    public GameInfoList(String searchTitle, SortingCriteria sortingCriteria) throws IOException {
        this.searchTitle = searchTitle;
        this.sortingCriteria = sortingCriteria;
        gameInfos = loadGameInfos();
    }

    /**
     * shift the pointer to the next game in the list
     * @return true if the shift was successful, otherwise it already reached the end of the list
     */
    public boolean nextGame() {
        if (currentGameInfoIndex != gameInfos.size() - 1) {
            currentGameInfoIndex++;
            return true;
        }
        return false;
    }

    /**
     * shift the pointer to the previous game in the list
     * @return {@code true} if the shift was successful, otherwise it is a the beginning of the list
     */
    public boolean previousGame() {
        if (currentGameInfoIndex != 0) {
            currentGameInfoIndex--;
            return true;
        }
        return false;
    }

    /**
     * @return the {@code GameInfo} currently pointed
     */
    public GameInfo getCurrentGameInfo() {
        return gameInfos.get(currentGameInfoIndex);
    }

    /**
     * @return the pointers position
     */
    public int getCurrentGameInfoIndex() {
        return currentGameInfoIndex;
    }

    /**
     * @return the size of the list
     */
    public int size() {
        return gameInfos.size();
    }

    /**
     * @return whether this {@code GameListInfo} has content or not (used to discard empty lists)
     */
    public boolean hasContent() {
        return gameInfos != null;
    }

    /**
     * Load the {@code GameInfo} from the online source and perform some processing to filter the best deals
     * @return a list of {@code GameInfo}
     * @throws IOException in case of a problem while fetching data or processing the json
     */
    private List<GameInfo> loadGameInfos() throws IOException {

        List<String> jsonObjects = Utils.splitJsonObjects(Utils.fetchRemote(getLinkQuery()));
        if (jsonObjects.isEmpty()) return null;

        return jsonObjects.stream()
                .map(GameInfo::jsonToGameInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        GameInfo::getTitle,
                        gameInfo ->  gameInfo,
                        (current, next) -> current.getSavingsDouble() >= next.getSavingsDouble() ? current : next,
                        LinkedHashMap::new))
                .values().stream()
                .filter(GameInfo::isValid)
                .toList();
    }


    /**
     * @return the link to get information from the API
     */
    private String getLinkQuery() {
        return dealUrlBase +
                "title=" + searchTitle +
                "&sortBy=" + sortingCriteria.toString();
    }

}
