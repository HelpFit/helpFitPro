package com.app.server.util.parser;

import com.app.server.models.Event.Event;
import com.app.server.models.Preferences.Ailment;
import com.app.server.models.Preferences.Habit;
import com.app.server.models.Preferences.Interest;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.JsonObject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDocumentParser {

	public static Document convertJsonToEventDocument(JSONObject json){
		Document doc = new Document();
		if (json.has("name"))
			doc.append("name",json.getString("name"));
		if (json.has("description"))
			doc.append("description",json.getString("description"));
		if (json.has("venue"))
			doc.append("venue",json.getString("venue"));
		if (json.has("regionOfVenue"))
			doc.append("regionOfVenue",json.getString("regionOfVenue"));
		if (json.has("address"))
			doc.append("address",json.getString("address"));
		if (json.has("startDate"))
			doc.append("startDate",json.getString("startDate"));
		if (json.has("endDate"))
			doc.append("endDate",json.getString("endDate"));
		if (json.has("startTime"))
			doc.append("startTime",json.getString("startTime"));
		if (json.has("endTime"))
			doc.append("endTime",json.getString("endTime"));
		if (json.has("organizerId"))
			doc.append("organizerId",json.getString("organizerId"));
		if (json.has("ticketPrice"))
			doc.append("ticketPrice",json.getString("ticketPrice"));

		if (json.has("ailmentTags")){
			ArrayList<Ailment> ailmentTags = new ArrayList<Ailment>();
			JSONArray ailmentArray = json.getJSONArray("ailmentTags");
			if(ailmentArray != null){
				int len = ailmentArray.length();
				for(int i=0;i<len;i++){
					ailmentTags.add(Ailment.getAilment(ailmentArray.getInt(i)));
				}
			}
			doc.append("ailmentTags",ailmentTags);
		}


		if (json.has("interestTags")){
			ArrayList<Interest> interestTags = new ArrayList<Interest>();
			JSONArray interestArray = json.getJSONArray("interestTags");
			if(interestArray != null){
				int len = interestArray.length();
				for(int i=0;i<len;i++){
					interestTags.add(Interest.getInterest(interestArray.getInt(i)));
				}
			}
			doc.append("interestTags",interestTags);
		}

		if (json.has("habitTags")){
			ArrayList<Habit> habitTags = new ArrayList<Habit>();
			JSONArray habitArray = json.getJSONArray("habitTags");
			if(habitArray != null){
				int len = habitArray.length();
				for(int i=0;i<len;i++){
					habitTags.add(Habit.getHabit(habitArray.getInt(i)));
				}
			}
			doc.append("habitTags",habitTags);
		}

		if (json.has("isActive"))
			doc.append("isActive",json.getBoolean("isActive"));
		if (json.has("isOpen"))
			doc.append("isOpen",json.getBoolean("isOpen"));
		if (json.has("timeCreated")) {
			doc.append("timeCreated",json.getString("timeCreated"));
		} else {
			doc.append("timeCreated",String.valueOf(Instant.now().getEpochSecond()));
		}
		doc.append("timeUpdated",String.valueOf(Instant.now().getEpochSecond()));

		return doc;
	}

	public static Event convertDocumentToEvent(Document item){

		List<Double> ailmentInts = (List<Double>) item.get("ailmentTags");
		List<Ailment> ailmentTags = new ArrayList<Ailment>();
		for(int i=0;i<ailmentInts.size();i++){
			Double ailment = ailmentInts.get(i);
			ailmentTags.add(Ailment.getAilment(ailment.intValue()));
		}
		/*for(Integer ailment: ailmentInts.in){
			ailmentTags.add(Ailment.getAilment(ailment));
		}*/
		List<Double> interestInts = (List<Double>) item.get("interestTags");
		List<Interest> interestTags = new ArrayList<Interest>();
		for(int i=0;i<interestInts.size();i++){
			Double interest = interestInts.get(i);
			interestTags.add(Interest.getInterest(interest.intValue()));
		}
		/*for(Integer interest: interestInts){
			interestTags.add(Interest.getInterest(interest));
		}*/

		List<Double> habitInts = (List<Double>) item.get("habitTags");
		List<Habit> habitTags = new ArrayList<Habit>();
		for(int i=0;i<habitInts.size();i++){
			Double habits = habitInts.get(i);
			habitTags.add(Habit.getHabit(habits.intValue()));
		}
		/*for(Integer habit: habitInts){
			habitTags.add(Habit.getHabit(habit));
		}*/

		Event event = new Event(
				item.getString("name"),
				item.getString("description"),
				item.getString("venue"),
				item.getString("regionOfVenue"),
				item.getString("address"),
				item.getString("startDate"),
				item.getString("endDate"),
				item.getString("startTime"),
				item.getString("endTime"),
				item.getString("organizerId"),
				item.getString("ticketPrice"),
				ailmentTags,
				interestTags,
				habitTags,
				item.getBoolean("isActive"),
				item.getBoolean("isOpen"),
				item.getString("timeCreated"),
				item.getString("timeUpdated"));
		event.setId(item.getObjectId("_id").toString());
		return event;
	}

	public static Document convertEventToDocument(Event event){

		ArrayList<Integer> ailmentTags = new ArrayList<Integer>();
		List<Ailment> ailmentVal = event.getAilmentTags();
		if(ailmentVal != null){
			for(int i=0;i<ailmentVal.size();i++){
				Ailment val = ailmentVal.get(i);
				if(val.getId() != null)
					ailmentTags.add(val.getId());
			}
		}

		ArrayList<Integer> interestTags = new ArrayList<Integer>();
		List<Interest> interestVal = event.getInterestTags();
		if(interestVal != null){
			for(int i=0;i<interestVal.size();i++){
				Interest val = interestVal.get(i);
				if(val.getId() != null)
					interestTags.add(val.getId());
			}
		}

		ArrayList<Integer> habitTags = new ArrayList<Integer>();
		List<Habit> habitVal = event.getHabitTags();
		if(habitVal != null){
			for(int i=0;i<habitVal.size();i++){
				Habit val = habitVal.get(i);
				if(val.getId() != null)
					habitTags.add(val.getId());
			}
		}

		Document doc = new Document("name", event.getName())
				.append("description", event.getDescription())
				.append("venue", event.getVenue())
				.append("regionOfVenue", event.getRegionOfVenue())
				.append("address", event.getAddress())
				.append("startDate", event.getStartDate())
				.append("endDate", event.getEndDate())
				.append("startTime", event.getStartTime())
				.append("endTime", event.getEndTime())
				.append("organizerId", event.getOrganizerId())
				.append("ticketPrice", event.getTicketPrice())
				.append("ailmentTags", ailmentTags)
				.append("interestTags", interestTags)
				.append("habitTags", habitTags)
				.append("isActive", event.getIsActive())
				.append("isOpen", event.getIsOpen())
				.append("timeCreated", event.getTimeCreated())
				.append("timeUpdated", event.getTimeUpdated());
		return doc;
	}

	public static Event convertJsonToEvent(JSONObject json){

		ArrayList<Ailment> ailmentTags = new ArrayList<Ailment>();
		JSONArray ailmentArray = json.getJSONArray("ailmentTags");
		if(ailmentArray != null){
			int len = ailmentArray.length();
			for(int i=0;i<len;i++){
				ailmentTags.add(Ailment.getAilment(ailmentArray.getInt(i)));
			}
		}

		ArrayList<Interest> interestTags = new ArrayList<Interest>();
		JSONArray interestArray = json.getJSONArray("interestTags");
		if(interestArray != null){
			int len = interestArray.length();
			for(int i=0;i<len;i++){
				interestTags.add(Interest.getInterest(interestArray.getInt(i)));
			}
		}

		ArrayList<Habit> habitTags = new ArrayList<Habit>();
		JSONArray habitArray = json.getJSONArray("habitTags");
		if(habitArray != null){
			int len = habitArray.length();
			for(int i=0;i<len;i++){
				habitTags.add(Habit.getHabit(habitArray.getInt(i)));
			}
		}

		Event event = new Event( json.getString("name"),
				json.getString("description"),
				json.getString("venue"),
				json.getString("regionOfVenue"),
				json.getString("address"),
				json.getString("startDate"),
				json.getString("endDate"),
				json.getString("startTime"),
				json.getString("endTime"),
				json.getString("organizerId"),
				json.getString("ticketPrice"),
				ailmentTags,
				interestTags,
				habitTags,
				json.getBoolean("isActive"),
				json.getBoolean("isOpen"),
				(new Date()).toString(),
				(new Date()).toString());
		return event;
	}
}
