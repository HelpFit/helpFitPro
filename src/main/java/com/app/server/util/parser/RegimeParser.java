package com.app.server.util.parser;

import com.app.server.models.HealthRegime.Regime;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegimeParser {

    public static Document convertRegimeToDocument(Regime regime){
        Document doc = new Document("helpFitID", "reg")
                .append("name", regime.getName())
                .append("description", regime.getDescription())
                .append("regimeType", regime.getRegimeType())
                .append("timeRequiredWeeks", regime.getTimeRequiredWeeks())
                .append("ailmentTags", regime.getAilmentTags())
                .append("interestTags", regime.getInsterestTags())
                .append("habitsTags", regime.getHabitsTags())
                .append("isActive", regime.getIsActive()).append("timeCreated", new Date()).append("timeUpdated", regime.getTimeUpdated());
        return doc;
    }

    public static Regime convertDocumentToRegime(Document item) {
        List<String> ailments = (List<String>) item.get("ailmentTags");
        List<String> interests = (List<String>) item.get("interestTags");
        List<String> habits = (List<String>) item.get("habitsTags");
        Regime regime = new Regime(item.getString("helpFitID"),
                item.getString("name"),
                item.getString("description"),
                item.getString("regimeType"),
                item.getString("timeRequiredWeeks"),
                ailments,
                interests,
                habits,
                item.getString("isActive"),
                item.getString("timeCreated"),
                item.getString("timeUpdated"));
        regime.setId(item.getObjectId("_id").toString());
        return regime;
    }

    public static Regime convertJsonToRegime(JSONObject json){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Regime regime = new Regime(
                json.getString("helpFitID"),
                json.getString("name"),
                json.getString("description"),
                json.getString("regimeType"),
                json.getString("timeRequiredWeeks"),
                jsonArraytoList(json.getJSONArray("ailmentTags")),
                jsonArraytoList(json.getJSONArray("insterestTags")),
                jsonArraytoList(json.getJSONArray("habitsTags")),
                json.getString("isActive"),
                json.has("timeCreated")?json.getString("isFulfilled"): String.valueOf(Instant.now().getEpochSecond()),
                String.valueOf(Instant.now().getEpochSecond()));
        regime.setId(json.getString("id"));
        return regime;
    }

    public static List<String> jsonArraytoList(JSONArray jsonArray){
        ArrayList<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.get(i).toString());
            }
        }
        return list;
    }
}
